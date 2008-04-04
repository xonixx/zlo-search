package info.xonix.zlo.web.test.junit;

import org.junit.Test;
import info.xonix.zlo.web.HtmlConstructor;
import static org.junit.Assert.*;

/**
 * Author: Vovan
 * Date: 16.10.2007
 * Time: 23:31:05
 */
public class TestsWeb {
    @Test
    public void testHtmlConstructor() {
        assertEquals("<select name=\"selId\">\n" +
                "<option value=\"0\">item1</option>\n" +
                "<option value=\"1\" selected>item2</option>\n" +
                "<option value=\"2\">item3</option>\n" +
                "</select>",
                HtmlConstructor.constructSelector("selId", null, new String[]{"item1", "item2", "item3"}, 1, true));

        assertEquals("<select name=\"selId\">\n" +
                "<option value=\"-1\">addItem</option>\n" +
                "<option value=\"0\">item1</option>\n" +
                "<option value=\"1\" selected>item2</option>\n" +
                "<option value=\"2\">item3</option>\n" +
                "</select>",
                HtmlConstructor.constructSelector("selId", null, new String[]{"addItem"}, new String[]{"item1", "item2", "item3"}, 1, true));

        assertEquals("<select name=\"selId\">\n" +
                "<option value=\"-1\">addItem1</option>\n" +
                "<option value=\"-2\" selected>addItem2</option>\n" +
                "<option value=\"0\">item1</option>\n" +
                "<option value=\"1\">item2</option>\n" +
                "<option value=\"2\">item3</option>\n" +
                "</select>",
                HtmlConstructor.constructSelector("selId", null, new String[]{"addItem1", "addItem2"},
                        new String[]{"item1", "item2", "item3"}, -2, true));
    }
}
