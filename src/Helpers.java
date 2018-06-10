import org.apache.lucene.analysis.CharArraySet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class Helpers {

    public static final String DOC_ID = "docID";
    public static final String TEXT_FIELD = "content";

    public static CharArraySet getStopList() {
        // TODO: Tweak stop words to maximize recall
        String[] listOfWords = {"an", "are", "be", "if", "into", "it", "no", "not", "or", "such", "their", "then", "there", "these",
                "they", "this", "will", "the", "of", "a", "to", "in", "and", "for", "that", "with", "was", "on", "last", "by",
                "but", "as", "at", "his", "from", "week", "is"};

        List<String> list = new ArrayList<String>(Arrays.asList(listOfWords));
        return new CharArraySet(list, true);
    }

    public static Map<String, String> getParams(File paramFile) throws IOException {
        Map<String, String> params = new TreeMap<>();
        List<String> lines = Files.readAllLines(paramFile.toPath());
        for (String line : lines) {
            String[] opts = line.split("=");
            if (opts.length < 2) {
                throw new Error("Not matching format");
            }
            params.put(opts[0], opts[1]);
        }
        return params;
    }

    public static Map<Integer, String> basicParser(File toParse, String separatorText) throws IOException {
        Map<Integer, String> docs = new TreeMap<>();
        List<String> lines = Files.readAllLines(toParse.toPath());
        Integer currentId = 0;
        StringBuilder stringBuilder = new StringBuilder();
        for (String line : lines) {
            if (line.startsWith(separatorText)) {
                addDocument(stringBuilder, currentId, docs);
                stringBuilder.setLength(0); // Clearing the string builder
                String[] headers = cleanSpaces(line).split(" "); // Gets the headers of the document
                currentId = Integer.valueOf(headers[1]); // Getting the doc id, second text after the separator (heuristic) not taking date and page
            } else {
                stringBuilder.append(line.trim());
            }
        }
        addDocument(stringBuilder, currentId, docs); // When we finish reading, we need to add the last text to the last document
        return docs;
    }

    public static Map<Integer, List<Integer>> truthParser(File toParse) throws IOException {
        Map<Integer, List<Integer>> truth = new TreeMap<>();
        List<String> lines = Files.readAllLines(toParse.toPath());
        for (String line : lines) {
            String[] values = cleanSpaces(line).split(" ");
            if (values.length > 1) {
                Integer key = Integer.valueOf(values[0]); // First number is the query number
                List<Integer> trueIds = new LinkedList<>();
                for (int i = 1; i < values.length; i++) {
                    trueIds.add(Integer.valueOf(values[i]));
                }
                truth.put(key, trueIds);
            }
        }
        return truth;
    }

    public static void addDocument(StringBuilder sb, Integer docId, Map<Integer, String> docs) {
        if (sb.length() != 0) {
            docs.put(docId, sb.toString()); // Adding the new document
        }
    }

    public static String cleanSpaces(String str) {
        return str.trim().replaceAll("\\s+", " ");
    }

}
