package info.xonix.zlo.web.rss;

import com.rometools.rome.feed.synd.*;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedOutput;
import info.xonix.zlo.search.HttpHeader;
import info.xonix.zlo.search.SearchResultPaginatedList;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.dto.SearchRequest;
import info.xonix.zlo.search.dto.SearchResult;
import info.xonix.zlo.search.logic.forum_adapters.ForumAdapter;
import info.xonix.zlo.search.domain.Message;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.utils.HtmlUtils;
import info.xonix.zlo.web.servlets.SearchServlet;
import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * User: Vovan
 * Date: 15.08.2009
 * Time: 20:53:36
 */
public class RssFormer {
    private static final Logger log = Logger.getLogger(RssFormer.class);

    private static final Config config = AppSpringContext.get(Config.class);

    public void formRss(ForwardingRequest request, HttpServletResponse response) {
        if (request.getAttribute(SearchServlet.ERROR) != null) {
            // process error
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
            response.setContentType("application/rss+xml");

            SearchResult searchResult = (SearchResult) request.getAttribute(SearchServlet.REQ_SEARCH_RESULT);
            Assert.notNull(searchResult, "SearchResult must be already set in SearchServlet!");

            SearchResultPaginatedList pl = (SearchResultPaginatedList) request.getAttribute(SearchServlet.REQ_PAGINATED_LIST);
            Assert.notNull(pl, "Paginated list must be already set in SearchServlet!");

            List msgsList = pl.getList();
            Date lastModifiedDateCurrent = msgsList != null && msgsList.size() > 0 ? ((Message) msgsList.get(0)).getDate() : null; // the youngest msg (max date)

            log.info("RSS request. User-Agent: " + request.getHeader(HttpHeader.USER_AGENT) + ", If-Modified-Since: " + request.getHeader(HttpHeader.IF_MODIFIED_SINCE));

            if (lastModifiedDateCurrent != null) {
                Date lastModifiedDateOld = new Date(request.getDateHeader("If-Modified-Since"));

                if (lastModifiedDateCurrent.after(lastModifiedDateOld)) {
                    response.setDateHeader("Last-Modified", lastModifiedDateCurrent.getTime());
                    log.info("Feed is modified: If-Modified-Since Date=" + lastModifiedDateOld + ", lastMsgFeedDate=" + lastModifiedDateCurrent);
                } else {
                    log.info("Sending 304 Not modified: If-Modified-Since Date=" + lastModifiedDateOld + " is _not before_ lastMsgFeedDate=" + lastModifiedDateCurrent);
                    response.setStatus(304); // Not Modified
                    return;
                }
            }

            SearchRequest lastSearch = searchResult.getLastSearch();

            SyndFeed feed = new SyndFeedImpl();
            feed.setFeedType("rss_2.0");

            List<String> l = new ArrayList<String>(3);
            for (String s : Arrays.asList(lastSearch.getText(), lastSearch.getNick(), lastSearch.getHost())) {
                if (StringUtils.isNotEmpty(s))
                    l.add(s);
            }
            String chTitle = "Board search: " + StringUtils.join(l, ", ");

            feed.setTitle(chTitle);

            String link = String.format("http://%s/search?%s", config.getWebsiteDomain(), request.getQueryString().replace("rss&", ""));
            feed.setLink(link);
            feed.setUri(link);
            feed.setDescription(lastSearch.describeToString());
            feed.setLanguage("ru");

//                FoundTextHighlighter hl = new FoundTextHighlighter();
//                hl.setHighlightWords(FoundTextHighlighter.formHighlightedWords(lastSearch.getText()));
            List<SyndEntry> entries = new ArrayList<SyndEntry>();
            feed.setEntries(entries);

            if (msgsList != null) {
                for (Object m1 : msgsList) {
                    Message m = (Message) m1;
                    SyndEntry entry = new SyndEntryImpl();
                    entries.add(entry);

                    ForumAdapter forumAdapter = GetForum.adapter(pl.getForumId());

                    // highlighting of all feed takes to much time & cpu

                    String title = HtmlUtils.unescapeHtml(m.getTitle());
                    if (!"без темы".equals(m.getTopic().toLowerCase()))
                        title = "[" + m.getTopic() + "] " + title;
                    entry.setTitle(title);

                    SyndContent descr = new SyndContentImpl();
                    descr.setType("text/html");
                    descr.setValue(m.getBody());
                    entry.setDescription(descr);

                    entry.setAuthor(m.getNick() + "@" + m.getHost());
                    entry.setPublishedDate(m.getDate());

                    SyndCategory category = new SyndCategoryImpl();
                    category.setName(m.getTopic());

                    entry.setCategories(Collections.singletonList(category));

                    String commentsLink = forumAdapter.prepareMessageUrl(m.getNum());
                    entry.setLink(commentsLink);
                    entry.setComments(commentsLink);
                }
            }

            SyndFeedOutput feedOutput = new SyndFeedOutput();
            try {
                feedOutput.output(feed, response.getWriter());
                response.flushBuffer();
            } catch (FeedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
