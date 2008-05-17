package info.xonix.zlo.search;

import info.xonix.zlo.search.config.Config;

/**
 * Author: Vovan
 * Date: 17.05.2008
 * Time: 4:24:59
 */
public class RussianWithNumbersAndSpecialStopWordsAnalyzer extends RussianWithNumbersAnalyzer {
    private String[] stopWords;

    public RussianWithNumbersAndSpecialStopWordsAnalyzer() {
        this.stopWords = Config.getProp("analyzer.stop.words").split("|");
        initialize();
    }

    public RussianWithNumbersAndSpecialStopWordsAnalyzer(String[] stopWords) {
        this.stopWords = stopWords;
        initialize();
    }

    protected String[] getStopWords() {
        return stopWords;
    }
}
