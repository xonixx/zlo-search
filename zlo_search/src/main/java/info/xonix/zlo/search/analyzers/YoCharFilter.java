package info.xonix.zlo.search.analyzers;

import org.apache.lucene.analysis.CharFilter;

import java.io.IOException;
import java.io.Reader;

/**
 * User: gubarkov
 * Date: 15.04.12
 * Time: 18:28
 */
public class YoCharFilter extends CharFilter {
    public YoCharFilter(Reader input) {
        super(input);
    }

    @Override
    protected int correct(int currentOff) {
        return currentOff;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        final int charsRead = input.read(cbuf, off, len);
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
