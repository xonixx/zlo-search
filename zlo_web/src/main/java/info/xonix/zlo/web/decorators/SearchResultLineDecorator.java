package info.xonix.zlo.web.decorators;

import info.xonix.zlo.search.config.DateFormats;
import info.xonix.zlo.search.model.Message;
import org.displaytag.decorator.TableDecorator;

/**
 * Author: gubarkov
 * Date: 31.08.2007
 * Time: 16:25:08
 */
public class SearchResultLineDecorator extends TableDecorator {
    public String getDate() {
        Message msg = (Message) getCurrentRowObject();
        return DateFormats.ddMMyyyyy_HHmm.get().format(msg.getDate());
    }
}
