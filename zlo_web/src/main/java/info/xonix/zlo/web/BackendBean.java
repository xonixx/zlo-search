package info.xonix.zlo.web;

import info.xonix.utils.UrlUtil;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.config.forums.ForumDescriptor;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.SortBy;
import info.xonix.zlo.search.logic.ControlsDataLogic;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.web.servlets.SearchServlet;
import info.xonix.zlo.web.utils.html.HtmlConstructor;
import info.xonix.zlo.web.utils.html.HtmlSelectBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Author: Vovan
 * Date: 03.09.2007
 * Time: 20:03:46
 */
public class BackendBean {
    private final static Logger log = Logger.getLogger(BackendBean.class);

    public static final String ALL_TOPICS = "Все темы";

    private String topic;
    private String title;
    private String body;
    private String nick;
    private String host;
    private String fromDate;
    private String toDate;
    private String site;
    private String pageSize;

    private String sort;

    public static final String SN_TOPIC = "topic";
    public static final String SN_SITE = "site";
    public static final String SN_PAGE_SIZE = "pageSize";
    public static final int SITE_URL_MAX_LEN = 30;

    private static final Config config = AppSpringContext.get(Config.class);
    private static final ControlsDataLogic controlsDataLogic = AppSpringContext.get(ControlsDataLogic.class);

    public BackendBean() {
    }

    public String getTopicSelector() {
        String forumId = GetForum.descriptor(getSiteInt()).getForumId();

        return HtmlConstructor.constructSelector(SN_TOPIC, SN_TOPIC,
                new String[][]{{"-1", ALL_TOPICS}},
                controlsDataLogic.getTopics(forumId), getTopicInt(), true);
    }

    public String getSiteSelector() {
        final List<ForumDescriptor> descriptors = GetForum.descriptors();

        String[] ids = new String[descriptors.size()];
        String[] vals = new String[descriptors.size()];

        int i = 0;
        for (ForumDescriptor descriptor : descriptors) {
            ids[i] = Integer.toString(descriptor.getForumIntId());
            vals[i] = UrlUtil.urlWithoutSchema(descriptor.getForumAdapter().getForumUrl());
            i++;
        }

        return HtmlConstructor.constructSelector(SN_SITE, SN_SITE, null,
                ids,
                cropSiteUrls(vals), getSiteInt(), true);
    }

    private String[] cropSiteUrls(String[] siteNames) {
        String[] res = new String[siteNames.length];

        int i = 0;
        for (String siteName : siteNames) {
//            res[i++] = StringUtils.substringBefore(siteName, "/");
            res[i++] = siteName.length() <= SITE_URL_MAX_LEN
                    ? siteName
                    : StringUtils.substring(siteName, 0, SITE_URL_MAX_LEN - 3) + "...";
        }
        return res;
    }

    public String getPageSizeSelector() {
//        return HtmlConstructor.constructSelector(SN_PAGE_SIZE, null, config.getNumsPerPage(), getPageSizeInt(), true);
        HtmlSelectBuilder numsPerPageSelectBuilder = new HtmlSelectBuilder()
                .id(SN_PAGE_SIZE)
                .name(SN_PAGE_SIZE)
                .value(Integer.toString(getPageSizeInt()));

        final int[] numsPerPage = config.getNumsPerPage();
        for (int i = 0; i < numsPerPage.length; i++) {
            numsPerPageSelectBuilder.addOption(i, numsPerPage[i]);
        }

        return numsPerPageSelectBuilder.build();
    }

    public String getSortSelector() {
        final HtmlSelectBuilder selector = new HtmlSelectBuilder()
                .id(SearchServlet.QS_SORT)
                .name(SearchServlet.QS_SORT)
                .value(sort)
                .addOption(SortBy.DATE.getName(), "дате")
                .addOption(SortBy.RELEVANCE.getName(), "релевантности");

        return selector.build();
    }

    public String getSite() {
        return site;
    }

    public int getSiteInt() {
        return NumberUtils.toInt(site, 0);
    }

    // forumIntId
    public void setSite(String site) {
        this.site = site;
    }

    public String getTopic() {
        return topic;
    }

    public int getTopicInt() {
        return NumberUtils.toInt(topic, -1);
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }

    public String getPageSize() {
        return pageSize;
    }

    public int getPageSizeInt() {
        return NumberUtils.toInt(pageSize, 0);
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
