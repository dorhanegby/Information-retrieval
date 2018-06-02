import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Retriever {
    private StandardAnalyzer analyzer;
    private Directory index ;
    public Retriever(Directory index, StandardAnalyzer analyzer) {
        this.index = index;
        this.analyzer = analyzer;
    }

    public Map<String, List> retrieve(Map<String,String> queries) throws ParseException, IOException {
        Map<String, List> results = new HashMap<>();
        for (Map.Entry<String, String> queryEntry : queries.entrySet()) {
            List<Integer> docIds = new LinkedList<>();

            Query q = new QueryParser(Helpers.TEXT_FIELD, analyzer).parse(queryEntry.getValue()); // this is already removing stopwords
            IndexReader reader = DirectoryReader.open(index);
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs docs = searcher.search(q, 10);
            ScoreDoc[] hits = docs.scoreDocs;

            for (int i = 0; i < hits.length; ++i) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                Float score = hits[i].score;
                docIds.add(Integer.valueOf(d.get(Helpers.DOC_ID)));
            }

            results.put(queryEntry.getKey(), docIds);
        }
        return results;
    }
}
