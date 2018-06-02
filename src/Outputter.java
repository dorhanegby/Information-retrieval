import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Outputter {
    public static void output(File outputFile, Map<Integer,List> results) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (Map.Entry<Integer, List> pair : results.entrySet()){
            //iterate over the pairs
            List docIds = pair.getValue();
            docIds.sort(Comparator.naturalOrder());
            StringBuilder sb = new StringBuilder();
            for(Object id : docIds)
            {
                sb.append(String.valueOf(id));
                sb.append(" ");
            }
            bw.write(pair.getKey() + " " + sb.toString());
            bw.newLine();

        }
        bw.close();
    }
}
