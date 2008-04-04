package info.xonix.zlo.search.site;

import info.xonix.zlo.search.dao.Site;

/**
 * Author: Vovan
 * Date: 14.01.2008
 * Time: 21:31:44
 */
public class SiteSource {
    private Site site;

    public SiteSource(String siteName) {
        setSiteName(siteName);
    }

    public SiteSource(Site site) {
        this.site = site;
    }

    public String getSiteName() {
        return site.getSiteName();
    }

    public void setSiteName(String siteName) {
        setSite(Site.forName(siteName));
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }
}
