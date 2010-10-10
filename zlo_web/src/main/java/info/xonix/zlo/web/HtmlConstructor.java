package info.xonix.zlo.web;

import org.apache.commons.lang.StringUtils;


/**
 * Author: gubarkov
 * Date: 11.09.2007
 * Time: 14:47:26
 */
public class HtmlConstructor {
    public static String constructSelector(String name, String id, String[][] additionalOptions,
                                           String[] values,
                                           String[] itemsCollection,
                                           Integer selected, boolean enumerate) {
        if (values == null) {
            values = new String[itemsCollection.length];
            for (Integer i = 0; i < itemsCollection.length; i++) {
                values[i] = i.toString();
            }
        }

        if (itemsCollection == null)
            itemsCollection = new String[0];

        StringBuilder res = new StringBuilder("<select name=\"");
        res.append(name).append("\"");
        if (id != null) {
            res.append(" id=\"").append(id).append("\"");
        }
        res.append(">\n");

        if (additionalOptions != null) {
            for (String[] additionalOption : additionalOptions) {
                int key = Integer.parseInt(additionalOption[0]);
                String val = additionalOption[1];
                if (key == selected)
                    res.append("<option value=\"")
                            .append(key).append("\" selected>")
                            .append(val).append("</option>\n");
                else
                    res.append("<option value=\"")
                            .append(key).append("\">")
                            .append(val).append("</option>\n");
            }
        }

        for (int i = 0; i < itemsCollection.length; i++) {
            String valueI = values[i];
            if (StringUtils.equals(valueI, selected.toString()))
                res.append("<option value=\"")
                        .append(valueI).append("\" selected>")
                        .append(itemsCollection[i]).append("</option>\n");
            else
                res.append("<option value=\"")
                        .append(enumerate ? valueI : itemsCollection[i]).append("\">")
                        .append(itemsCollection[i]).append("</option>\n");
        }

        res.append("</select>");
        return res.toString();
    }

    public static String constructSelector(String name, String id, String[][] additionalOptions,
                                           String[] itemsCollection,
                                           int selected, boolean enumerate) {
        return constructSelector(name, id, additionalOptions, null, itemsCollection, selected, enumerate);
    }

    public static String constructSelector(String name, String id, String[] itemsCollection,
                                           int selected, boolean enumerate) {
        return constructSelector(name, id, (String[][]) null, itemsCollection,
                selected, enumerate);
    }

    public static String constructSelector(String name, String id, String[] additioanlOptions, String[] itemsCollection,
                                           int selected, boolean enumerate) {
        String[][] additionalOptionsM = new String[additioanlOptions.length][2];

        for (int i = 0; i < additioanlOptions.length; i++) {
            additionalOptionsM[i][0] = Integer.toString(-(i + 1));
            additionalOptionsM[i][1] = additioanlOptions[i];
        }

        return constructSelector(name, id, additionalOptionsM, itemsCollection,
                selected, enumerate);
    }

/*    public static String constructSelector(String name, String id, String additioanlOption, String[] itemsCollection,
                                           int selected, boolean enumerate) {
        return constructSelector(name, id, new String[] {additioanlOption}, itemsCollection,
                                           selected, enumerate);
    }*/
}
