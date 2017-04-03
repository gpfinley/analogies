import java.util.*;
import java.util.logging.Logger;

/**
 * Low-memory implementation of Phrase
 * Stores the whole phrase as a string, which means that fancy operations will take longer
 * Delimiter will always be a space in the internal representation
 * Created by gpfinley on 2/26/16.
 */
public final class Phrase implements Iterable<String>, Comparable<Phrase> {

    private static Logger LOGGER = Logger.getLogger(Phrase.class.getName());

    private final String form;

    public Phrase(String phrase) {
        this(phrase, "\\s+");
    }

    public Phrase(String phrase, String delimRegex) {
        String thisForm = phrase.trim().replaceAll(delimRegex, " ").trim();
        if(thisForm.equals(" ")) thisForm = "";
        if(thisForm.length() == 0) zeroLengthWarn(phrase);
        form = thisForm;
    }

    public Phrase(String[] words) {
        String form = words[0];
        for(int i=1; i<words.length; i++) {
            form += " " + words[i];
        }
        this.form = form;
        if(form.length() == 0) zeroLengthWarn();
    }

    public Phrase(Iterable<String> words) {
        Iterator<String> iter = words.iterator();
        String form = iter.next();
        while(iter.hasNext()) {
            form += " " + iter.next();
        }
        this.form = form;
        if(form.length() == 0) zeroLengthWarn();
    }

    private static void zeroLengthWarn() {
        LOGGER.warning("Creating an empty phrase! May cause trouble down the line.");
    }
    private static void zeroLengthWarn(String word) {
        LOGGER.warning("Creating an empty phrase from '" + word + "'! May cause trouble down the line.");
    }

    /**
     * Get the word at the specified index. Can be negative to count from end
     * @param i
     * @return
     */
    public String word(int i) {
        String[] words = words();
        return i >= 0 ? words[i] : words[i+words.length];
    }

    public Phrase getOneWordPhrase(int i) {
        return new Phrase(word(i));
    }

    public boolean contains(Phrase other) {
        return form.contains(other.toString());
    }

    /**
     * See if this phrase wholly contains another phrase
     * @param sub
     * @return true if this phrase contains all the words of sub, in order
     */
    public boolean hasSubphrase(Phrase sub) {
        String[] words = words();
        String[] otherWords = sub.words();
        if (otherWords.length > words.length) return false;
        List<String> otherWordsList = Arrays.asList(otherWords);
        for (int i=0; i <= words.length - otherWords.length; i++) {
            if (Arrays.asList(Arrays.copyOfRange(words, i, i+otherWords.length)).equals(otherWordsList)) return true;
        }
        return false;
    }


    public Phrase concatenate(Phrase other) {
        return new Phrase(toString() + " " + other.toString());
    }

    @Override
    public String toString() {
        return form;
    }

    /**
     * Merge into a single string with a given delimiter
     * @param delim the delimiter to use
     * @return [this, great, phrase], "_" -> (String) "this_great_phrase"
     */
    public String toStringDelimited(String delim) {
        return form.replace(" ", delim);
    }

    /**
     * Return a new Phrase object without a word at the specified index
     * @param index the index of the word to omit
     * @return a new Phrase without that word
     */
    public Phrase without(int index) {
        String[] words = form.split(" ");
        if(index < 0) {
            index = words.length - index;
        }

        String[] newWords = new String[words.length-1];
        for(int i=0, j=0; i < words.length-1; i++, j++) {
            if(index == i) {
                j++;
            }
            newWords[i] = words[j];
        }
        return new Phrase(newWords);
    }

    /**
     * number of words
     * @return
     */
    public int size() {
        int s = 1;
        for(int i=0; i<form.length(); i++) {
            if(form.charAt(i) == ' ') s++;
        }
        return s;
    }

    /**
     * number of characters (same as toString.length())
     * @return
     */
    public int length() {
        return form.length();
    }

    public List<String> getWords() {
        return Arrays.asList(form.split(" "));
    }

    private String[] words() {
        return form.split(" ");
    }

    /**
     * Return all possible permutations of the words in this phrase
     * @return a set of Phrases, each with a possible word order (including the original phrase)
     */
    public Set<Phrase> permutations() {
        return permute(new ArrayList<>(), getWords());
    }

    /**
     * Recursive algorithm for getting all permutations
     * @param usedWords the words that have been added to this permutation so far
     * @param remainingWords the words remaining to be added
     * @return a set of all phrases possible from the words remaining
     */
    private Set<Phrase> permute(List<String> usedWords, List<String> remainingWords) {
        Set<Phrase> toReturn = new HashSet<>();
        if(remainingWords.size() == 0) {
            toReturn.add(new Phrase(usedWords));
        } else {
            for (int i=0; i<remainingWords.size(); i++) {
                List<String> newRemainingWords = new ArrayList<>(remainingWords);
                String newWord = newRemainingWords.remove(i);
                usedWords.add(newWord);
                toReturn.addAll(permute(usedWords, newRemainingWords));
                usedWords.remove(usedWords.size()-1);
            }
        }
        return toReturn;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if(obj instanceof Phrase)
            return toString().equals(obj.toString());
        if(obj instanceof String)
            return toString().equals(obj);
        if(obj instanceof String[])
            return Arrays.equals(words(), (String[]) obj);
        if(obj instanceof Collection)
            return Arrays.equals(((Collection)obj).toArray(), words());
        return false;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public int compareTo(Phrase phrase) {
        return this.toString().compareTo(phrase.toString());
    }

    @Override
    public Iterator<String> iterator() {
        return getWords().iterator();
    }

}