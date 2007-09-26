package org.xonix.zlo.web.decorators;

import org.displaytag.decorator.TableDecorator;
import org.xonix.zlo.search.model.ZloMessage;
import org.xonix.zlo.search.model.ZloMessageAccessor;

import java.text.SimpleDateFormat;

/**
 * Author: gubarkov
 * Date: 31.08.2007
 * Time: 16:25:08
 */
public class SearchResultLineDecorator extends TableDecorator {
    public static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public String getDate() {
        ZloMessageAccessor msg = (ZloMessageAccessor) getCurrentRowObject();
        return DATE_FORMAT.format(msg.getDate());
    }
}
