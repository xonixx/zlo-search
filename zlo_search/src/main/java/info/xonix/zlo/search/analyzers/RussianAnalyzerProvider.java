package info.xonix.zlo.search.analyzers;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.util.Version;

/**
 * User: Vovan
 * Date: 23.03.11
 * Time: 20:11
 */
public class RussianAnalyzerProvider implements AnalyzerProvider {
    @Override
    public Analyzer getAnalyzer() {
        return new RussianAnalyzerImproved(Version.LUCENE_30);
//        return new RussianAnalyzer(Version.LUCENE_30);
    }
}
