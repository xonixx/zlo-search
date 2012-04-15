package info.xonix.zlo.search.analyzers;

import org.apache.lucene.analysis.CharFilter;
import org.apache.lucene.analysis.CharStream;

import java.io.IOException;

/**
 * User: gubarkov
 * Date: 15.04.12
 * Time: 18:28
 */
public class YoCharFilter extends CharFilter {
    public YoCharFilter(CharStream in) {
        super(in);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        final int charsRead = super.read(cbuf, off, len);
        if (charsRead > 0) {
            final int end = off + charsRead;
            while (off < end) {
                if (cbuf[off] == 'ё' || cbuf[off] == 'Ё')
                    cbuf[off] = 'е';
                off++;
            }
        }
        return charsRead;
    }
}
