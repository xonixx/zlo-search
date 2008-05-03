package info.xonix.zlo.search;

import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.site.SiteSource;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * Author: Vovan
 * Date: 30.09.2007
 * Time: 3:08:16
 */
public class SearchRequest extends SiteSource {

    private String text;

    private boolean inTitle;
    private boolean inBody;

    private boolean inReg;
    private boolean inHasUrl;
    private boolean inHasImg;

    private String nick;
    private String host;
    private int topicCode = -1; // by default - all

    private Date fromDate;
    private Date toDate;

    private boolean searchAll;

    public SearchRequest(Site site, String text, boolean inTitle, boolean inBody,
                         boolean inReg, boolean inHasUrl, boolean inHasImg,
                         String nick, String host, int topicCode, Date fromDate, Date toDate,
                         boolean searchAll) {
        super(site);
        this.text = text;
        this.inTitle = inTitle;
        this.inBody = inBody;
        this.inReg = inReg;
        this.inHasUrl = inHasUrl;
        this.inHasImg = inHasImg;
        this.nick = nick;
        this.host = host;
        this.topicCode = topicCode;
        this.fromDate = fromDate;
        this.toDate = toDate;
        this.searchAll = searchAll;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isInTitle() {
        return inTitle;
    }

    public void setInTitle(boolean inTitle) {
        this.inTitle = inTitle;
    }

    public boolean isInBody() {
        return inBody;
    }

    public void setInBody(boolean inBody) {
        this.inBody = inBody;
    }

    public boolean isInReg() {
        return inReg;
    }

    public void setInReg(boolean inReg) {
        this.inReg = inReg;
    }

    public boolean isInHasUrl() {
        return inHasUrl;
    }

    public void setInHasUrl(boolean inHasUrl) {
        this.inHasUrl = inHasUrl;
    }

    public boolean isInHasImg() {
        return inHasImg;
    }

    public void setInHasImg(boolean inHasImg) {
        this.inHasImg = inHasImg;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getTopicCode() {
        return topicCode;
    }

    public void setTopicCode(int topicCode) {
        this.topicCode = topicCode;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public boolean isSearchAll() {
        return searchAll;
    }

    public void setSearchAll(boolean searchAll) {
        this.searchAll = searchAll;
    }

    public boolean canBeProcessed() {
        return StringUtils.isNotEmpty(text)
                || StringUtils.isNotEmpty(nick)
                || StringUtils.isNotEmpty(host)
                || inHasUrl
                || inHasImg
//                || -1 != topicCode
                ;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SearchRequest))
            return false;

        SearchRequest req = (SearchRequest) obj;

        return getSite().equals(req.getSite()) &&
                topicCode == req.getTopicCode() &&
                StringUtils.equals(text, req.getText()) &&
                inTitle == req.isInTitle() &&
                inBody == req.isInBody() &&
                inReg == req.isInReg() &&
                inHasUrl == req.isInHasUrl() &&
                inHasImg == req.isInHasImg() &&
                StringUtils.equals(nick, req.getNick()) &&
                StringUtils.equals(host, req.getHost()) &&
                (fromDate == req.getFromDate() || fromDate != null && fromDate.equals(req.getFromDate())) &&
                (toDate == req.getToDate() || toDate != null && toDate.equals(req.getToDate())) &&
                searchAll == req.isSearchAll();
    }

    public int hashCode() {
        return StringUtils.join(new Object[]{text, inTitle, inBody, inReg, inHasUrl, inHasImg, nick, host, topicCode, searchAll, getSiteName()}, '|').hashCode();
    }

    public boolean isTheSameSearch(SearchRequest searchRequest) {
        return this.equals(searchRequest);
    }

    public boolean isNotTheSameSearch(SearchRequest searchRequest) {
        return !isTheSameSearch(searchRequest);
    }

    public SearchResult performSearch() {
        // just to throw exception if db connection broken and can't be fixed
        SearchResult result = getSite().getZloSearcher().search(this);
        result.setLastSearch(this);
        return result;
    }
}
