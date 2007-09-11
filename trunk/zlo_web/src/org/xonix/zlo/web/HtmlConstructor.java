package org.xonix.zlo.web;

/**
 * Author: gubarkov
 * Date: 11.09.2007
 * Time: 14:47:26
 */
public class HtmlConstructor {
    public static String constructSelector(String name, String [] itemsCollection,
                                           int selected, boolean enumerate) {
        StringBuilder res = new StringBuilder("<select name=\"");
        res.append(name).append("\">\n");

        for (int i=0; i<itemsCollection.length; i++) {
            if (i == selected)
                res.append("<option value=\"")
                        .append(i).append("\" selected>")
                        .append(itemsCollection[i]).append("</option>\n");
            else
                res.append("<option value=\"")
                        .append(enumerate ? i : itemsCollection[i]).append("\">")
                        .append(itemsCollection[i]).append("</option>\n");
        }

        res.append("</select>");
        return res.toString();
    }
}
