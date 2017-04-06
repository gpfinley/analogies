import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Reads a vectors file as output by the word2vec C program into a WordVectorSpace
 * Created by xxxxxxxx on 2/9/16.
 */
public class Word2vecReader {

    private static Logger LOGGER = Logger.getLogger(Word2vecReader.class.getName());

    public static Embeddings readBinFile(String filename) throws IOException {
        return readBinFile(filename, 0);
    }

    public static Embeddings readBinFile(String filename, int maxWords) throws IOException {
        LOGGER.info("Reading vectors from word2vec binary file...");
        InputStream reader = new FileInputStream(filename);
        char c;
        String nWordsStr = "";
        while(true) {
            c = (char)reader.read();
            if(c==' ') break;
            nWordsStr += c;
        }
        String sizeStr = "";
        while(true) {
            c = (char)reader.read();
            if(c=='\n') break;
            sizeStr += c;
        }
        int nWords = Integer.parseInt(nWordsStr);
        if (maxWords > 0 && maxWords < nWords) {
            nWords = maxWords;
        }
        int size = Integer.parseInt(sizeStr);
        Embeddings wes = new Embeddings(size);
        char firstchar = '\n';
        byte[] bytes = new byte[size*4];
        for(int i=0; i<nWords; i++) {
            String word = "";
            if(firstchar != '\n')
                word += firstchar;
            while((c = (char)reader.read()) != ' ') {
                word += c;
            }
            // Read in all bytes associated with this vector
            reader.read(bytes, 0, size*4);
            float vector[] = new float[size];
            for(int j=0; j<size; j++) {
                vector[j] = ByteBuffer.wrap(bytes, j*4, 4).order(ByteOrder.LITTLE_ENDIAN).getFloat();
            }
            // For some files, there's an extra \n (such as those generated by the C word2vec)
            // For others, there's no newline--it goes straight to the next word (the GoogleNews vectors, e.g.)
            firstchar = (char) reader.read();
            Phrase newPhrase = new Phrase(word, "_+");
            if (newPhrase.length() > 0) {
                wes.addWordAndEmbedding(newPhrase, new WordEmbedding(vector));
            }
            else {
                LOGGER.info("Not including zero-length phrase in Embeddings (don't worry about it)");
            }
        }
        LOGGER.info("Read " + nWords + " word vectors with " + sizeStr + " dimensions");

        return wes;
    }

    /**
     * Read all words that have at least a certain number of appearances in the corpus
     * @param binFile the binary file of embeddings
     * @param vocabFile the vocabulary file with occurrence stats
     * @param minFreq the minimum number of occurrences of a word
     * @return an Embeddings object with only words of high enough frequency
     * @throws IOException
     */
    public static Embeddings readBinFile(String binFile, String vocabFile, int minFreq) throws IOException {
        BufferedReader vocabReader = new BufferedReader(new FileReader(vocabFile));
        String line;
        int howManyWords = 0;
        while((line = vocabReader.readLine()) != null) {
            String[] fields = line.split("\\s+");
            if(Integer.parseInt(fields[1]) < minFreq) {
                return readBinFile(binFile, howManyWords);
            }
            howManyWords++;
        }
        LOGGER.warning("No words less than specified minimum frequency; reading all words");
        return readBinFile(binFile, 0);
    }

    /**
     * Read in a word2vec vocab file to get frequency counts of all words and phrases.
     * @param vocabFile a file path
     * @return a map between phrases and their counts
     * @throws IOException
     */
    public static Map<Phrase, Integer> readVocabFile(String vocabFile, int nWords) throws IOException {
        Map<Phrase, Integer> counts = new HashMap<>();
        BufferedReader reader = new BufferedReader(new FileReader(vocabFile));
        String line;
        Pattern splitter = Pattern.compile("\\s+");
        while((line = reader.readLine()) != null) {
            String[] fields = splitter.split(line);
            counts.put(new Phrase(fields[0], "_+"), Integer.parseInt(fields[1]));
            if (counts.size() >= nWords && nWords > 0) break;
        }
        return counts;
    }
    public static Map<Phrase, Integer> readVocabFile(String vocabFile) throws IOException {
        return readVocabFile(vocabFile, 0);
    }


}
