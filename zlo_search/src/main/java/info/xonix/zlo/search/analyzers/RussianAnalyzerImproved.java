package info.xonix.zlo.search.analyzers;

import info.xonix.zlo.search.LuceneVersion;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ru.RussianAnalyzer;

import java.io.IOException;
import java.io.Reader;

/**
 * User: Vovan
 * Date: 24.04.11
 * Time: 16:19
 */
public class RussianAnalyzerImproved extends Analyzer {
    private RussianAnalyzer russianAnalyzer = new RussianAnalyzer(LuceneVersion.VERSION);

    @Override
    public TokenStream tokenStream(String fieldName, Reader reader) {
        final TokenStream tokenStream = russianAnalyzer.tokenStream(fieldName, reader);
        return new YoLetterFilter(tokenStream);
    }

    @Override
    public TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
        final TokenStream tokenStream = russianAnalyzer.reusableTokenStream(fieldName, reader);
        return new YoLetterFilter(tokenStream);
    }
}
