package info.xonix.zlo.web.decorators;

import info.xonix.zlo.search.config.DateFormats;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.utils.obscene.ObsceneAnalyzer;
import info.xonix.zlo.web.utils.RequestUtils;
import org.apache.commons.lang.StringUtils;
import org.displaytag.decorator.TableDecorator;
import org.springframework.web.util.HtmlUtils;

import java.util.TreeMap;

/**
 * Author: Vovan
 * Date: 30.03.2008
 * Time: 16:56:20
 */
public class HistoryTableDecorator extends TableDecorator {
    public static final String REQ_TEXT = "req_text";
    public static final String REQ_NICK = "req_nick";
    public static final String REQ_HOST = "req_host";
    private final ObsceneAnalyzer obsceneAnalyzer = AppSpringContext.get(ObsceneAnalyzer.class);

    public static final int MAX_LEN = 40;
    public static final String REPLACEMENT_FOR_OBSCENE = "&lt;неразборчиво&gt;";

    private boolean admin;

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    private String shortenString(String s, int len) {
        if (s.length() > len) {
            s = StringUtils.substring(s, 0, len - 3) + "...";
        }

        return s;
    }

    private String shortenString(Object s) {
        if (s == null)
            return "";
        return shortenString(s.toString(), MAX_LEN);
    }

    private Object get(final String field) {
        return ((TreeMap) getCurrentRowObject()).get(field);
    }

    /**
     * @param field fieldname
     * @return escaped for html & unobscened text
     */
    private String getSafeText(final String field) {
        final String initialText = (String) get(field);
        final String resultText = HtmlUtils.htmlEscape(shortenString(initialText));

        if (obsceneAnalyzer.containsObsceneWord(initialText)) {
            if (admin) {
                return "<span class='attention'>" + resultText + "</span>";
            } else {
                return REPLACEMENT_FOR_OBSCENE; // don't show obscene in history
            }
        }

        return resultText;
    }

    public String getSearchText() {
        return getSafeText(REQ_TEXT);
    }

    public String getSearchNick() {
        return getSafeText(REQ_NICK);
    }

    public String getSearchHost() {
        return getSafeText(REQ_HOST);
    }

    public String getReqDate() {
        return DateFormats.ddMMyyyyy_HHmm.format(get("req_date"));
    }

/*    public boolean isObscene() {
        return obsceneAnalyzer.containsObsceneWord((String) get(REQ_TEXT)) ||
                obsceneAnalyzer.containsObsceneWord((String) get(REQ_NICK)) ||
                obsceneAnalyzer.containsObsceneWord((String) get(REQ_HOST));
    }

    public boolean isSafe() {
        return !isObscene();
    }*/

    public String getUserAgentSmall() {
        return RequestUtils.getUserAgentSmall((String) (get("user_agent")));
    }
}
