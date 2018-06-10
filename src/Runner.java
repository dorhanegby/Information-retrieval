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
  Map<Integer, List<Integer>> truthMap = Helpers.truthParser(new File("hw3_data/truth.txt"));

  IndexDocs index = new IndexDocs(documentsMap);

  Map<Integer, List<Integer>> results;

  Retriever masterRetriever = new Retriever(index.getIndex(), index.getAnalyzer());

  results = masterRetriever.retrieve(queriesMap, BasicSimilarity.Tf.LOG_NORMALIZATION, BasicSimilarity.Idf.PROBABILISTIC_IDF);
  Outputter.output(outputFile, results);

  Evaluator evaluator = new Evaluator(truthMap, results);
  double[] evalResults = evaluator.calcRPF();
  double fScore = evalResults[2];

  System.out.println("fscore: " + fScore);

 }
}

