import org.apache.lucene.classification.utils.DatasetSplitter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Runner {


 public static void main(String[] args) throws Exception {
  File paramFile = new File(args[0]);
  Map<String, String> params = Helpers.getParams(paramFile);
  File outputFile = new File(params.get("outputFile"));
  File trainFile = new File(params.get("trainFile"));
  File testFile = new File(params.get("testFile"));


  int kNeighbors = Integer.valueOf(params.get("k"));

  String[] csvHeaders = {"docId", "class", "title", "content"};
  TreeMap<Integer, Map<String,String>> trainMap = new TreeMap<Integer, Map<String,String>>(Helpers.csvParser(trainFile,csvHeaders));

  HashMap<Integer, Map<String, String>> docsMap = new HashMap<Integer, Map<String, String>>(trainMap);
  Map<Integer, Map<String,String>> validMap = new TreeMap<>();
  Map<Integer, Map<String,String>> testMap = Helpers.csvParser(testFile,csvHeaders);

  Random random = new Random();
  List<Integer> keys = new ArrayList<Integer>(docsMap.keySet());
  for(int i =0;i<docsMap.size()/100;i++) {
   Integer randomKey = keys.get(random.nextInt(keys.size()));
   keys.remove(randomKey);
   Map<String, String> value = docsMap.get(randomKey);
   validMap.put(randomKey, value);
   trainMap.remove(randomKey, value);
  }




  IndexDocs index = new IndexDocs(trainMap);
  IndexDocs validDocs = new IndexDocs(validMap);

  Map<Integer, List<Integer>> results;

  for (int i = 0; i <= 20; i++) {

   // TODO: Confusion Matrix @Julian
   Retriever masterRetriever = new Retriever(index.getIndex(), index.getAnalyzer(), BasicSimilarity.Tf.LOG_NORMALIZATION, BasicSimilarity.Idf.PROBABILISTIC_IDF, i);
   System.out.println("Test for k = " + i);
   double error = masterRetriever.checkK(validDocs.getIndex());
   System.out.println("Valid Error = " + error);

  }

 }
}

