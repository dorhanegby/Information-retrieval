import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.List;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.LowerCaseFilter;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.StopwordAnalyzerBase;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;

public class PorterAnalyzer extends StopwordAnalyzerBase {
    public static final CharArraySet ENGLISH_STOP_WORDS_SET;
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    private int maxTokenLength;
    public static final CharArraySet STOP_WORDS_SET;

    public PorterAnalyzer(CharArraySet stopWords) {
        super(stopWords);
        this.maxTokenLength = 255;
    }

    public PorterAnalyzer() {
        this(STOP_WORDS_SET);
    }

    public PorterAnalyzer(Reader stopwords) throws IOException {
        this(loadStopwordSet(stopwords));
    }

    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
    }

    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        final StandardTokenizer src = new StandardTokenizer();
        src.setMaxTokenLength(this.maxTokenLength);
        TokenStream tok = new StandardFilter(src);
        tok = new LowerCaseFilter(tok);
        tok = new StopFilter(tok, this.stopwords);
        tok = new PorterStemFilter(tok);
        return new TokenStreamComponents(src, tok) {
            protected void setReader(Reader reader) {
                src.setMaxTokenLength(PorterAnalyzer.this.maxTokenLength);
                super.setReader(reader);
            }
        };
    }

    protected TokenStream normalize(String fieldName, TokenStream in) {
        TokenStream result = new StandardFilter(in);
        result = new LowerCaseFilter(result);
        return result;
    }

    static {
        List<String> stopWords = Arrays.asList(new String[]{"a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is", "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there", "these", "they", "this", "to", "was", "will", "with"});
        CharArraySet stopSet = new CharArraySet(stopWords, false);
        ENGLISH_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
        STOP_WORDS_SET = ENGLISH_STOP_WORDS_SET;
    }
}
