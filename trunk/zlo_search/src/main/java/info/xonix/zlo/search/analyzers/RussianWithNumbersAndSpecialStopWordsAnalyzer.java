package info.xonix.zlo.search.analyzers;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.spring.AppSpringContext;

/**
 * Author: Vovan
 * Date: 17.05.2008
 * Time: 4:24:59
 */
@Deprecated
public class RussianWithNumbersAndSpecialStopWordsAnalyzer extends RussianWithNumbersAnalyzer {
    private String[] stopWords;
    private static Config config = AppSpringContext.get(Config.class);

/*
    public RussianWithNumbersAndSpecialStopWordsAnalyzer() {
        this.stopWords = config.getProp("analyzer.stop.words").split("|");
        initialize();
    }

    public RussianWithNumbersAndSpecialStopWordsAnalyzer(String[] stopWords) {
        this.stopWords = stopWords;
        initialize();
    }
*/

    protected String[] getStopWords() {
        return stopWords;
    }
}
