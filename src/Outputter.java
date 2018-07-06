import java.io.*;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Outputter {
    public static void output(File outputFile, Map<Integer, List<Integer>> results) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (Map.Entry<Integer, List<Integer>> pair : results.entrySet()) {
            //iterate over the pairs
            List docIds = pair.getValue();
            docIds.sort(Comparator.naturalOrder());
            StringBuilder sb = new StringBuilder();
            for (Object id : docIds) {
                sb.append(String.valueOf(id));
                sb.append(" ");
            }
            bw.write(pair.getKey() + " " + sb.toString());
            bw.newLine();

        }
        bw.close();
    }

    public static void outputKnn(File outputFile, Map<Integer, Map<String, String>> results) throws IOException {
        FileOutputStream fos = new FileOutputStream(outputFile);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));

        for(Map.Entry<Integer, Map<String, String>> doc : results.entrySet()) {

            Map<String, String> docMap = doc.getValue();

            String docId =  docMap.get("docId"); // doc.getKey().toString()
            String predicted =  docMap.get("predicted");
            String actual =  docMap.get("actual");

            bw.write(docId + "," + predicted + "," + actual);
            bw.newLine();
        }

        bw.close();

    }
}
