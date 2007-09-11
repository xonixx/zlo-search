package org.xonix.zlo.search.config;

/**
 * Author: gubarkov
 * Date: 11.09.2007
 * Time: 17:58:00
 */
public enum HtmlStrings {
    PAGE_TITLE("page.title"),
    LABEL_TITLE("label.title"),
    LABEL_TOPIC("label.topic"),
    LABEL_TEXT("label.text"),
    LABEL_NICK("label.nick"),
    LABEL_HOST("label.host"),
    LABEL_SITE("label.site"),
    LABEL_DATES("label.dates"),
    LABEL_FROM_DATE("label.from.date"),
    LABEL_TO_DATE("label.to.date"),
    LABEL_PER_PAGE("label.per.page");

    private String val;

    HtmlStrings(String key) {
        this.val = Config.getProp(key);
    }

    public String toString() {
        return val;
    }
}
