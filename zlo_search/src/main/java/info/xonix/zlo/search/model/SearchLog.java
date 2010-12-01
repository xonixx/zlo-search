package info.xonix.zlo.search.model;

import info.xonix.zlo.search.domainobj.Site;

/**
 * User: Vovan
 * Date: 04.07.2010
 * Time: 17:06:26
 */
public class SearchLog {
    private Site site;

    private String clientIp;
    private String userAgent;
    private String referer;

    private String searchText;
    private String searchNick;
    private String searchHost;

    private String searchQuery;
    private String searchQueryString;

    private boolean rssAsked;
    private boolean adminRequest;

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public String getClientIp() {
        return clientIp;
    }

    public void setClientIp(String clientIp) {
        this.clientIp = clientIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getSearchText() {
        return searchText;
    }

    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    public String getSearchNick() {
        return searchNick;
    }

    public void setSearchNick(String searchNick) {
        this.searchNick = searchNick;
    }

    public String getSearchHost() {
        return searchHost;
    }

    public void setSearchHost(String searchHost) {
        this.searchHost = searchHost;
    }

    public String getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(String searchQuery) {
        this.searchQuery = searchQuery;
    }

    public String getSearchQueryString() {
        return searchQueryString;
    }

    public void setSearchQueryString(String searchQueryString) {
        this.searchQueryString = searchQueryString;
    }

    public boolean isRssAsked() {
        return rssAsked;
    }

    public void setRssAsked(boolean rssAsked) {
        this.rssAsked = rssAsked;
    }

    public boolean isAdminRequest() {
        return adminRequest;
    }

    public void setAdminRequest(boolean adminRequest) {
        this.adminRequest = adminRequest;
    }
}
