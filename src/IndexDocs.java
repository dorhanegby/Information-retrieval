import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IndexDocs {

    public IndexDocs(Map<String,String> documentsMap) throws Exception {
        CharArraySet stopList = Helpers.getStopList();

        StandardAnalyzer analyzer = new StandardAnalyzer(stopList); // TODO: add method getAnalyzer
        Directory index = new RAMDirectory(); // TODO: add method getDirectory

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        for (Map.Entry<String, String> doc : documentsMap.entrySet()) {
            addDoc(w, doc.getValue(), doc.getKey());
        }

        w.close();

        String querystr = "p KENNEDY ADMINISTRATION PRESSURE ON NGO DINH DIEM TO STOP\n" +
                "\n" +
                "SUPPRESSING THE BUDDHISTS .";
        Query q = new QueryParser(Helpers.TEXT_FIELD, analyzer).parse(querystr);

        int hitsPerPage = 18;
        IndexReader reader = DirectoryReader.open(index);
        IndexSearcher searcher = new IndexSearcher(reader);
        TopDocs docs = searcher.search(q, hitsPerPage);
        ScoreDoc[] hits = docs.scoreDocs;

        System.out.println("Found " + hits.length + " hits.");
        for(int i=0;i<hits.length;++i) {
            int docId = hits[i].doc;
            Document d = searcher.doc(docId);
            System.out.println((i + 1) + ". " + d.get(Helpers.DOC_ID) + " score: " + hits[i].score);
        }


    }

    private static void addDoc(IndexWriter w, String value, String docID) throws Exception {
        Document doc = new Document();
        doc.add(new TextField(Helpers.TEXT_FIELD, value, Field.Store.YES));
        doc.add(new StringField(Helpers.DOC_ID, docID, Field.Store.YES));
        w.addDocument(doc);
    }
}
