package org.xonix.zlo.web;

/**
 * Author: gubarkov
 * Date: 11.09.2007
 * Time: 14:47:26
 */
public class HtmlConstructor {
    public static String constructSelector(String name, String[][] additionalOptions, String[] itemsCollection,
                                           int selected, boolean enumerate) {
        if (itemsCollection == null)
            itemsCollection = new String[0];
        
        StringBuilder res = new StringBuilder("<select name=\"");
        res.append(name).append("\">\n");

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

    public static String constructSelector(String name, String[] itemsCollection,
                                           int selected, boolean enumerate) {
        return constructSelector(name, (String[][]) null, itemsCollection,
                                           selected, enumerate);
    }

    public static String constructSelector(String name, String[] additioanlOptions, String[] itemsCollection,
                                           int selected, boolean enumerate) {
        String[][] additionalOptionsM = new String[additioanlOptions.length][2];

        for(int i=0; i<additioanlOptions.length; i++) {
            additionalOptionsM[i][0] = Integer.toString(-(i+1));
            additionalOptionsM[i][1] = additioanlOptions[i];
        }
        
        return constructSelector(name, additionalOptionsM, itemsCollection,
                                           selected, enumerate);
    }

    public static String constructSelector(String name, String additioanlOption, String[] itemsCollection,
                                           int selected, boolean enumerate) {
        return constructSelector(name, new String[] {additioanlOption}, itemsCollection,
                                           selected, enumerate);
    }
}
