package info.xonix.zlo.web.servlets;


import info.xonix.zlo.search.config.forums.ForumDescriptor;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import info.xonix.zlo.web.servlets.helpful.ForwardingServlet;
import info.xonix.zlo.web.utils.CookieUtils;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: Vovan
 * Date: 21.12.2007
 * Time: 16:32:17
 */
public class BaseServlet extends ForwardingServlet {
//    public static final String REQ_SITE_ROOT = "siteRoot";
    public static final String QS_SITE = "site";

    protected void setSiteInReq(ForwardingRequest request, HttpServletResponse response) {
        String siteInCookie;

        ForumDescriptor forumDescriptor;
        String siteNumStr = request.getParameter(QS_SITE);

        if (StringUtils.isNotEmpty(siteNumStr)) {
            forumDescriptor = getSiteOrDefault(siteNumStr);
            CookieUtils.rememberInCookie(response, QS_SITE, String.valueOf(forumDescriptor.getForumIntId()));
        } else if (StringUtils.isNotEmpty(siteInCookie = CookieUtils.recallFromCookie(request, QS_SITE))) {
            forumDescriptor = getSiteOrDefault(siteInCookie);
        } else {
            forumDescriptor = getSiteOrDefault("0");
        }

        request.setParameter(QS_SITE, String.valueOf(forumDescriptor.getForumIntId()));
        request.setAttribute(QS_SITE, forumDescriptor);
//        request.setAttribute(REQ_SITE_ROOT, RequestUtils.getSiteRoot(request, forumDescriptor));
    }

    private ForumDescriptor getSiteOrDefault(String siteNumStr) {
        ForumDescriptor defaultSite = GetForum.descriptor(0);
        ForumDescriptor site;
        try {
            // TODO: logic should not rely on RuntimeExceptions
            site = GetForum.descriptor(Integer.parseInt(siteNumStr));
            if (site == null) {
                site = defaultSite;
            }
        } catch (Exception e) {
            site = defaultSite;
        }
        return site;
    }

    public static ForumDescriptor getSite(HttpServletRequest req) {
        String sn = req.getParameter(QS_SITE);
        int siteId;
        if (StringUtils.isNotEmpty(sn)) {
            siteId = Integer.parseInt(sn);
        } else {
            siteId = 0;
        }

        return GetForum.descriptor(siteId);
    }
}
