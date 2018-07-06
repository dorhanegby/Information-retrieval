import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.classification.document.KNearestNeighborDocumentClassifier;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.*;

public class Retriever {

    private double SCORE_THRESHOLD = 8.7d;

    private Analyzer analyzer;
    private Directory index;

    IndexReader reader;
    IndexSearcher searcher;
    KNearestNeighborDocumentClassifier classifier;

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

        this.classifier = new KNearestNeighborDocumentClassifier(reader, sim, null, kNeighbors, 0, 0, "class", field2analyzer, "content", "title");
    }

    public double checkK(Directory docs) throws IOException {
        IndexReader docReader = DirectoryReader.open(docs);
        int mistakes = 0;
        for (int i = 0; i < docReader.numDocs(); i++) {
            Document doc = docReader.document(i);
            String predicted = this.classifier.assignClass(doc).getAssignedClass().utf8ToString();
            String actual = doc.get("class");
            if(!predicted.equals(actual)) {
                mistakes++;
            }
        }
        return (double) mistakes / docReader.numDocs();
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
    public KNearestNeighborDocumentClassifier getClassifier()
    {
        return this.classifier;
    }
    public int classify(Document doc) throws Exception
    {
       return Integer.valueOf(this.classifier.assignClass(doc).getAssignedClass().utf8ToString());
    }

}
