package info.xonix.zlo.search;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import info.xonix.zlo.search.model.ZloMessage;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.PatternSyntaxException;

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

    private String hlClass;

    public String getHlClass() {
        return hlClass;
    }

    public void setHlClass(String hlClass) {
        this.hlClass = hlClass;
    }

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
        try {
            for (String w : highlightWords) {
                w = w.replaceAll("\\?", "[^\\\\s]{1}").replaceAll("\\*", "[^\\\\s]*?");
                txt = txt.replaceAll("(?iu)" +                                              // case insensetive, unicode
                        "(?<!\\<[^<>]{0,300})" +                                            // not to break html tags
                        "(\\b" + w + "[^\\s]*?)\\b", "<span class=\"" + (StringUtils.isEmpty(hlClass) ? "hl" : hlClass) + "\">$1</span>");               // highlight;
            }
        } catch (PatternSyntaxException ex) {
            logger.error("Regex parse error: ", ex);        
        }
        return txt;
    }

    /**
     * to fix search with : f.e. url search, "." is for gluing together into exact phrase
     * @param text
     * @return
     */
    public static String escapeColon(String text){
        if (text != null && !StringUtils.equals(text, "*:*")) { // matchAllQuery
            return text.replace(":", ".");
        } else
            return text;
    }

    public static String[] formHighlightedWords(String txt) {
        txt = escapeColon(txt);

        if (StringUtils.isEmpty(txt))
            return new String[0];

        Query query = null;
        try {
            String queryStr = MessageFormat.format("{0}:({1})", ZloMessage.FIELDS.BODY, txt);
            QueryParser parser = new QueryParser(ZloMessage.FIELDS.BODY, ZloMessage.constructAnalyzer());
            query = parser.parse(queryStr);
            Set<Term> set = new HashSet<Term>();
            query.extractTerms(set);
            String[] res = new String[set.size()];
            int i = 0;
            for (Term t : set) {
                res[i++] = t.text();
            }
            return res;
        } catch (org.apache.lucene.queryParser.ParseException e) {
            logger.error(e);
        } catch (UnsupportedOperationException e) {
            // for wildcard query
            String qs = query.toString(ZloMessage.FIELDS.BODY);
            qs = qs.replaceAll("-\\b.+?(?:\\s|$)", " ").replaceAll("\\(|\\)|\\+|\\[|\\]|\\{|\\}|\"", " ");
            return qs.trim().split("\\s+");
        }
        return new String[0];
    }
}
