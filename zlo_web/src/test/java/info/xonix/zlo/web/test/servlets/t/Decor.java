package info.xonix.zlo.web.test.servlets.t;

import org.displaytag.decorator.TableDecorator;

/**
 * Author: Vovan
 * Date: 30.04.2008
 * Time: 23:33:04
 */
public class Decor extends TableDecorator {

    public String getT() {
        Obj obj = (Obj) getCurrentRowObject();
        return "" + obj.getN() * 2;
    }

}
