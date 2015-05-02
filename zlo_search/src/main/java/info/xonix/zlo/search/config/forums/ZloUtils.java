package info.xonix.zlo.search.config.forums;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * User: xonix
 * Date: 28.04.15
 * Time: 1:17
 */
public class ZloUtils {
    private static final Logger log = Logger.getLogger(ZloUtils.class);

    public static final int ROOT = 0;
    public static final int ERR = -5;

    public static int extractParentId(String msgHtml) {
        Document document = Jsoup.parse(msgHtml);
        Elements thisCommentElt = document.select("span[id]>a[name]>b");

        if (thisCommentElt == null || thisCommentElt.isEmpty()) {
            return ERR;
        }

        Element div = thisCommentElt.first().parent().parent().parent();
        if ("div".equals(div.tagName()) && div.hasAttr("id")) {
            String id = div.attr("id");
            try {
                return Integer.parseInt(id.substring(1));
            } catch (NumberFormatException e) {
                log.warn("Unable to parse id: " + id);
                return ERR;
            }
        }

        return ROOT;
    }
}
