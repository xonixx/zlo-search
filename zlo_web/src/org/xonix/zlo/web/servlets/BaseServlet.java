package org.xonix.zlo.web.servlets;

import org.xonix.zlo.web.servlets.helpful.ForwardingServlet;
import org.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.xonix.zlo.web.CookieUtils;
import org.xonix.zlo.search.config.Config;
import org.xonix.zlo.search.dao.Site;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

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
        if (StringUtils.isNotEmpty(request.getParameter(QS_SITE))) {
            String site;
            try {
                site = Config.SITES[Integer.parseInt(request.getParameter(QS_SITE))];
            } catch (Exception e) {
                site = Config.SITES[0];
                request.setParameter(QS_SITE, "0");
            }
            session.setAttribute(SESS_SITE_ROOT, site);
            CookieUtils.rememberInCookie(response, QS_SITE, request.getParameter(QS_SITE));
        } else if (StringUtils.isNotEmpty(siteInCookie = CookieUtils.recallFromCookie(request, QS_SITE))) {
            request.setParameter(QS_SITE, siteInCookie); // for drop-down
            session.setAttribute(SESS_SITE_ROOT, Config.SITES[Integer.parseInt(siteInCookie)]); // for search result list
        } else {
            session.setAttribute(SESS_SITE_ROOT, Config.SITES[0]);
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
