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
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class IndexDocs {
    private StandardAnalyzer analyzer;
    private Directory index ;

    public StandardAnalyzer getAnalyzer() {
        return analyzer;
    }

    public Directory getIndex() {
        return index;
    }


    public IndexDocs(Map<String,String> documentsMap) throws Exception {
        CharArraySet stopList = Helpers.getStopList();

        this.analyzer = new StandardAnalyzer(stopList);
        this.index = new RAMDirectory();

        IndexWriterConfig config = new IndexWriterConfig(analyzer);

        IndexWriter w = new IndexWriter(index, config);
        for (Map.Entry<String, String> doc : documentsMap.entrySet()) {
            addDoc(w, doc.getValue(), doc.getKey());
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
