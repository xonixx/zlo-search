package info.xonix.zlo.web.servlets;

import org.apache.commons.lang.StringUtils;
import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.web.CookieUtils;
import info.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import info.xonix.zlo.web.servlets.helpful.ForwardingServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Author: Vovan
 * Date: 21.12.2007
 * Time: 16:32:17
 */
public class BaseServlet extends ForwardingServlet {
    public static final String SESS_SITE_ROOT = "siteRoot";
    public static final String QS_SITE = "site";

    protected void setSiteInSession(ForwardingRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        String siteInCookie;
        String[] sites = Site.getSiteNames();
        if (StringUtils.isNotEmpty(request.getParameter(QS_SITE))) {
            String site;
            try {
                site = sites[Integer.parseInt(request.getParameter(QS_SITE))];
            } catch (Exception e) {
                site = sites[0];
                request.setParameter(QS_SITE, "0");
            }
            session.setAttribute(SESS_SITE_ROOT, site);
            CookieUtils.rememberInCookie(response, QS_SITE, request.getParameter(QS_SITE));
        } else if (StringUtils.isNotEmpty(siteInCookie = CookieUtils.recallFromCookie(request, QS_SITE))) {
            request.setParameter(QS_SITE, siteInCookie); // for drop-down
            session.setAttribute(SESS_SITE_ROOT, sites[Integer.parseInt(siteInCookie)]); // for search result list
        } else {
            session.setAttribute(SESS_SITE_ROOT, sites[0]);
        }
    }

    protected Site getSite(HttpServletRequest req) {
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
