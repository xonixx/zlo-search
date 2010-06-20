package info.xonix.zlo.search.site;

import info.xonix.zlo.search.model.Site;

/**
 * Author: Vovan
 * Date: 14.01.2008
 * Time: 21:31:44
 */
@Deprecated
public class SiteSourceKillMe {
    private Site site;

    public SiteSourceKillMe(String siteName) {
        setSiteName(siteName);
    }

    public SiteSourceKillMe(Site site) {
        this.site = site;
    }

    public String getSiteName() {
        return site.getName();
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
