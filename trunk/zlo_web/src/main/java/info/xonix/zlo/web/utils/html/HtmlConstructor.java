package info.xonix.zlo.web.utils.html;

import org.apache.commons.lang.StringUtils;

import java.util.Map;


/**
 * Author: gubarkov
 * Date: 11.09.2007
 * Time: 14:47:26
 */
public class HtmlConstructor {
    public static String constructSelector(String name, String id, String[][] additionalOptions,
                                           Map<Integer, String> data,
                                           Integer selected, boolean enumerate) {
        String[] keys = new String[data.size()];
        String[] titles = new String[data.size()];

        int i = 0;
        for (Integer key : data.keySet()) {
            keys[i] = key.toString();
            titles[i] = data.get(key);
            i++;
        }

        return constructSelector(name, id, additionalOptions, keys, titles, selected, enumerate);
    }

    public static String constructSelector(String name, String id, String[][] additionalOptions,
                                           String[] values,
                                           String[] titles,
                                           Integer selected, boolean enumerate) { // TODO: wtf enumerate?
        if (values == null) {
            values = new String[titles.length];
            for (Integer i = 0; i < titles.length; i++) {
                values[i] = i.toString();
            }
        }

        if (titles == null)
            titles = new String[0];

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

        for (int i = 0; i < titles.length; i++) {
            String valueI = values[i];
            if (StringUtils.equals(valueI, selected.toString()))
                res.append("<option value=\"")
                        .append(valueI).append("\" selected>")
                        .append(titles[i]).append("</option>\n");
            else
                res.append("<option value=\"")
                        .append(enumerate ? valueI : titles[i]).append("\">")
                        .append(titles[i]).append("</option>\n");
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

/*    public static String constructSelector(String name, String id, String additioanlOption, String[] titles,
                                           int selected, boolean enumerate) {
        return constructSelector(name, id, new String[] {additioanlOption}, titles,
                                           selected, enumerate);
    }*/
}
