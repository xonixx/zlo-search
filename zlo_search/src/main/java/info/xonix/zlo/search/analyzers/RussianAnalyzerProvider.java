package info.xonix.zlo.search.analyzers;

import info.xonix.zlo.search.LuceneVersion;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.ru.RussianAnalyzer;

/**
 * User: Vovan
 * Date: 23.03.11
 * Time: 20:11
 */
public class RussianAnalyzerProvider implements AnalyzerProvider {
    @Override
    public Analyzer getAnalyzer() {
//        return new RussianAnalyzerImproved();
        return new RussianAnalyzer(LuceneVersion.VERSION);
    }
}
