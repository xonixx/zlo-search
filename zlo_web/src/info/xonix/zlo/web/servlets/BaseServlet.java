package info.xonix.zlo.web.servlets;

import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.web.CookieUtils;
import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import info.xonix.zlo.web.servlets.helpful.ForwardingServlet;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Author: Vovan
 * Date: 21.12.2007
 * Time: 16:32:17
 */
public class BaseServlet extends ForwardingServlet {
    public static final String REQ_SITE_ROOT = "siteRoot";
    public static final String QS_SITE = "site";

    protected void setSiteInReq(ForwardingRequest request, HttpServletResponse response) {
        String siteInCookie;
        String[] sites = Site.getSiteNames();

        String siteNumStr = request.getParameter(QS_SITE);
        if (StringUtils.isNotEmpty(siteNumStr)) {
            String siteRoot;
            try {
                siteRoot = sites[Integer.parseInt(siteNumStr)];
            } catch (Exception e) {
                siteRoot = sites[0];
                request.setParameter(QS_SITE, "0");
            }
            request.setAttribute(REQ_SITE_ROOT, siteRoot);
            CookieUtils.rememberInCookie(response, QS_SITE, siteNumStr);
        } else if (StringUtils.isNotEmpty(siteInCookie = CookieUtils.recallFromCookie(request, QS_SITE))) {
            int siteInCookieInt = Integer.parseInt(siteInCookie);
            siteInCookieInt = siteInCookieInt > sites.length ? 0 : siteInCookieInt;
            request.setAttribute(REQ_SITE_ROOT, sites[siteInCookieInt]); // for search result list
            request.setParameter(QS_SITE, Integer.toString(siteInCookieInt)); // for drop-down
        } else {
            request.setParameter(QS_SITE, "0");
            request.setAttribute(REQ_SITE_ROOT, sites[0]);
        }

        request.setAttribute(QS_SITE, getSite(request));
    }

    public static Site getSite(HttpServletRequest req) {
        // todo: tmp
        String sn = req.getParameter(QS_SITE);
        int siteId;
        if (StringUtils.isNotEmpty(sn)) {
            siteId = Integer.parseInt(sn);
        } else {
            siteId = 0;
        }

        return Site.getSite(siteId);
    }
}
