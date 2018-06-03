import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Runner {

 public static void main(String[] args) throws Exception {
  File paramFile = new File(args[0]);
  Map<String, String> params = Helpers.getParams(paramFile);
  File outputFile = new File(params.get("outputFile"));
  File queryFile = new File(params.get("queryFile"));
  File docsFile = new File(params.get("docsFile"));
  String searchAlgo = params.get("retrievalAlgorithm");
  Map<Integer, String> documentsMap = Helpers.basicParser(docsFile, "*TEXT");
  Map<Integer, String> queriesMap = Helpers.basicParser(queryFile, "*FIND");

  IndexDocs index = new IndexDocs(documentsMap);

  Map<Integer, List> results;

  for (int t = 1; t <= 20; t++) {
   for (BasicSimilarity.Idf idf : BasicSimilarity.Idf.values()) {
    for (BasicSimilarity.Tf tf : BasicSimilarity.Tf.values()) {
     Retriever masterRetriever = new Retriever(index.getIndex(), index.getAnalyzer(), t);

     results = masterRetriever.retrieve(queriesMap, tf, idf);
     Outputter.output(outputFile, results);

     // TODO: Calculate F score and max it!!
    }
   }
  }

  System.out.println("Fin");

 }
}

