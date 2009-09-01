package info.xonix.zlo.web.decorators;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.model.ZloMessageAccessor;
import org.displaytag.decorator.TableDecorator;

/**
 * Author: gubarkov
 * Date: 31.08.2007
 * Time: 16:25:08
 */
public class SearchResultLineDecorator extends TableDecorator {
    public String getDate() {
        ZloMessageAccessor msg = (ZloMessageAccessor) getCurrentRowObject();
        return Config.DateFormats.DF_1.format(msg.getDate());
    }
}
