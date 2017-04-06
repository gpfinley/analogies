import com.sun.istack.internal.Nullable;
import org.kohsuke.args4j.Option;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Simple API for parsing WordNet 3.0
 * Will also create analogies and measure word sense entropy
 *
 * Created by xxxxxxxx on 4/28/16.
 */
public class WordNet {

    class Lemma {
        public final String form;
        public final String pos;
        public List<Sense> senses;
        Lemma(String form, String pos) {
            this.form = form;
            this.pos = pos;
            senses = new ArrayList<>();
        }
        public void addSense(Synset synset) {
            senses.add(new Sense(this, senses.size(), synset));
        }
        @Override
        public String toString() {
            return this.form + ", " + pos;
        }
        @Override
        public int hashCode() {
            return toString().hashCode();
        }
        // be careful! This could cause problems if multiple versions of the same word exist with different sense lists
        @Override
        public boolean equals(Object other) {
            return toString().equals(other.toString());
        }

    }
    class Sense {
        public final Lemma lemma;
        public final int number;
        public final Synset synset;
        public List<LexPointer> pointers;
        public int frequency;       // from WordNet's cntlist
        public Sense(Lemma lemma, int number, Synset synset) {
            this.lemma = lemma;
            this.number = number;
            this.synset = synset;
            pointers = new ArrayList<>();
        }
        @Override
        public String toString() {
            return lemma.form + "_" + lemma.pos + "_" + number;
        }
        // careful...
        @Override
        public boolean equals(Object other) {
            return toString().equals(other.toString());
        }
    }
    class LexPointer {
        public Sense target;
        public String type;
        public LexPointer(Sense target, String type) {
            this.target = target;
            this.type = type;
        }
        @Override
        public String toString() {
            return this.type + this.target;
        }
    }
    class SemPointer {
        public Synset target;
        public String type;
        public SemPointer(Synset target, String type) {
            this.target = target;
            this.type = type;
        }
        @Override
        public String toString() {
            return this.type + this.target;
        }
    }
    class Synset {
        public List<Sense> senses = new ArrayList<>();
        public List<SemPointer> pointers = new ArrayList<>();
        public String pos;
        public Synset(String pos) {
            this.pos = pos;
        }
        @Override
        public String toString() {
            return "Synset with senses: " + this.senses.toString();
        }
    }
    class Pair implements Comparable<Pair> {
        public final String w1;
        public final String w2;
        Pair(String w1, String w2) {
            this.w1 = w1;
            this.w2 = w2;
        }
        @Override
        public String toString() {
            return (w1+":"+w2);
        }
        @Override
        public int hashCode() {
            return toString().hashCode();
        }
        @Override
        public boolean equals(Object other) {
            return toString().equals(other.toString());
        }
        // is this one really necessary?
        @Override
        public int compareTo(Pair other) {
            return toString().compareTo(other.toString());
        }
    }

    // filenames use less abbreviated forms of part of speech than fields in the file
    private static final Map<String, String> shortLongPos;
    private static final Map<String, Map<String, String>> posMorphReplacements;
    static {
        shortLongPos = new HashMap<>();
        shortLongPos.put("a", "adj");
        shortLongPos.put("n", "noun");
        shortLongPos.put("r", "adv");
        shortLongPos.put("v", "verb");
        posMorphReplacements = new HashMap<>();
        Map<String, String> nounMorphReplacements = new HashMap<>();
        Map<String, String> verbMorphReplacements = new HashMap<>();
        Map<String, String> adjMorphReplacements = new HashMap<>();
        nounMorphReplacements.put("s","");
        nounMorphReplacements.put("ses","s");
        nounMorphReplacements.put("xes","x");
        nounMorphReplacements.put("zes","z");
        nounMorphReplacements.put("ches","ch");
        nounMorphReplacements.put("shes","sh");
        nounMorphReplacements.put("men","man");
        nounMorphReplacements.put("ies","y");
        verbMorphReplacements.put("s","");
        verbMorphReplacements.put("ies","y");
        verbMorphReplacements.put("es","e");
        verbMorphReplacements.put("es","");
        verbMorphReplacements.put("ed","e");
        verbMorphReplacements.put("ed","");
        verbMorphReplacements.put("ing","e");
        verbMorphReplacements.put("ing","");
        adjMorphReplacements.put("er","");
        adjMorphReplacements.put("est","");
        adjMorphReplacements.put("er","e");
        adjMorphReplacements.put("est","e");
        posMorphReplacements.put("n", nounMorphReplacements);
        posMorphReplacements.put("v", verbMorphReplacements);
        posMorphReplacements.put("a", adjMorphReplacements);
        posMorphReplacements.put("r", new HashMap<>());
    }

    private Map<String, Map<String, Lemma>> lemmasByPos;
    private Set<Lemma> allLemmas;
    private Map<String, Map<String, String>> morphExceptionReplacementsByPos;
    private Set<String> allowableWords = null;

    private final static double MIN_COUNT = 0.1;

    /**
     * Find the number of senses of a given word
     * @param word
     * @return
     */
    public int numSenses(String pos, String word) {
        if(!lemmasByPos.containsKey(pos) || !lemmasByPos.get(pos).containsKey(word)) return 0;
        return lemmasByPos.get(pos).get(word).senses.size();
    }

    public void setAllowableWords(Set<String> allowableWords) {
        this.allowableWords = allowableWords;
    }

    public void printAntonyms(String pos, String word) {
        for(Sense sense : lemmasByPos.get(pos).get(word).senses) {
            for(LexPointer pointer : sense.pointers) {
                if(pointer.type.equals("!")) {
                    System.out.println(sense + " is an antonym of " + pointer.target + " " + pointer.type);
                }
            }
        }
    }

    /**
     * Print out analogies according to these stipulations:
     *      - both sides of the analogy have the same relation
     *      - the relation is unambiguous on the left side for the words given
     *      - the fourth word can be guessed unambiguously given the relation
     * Sort them according to perceived difficulty (degree of word ambiguity)
     * Currently restricted to single-word terms
     * @param relation a string pertaining to the WordNet relation (see WN doc)
     * @param minEntropy only consider analogies with at least this much total sense entropy
     * @param maxEntropy only consider analogies with at least this much total sense entropy
     * @param minPros only consider analogies with at least high PRoS (for EACH pair)
     * @param maxPros only consider analogies with less than this PRoS (for EACH pair)
     */
    public NavigableMap<Analogy, Double> solvableAnalogies(String relation, double minEntropy, double maxEntropy, double minPros, double maxPros, Collection<String> restrictPos) {

        if(maxEntropy < 0) maxEntropy = Double.MAX_VALUE;

        // find all relations of this type
        // of these, find the ones that are 1) the only possible relation between these terms, and 2) guessable from the first term
        // then sort them in order of decreasing total word entropy (sum over all 4 words)

        Set<Pair> pairs;
        Set<Pair> hasOtherRelation;

        pairs = new HashSet<>();
        hasOtherRelation = new HashSet<>();

        // set up a non-PoS-dependent entropy lookup
        Map<String, Double> wordEntropy = new HashMap<>();

        System.out.println("Finding all pairs with this relation...");
        for(Lemma lemma : allLemmas) {
            if(!wordEntropy.containsKey(lemma.form)) {
                wordEntropy.put(lemma.form, getEntropyOverLemmas(lemma.form));
            }
            if(allowableWords != null && !allowableWords.contains(lemma.form)) {
                continue;
            }
            if(restrictPos != null && !restrictPos.contains(lemma.pos)) {
                continue;
            }
            for(Sense sense : lemma.senses) {
                for(LexPointer pointer : sense.pointers) {
                    if(allowableWords != null && !allowableWords.contains(pointer.target.lemma.form)) {
                        continue;
                    }
                    Pair pair = new Pair(sense.lemma.form, pointer.target.lemma.form);
                    if(!pair.w1.equals(pair.w2)) {
                        if (pointer.type.equals(relation)) {
                            pairs.add(pair);
                        } else {
                            hasOtherRelation.add(pair);
                        }
                    }
                }
                for(SemPointer pointer : sense.synset.pointers) {
                    for(Sense pointedSense : pointer.target.senses) {
                        if(allowableWords != null && !allowableWords.contains(pointedSense.lemma.form)) {
                            continue;
                        }
                        Pair pair = new Pair(sense.lemma.form, pointedSense.lemma.form);
                        if(!pair.w1.equals(pair.w2)) {
                            if (pointer.type.equals(relation)) {
                                pairs.add(pair);
                            } else {
                                hasOtherRelation.add(pair);
                            }
                        }
                    }
                }
            }
        }

        System.out.println("Filtering for pairs with an unambiguous relation...");
        Set<Pair> unambiguousRelation = new HashSet<>(pairs);
//        unambiguousRelation.removeAll(hasOtherRelation);      // more elegant but harder to filter on single word
        for(Pair pair : pairs) {
            // single word terms only
            if(pair.w1.contains("_") || pair.w2.contains("_") || hasOtherRelation.contains(pair))
                unambiguousRelation.remove(pair);
        }

        System.out.println("Filtering for pairs guessable by the first term...");
        Map<String, List<String>> pairsMap = new HashMap<>();
        for(Pair pair : pairs) {
            pairsMap.putIfAbsent(pair.w1, new ArrayList<String>());
            pairsMap.get(pair.w1).add(pair.w2);
        }
        Set<Pair> pairsGuessableFromFirstTerm = new HashSet<>(pairs);
        for(Pair pair : pairs) {
            if(pairsMap.containsKey(pair.w1) && pairsMap.get(pair.w1).size() > 1) {
                pairsGuessableFromFirstTerm.remove(pair);
            }
            // single word terms only
            else if(pair.w1.contains("_") || pair.w2.contains("_"))
                pairsGuessableFromFirstTerm.remove(pair);
        }

        Map<Analogy, Double> analogiesAndEntropy = new HashMap<>();
        long enoughEntropy = 0;
        long notEnoughEntropy = 0;

        System.out.println("Constructing " + (long)unambiguousRelation.size()*pairsGuessableFromFirstTerm.size() + " analogies");
        System.out.println(unambiguousRelation.size());
        System.out.println(pairsGuessableFromFirstTerm.size());
        for(Pair pair1 : unambiguousRelation) {
            for(Pair pair2 : pairsGuessableFromFirstTerm) {
                if(pair1.w1.equals(pair2.w1) || pair1.w1.equals(pair2.w2) || pair1.w2.equals(pair2.w1) || pair1.w2.equals(pair2.w2)) continue;
                double entropy = wordEntropy.get(pair1.w1) + wordEntropy.get(pair1.w2) + wordEntropy.get(pair2.w1) + wordEntropy.get(pair2.w2);
                if(entropy >= minEntropy && entropy < maxEntropy) {
                    analogiesAndEntropy.put(new Analogy(pair1.w1, pair1.w2, pair2.w1, pair2.w2), entropy);
                    enoughEntropy++;
                } else {
                    notEnoughEntropy++;
                }
                if((enoughEntropy + notEnoughEntropy) % 1000000 == 0) {
                    System.out.println(enoughEntropy + " analogies above threshold; " + notEnoughEntropy + " below threshold (total: " + (enoughEntropy+notEnoughEntropy) + ")");
                }
            }
        }
        System.out.println(analogiesAndEntropy.size());

        System.out.println("Sorting analogies...");
        TreeMap<Analogy, Double> sortedAnalogiesEntropy = new TreeMap<>(new ByValue(analogiesAndEntropy));
        sortedAnalogiesEntropy.putAll(analogiesAndEntropy);
        return sortedAnalogiesEntropy;
    }

    /**
     * Constructor reads in the entire core of the WordNet database (all data.* and index.* files)
     * @param wordNetHome the directory containing WordNet (this directory should contain a "dict" folder w/ DB files)
     * @throws IOException
     */
    public WordNet(String wordNetHome) throws IOException {

        lemmasByPos = new HashMap<>();

        Map<String, Map<String, Synset>> posOffsetSynsetMap = new HashMap<>();

        Path wordNetDict = Paths.get(wordNetHome).resolve("dict");

        String[] poses = {"n", "a", "v", "r"};
        for(String pos : poses) {
            lemmasByPos.put(pos, new HashMap<String, Lemma>());
        }

        // Load up all the synset offsets from all parts of speech before doing anything else
        for(String pos : poses) {
            posOffsetSynsetMap.put(pos, new HashMap<String, Synset>());
            Path dataFile = wordNetDict.resolve("data." + shortLongPos.get(pos));
            BufferedReader reader = new BufferedReader(new FileReader(dataFile.toFile()));
            String line;
            while((line = reader.readLine()) != null) {
                if (line.startsWith("  ")) continue;
                String[] fields = line.split(" ");
                posOffsetSynsetMap.get(pos).put(fields[0], new Synset(pos));
            }
        }

        // Find all lemmas from the index files, then all sense information from the data files
        for(String pos : poses) {
            Path indexFile = wordNetDict.resolve("index." + shortLongPos.get(pos));
            BufferedReader reader = new BufferedReader(new FileReader(indexFile.toFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("  ")) continue;
                String[] fields = line.split(" ");
                Lemma thisLemma = new Lemma(fields[0], fields[1]);
                int pointerTypes = Integer.parseInt(fields[3]);
                String[] synsetOffsets = Arrays.copyOfRange(fields, pointerTypes + 6, fields.length);
                for (String offset : synsetOffsets) {
                    Synset thisSynset = posOffsetSynsetMap.get(pos).get(offset);
                    thisLemma.addSense(thisSynset);
                    posOffsetSynsetMap.get(pos).put(offset, thisSynset);
                }
                lemmasByPos.get(pos).put(fields[0], thisLemma);
            }
        }

        for(String pos : poses) {
            Path dataFile = wordNetDict.resolve("data." + shortLongPos.get(pos));
            BufferedReader reader = new BufferedReader(new FileReader(dataFile.toFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("  ")) continue;
                String[] fields = line.split(" ");
                Synset thisSynset = posOffsetSynsetMap.get(pos).get(fields[0]);
                int wordCount = Integer.parseInt(fields[3], 16);
                // loop through all words for this synset and their sense number
                for (int i = 4; i < 4 + wordCount * 2; i += 2) {
                    String word = fields[i].toLowerCase();
                    // DON'T USE THE SENSE NUMBER PROVIDED IN THE DATA FILE; USE ITS POSITION FROM THE INDEX FILE
                    // WordNet is full of places where the sense number in data disagrees with the order in index.
                    // It's not entirely clear that it should be done this way, but the GUI does it this way...
                    String wordPos = pos;
                    if(word.contains("(")) {
                        int posStart = word.indexOf("(");
                        int posEnd = word.indexOf(")");
                        wordPos = word.substring(posStart + 1, posEnd);
                        word = word.substring(0, posStart);
                    }
                    if(wordPos.equals("ip") || wordPos.equals("p")) wordPos = "a";
                    if(!lemmasByPos.containsKey(wordPos)) {
                        System.out.println("Unknown part of speech " + wordPos + " on this line: " + line);
                    } else {
                        if (lemmasByPos.get(wordPos).containsKey(word)) {
                            for(Sense sense : lemmasByPos.get(wordPos).get(word).senses) {
                                if(sense.synset == thisSynset) {
                                    thisSynset.senses.add(sense);
                                    break;
                                }
                            }
                        } else {
                            System.out.println("Word " + word + " not found for pos " + wordPos);
                        }
                    }
                }
            }
        }

        // get relations (pointers)
        for(String pos : poses) {
            Path dataFile = wordNetDict.resolve("data." + shortLongPos.get(pos));
            BufferedReader reader = new BufferedReader(new FileReader(dataFile.toFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("  ")) continue;
                String[] fields = line.split(" ");
                Synset thisSynset = posOffsetSynsetMap.get(pos).get(fields[0]);
                int wordCount = Integer.parseInt(fields[3], 16);
                int start = 5 + 2*wordCount;
                int pCount = Integer.parseInt(fields[start-1]);
                int end = start + 4 * pCount;
                for(int i = start; i < end; i += 4) {
                    String pointerType = fields[i];
                    String pointerOffset = fields[i+1];
                    String pointerPos = fields[i+2];
                    // "s" for satellite adjective; simplifying to just "a"
                    if(pointerPos.equals("s")) pointerPos = "a";
                    String pointerWordFromTo = fields[i+3];
                    // synset pointer (semantic; not dependent on word)
                    if(pointerWordFromTo.equals("0000")) {
                        thisSynset.pointers.add(new SemPointer(posOffsetSynsetMap.get(pointerPos).get(pointerOffset), pointerType));
                    } else {
                        // AABB
                        // this pointer's "from" is the AAth sense in this synset
                        // this pointer's "to" is the BBth sense in the synset linked by the offset
                        int fromSenseInt = Integer.parseInt(pointerWordFromTo.substring(0,2), 16) - 1;
                        int toSenseInt = Integer.parseInt(pointerWordFromTo.substring(2,4), 16) - 1;
                        Sense fromSense = thisSynset.senses.get(fromSenseInt);
                        Synset toSynset = posOffsetSynsetMap.get(pointerPos).get(pointerOffset);
                        Sense toSense = toSynset.senses.get(toSenseInt);
                        fromSense.pointers.add(new LexPointer(toSense, pointerType));
                    }
                }

            }
        }

        // read senses.index to get the counts (contains almost all information from cntlist)
        {
            // WordNet sense_keys have an integer that corresponds to PoS for some reason
            Map<String, String> intPosMap = new HashMap<>();
            intPosMap.put("1", "n");
            intPosMap.put("2", "v");
            intPosMap.put("3", "a");
            intPosMap.put("4", "r");
            intPosMap.put("5", "a");        // this is the category for 'adjective satellites' which I don't distinguish

            BufferedReader reader = new BufferedReader(new FileReader(wordNetDict.resolve("index.sense").toFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(" ");
                int count = Integer.parseInt(fields[3]);
                if (count == 0) continue;
                int senseNum = Integer.parseInt(fields[2]) - 1;
//            String offset = fields[1];            // not needed!
                String senseKey = fields[0];
                String[] senseKeyFields = senseKey.split("[%:]");
                String lemmaForm = senseKeyFields[0];
                String pos = intPosMap.get(senseKeyFields[1]);
                Lemma relevantLemma = lemmasByPos.get(pos).get(lemmaForm);
                relevantLemma.senses.get(senseNum).frequency = count;
            }


            // this set might be useful later
            allLemmas = new HashSet<>();
            for (Map<String, Lemma> lemmasOfSinglePos : lemmasByPos.values()) {
                allLemmas.addAll(lemmasOfSinglePos.values());
            }
        }

        // Load morphological exceptions for replacement table

        morphExceptionReplacementsByPos = new HashMap<>();
        for(String pos : poses) {
            Map<String, String> replacements = new HashMap<>();
            Path dataFile = wordNetDict.resolve(shortLongPos.get(pos) + ".exc");
            BufferedReader reader = new BufferedReader(new FileReader(dataFile.toFile()));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(" ");
                replacements.put(fields[0], fields[1]);
            }
            morphExceptionReplacementsByPos.put(pos, replacements);
        }
    }

    /**
     *
     * @param analogies
     * @param outFile
     * @throws IOException
     */
    public void writeAnalogiesToTextFile(Map<Analogy, Double> analogies, String outFile, String categoryName) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
        if(categoryName.length() > 0) {
            writer.write(": " + categoryName + "\n");
        }
        for(Analogy analogy : analogies.keySet()) {
            writer.write(analogy.w1.toString() + " " + analogy.w2.toString() + " " + analogy.w3.toString() + " " + analogy.w4.toString() + "\n");
        }
        writer.flush();
        writer.close();
        writer = new BufferedWriter(new FileWriter(outFile + "_scores"));
        if(categoryName.length() > 0) {
            writer.write(": " + categoryName + "\n");
        }
        for(Analogy analogy : analogies.keySet()) {
            writer.write(analogyEntropy(analogy) + " " + prosScore(analogy) + "\n");
        }
        writer.flush();
        writer.close();
    }

    public double analogyEntropy(Analogy analogy) {
        double entropy = 0;
        entropy += getEntropyOverLemmas(analogy.w1.toString());
        entropy += getEntropyOverLemmas(analogy.w2.toString());
        entropy += getEntropyOverLemmas(analogy.w3.toString());
        entropy += getEntropyOverLemmas(analogy.w4.toString());
        return entropy;
    }

    public double prosScore(Analogy analogy) {
        return probRelationOverSenses(analogy.w1.toString(), analogy.w2.toString(), null) + probRelationOverSenses(analogy.w3.toString(), analogy.w4.toString(), null);
    }
    public double prosScore(Analogy analogy, String forceRelation) {
        return probRelationOverSenses(analogy.w1.toString(), analogy.w2.toString(), forceRelation) + probRelationOverSenses(analogy.w3.toString(), analogy.w4.toString(), forceRelation);
    }

    /**
     * Get the proportion of sense combinations for these two words that involve some kind of relationship between them
     * (might not be the same relationship as intended, but I imagine it usually will)
     * @param w1 first word
     * @param w2 second word
     * @return how often senses of these two words will have this relationship
     */
    public double probRelationOverSenses(String w1, String w2, @Nullable String forceRelation) {
        double ratio = 0;
        Set<Lemma> lemmas1 = lemmasOf(w1);
        Set<Lemma> lemmas2 = lemmasOf(w2);

        // Estimate probabilities (based on frequency) for each sense
        Map<Sense, Double> senseProbs1 = new HashMap<>();
        Map<Sense, Double> senseProbs2 = new HashMap<>();
        double sum1 = 0;
        double sum2 = 0;
        for(Lemma l : lemmas1) {
            for(Sense sense : l.senses) {
                senseProbs1.put(sense, sense.frequency + MIN_COUNT);
                sum1 += sense.frequency + MIN_COUNT;
            }
        }
        for(Lemma l : lemmas2) {
            for(Sense sense : l.senses) {
                senseProbs2.put(sense, sense.frequency + MIN_COUNT);
                sum2 += sense.frequency + MIN_COUNT;
            }
        }

        for(Sense sense1 : senseProbs1.keySet()) {
            for(Sense sense2 : senseProbs2.keySet()) {
                searchForRelation: {
                    /**
                     * Relation should be found if either:
                     *  - the lemmas of the two forms are equivalent (there's obviously a relation there)
                     *  - the sense of each term points to any sense of the other
                     *          (w1i -> w2 && w2j -> w1)            where w1 is lemma 1 and w1i is the i'th sense of w1
                     *  - the sense of one or the other points to the sense of the other
                     *          (w1i -> w2j || w2j -> w1i)
                     */
                    // condition 1
                    double jointProb = senseProbs1.get(sense1) * senseProbs2.get(sense2) / sum1 / sum2;
                    if (sense1.lemma.form.equals(sense2.lemma.form)) {
                        ratio += jointProb;
                        break searchForRelation;
                    }

                    // condition 2 (lex pointers only; not defined for synset pointers, which don't point to forms)
                    for (LexPointer pointer : sense1.pointers) {
                        if (pointer.target.lemma.form.equals(sense2.lemma.form)) {
                            for (LexPointer pointer2 : sense2.pointers) {
                                if (pointer2.target.lemma.form.equals(sense1.lemma.form)) {
                                    // ADDED JUNE 2 TO FORCE THE SAME RELATION, NOT JUST ANY
                                    if(forceRelation != null && pointer.type.equals(forceRelation) && pointer2.type.equals(forceRelation)) {
                                        ratio += jointProb;
                                        break searchForRelation;
                                    }
                                }
                            }
                            break;
                        }
                    }

                    // condition 3 for synset pointers
                    for (SemPointer pointer : sense1.synset.pointers) {
                        if (pointer.target == sense2.synset) {
                            if(forceRelation != null && pointer.type.equals(forceRelation)) {
                                ratio += jointProb;
                                break searchForRelation;
                            }
                        }
                    }
                    for (SemPointer pointer : sense2.synset.pointers) {
                        if (pointer.target == sense1.synset) {
                            if(forceRelation != null && pointer.type.equals(forceRelation)) {
                                ratio += jointProb;
                                break searchForRelation;
                            }
                        }
                    }
                    // condition 3 for lexical pointers
                    for (LexPointer pointer : sense1.pointers) {
                        if(pointer.target == sense2) {
                            if(forceRelation != null && pointer.type.equals(forceRelation)) {
                                ratio += jointProb;
                                break searchForRelation;
                            }
                        }
                    }
                    for (LexPointer pointer : sense2.pointers) {
                        if(pointer.target == sense1) {
                            if(forceRelation != null && pointer.type.equals(forceRelation)) {
                                ratio += jointProb;
                                break searchForRelation;
                            }
                        }
                    }
                }
            }
        }

        return ratio;
    }

    /**
     * Get all lemmas that could possibly be referred to by this string through morphological rules and exceptions
     * @param word a string of the word in question (not case sensitive)
     * @return a Set of all Lemmas, of all possible parts of speech, that this word could refer to
     */
    public Set<Lemma> lemmasOf(String word) {
        word = word.toLowerCase();
        Set<Lemma> foundLemmas = new HashSet<>();
        for(String pos : shortLongPos.keySet()) {
            // see if this word unaltered is in the dictionary
            if(lemmasByPos.get(pos).containsKey(word)) foundLemmas.add(lemmasByPos.get(pos).get(word));
            // see if this word minus known regular suffixes is in the dictionary
            for(String suffix : posMorphReplacements.get(pos).keySet()) {
                if(word.endsWith(suffix)) {
                    String subbed = word.substring(0, word.length() - suffix.length()) + posMorphReplacements.get(pos).get(suffix);
                    if(lemmasByPos.get(pos).containsKey(subbed)) {
                        foundLemmas.add(lemmasByPos.get(pos).get(subbed));
                    }
                }
            }
            // see if this word is an irregular form
            if(morphExceptionReplacementsByPos.get(pos).containsKey(word)) {
                String baseForm = morphExceptionReplacementsByPos.get(pos).get(word);
                if(lemmasByPos.get(pos).containsKey(baseForm)) {
                    foundLemmas.add(lemmasByPos.get(pos).get(baseForm));
                }
            }
        }
        return foundLemmas;
    }

    public double getEntropyOverLemmas(String word) {
        List<Double> freqsList = new ArrayList<>();
        for(Lemma lemma : lemmasOf(word)) {
            for(Sense sense : lemma.senses) {
                freqsList.add(sense.frequency + MIN_COUNT);
            }
        }
        return entropy(freqsList);
    }

    private double entropy(Collection<Double> frequencies) {
        double total = 0;
        double entropy = 0;
        for(double f : frequencies) {
            total += f;
        }
        for(double f : frequencies) {
            entropy -= (f/total) * Math.log(f/total);
        }
        return entropy / Math.log(2);
    }

    /**
     * Provide min/max entropy and an output file. Callable from script
     *
     * Must provide at least three arguments, in order:
     *      - path to WordNet home
     *      - types of relations to use (string of characters, one for each relation)
     *      - file to save output to
     *
     * Also contains a number of options, which you'll probably want to use. See code below
     *
     * todo: test new method (June 2016)
     */
    public static class WriteRelations {

        @Option(name="-emin")
        private static Double minEntropy = null;

        @Option(name="-emax")
        private static Double maxEntropy = null;

        @Option(name="-pmin")
        private static Double minPros = null;

        @Option(name="-pmax")
        private static Double maxPros = null;

        @Option(name="-vocab")
        private static String vocabFile = null;

        @Option(name="-minfreq")
        private static Integer maxthWord = null;

        @Option(name="-pos")
        private static String pos = null;

        public static void main(String[] args) throws IOException {
            WordNet wordNet = new WordNet(args[0]);
            String relType = args[1];
            String outfile = args[2];
            BufferedReader reader = new BufferedReader(new FileReader(vocabFile));
            Set<String> allowableWords = new HashSet<>();
            // last argument, if provided, is the frequency rank cutoff for words to include in analogies
            if(maxthWord != null) {
                for (int i = 0; i < maxthWord; i++) {
                    try {
                        allowableWords.add(reader.readLine().split(" ")[0]);
                    } catch (Exception e) {
                        System.out.println("reached end of file");
                        break;
                    }
                }
                wordNet.setAllowableWords(allowableWords);
            }
            Set<String> restrictPos = null;
            if(pos != null) {
                restrictPos = new HashSet<>();
                for(char c : pos.toCharArray()) {
                    restrictPos.add(Character.toString(c));
                }
            }
            if(minEntropy == null) minEntropy = -Double.MAX_VALUE;
            if(maxEntropy == null) minEntropy = Double.MAX_VALUE;
            if(minPros == null) minPros = -Double.MAX_VALUE;
            if(maxPros == null) maxPros = Double.MAX_VALUE;
            Map<Analogy, Double> analogies = wordNet.solvableAnalogies(relType, minEntropy, maxEntropy, minPros, maxPros, restrictPos);
            wordNet.writeAnalogiesToTextFile(analogies, outfile, "");
        }
    }
}
