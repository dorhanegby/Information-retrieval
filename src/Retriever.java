import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

    private static final int SCORE_THRESHOLD = 10;

    private StandardAnalyzer analyzer;
    private Directory index ;

    IndexReader reader;
    IndexSearcher searcher;

    public Retriever(Directory index, StandardAnalyzer analyzer) throws IOException {
        this.index = index;
        this.analyzer = analyzer;
        this.reader = DirectoryReader.open(index);
        this.searcher = new IndexSearcher(reader);

    }
    public Map<Integer, List> retrieve(Map<Integer,String> queries) throws ParseException, IOException {
        return this.retrieve(queries,BasicSimilarity.Tf.LOG_NORMALIZATION, BasicSimilarity.Idf.IDF);
    }

    public Map<Integer, List> retrieve(Map<Integer,String> queries, BasicSimilarity.Tf tf, BasicSimilarity.Idf idf) throws ParseException, IOException {
        Similarity sim = new BasicSimilarity(tf, idf);

        searcher.setSimilarity(sim);

        Map<Integer, List> results = new TreeMap<>();
        for (Map.Entry<Integer, String> queryEntry : queries.entrySet()) {

            List<Integer> docIds = new LinkedList<>();
            Query q = new QueryParser(Helpers.TEXT_FIELD, analyzer).parse(queryEntry.getValue()); // this is already removing stopwords
            TopDocs docs = searcher.search(q, 20);
            ScoreDoc[] hits = docs.scoreDocs;
            System.out.println("Query n: "+ queryEntry.getKey()); // TODO: DELETE THIS
            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                Float score = hits[i].score;
                if(score < SCORE_THRESHOLD) {
                    break;
                }

                String actualDocId = d.get(Helpers.DOC_ID);

                docIds.add(Integer.valueOf(actualDocId));

                System.out.println(actualDocId+" | score: " + score); // TODO: DELETE THIS
            }

            results.put(queryEntry.getKey(), docIds);
            System.out.println("++++++++++++++++++++++++++++++++++++"); // TODO: DELETE THIS

        }
        return results;
    }
}
