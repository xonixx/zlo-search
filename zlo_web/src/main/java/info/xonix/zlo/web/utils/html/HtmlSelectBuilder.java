package info.xonix.zlo.web.utils.html;

import org.apache.commons.lang.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * User: gubarkov
 * Date: 17.04.12
 * Time: 23:04
 */
public class HtmlSelectBuilder {
    private String id;
    private String name;
    private String value;

    private Map<String, String> options = new LinkedHashMap<String, String>();

    public HtmlSelectBuilder id(String id) {
        this.id = id;
        return this;
    }

    public HtmlSelectBuilder name(String name) {
        this.name = name;
        return this;
    }

    public HtmlSelectBuilder value(String value) {
        this.value = value;
        return this;
    }

    public HtmlSelectBuilder addOption(String val) {
        return addOption(val, val);
    }

    public HtmlSelectBuilder addOption(String value, String title) {
        options.put(value, title);
        return this;
    }

    public HtmlSelectBuilder addOption(Object value, Object title) {
        return addOption(value.toString(), title.toString());
    }

    public String build() {
        if (name == null) {
            throw new IllegalArgumentException("name not set!");
        }

        StringBuilder res = new StringBuilder("<select name=\"");
        res.append(name).append("\"");
        if (id != null) {
            res.append(" id=\"").append(id).append("\"");
        }
        res.append(">\n");

        for (Map.Entry<String, String> option : options.entrySet()) {
            final String val = option.getKey();
            final String title = option.getValue();

            res.append("<option value=\"")
                    .append(val)
                    .append("\"");

            if (StringUtils.equals(value, val)) {
                res.append(" selected");
            }

            res.append(">")
                    .append(title)
                    .append("</option>\n");
        }

        res.append("</select>");

        return res.toString();
    }
}
