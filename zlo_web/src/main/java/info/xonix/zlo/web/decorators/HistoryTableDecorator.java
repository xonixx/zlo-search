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
    private final ObsceneAnalyzer obsceneAnalyzer = AppSpringContext.get(ObsceneAnalyzer.class);

    public static final int MAX_LEN = 40;

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
                return ""; // don't show obscene in history
            }
        }

        return resultText;
    }

    public String getSearchText() {
        return getSafeText("req_text");
    }

    public String getSearchNick() {
        return getSafeText("req_nick");
    }

    public String getSearchHost() {
        return getSafeText("req_host");
    }

    public String getReqDate() {
        return DateFormats.ddMMyyyyy_HHmm.format(get("req_date"));
    }

    public String getUserAgentSmall() {
        return RequestUtils.getUserAgentSmall((String) (get("user_agent")));
    }
}
