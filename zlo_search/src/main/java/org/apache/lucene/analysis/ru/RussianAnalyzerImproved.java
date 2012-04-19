package org.apache.lucene.analysis.ru;

import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.analyzers.YoCharFilter;
import org.apache.lucene.analysis.CharReader;
import org.apache.lucene.analysis.ReusableAnalyzerBase;
import org.apache.lucene.util.Version;

import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;

/**
 * User: Vovan
 * Date: 24.04.11
 * Time: 16:19
 */
public class RussianAnalyzerImproved extends ReusableAnalyzerBase{
    /*
    see:
    http://mail-archives.apache.org/mod_mbox/lucene-java-user/201204.mbox/%3CCACP%2BLpfiGRGHMBVvXKzciXn4GRgBUS%2BeqHCY_PryMcBj9bD42Q%40mail.gmail.com%3E
     */
//    private static final Version RUSSIAN_ANALYZER_VERSION = LuceneVersion.VERSION;
    private static final Version RUSSIAN_ANALYZER_VERSION = Version.LUCENE_30;

    private static final String[] RUSSIAN_STOP_WORDS_30 = {
            "а", "без", "более", "бы", "был", "была", "были", "было", "быть", "в",
            "вам", "вас", "весь", "во", "вот", "все", "всего", "всех", "вы", "где",
            "да", "даже", "для", "до", "его", "ее", "ей", "ею", "если", "есть",
            "еще", "же", "за", "здесь", "и", "из", "или", "им", "их", "к", "как",
            "ко", "когда", "кто", "ли", "либо", "мне", "может", "мы", "на", "надо",
            "наш", "не", "него", "нее", "нет", "ни", "них", "но", "ну", "о", "об",
            "однако", "он", "она", "они", "оно", "от", "очень", "по", "под", "при",
            "с", "со", "так", "также", "такой", "там", "те", "тем", "то", "того",
            "тоже", "той", "только", "том", "ты", "у", "уже", "хотя", "чего", "чей",
            "чем", "что", "чтобы", "чье", "чья", "эта", "эти", "это", "я"
    };

    private RussianAnalyzer russianAnalyzer = new RussianAnalyzer(RUSSIAN_ANALYZER_VERSION,
            new HashSet<Object>(Arrays.asList(RUSSIAN_STOP_WORDS_30)));

    @Override
    protected Reader initReader(Reader reader) {
        return new YoCharFilter(CharReader.get(reader));
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName, Reader reader) {
        return russianAnalyzer.createComponents(fieldName, reader);
    }
}
