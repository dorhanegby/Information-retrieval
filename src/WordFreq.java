import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.misc.HighFreqTerms;
import org.apache.lucene.misc.TermStats;
import org.apache.lucene.store.Directory;

public class WordFreq {

    TermStats[] termStats;

    public TermStats[] getTermStats() {
        return termStats;
    }

    public WordFreq(Directory index) throws Exception {
        IndexReader indexReader = DirectoryReader.open(index);
        HighFreqTerms highFreqTerms = new HighFreqTerms();
        this.termStats = calcHighFreqTerms(highFreqTerms, indexReader);
    }

    private static TermStats[] calcHighFreqTerms(HighFreqTerms highFreqTerms, IndexReader indexReader) throws Exception {
        return highFreqTerms.getHighFreqTerms(indexReader, 20, Helpers.TEXT_FIELD, new HighFreqTerms.DocFreqComparator());
    }
}
