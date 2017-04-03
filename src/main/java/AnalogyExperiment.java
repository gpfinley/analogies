import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Use the analogy corpus provided with word2vec and score based on mean correlation with term 4
 *
 * Created by gpfinley on 4/26/16.
 */
public class AnalogyExperiment {

    public static class Builder {

        private String embeddingsFile;
        private String[] analogiesFiles;
        // todo: delete wordnet?
        private String wordNetPath = null;
        private boolean caseSensitive = false;
        private int filterOn = 1000000000;
        private String vocabFile = null;
        private Embeddings emb = null;
        private List<Analogy> analogies = null;
        private Map<String, List<Analogy>> analogiesByCategory = null;
        private boolean calculateBaselineRank = true;
        private boolean calculateAddRank = true;
        private boolean calculateMulRank = false;
        private boolean calculateDiffRank = false;
        private boolean calculateDomainSimilarityRank = false;

        public Builder embeddingsFile(String embeddingsFile) {
            this.embeddingsFile = embeddingsFile;
            return this;
        }
        public Builder useEmbeddings(Embeddings emb) {
            this.emb = emb;
            return this;
        }
        public Builder useAnalogies(List<Analogy> analogies) {
            this.analogies = analogies;
            return this;
        }
        public Builder useCategorizedAnalogies(Map<String, List<Analogy>> analogies) {
            this.analogiesByCategory = analogies;
            return this;
        }
        public Builder calculateBaselineRank(boolean calculateBaselineRank) {
            this.calculateBaselineRank = calculateBaselineRank;
            return this;
        }
        public Builder calculateDomainSimilarityRank(boolean calculateDomainSimilarityRank) {
            this.calculateDomainSimilarityRank = calculateDomainSimilarityRank;
            return this;
        }
        public Builder calculateAddRank(boolean calculateAddRank) {
            this.calculateAddRank = calculateAddRank;
            return this;
        }
        public Builder calculateMulRank(boolean calculateMulRank) {
            this.calculateMulRank = calculateMulRank;
            return this;
        }
        public Builder calculateDiffRank(boolean calculateDiffRank) {
            this.calculateDiffRank = calculateDiffRank;
            return this;
        }
        public Builder analogiesFiles(String[] analogiesFiles) {
            this.analogiesFiles = analogiesFiles;
            return this;
        }
        public Builder wordNetPath(String wordNetPath) {
            this.wordNetPath = wordNetPath;
            return this;
        }
        public Builder caseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }
        public Builder filterOn(int filterOn) {
            this.filterOn = filterOn;
            return this;
        }

        public Builder useGlove(String vocabFile) {
            this.vocabFile = vocabFile;
            return this;
        }

        public AnalogyExperiment createExperiment() throws IOException {

            if(emb == null) {
                if(vocabFile == null) {
                    emb = Word2vecReader.readBinFile(embeddingsFile);
                }
                else {
                    emb = GloVeReader.readBinFile(embeddingsFile, vocabFile);
                }
                emb.normalizeAll();
                if(filterOn < emb.size()) {
                    emb.filterOn(new HashSet<>(emb.getLexicon().subList(0, filterOn)));
                }
            }

            List<String> categories = new ArrayList<>();

            if(analogies == null) {
                analogies = new ArrayList<>();
                String activeCategory = null;
                for (String analogiesFile : analogiesFiles) {
                    InputStream inputStream = new FileInputStream(analogiesFile);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    Pattern whitespaceOnly = Pattern.compile("\\s*");
                    while ((line = reader.readLine()) != null) {
                        if (whitespaceOnly.matcher(line).matches()) continue;
                        if (line.startsWith(":")) {
                            activeCategory = line;
                        } else {
                            if (!this.caseSensitive) line = line.toLowerCase();
                            String[] fields = line.split(" ");
                            if (emb.contains(fields[0]) && emb.contains(fields[1]) && emb.contains(fields[2]) && emb.contains(fields[3])) {
                                Analogy thisAnalogy = new Analogy(fields);
                                analogies.add(thisAnalogy);
                                categories.add(activeCategory);
                            } else {
                                System.out.println("Throwing out analogy " + line);
                            }
                        }
                    }
                }
            } else {
                if(analogiesByCategory != null) {
                    analogies = new ArrayList<>();
                    for(List<Analogy> oneList : analogiesByCategory.values()) {
                        analogies.addAll(oneList);
                    }
                }
            }

            AnalogyExperiment experiment = new AnalogyExperiment();
            experiment.useBaseRank = calculateBaselineRank;
            experiment.useDomainsimRank = calculateDomainSimilarityRank;
            experiment.useAddRank = calculateAddRank;
            experiment.useMulRank = calculateMulRank;
            experiment.useDiffRank = calculateDiffRank;
            experiment.emb = emb;
            experiment.analogies = analogies;
            experiment.categories = categories;
            if(wordNetPath != null) {
                experiment.wordNet = new WordNet(wordNetPath);
            }

            experiment.process();

            return experiment;
        }
    }

    private List<Analogy> analogies;
    private List<String> categories;
    private Embeddings emb;
    private WordNet wordNet;

    private boolean useBaseRank;
    private boolean useDomainsimRank;
    private boolean useAddRank;
    private boolean useMulRank;
    private boolean useDiffRank;

    private List<Integer> baselineRanks;
    private List<Integer> domainsimRanks;
    private List<Integer> addRanks;
    private List<Integer> mulRanks;
    private List<Integer> diffRanks;

    private List<Double> baselineSimilarity;
    private List<Double> domainSimilarity;
    private List<Double> additiveSimilarity;
    private List<Double> multiplicativeScores;

    public List<Double> getBaselineSimilarity() {
        return baselineSimilarity;
    }

    public List<Double> getDomainSimilarity() {
        return domainSimilarity;
    }

    public List<Double> getAdditiveSimilarity() {
        return additiveSimilarity;
    }

    public List<Double> getMultiplicativeScores() {
        return multiplicativeScores;
    }

    public Embeddings getEmbeddings() {
        return emb;
    }

    public WordNet getWordNet() {
        return wordNet;
    }

    public List<Analogy> getAnalogies() {
        return analogies;
    }

    public List<String> getCategories() {
        return categories;
    }

    public List<Integer> getBaselineRanks() {
        return baselineRanks;
    }

    public List<Integer> getDomainsimRanks() {
        return domainsimRanks;
    }

    public List<Integer> getAddRanks() {
        return addRanks;
    }

    public List<Integer> getMulRanks() {
        return mulRanks;
    }

    public List<Integer> getDiffRanks() {
        return diffRanks;
    }

    // prevent instantiation outside Builder
    private AnalogyExperiment() {}

    /**
     * Determine all words or phrases used among all analogies
     * @return
     */
    public Set<Phrase> wordsUsed() {
        Set<Phrase> used = new HashSet<>();
        for(Analogy analogy : analogies) {
            used.add(analogy.w1);
            used.add(analogy.w2);
            used.add(analogy.w3);
            used.add(analogy.w4);
        }
        return used;
    }

    /**
     * Scores all ranks and similarities. Will take a long time!
     */
    private void process() {
        baselineRanks = new ArrayList<>();
        domainsimRanks = new ArrayList<>();
        addRanks = new ArrayList<>();
        mulRanks = new ArrayList<>();
        diffRanks = new ArrayList<>();

        // create a list of integer arrays, each of which holds ranks for: w3*w4, w2*w4, 3CosAdd, 3CosMul
        List<Integer[]> answersList = new ArrayList<>();
        answersList.add(new Integer[analogies.size()]);
        answersList.add(new Integer[analogies.size()]);
        answersList.add(new Integer[analogies.size()]);
        answersList.add(new Integer[analogies.size()]);
        answersList.add(new Integer[analogies.size()]);

        Threading.fillArraysThreaded(answersList, index -> {
            Analogy analogy = analogies.get(index);
            WordEmbedding w1 = emb.get(analogy.w1);
            WordEmbedding w2 = emb.get(analogy.w2);
            WordEmbedding w3 = emb.get(analogy.w3);
            WordEmbedding w4 = emb.get(analogy.w4);
            WordEmbedding w1p = null;
            WordEmbedding w2p = null;
            WordEmbedding w3p = null;
            WordEmbedding w4p = null;
            if (useMulRank) {
                w1p = new WordEmbedding(w1);
                w2p = new WordEmbedding(w2);
                w3p = new WordEmbedding(w3);
                w4p = new WordEmbedding(w4);
                w1p.add(1);
                w2p.add(1);
                w3p.add(1);
                w4p.add(1);
            }
            int addRank = 1;
            int mulRank = 1;
            int diffRank = 1;
            int baselineRank = 1;
            int domainsimRank = 1;
            // shouldn't need this check...but just in case
            if (emb.contains(analogy.w1) && emb.contains(analogy.w2) && emb.contains(analogy.w3) && emb.contains(analogy.w4)) {
                double addScore = 0;
                double baselineScore = 0;
                double domainsimScore = 0;
                double mulScore = 0;
                double diffScore = 0;
                WordEmbedding calculated = null;
                WordEmbedding diffVec = null;
                if (useAddRank) {
                    calculated = analogyHypothesisEmbedding(analogy);
                    addScore = calculated.dot(emb.get(analogy.w4));
                }
                if (useBaseRank) {
                    baselineScore = w3.dot(w4);
                }
                if (useDomainsimRank) {
                    domainsimScore = w2.dot(w4);
                }
                if (useMulRank) {
                    mulScore = scoreLevyGoldberg(w1p, w2p, w3p, w4p);
                }
                if (useDiffRank) {
                    diffVec = w2.difference(w1);
                    diffScore = diffVec.cosSim(w4.difference(w3));
                }
                Iterator<WordEmbedding> iter = emb.embeddingIterator();
                while (iter.hasNext()) {
                    WordEmbedding hyp = iter.next();
                    if (hyp == w1 || hyp == w2 || hyp == w3) continue;
                    if (useAddRank) {
                        double addCompScore = calculated.dot(hyp);
                        if (addCompScore > addScore) addRank += 1;
                    }
                    if (useBaseRank) {
                        double baselineCompScore = w3.dot(hyp);
                        if (baselineCompScore > baselineScore) baselineRank += 1;
                    }
                    if (useDomainsimRank) {
                        double domainsimCompScore = w2.dot(hyp);
                        if (domainsimCompScore > domainsimScore) domainsimRank += 1;
                    }
                    if (useDiffRank) {
                        double diffCompScore = diffVec.cosSim(hyp.difference(w3));
                        if (diffCompScore > diffScore) diffRank += 1;
                    }
                    if (useMulRank) {
                        hyp = new WordEmbedding(hyp);
                        hyp.add(1);
                        double mulCompScore = scoreLevyGoldberg(w1p, w2p, w3p, hyp);
                        if (mulCompScore > mulScore) mulRank += 1;
                    }
                }
            } else {
                baselineRank = emb.size();
                domainsimRank = emb.size();
                addRank = emb.size();
                mulRank = emb.size();
                diffRank = emb.size();
            }
            List<Integer> ranksList = new ArrayList<>();
            ranksList.add(baselineRank);
            ranksList.add(domainsimRank);
            ranksList.add(addRank);
            ranksList.add(mulRank);
            ranksList.add(diffRank);
            return ranksList;
        });

        baselineRanks = Arrays.asList(answersList.get(0));
        domainsimRanks = Arrays.asList(answersList.get(1));
        addRanks = Arrays.asList(answersList.get(2));
        mulRanks = Arrays.asList(answersList.get(3));
        diffRanks = Arrays.asList(answersList.get(4));

        baselineSimilarity = new ArrayList<>();
        domainSimilarity = new ArrayList<>();
        additiveSimilarity = new ArrayList<>();
        multiplicativeScores = new ArrayList<>();
        analogies.forEach(analogy -> {
            baselineSimilarity.add(emb.get(analogy.w3).cosSim(emb.get(analogy.w4)));
            domainSimilarity.add(emb.get(analogy.w2).cosSim(emb.get(analogy.w4)));
            additiveSimilarity.add(analogyHypothesisEmbedding(analogy).cosSim(emb.get(analogy.w4)));
            multiplicativeScores.add(scoreLevyGoldberg(emb.get(analogy.w1), emb.get(analogy.w2), emb.get(analogy.w3), emb.get(analogy.w4)));
        });
    }


    // todo: needed?
    /**
     * Return a mapping between all category names and all pairs (represented as strings w/ ':' in middle)
     *      represented in any analogy of that category
     */
    public Map<String, Set<String>> getPairsByCategory() {

        Map<String, Set<String>> pairsByCategory = new HashMap<>();

        for (int i = 0; i < analogies.size(); i++) {
            String category = categories.get(i);
            Analogy analogy = analogies.get(i);
            pairsByCategory.putIfAbsent(category, new HashSet<>());
            String relation1 = analogy.w1 + ":" + analogy.w2;
            String relation2 = analogy.w3 + ":" + analogy.w4;
            pairsByCategory.get(category).add(relation1);
            pairsByCategory.get(category).add(relation2);
        }

        return pairsByCategory;
    }


    /**
     * todo: doc
     * @return
     */
    public Map<String, List<Analogy>> getAnalogiesByCategory() {
        Map<String, List<Analogy>> analogiesByCategory = new HashMap<>();
        for (int i = 0; i < analogies.size(); i++) {
            String category = categories.get(i);
            Analogy analogy = analogies.get(i);
            analogiesByCategory.putIfAbsent(category, new ArrayList<>());
            analogiesByCategory.get(category).add(analogy);
        }
        return analogiesByCategory;
    }


//    public Map<String, Double> similaritiesToCategoryMean() {
//        Map<String, WordEmbedding> meanVectors = new HashMap<>();
//        Map<String, Double> sims = new HashMap<>();
//
//        for(String category : analogiesByCategory.keySet()) {
//            List<Analogy> analogies = analogiesByCategory.get(category);
//            Map<String, Double> similarities = new HashMap<>();
//
//            WordEmbedding meanVector = new WordEmbedding(emb.dimensionality());
//            for (Analogy analogy : analogies) {
//                String relation1 = analogy.w1 + ":" + analogy.w2;
//                String relation2 = analogy.w3 + ":" + analogy.w4;
//                if (emb.contains(analogy.w1) && emb.contains(analogy.w2)) {
//                    if(!similarities.containsKey(relation1)) {
//                        meanVector.add(emb.get(analogy.w1).difference(emb.get(analogy.w2)));
//                        similarities.put(relation1, -1.);
//                    }
//                }
//                if (emb.contains(analogy.w3) && emb.contains(analogy.w4)) {
//                    if (!similarities.containsKey(relation2)) {
//                        meanVector.add(emb.get(analogy.w3).difference(emb.get(analogy.w4)));
//                        similarities.put(relation2, -1.);
//                    }
//                }
//            }
//            meanVector.scalarMultiply(1. / similarities.size());
//            meanVectors.put(category, meanVector);
//
//            for (Analogy analogy : analogies) {
//                String relation1 = analogy.w1 + ":" + analogy.w2;
//                String relation2 = analogy.w3 + ":" + analogy.w4;
//                if(similarities.get(relation1) == -1.) {
//                    double sim = emb.get(analogy.w1).difference(emb.get(analogy.w2)).cosSim(meanVector);
//                    similarities.put(relation1, sim);
//                }
//                if(similarities.get(relation2) == -1.) {
//                    double sim = emb.get(analogy.w3).difference(emb.get(analogy.w4)).cosSim(meanVector);
//                    similarities.put(relation2, sim);
//                }
//            }
//
//            System.out.println(similarities.size() + "\t" + category);
//            sims.putAll(similarities);
//        }
//        return sims;
//    }

    private WordEmbedding analogyHypothesisEmbedding(Analogy analogy) {
        WordEmbedding calculated = new WordEmbedding(emb.get(analogy.w3));
        calculated.add(emb.get(analogy.w2));
        calculated.subtract(emb.get(analogy.w1));
        return calculated;
    }

    /**
     * Compute Levy & Goldberg's 3CosMul score for an analogy
     * All vectors should be positive!
     * @param w1 word embedding for first word in analogy
     * @param w2 second
     * @param w3 third
     * @param w4 fourth (hypothesis or gold)
     * @return multiplicative score (does not normalize vectors)
     */
    private double scoreLevyGoldberg(WordEmbedding w1, WordEmbedding w2, WordEmbedding w3, WordEmbedding w4) {
        return w4.cosSim(w3) * w4.cosSim(w2) / (.001 + w4.cosSim(w1));
    }

}

