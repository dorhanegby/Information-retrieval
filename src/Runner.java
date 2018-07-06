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
        System.out.println("Parsing...");
        TreeMap<Integer, Map<String, String>> trainMap = new TreeMap<Integer, Map<String, String>>(Helpers.csvParser(trainFile, csvHeaders));
        Map<Integer, Map<String, String>> testMap = Helpers.csvParser(testFile, csvHeaders);
        System.out.println("Done Parsing");

        System.out.println("Indexing...");
        IndexDocs index = new IndexDocs(trainMap);
        IndexDocs testDocs = new IndexDocs(testMap);
        System.out.println("Done Indexing");

        Retriever masterRetriever = new Retriever(index.getIndex(), index.getAnalyzer(), BasicSimilarity.Tf.LOG_NORMALIZATION, BasicSimilarity.Idf.PROBABILISTIC_IDF, kNeighbors);

        Evaluator eva = new Evaluator(testDocs.getIndex(), masterRetriever.getClassifier());
        double fScoreContent = eva.matrixContent.getF1Measure();
        double fScoreTitle = eva.matrixTitle.getF1Measure();

        double precisionContent = eva.matrixContent.getPrecision();
        double precisionTitle = eva.matrixTitle.getPrecision();

        double recallContent = eva.matrixContent.getRecall();
        double recallTitle = eva.matrixTitle.getRecall();

        System.out.println("f-score: " + 0.8 * fScoreContent + 0.2 * fScoreTitle);
        System.out.println("precision: " + 0.8 * precisionContent + 0.2 * precisionTitle);
        System.out.println("recallContent: " + 0.8 * recallContent + 0.2 * recallTitle);


        Map<Integer, Map<String,String>> validResults = masterRetriever.classify(testDocs.getIndex());
        System.out.println("Starting to output");
        Outputter out = new Outputter();
        out.outputKnn(outputFile,validResults);
        System.out.println("Finished to output");

        // THIS IS FOR MAXIMIZING HYPER-PARAMETERS
// IndexDocs validDocs = new IndexDocs(validMap);
// Map<Integer, Map<String, String>> validMap = new TreeMap<>();
//        Random random = new Random();
//        List<Integer> keys = new ArrayList<Integer>(docsMap.keySet());
//        System.out.println("Creating validation data set...");
//        for (int i = 0; i < docsMap.size() / 100; i++) {
//            Integer randomKey = keys.get(random.nextInt(keys.size()));
//            keys.remove(randomKey);
//            Map<String, String> value = docsMap.get(randomKey);
//            validMap.put(randomKey, value);
//            trainMap.remove(randomKey, value);
//        }
//
//        System.out.println("Done creating validation data set");



        // THIS IS FOR MAXIMIZING HYPER-PARAMETERS
//        for(int i=1;i<=20;i++) {
//            Retriever masterRetriever = new Retriever(index.getIndex(), validDocs.getAnalyzer(), BasicSimilarity.Tf.LOG_NORMALIZATION, BasicSimilarity.Idf.PROBABILISTIC_IDF, i);
//            Evaluator eva = new Evaluator(validDocs.getIndex(), masterRetriever.getClassifier());
//            double fScoreContent = eva.matrixContent.getF1Measure();
//            double fScoreTitle = eva.matrixTitle.getF1Measure();
//
//            double precisionContent = eva.matrixContent.getPrecision();
//            double precisionTitle = eva.matrixTitle.getPrecision();
//
//            double recallContent = eva.matrixContent.getRecall();
//            double recallTitle = eva.matrixTitle.getRecall();
//
//            System.out.println("k: " + i);
//            System.out.println("fScoreContent: " + fScoreContent);
//            System.out.println("fScoreTitle: " + fScoreTitle);
//            System.out.println("precisionContent: " + precisionContent);
//            System.out.println("precisionTitle: " + precisionTitle);
//            System.out.println("recallContent: " + recallContent);
//            System.out.println("recallTitle: " + recallTitle);
//            System.out.println("========================================");
//        }

    }
}

