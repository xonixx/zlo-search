package org.xonix.zlo.web;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * Author: Vovan
 * Date: 14.12.2007
 * Time: 16:49:08
 */
public class FoundTextHighlighter {

    private static final Logger logger = Logger.getLogger(FoundTextHighlighter.class);
    public static final String WORDS_SEPARATOR = "/";

    private String[] highlightWords;
    private String text;

    public void setHighlightWords(String[] highlightWords) {
        this.highlightWords = highlightWords;
    }

    public String getWordsStr() {
        return StringUtils.join(highlightWords, WORDS_SEPARATOR);
    }

    public void setWordsStr(String wordsStr) {
        if (StringUtils.isNotEmpty(wordsStr)) {
            highlightWords = wordsStr.split(WORDS_SEPARATOR);
        } else {
            highlightWords = new String[0];
        }
    }

    public void setText(String text) {
        this.text = text;
    }

    // todo: works, but very slow
    public String getHighlightedText() {
        String txt = text;
        for (String w : highlightWords) {
            w = w.replaceAll("\\?", "[^\\\\s]{1}").replaceAll("\\*", "[^\\\\s]*?");
            txt = txt.replaceAll("(?iu)" +                                              // case insensetive, unicode
                    "(?<!\\<a href=\"http(?:s?)://[^\"<>]{0,300})" +                     // not to break links
                    "(?<!\\<img class=\"imgtag\" src=\"http(?:s?)://[^\"<>]{0,300})" +   // not to break pics
                    "(\\b" + w + "[^\\s]*?)\\b", "<span class=\"hl\">$1</span>");               // highlight
        }
        return txt;
    }
}
