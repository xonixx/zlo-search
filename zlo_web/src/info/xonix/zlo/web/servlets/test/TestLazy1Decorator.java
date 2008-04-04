package info.xonix.zlo.web.servlets.test;

import org.displaytag.decorator.TableDecorator;

/**
 * Author: Vovan
 * Date: 25.09.2007
 * Time: 20:04:17
 */
public class TestLazy1Decorator extends TableDecorator {

    public Object getVal1 () {
        Object o = getCurrentRowObject();
        Object d = getDecoratedObject();
        return "{"+((TestLazy1.Val)o).getField()+"}";
    }
}
