import org.apache.lucene.classification.Classifier;
import org.apache.lucene.classification.utils.ConfusionMatrixGenerator;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.store.Directory;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Evaluator {
    final static double ALPHA = 0.5d;

    Map<Integer, List<Integer>> truth;
    Map<Integer, List<Integer>> results;
    IndexReader reader;
    ConfusionMatrixGenerator.ConfusionMatrix matrix;

    public Evaluator(Map<Integer, List<Integer>> truth, Map<Integer, List<Integer>> results) {
        this.truth = truth;
        this.results = results;
    }
    public Evaluator(Directory docsToEvaluate, Classifier classifier) throws Exception
    {
        this.reader = DirectoryReader.open(docsToEvaluate);
        System.out.println("Creating Confusion Matrix...");
        this.matrix = ConfusionMatrixGenerator.getConfusionMatrix(reader, classifier,"class", "content" , 300 *1000);
        System.out.println("Done Creating Confusion Matrix");
    }

    /**
     *
     * TODO: a function that returns Map<Integer, Map<String, String>> results
     * it should contain { docId: "", predicted: "", actual: "" }
     * you can have the keys being the docIds, should be a tree map sorted by docIds
     * @return
     */

    public TreeMap<Integer, Map<String, String>> getResults() {
        return null;
    }

    public double[] calcRPF() {
        double totalRecall = 0;
        double totalPrecision = 0;
        double totalFScore = 0;
        for (Integer resultKey : results.keySet()) {
            double currentRecall = this.calcRecallQuery(resultKey);
            double currentPrecision = this.calcPrecisionQuery(resultKey);
            double currentFScore = this.calcFScore(currentRecall, currentPrecision);

            totalRecall += currentRecall;
            totalPrecision += currentPrecision;
            totalFScore += currentFScore;
        }
        totalRecall = (double) totalRecall / results.size();
        totalPrecision = (double) totalPrecision / results.size();
        totalFScore = (double) totalFScore / results.size();
        return new double[]{totalRecall, totalPrecision, totalFScore};
    }

    public double calcRecall(List<Integer> trueIds, List<Integer> retrievedIds) {
        List<Integer> relevantRetrieved = findIntersection(trueIds, retrievedIds);
        int totalRelevantItems = trueIds.size();
        int totalRelevantRetrieved = relevantRetrieved.size();
        return cleanDivide(totalRelevantRetrieved, totalRelevantItems);
    }

    public double calcPrecision(List<Integer> trueIds, List<Integer> retrievedIds) {
        List<Integer> relevantRetrieved = findIntersection(trueIds, retrievedIds);
        int totalRetrieved = retrievedIds.size();
        int totalRelevantRetrieved = relevantRetrieved.size();
        return cleanDivide(totalRelevantRetrieved, totalRetrieved);
    }

    public double calcFScore(double recall, double precision) {
        double precisionAlpha = ALPHA * cleanDivide(1, precision);
        double recallAlpha = (1 - ALPHA) * cleanDivide(1, recall);
        return cleanDivide(1, precisionAlpha + recallAlpha);
    }

    public double calcRecallQuery(int q) {
        return this.calcRecall(truth.get(q), results.get(q));
    }

    public double calcPrecisionQuery(int q) {
        return this.calcPrecision(truth.get(q), results.get(q));
    }


    public static List<Integer> findIntersection(List<Integer> trueIds, List<Integer> retrievedIds) {
        List<Integer> relevantRetrieved = new LinkedList<>(); // Making a copy of the truth to do intersection
        relevantRetrieved.addAll(trueIds);
        relevantRetrieved.retainAll(retrievedIds);
        return relevantRetrieved;
    }

    public static double cleanDivide(double a, double b) {
        if (a == 0) {
            return 0;
        }
        if (b == 0) {
            return 0;
        }
        return (double) a / b;
    }
}
