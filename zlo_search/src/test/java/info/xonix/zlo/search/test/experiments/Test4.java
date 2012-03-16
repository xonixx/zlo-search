package info.xonix.zlo.search.test.experiments;

import info.xonix.zlo.search.FoundTextHighlighter;

/**
 * Author: Vovan
 * Date: 29.04.2008
 * Time: 2:38:54
 */
public class Test4 {
    public static void main(String[] args) {
        m1();
    }

    public static void m1() {
        for (String w: FoundTextHighlighter.formHighlightedWords("колон* s-90 ")){
            System.out.println(w);
        }

    }
}
