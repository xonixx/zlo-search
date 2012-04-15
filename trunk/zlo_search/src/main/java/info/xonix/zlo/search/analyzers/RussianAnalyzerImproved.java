package info.xonix.zlo.search.analyzers;

import info.xonix.zlo.search.LuceneVersion;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.ru.RussianAnalyzer;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;

/**
 * User: Vovan
 * Date: 24.04.11
 * Time: 16:19
 */
public class RussianAnalyzerImproved extends Analyzer {
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
