package info.xonix.zlo.web.test.junit;

import info.xonix.zlo.search.SortBy;
import info.xonix.zlo.web.servlets.SearchServlet;
import info.xonix.zlo.web.utils.html.HtmlConstructor;
import info.xonix.zlo.web.utils.html.HtmlSelectBuilder;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Author: Vovan
 * Date: 16.10.2007
 * Time: 23:31:05
 */
public class TestsHtmlConstructor {
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

    @Test
    public void testSortSelector() {
        final HtmlSelectBuilder selector = new HtmlSelectBuilder()
                .name(SearchServlet.QS_SORT)
                .value(SortBy.RELEVANCE.getName())
                .addOption(SortBy.DATE.getName(), "дате")
                .addOption(SortBy.RELEVANCE.getName(), "релевантности");

        System.out.println(selector.build());

        final HtmlSelectBuilder selector1 = new HtmlSelectBuilder()
                .name(SearchServlet.QS_SORT)
                .value("123")
                .addOption(SortBy.DATE.getName(), "дате")
                .addOption(SortBy.RELEVANCE.getName(), "релевантности");

        System.out.println(selector1.build());
    }
}
