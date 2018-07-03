import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Runner {


 public static void main(String[] args) throws Exception {
  File paramFile = new File(args[0]);
  Map<String, String> params = Helpers.getParams(paramFile);
  File outputFile = new File(params.get("outputFile"));
  File trainFile = new File(params.get("trainFile"));
  File testFile = new File(params.get("testFile"));
  int kNeighbors = Integer.valueOf(params.get("k"));

  String[] csvHeaders = {"docId", "class", "title", "content"};
  Map<Integer, Map<String,String>> trainMap = Helpers.csvParser(trainFile,csvHeaders);
  Map<Integer, Map<String,String>> testMap = Helpers.csvParser(testFile,csvHeaders);

  IndexDocs index = new IndexDocs(trainMap);

     System.out.println("saian");
  Map<Integer, List<Integer>> results;

//  Retriever masterRetriever = new Retriever(index.getIndex(), index.getAnalyzer());
//
//  results = masterRetriever.retrieve(queriesMap, BasicSimilarity.Tf.LOG_NORMALIZATION, BasicSimilarity.Idf.PROBABILISTIC_IDF);
//  Outputter.output(outputFile, results);
//
//  Evaluator evaluator = new Evaluator(truthMap, results);
//  double[] evalResults = evaluator.calcRPF();
//  double fScore = evalResults[2];

//  System.out.println("fscore: " + fScore);

 }
}

