/**
 * Basically a four-word tuple class
 * Created by xxxxxxxx on 4/29/16.
 */
public class Analogy implements Comparable {
    public final Phrase w1;
    public final Phrase w2;
    public final Phrase w3;
    public final Phrase w4;
    public Analogy(String[] words) {
        w1 = new Phrase(words[0], "_");
        w2 = new Phrase(words[1], "_");
        w3 = new Phrase(words[2], "_");
        w4 = new Phrase(words[3], "_");
    }
    public Analogy(Phrase w1, Phrase w2, Phrase w3, Phrase w4) {
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
        this.w4 = w4;
    }

    /**
     * todo: is there a good reason that this has underscore as the default delimiter??
     * @param w1
     * @param w2
     * @param w3
     * @param w4
     */
    public Analogy(String w1, String w2, String w3, String w4) {
        this.w1 = new Phrase(w1, "_");
        this.w2 = new Phrase(w2, "_");
        this.w3 = new Phrase(w3, "_");
        this.w4 = new Phrase(w4, "_");
    }
    public boolean contains(Phrase phrase) {
        return (w1.equals(phrase) || w2.equals(phrase) || w3.equals(phrase) || w4.equals(phrase));
    }
    @Override
    public String toString() {
        return w1 + ":" + w2 + "::" + w3 + ":" + w4;
    }
    @Override
    public boolean equals(Object other) {
        return toString().equals(other.toString());
    }
    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public int compareTo(Object other) {
        return toString().compareTo(other.toString());
    }
}
