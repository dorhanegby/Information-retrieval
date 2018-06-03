import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.SimilarityBase;

public class BasicSimilarity extends SimilarityBase {

    private Tf tf;
    private Idf idf;

    public BasicSimilarity(Tf tf, Idf idf) {
        super();
        this.tf = tf;
        this.idf = idf;
    }

    // TODO: verify this formula
    @Override
    protected float score(BasicStats basicStats, float ftd, float docLength) {
        long N = basicStats.getNumberOfDocuments(); // Total number of docs
        long nt = basicStats.getDocFreq(); // Total number of docs that contain the term.

        float tf = (float) Math.log(1 + ftd);
        float idf = (float) Math.log(1 + (N / nt));
        return tf * idf;
    }

    private float getTF(float ftd) {
        if(tf == Tf.BINARY) {
            return ftd > 0 ? 1.0f : 0.0f;
        }
        else if(tf == Tf.RAW_COUNT) {
            return ftd;
        }
        else if(tf == Tf.LOG_NORMALIZATION) {
            return (float) Math.log(1 + ftd);
        }

        return ftd;
    }

    private float getIDF(long N, long nt) {
        if (idf == Idf.UNARY) {
            return 1.0f;
        }
        else if(idf == Idf.IDF) {
            return (float) Math.log((double) N / nt);
        }
        else if(idf == Idf.IDF_SMOOTH) {
            return (float) Math.log(1 + (double) N / nt);
        }
        else if(idf == Idf.PROBABILISTIC_IDF) {
            return (float) Math.log((double) (N - nt) / nt);
        }

        return 1.0f;
    }

    public enum Idf {
        UNARY,
        IDF,
        IDF_SMOOTH,
        // IDF_MAX,
        PROBABILISTIC_IDF


    }

    public enum Tf {
        BINARY,
        RAW_COUNT,
        // TERM_FREQUENCY,
        LOG_NORMALIZATION,
        // DOUBLE_NORMALIZATION_05,
        // DOUBLE_NORMALIZATION_K

    }



    @Override
    public String toString() {
        return null;
    }
}
