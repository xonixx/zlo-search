package org.xonix.zlo.web.decorators;

import org.displaytag.decorator.TableDecorator;
import org.xonix.zlo.search.model.ZloMessage;
import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;

/**
 * Author: gubarkov
 * Date: 31.08.2007
 * Time: 16:25:08
 */
public class SearchResultLineDecorator extends TableDecorator {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public String getTitle() {
        ZloMessage msg = (ZloMessage) getCurrentRowObject();

        if (StringUtils.isNotEmpty(msg.getTopic()))
            return "[" + msg.getTopic() + "] " + msg.getTitle();
        else
            return msg.getTitle();
    }

    public String getDate() {
        ZloMessage msg = (ZloMessage) getCurrentRowObject();
        return DATE_FORMAT.format(msg.getDate());
    }

/*    public String getNick() {
        return ((ZloMessage) getCurrentRowObject()).getNick();
    }*/
}
