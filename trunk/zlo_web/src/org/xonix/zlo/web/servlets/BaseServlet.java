package org.xonix.zlo.web.servlets;

import org.xonix.zlo.web.servlets.helpful.ForwardingServlet;
import org.xonix.zlo.web.servlets.helpful.ForwardingRequest;
import org.xonix.zlo.web.CookieUtils;
import org.xonix.zlo.search.config.Config;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Author: Vovan
 * Date: 21.12.2007
 * Time: 16:32:17
 */
public class BaseServlet extends ForwardingServlet {

    protected void setSiteInSession(ForwardingRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(true);
        String siteInCookie;
        if (StringUtils.isNotEmpty(request.getParameter(SearchServlet.QS_SITE))) {
            String site;
            try {
                site = Config.SITES[Integer.parseInt(request.getParameter(SearchServlet.QS_SITE))];
            } catch (Exception e) {
                site = Config.SITES[0];
                request.setParameter(SearchServlet.QS_SITE, "0");
            }
            session.setAttribute(SearchServlet.SESS_SITE_ROOT, site);
            CookieUtils.rememberInCookie(response, SearchServlet.QS_SITE, request.getParameter(SearchServlet.QS_SITE));
        } else if (StringUtils.isNotEmpty(siteInCookie = CookieUtils.recallFromCookie(request, SearchServlet.QS_SITE))) {
            request.setParameter(SearchServlet.QS_SITE, siteInCookie); // for drop-down
            session.setAttribute(SearchServlet.SESS_SITE_ROOT, Config.SITES[Integer.parseInt(siteInCookie)]); // for search result list
        } else {
            session.setAttribute(SearchServlet.SESS_SITE_ROOT, Config.SITES[0]);
        }
    }
}
