package info.xonix.zlo.search.analyzers;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.TermAttribute;

import java.io.IOException;

/**
 * User: Vovan
 * Date: 24.04.11
 * Time: 16:28
 */
public class YoLetterFilter extends TokenFilter {
    public YoLetterFilter(TokenStream in) {
        super(in);
        termAtt = addAttribute(TermAttribute.class);
    }

    private TermAttribute termAtt;

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {

            final char[] buffer = termAtt.termBuffer();
            final int length = termAtt.termLength();
            for (int i = 0; i < length; i++) {
                if (buffer[i] == 'ё' || buffer[i] == 'Ё') {
                    buffer[i] = 'е';
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
