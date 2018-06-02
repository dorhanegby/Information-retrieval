import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class BasicSimilarity extends SimilarityBase {

    // TODO: verify this formula
    @Override
    protected float score(BasicStats basicStats, float freq, float v1) {
        float tf = (float) (1 + Math.log(freq));
        float idf = (float) (1 + Math.log(basicStats.getNumberOfDocuments()
                / (basicStats.getDocFreq() + 1)));
        return tf * idf;
    }

    @Override
    public String toString() {
        return null;
    }
}
