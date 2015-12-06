package info.xonix.zlo.search.analyzers;

import info.xonix.zlo.search.utils.IOUtil;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.util.Version;

/**
 * User: Vovan
 * Date: 23.03.11
 * Time: 20:11
 */
public class RussianAnalyzerProvider implements AnalyzerProvider {
    @Override
    public Analyzer getAnalyzer() {
        return new RussianAnalyzerImproved(Version.LUCENE_30,
                new CharArraySet(Version.LUCENE_30,
                        IOUtil.loadStrings(RussianAnalyzerImproved.class.getResourceAsStream("zlo-russian-stop.txt")),
                        false)/*,
                new CharArraySet(Version.LUCENE_30,
                        IOUtil.loadStrings(RussianAnalyzerImproved.class.getResourceAsStream("zlo-nostem.txt")),
                        false)*/);
        // TODO: handle nostem: C++
    }
}
