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

    LABEL_SEARCH("label.search"),
    LABEL_IN_TITLE("label.search.in.title"),
    LABEL_IN_BODY("label.search.in.body"),

    LABEL_MESSAGES("label.search.messages"),
    LABEL_IN_REG("label.search.in.reg"),
    LABEL_IN_HAS_URL("label.search.in.has.url"),
    LABEL_IN_HAS_IMG("label.search.in.has.img"),

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
