import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by greg on 2/2/17.
 */
public class RunAnalogyExperiment {

    public final static String DELIMITER = ";";

    public final static String DEFAULT_PROPERTIES_PATH = "default.properties";

    private static String header;

    public static void main(String[] args) throws IOException {

        // Allow use of custom properties file as a single command-line argument
        Reader reader;
        if(args.length > 0) {
            reader = new FileReader(args[0]);
        } else {
            reader = new FileReader(DEFAULT_PROPERTIES_PATH);
        }

        Properties props = new Properties();
        props.load(reader);

        String analogiesFilesDelimited = props.getProperty("analogiesPaths");
        String[] analogiesFiles = analogiesFilesDelimited.split(DELIMITER);
        boolean caseSensitive = Boolean.parseBoolean(props.getProperty("caseSensitive"));
        String embeddingsFile = props.getProperty("vectorsPath");
        boolean calculateBaselineRank = Boolean.parseBoolean(props.getProperty("calculateBaselineRank"));
        boolean calculateDomainSimilarityRank = Boolean.parseBoolean(props.getProperty("calculateDomainSimilarityRank"));
        boolean calculateAddRank = Boolean.parseBoolean(props.getProperty("calculateAddRank"));
        boolean calculateMulRank = Boolean.parseBoolean(props.getProperty("calculateMulRank"));
        boolean calculateDiffRank = Boolean.parseBoolean(props.getProperty("calculateDiffRank"));

        // should be null if not using GloVe!
        String vocabFilePath = props.getProperty("vocabFile");

        Integer cutoff = null;
        try {
            cutoff = Integer.parseInt(props.getProperty("vectorCutoff"));
        } catch(NumberFormatException e) {}

        AnalogyExperiment.Builder builder = new AnalogyExperiment.Builder()
                .analogiesFiles(analogiesFiles)
                .embeddingsFile(embeddingsFile)
                .caseSensitive(caseSensitive)
                .calculateBaselineRank(calculateBaselineRank)
                .calculateDomainSimilarityRank(calculateDomainSimilarityRank)
                .calculateAddRank(calculateAddRank)
                .calculateMulRank(calculateMulRank)
                .calculateDiffRank(calculateDiffRank);
        if(cutoff != null)
            builder = builder.filterOn(cutoff);
        if(vocabFilePath != null)
            builder = builder.useGlove(vocabFilePath);

        AnalogyExperiment exp = builder.createExperiment();

        header = "analogy";
        List<List> parametersTested = new ArrayList<>();
        header += ",cos";
        parametersTested.add(exp.getAdditiveSimilarity());
        header += ",w3w4";
        parametersTested.add(exp.getBaselineSimilarity());
        header += ",w2w4";
        parametersTested.add(exp.getDomainSimilarity());
        header += ",diffsim";
        parametersTested.add(exp.getDifferenceSimilarity());

        if (calculateAddRank) {
            header += ",addrank";
            parametersTested.add(exp.getAddRanks());
        }
        if (calculateBaselineRank) {
            header += ",baserank";
            parametersTested.add(exp.getBaselineRanks());
        }
        if (calculateDomainSimilarityRank) {
            header += ",domainsimrank";
            parametersTested.add(exp.getDomainsimRanks());
        }
        if (calculateMulRank) {
            header += ",mulrank";
            parametersTested.add(exp.getMulRanks());
        }
        if (calculateDiffRank) {
            header += ",diffrank";
            parametersTested.add(exp.getDiffRanks());
        }

        try {
            assert (parametersTested.size() == header.split(",").length);
        } catch (AssertionError e) {
            System.out.println("Problem: parameters tested not equal to header size; this is a bug");
            throw new RuntimeException();
        }

        // Write out performance stats and predictors to CSV (can use in R later)

        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date date = new Date();
        String datetime = dateFormat.format(date);
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("analogy_experiment_stats_" + datetime + ".csv")));
        writer.write(header + ",category\n");
        for (int i=0; i<exp.getAnalogies().size(); i++) {
            Analogy analogy = exp.getAnalogies().get(i);
            writer.write(analogy.toString());
            for(List list : parametersTested) {
                writer.write("," + list.get(i));
            }
            writer.write("," + exp.getCategories().get(i));
            writer.write("\n");
        }
        writer.flush();
        writer.close();
    }


}
