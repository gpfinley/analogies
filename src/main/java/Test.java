/**
 * Created by xxxxxxxx on 4/2/17.
 */
public class Test {

    public static void main(String[] args) {
        int max = 97345;
        int nThreads = 40;


        int chunkSize = max/nThreads;
        int addAnExtra = (int) ((max/((double) nThreads) - chunkSize) * nThreads);
        if (addAnExtra > 0) chunkSize++;
        int lastEnd = 0;
        for(int i=0; i<nThreads; i++) {
            if (i == addAnExtra) {
                chunkSize--;
            }
            final int begin=lastEnd;
            final int end;
            if (i < nThreads - 1) {
                end = begin + chunkSize;
            } else {
                end = max;
            }
            lastEnd = end;
        }
    }
}
