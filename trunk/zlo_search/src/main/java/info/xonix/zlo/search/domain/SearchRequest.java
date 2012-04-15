package info.xonix.zlo.search.domain;

import info.xonix.zlo.search.config.DateFormats;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.logic.ControlsDataLogic;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;

/**
 * Author: Vovan
 * Date: 30.09.2007
 * Time: 3:08:16
 */
public class SearchRequest {

    private ControlsDataLogic controlsDataLogic = AppSpringContext.get(ControlsDataLogic.class);

    private String forumId;

    private String text;

    private boolean root;
    private boolean inTitle;
    private boolean inBody;

    private boolean inReg;
    private boolean inHasUrl;
    private boolean inHasImg;

    private String nick;
    private String host;
    private int topicCode = -1; // by default - all

    private boolean isDateSet = false;
    private Date fromDate;
    private Date toDate;

    private boolean searchAll;

    public SearchRequest(String forumId, String text, boolean isRoot, boolean inTitle, boolean inBody,
                         boolean inReg, boolean inHasUrl, boolean inHasImg,
                         String nick, String host, int topicCode, boolean isDateSet, Date fromDate, Date toDate,
                         boolean searchAll) {
        this.forumId = forumId;

        this.text = text;
        root = isRoot;
        this.inTitle = inTitle;
        this.inBody = inBody;
        this.inReg = inReg;
        this.inHasUrl = inHasUrl;
        this.inHasImg = inHasImg;
        this.nick = nick;
        this.host = host;
        this.topicCode = topicCode;

        this.isDateSet = isDateSet;
        this.fromDate = fromDate;
        this.toDate = toDate;

        this.searchAll = searchAll;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isRoot() {
        return root;
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
                || isDateSet
                || root
//                || -1 != topicCode
                ;
    }

    // TODO: rfct equals + hashCode
    public boolean equals(Object obj) {
        if (!(obj instanceof SearchRequest))
            return false;

        SearchRequest req = (SearchRequest) obj;

        return forumId.equals(req.getForumId()) &&
                topicCode == req.getTopicCode() &&
                root == req.root &&
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
        return StringUtils.join(new Object[]{text, root, inTitle, inBody, inReg, inHasUrl, inHasImg, nick, host, topicCode, searchAll, forumId}, '|').hashCode();
    }

    public boolean isTheSameSearch(SearchRequest searchRequest) {
        return this.equals(searchRequest);
    }

    public boolean isNotTheSameSearch(SearchRequest searchRequest) {
        return !isTheSameSearch(searchRequest);
    }

    public String describeToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("форум:(").append(GetForum.adapter(forumId).getForumUrl()).append(")");

        if (StringUtils.isNotEmpty(text)) sb.append(" текст:(").append(text).append(")");
        if (StringUtils.isNotEmpty(nick)) sb.append(" ник:(").append(nick).append(")");
        if (StringUtils.isNotEmpty(host)) sb.append(" хост:(").append(host).append(")");

        if (topicCode != -1) {
            sb.append(" категория:(").append(
                    controlsDataLogic.getTopics(forumId).get(topicCode)).append(")");
        }

        ArrayList<String> options = new ArrayList<String>(5);

        if (root) options.add("в корневых сообщ.");
        if (inTitle) options.add("в заголовках");
        if (inBody) options.add("в теле");
        if (inHasImg) options.add("с картинками");
        if (inHasUrl) options.add("со ссылками");
        if (inReg) options.add("от регов");

        if (options.size() > 0)
            sb.append(" ищем:(").append(StringUtils.join(options, ", ")).append(")");

        options.clear();

        if (fromDate != null || toDate != null) {
            options.add(fromDate != null ? DateFormats.ddMMyyyy.format(fromDate) : "-inf");
            options.add(toDate != null ? DateFormats.ddMMyyyy.format(toDate) : "inf");
            sb.append(" в промежутке дат:(").append(StringUtils.join(options, ", ")).append(")");
        }

        return sb.toString();
    }
}
