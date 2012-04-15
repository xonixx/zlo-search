package info.xonix.zlo.search.analyzers;

import org.apache.lucene.analysis.TokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

import java.io.IOException;

/**
 * User: Vovan
 * Date: 24.04.11
 * Time: 16:28
 * @deprecated not used for now
 */
@Deprecated
public class YoLetterFilter extends TokenFilter {
    public YoLetterFilter(TokenStream in) {
        super(in);
        termAtt = addAttribute(CharTermAttribute.class);
    }

    private CharTermAttribute termAtt;

    @Override
    public final boolean incrementToken() throws IOException {
        if (input.incrementToken()) {

            final char[] buffer = termAtt.buffer();
            final int length = termAtt.length();
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
