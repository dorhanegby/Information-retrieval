import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.util.Map;

public class IndexDocs {
    private Analyzer analyzer;
    private Directory index;

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public Directory getIndex() {
        return index;
    }


    public IndexDocs(Map<Integer, String> documentsMap) throws Exception {
        CharArraySet stopList = Helpers.getStopList();

        this.analyzer = new StandardAnalyzer(stopList); // PorterAnalyzer(stopList);
        this.index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        for (Map.Entry<Integer, String> doc : documentsMap.entrySet()) {
            addDoc(w, doc.getValue(), doc.getKey().toString());
        }
        w.close();
    }

    private static void addDoc(IndexWriter w, String value, String docID) throws Exception {
        Document doc = new Document();
        doc.add(new TextField(Helpers.TEXT_FIELD, value, Field.Store.YES));
        doc.add(new StringField(Helpers.DOC_ID, docID, Field.Store.YES));
        w.addDocument(doc);
    }
}
