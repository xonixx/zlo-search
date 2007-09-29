package org.xonix.zlo.search.config;

/**
 * Author: gubarkov
 * Date: 11.09.2007
 * Time: 17:58:00
 */
public enum HtmlStrings {
    PAGE_TITLE("page.title"),
    LABEL_TOPIC("label.topic"),
    LABEL_TEXT("label.text"),
    LABEL_NICK("label.nick"),
    LABEL_HOST("label.host"),
    LABEL_SITE("label.site"),
    LABEL_DATES("label.dates"),
    LABEL_FROM_DATE("label.from.date"),
    LABEL_TO_DATE("label.to.date"),
    LABEL_PER_PAGE("label.per.page"),
    LINK_SAVED_MSG("link.saved.msg"),

    LABLE_SEARCH("label.search"),
    LABLE_IN_TITLE("label.search.in.title"),
    LABLE_IN_BODY("label.search.in.body"),

    HEADER_NUM("header.num"),
    HEADER_TITLE("header.title"),
    HEADER_NICK("header.nick"),
    HEADER_HOST("header.host"),
    HEADER_DATE("header.date"),
    
    ;

    private String val;

    HtmlStrings(String key) {
        this.val = Config.getProp(key);
    }

    public String toString() {
        return val;
    }
}
