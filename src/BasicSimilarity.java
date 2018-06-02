import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class BasicSimilarity extends SimilarityBase {

    // TODO: verify this formula
    @Override
    protected float score(BasicStats basicStats, float freq, float docLength) {
        float tf = (float) Math.log(1 + freq);
        float idf = (float) Math.log(1 + (basicStats.getNumberOfDocuments()
                / (basicStats.getDocFreq())));
        return tf * idf;
    }

    @Override
    public String toString() {
        return null;
    }
}
