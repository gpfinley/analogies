import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Script class to convert a list of pairs into a set of analogies.
 * Will take all pairings of pairs in a category.
 *
 * Created by greg on 4/1/17.
 */
public class CreateAnalogiesFromPairs {

    public static void main(String[] args) throws IOException {

        if (args.length == 0) {
            System.out.println("Usage: names of all files with pairs in them");
            System.exit(-1);
        }

//        args = new String[]{"semantic_pairs.txt", "semantic_analogies.txt"};

//        String[] infiles = Arrays.copyOfRange(args, 0, args.length - 1);
//        BufferedWriter writer = new BufferedWriter(new FileWriter(outfile));
        Writer writer = new OutputStreamWriter(System.out);

        for (String infile : args) {
            BufferedReader reader = new BufferedReader(new FileReader(infile));
            String line;
            Pattern noLetters = Pattern.compile("^[^A-Za-z]*$");
            String category = Paths.get(infile).getFileName().toString();
            List<Pair> pairs = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || noLetters.matcher(line).matches()) continue;
                if (line.startsWith(":")) {
                    if (pairs.size() > 0) {
                        writer.write(getWriteString(category, pairs));
                        pairs = new ArrayList<>();
                    }
                    category = line;
                } else {
                    pairs.add(new Pair(line));
                }
            }
            writer.write(getWriteString(category, pairs));
            writer.flush();
        }
        writer.close();
    }

    private static String getWriteString(String category, List<Pair> pairs) {
        StringBuilder builder = new StringBuilder();
        if (!category.startsWith(":")) {
            category = ": " + category;
        }
        builder.append(category)
                .append("\n");
        for (Pair pair1 : pairs) {
            for (Pair pair2 : pairs) {
                if (!pair1.equals(pair2)) {
                    builder.append(pair1.w1)
                            .append(" ")
                            .append(pair1.w2)
                            .append(" ")
                            .append(pair2.w1)
                            .append(" ")
                            .append(pair2.w2)
                            .append("\n");
                }
            }
        }
        return builder.append("\n").toString();
    }

    private static class Pair {
        String w1;
        String w2;
        Pair(String line) {
            w1 = line.split("\\s+")[0].trim();
            w2 = line.split("\\s+")[1].trim().split("/")[0];
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Pair pair = (Pair) o;

            if (w1 != null ? !w1.equals(pair.w1) : pair.w1 != null) return false;
            return !(w2 != null ? !w2.equals(pair.w2) : pair.w2 != null);

        }

        @Override
        public int hashCode() {
            int result = w1 != null ? w1.hashCode() : 0;
            result = 31 * result + (w2 != null ? w2.hashCode() : 0);
            return result;
        }
    }
}
