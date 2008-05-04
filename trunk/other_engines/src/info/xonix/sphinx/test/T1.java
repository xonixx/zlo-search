package info.xonix.sphinx.test;

import org.sphx.api.test;
import org.sphx.api.SphinxException;

import java.io.UnsupportedEncodingException;

/**
 * Author: Vovan
 * Date: 04.05.2008
 * Time: 3:53:24
 */
public class T1 {
    public static void main(String[] args) throws SphinxException, UnsupportedEncodingException {
        test.main(new String[]{"-e", "-i", "index_sport", "-l", "100", "-s", "msgDate DESC", "28160"});    
    }
}
