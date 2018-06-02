import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helpers {
    public static Map<String,String> getParams(File paramFile) throws IOException {
        Map<String, String> params = new HashMap<>();
        List<String> lines = Files.readAllLines(paramFile.toPath());
        for (String line : lines) {
            String[] opts = line.split("=");
            if(opts.length<2)
            {
                throw new Error("Not matching format");
            }
            params.put(opts[0], opts[1]);
        }
        return params;
    }

    public static Map<Integer,String> basicParser(File toParse, String separatorText) throws IOException {
        Map<Integer, String> docs = new HashMap<>();
        List<String> lines = Files.readAllLines(toParse.toPath());
        Integer currentId = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            if(line.startsWith(separatorText)) {
                addDocument(stringBuilder,currentId,docs);
                stringBuilder.setLength(0); // Clearing the string builder
                String[] headers = cleanSpaces(line).split(" "); // Gets the headers of the document
                currentId = Integer.valueOf(headers[1]); // Getting the doc id, second text after the separator (heuristic)
            } else{
                stringBuilder.append(line.trim());
            }
        }
        addDocument(stringBuilder,currentId,docs); // When we finish reading, we need to add the last text to the last document
        return docs;
    }

    public static void addDocument(StringBuilder sb, Integer docId, Map<Integer,String> docs)
    {
        if(sb.length()!= 0)
        {
            docs.put(docId, sb.toString()); // Adding the new document
        }
    }

    public static String cleanSpaces(String str)
    {
        return str.trim().replaceAll("\\s+", " ");
    }

}
