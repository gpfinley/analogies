import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads a vectors file as output by GloVe
 * Created by gpfinley on 2/9/16.
 */
public class GloVeReader {

    // Whether or not to add in the context vector as given in the bin file
    public static boolean addContextVector = true;

    /**
     * Reads vectors from a GloVe bin file
     * MAKES ASSUMPTIONS:   bias term appears after vector
     *                      W and W~ are both written to file
     * @param binFile
     * @param vocabFile
     * @return
     * @throws IOException
     */
    public static Embeddings readBinFile(String binFile, String vocabFile) throws IOException {

        System.out.println("Reading GloVe vectors from file " + binFile + "...");

        BufferedReader vocabReader = new BufferedReader(new FileReader(vocabFile));
        List<String> vocab = new ArrayList<>();
        List<Integer> frequencies = new ArrayList<>();
        String line;
        while((line = vocabReader.readLine()) != null) {
            String[] fields = line.split(" ");
            vocab.add(fields[0]);
            frequencies.add(Integer.parseInt(fields[1]));
        }
        int nWords = vocab.size();

        // determine dimensionality by looking at input file: binfilesize_in_bytes = 8 * (dimensionality+1)*2
        //      bias term is written after each vector (the +1), and two sets of vectors are written (the *2)
        long nBytes = Paths.get(binFile).toFile().length();
        int dim = (int) (nBytes / 8 / 2 / nWords - 1);

        Embeddings embeddings = new Embeddings(dim);
        InputStream binReader = new FileInputStream(binFile);
        for(int i=0; i<nWords*2; i++) {
            byte[] bytes = new byte[dim*8+8];
            binReader.read(bytes);
            float[] vector = new float[dim];
            for(int j=0; j<dim; j++) {
                vector[j] = (float) ByteBuffer.wrap(bytes, j * 8, 8).order(ByteOrder.LITTLE_ENDIAN).getDouble();
            }
            if(i < nWords) {
                Phrase phrase = new Phrase(vocab.get(i), "_");      // assume underscore delimiters if phrases are present
                embeddings.addWordAndEmbedding(phrase, new WordEmbedding(vector));
                embeddings.setWordFrequency(phrase, frequencies.get(i));
            } else {
                Phrase phrase = new Phrase(vocab.get(i-nWords), "_");
                embeddings.get(phrase).add(new WordEmbedding(vector));
            }
        }
        embeddings.normalizeAll();
        System.out.println("read " + nWords + " embeddings with " + dim + " dimensions");
        return embeddings;
    }
}
