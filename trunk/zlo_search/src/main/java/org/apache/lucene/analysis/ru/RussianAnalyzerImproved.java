package org.apache.lucene.analysis.ru;

import info.xonix.zlo.search.LuceneVersion;
import info.xonix.zlo.search.analyzers.YoCharFilter;
import org.apache.lucene.analysis.CharReader;
import org.apache.lucene.analysis.ReusableAnalyzerBase;

import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;

/**
 * User: Vovan
 * Date: 24.04.11
 * Time: 16:19
 */
public class RussianAnalyzerImproved extends ReusableAnalyzerBase{
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

    private RussianAnalyzer russianAnalyzer = new RussianAnalyzer(LuceneVersion.VERSION,
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
