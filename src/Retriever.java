import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.classification.ClassificationResult;
import org.apache.lucene.classification.KNearestNeighborClassifier;
import org.apache.lucene.classification.document.KNearestNeighborDocumentClassifier;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.util.BytesRef;

import java.io.IOException;
import java.util.*;

public class Retriever {

    private double SCORE_THRESHOLD = 8.7d;

    private Analyzer analyzer;
    private Directory index;
    private double TITLE_ALPHA = 0.2;
    private double CONTENT_ALPHA = 1 - TITLE_ALPHA;

    IndexReader reader;
    IndexSearcher searcher;
    KNearestNeighborClassifier classifier;

    public Retriever(Directory index, Analyzer analyzer, BasicSimilarity.Tf tf, BasicSimilarity.Idf idf, int kNeighbors) throws IOException {
        this.index = index;
        this.analyzer = analyzer;
        this.reader = DirectoryReader.open(index);
        this.searcher = new IndexSearcher(reader);

        Similarity sim = new BasicSimilarity(tf, idf);

        searcher.setSimilarity(sim);

        Map<String, Analyzer> field2analyzer = new HashMap<>();
        field2analyzer.put("content", analyzer);
        field2analyzer.put("title", analyzer);
        field2analyzer.put("docId", analyzer);
        field2analyzer.put("class", analyzer);

        this.classifier = new KNearestNeighborClassifier(reader, sim, analyzer, null, kNeighbors, 0, 0, "class", "content", "title"); // KNearestNeighborDocumentClassifier(reader, sim, null, kNeighbors, 0, 0, "class", field2analyzer, "content", "title");
    }

    public Map<Integer, List<Integer>> retrieve(Map<Integer, String> queries) throws ParseException, IOException {


        Map<Integer, List<Integer>> results = new TreeMap<>();
        for (Map.Entry<Integer, String> queryEntry : queries.entrySet()) {

            List<Integer> docIds = new LinkedList<>();
            Query q = new QueryParser(Helpers.TEXT_FIELD, analyzer).parse(queryEntry.getValue()); // this is already removing stopwords
            TopDocs docs = searcher.search(q, 20);
            ScoreDoc[] hits = docs.scoreDocs;
            for (ScoreDoc hit : hits) {
                int docId = hit.doc;
                Document d = searcher.doc(docId);
                Float score = hit.score;
                if (score < SCORE_THRESHOLD) {
                    break;
                }
                String actualDocId = d.get(Helpers.DOC_ID);
                docIds.add(Integer.valueOf(actualDocId));
            }
            results.put(queryEntry.getKey(), docIds);
        }
        return results;
    }
    public KNearestNeighborClassifier getClassifier()
    {
        return this.classifier;
    }
    public String classify(Document doc) throws Exception
    {
      String content  = doc.get("content");
      String title = doc.get("title");
      Map<String, Double> contentClasses = this.classifyResult2FlatList(this.classifier.getClasses(content));
      Map<String, Double> titleClasses = this.classifyResult2FlatList(this.classifier.getClasses(title));
      return this.getMaxJoined(contentClasses, titleClasses);
    }

    public Map<Integer, Map<String,String>> classify(Directory docs) throws Exception
    {

        IndexReader docReader = DirectoryReader.open(docs);
        System.out.print("About to classify #");System.out.print(docReader.numDocs());System.out.println(" documents");
        TreeMap<Integer, Map<String,String>> result  = new TreeMap<>();
        for (int i = 0; i < docReader.numDocs(); i++) {
            Document doc = docReader.document(i);
            Map<String,String> docMap = new TreeMap<>();

            int Id = Integer.valueOf(doc.get("docId"));
            docMap.put("actual", doc.get("class"));
            docMap.put("predicted",this.classify(doc));
            result.put(Id, docMap);
        }
        return result;
    }

    private Map<String, Double> classifyResult2FlatList(List<ClassificationResult<BytesRef>> list)
    {
        Map<String, Double> resultList = new TreeMap<>();
        for(ClassificationResult<BytesRef> result : list)
        {
            resultList.put(result.getAssignedClass().utf8ToString(), result.getScore());
        }
        return resultList;
    }

    private String getMaxJoined( Map<String, Double> contentClasses,  Map<String, Double> titleClasses)
    {
        Map<String, Double> merged = new TreeMap<>();

        for(Map.Entry<String, Double> doc  : contentClasses.entrySet())
        {
            merged.put(doc.getKey(), CONTENT_ALPHA * doc.getValue());
        }

        for(Map.Entry<String, Double> doc : titleClasses.entrySet())
        {
            double val = TITLE_ALPHA * doc.getValue();
            val += merged.getOrDefault(doc.getKey(), 0.0);
            merged.put(doc.getKey(), val);
        }

        String maxClass = "NONE";
        double maxScore = 0;

        for(Map.Entry<String, Double> doc : merged.entrySet())
        {
            if(doc.getValue() > maxScore)
            {
                maxClass = doc.getKey();
                maxScore = doc.getValue();
            }
        }
        return maxClass;
    }
}
