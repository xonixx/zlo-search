package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.model.Site;

import java.util.List;

/**
 * User: Vovan
 * Date: 12.06.2010
 * Time: 22:00:19
 */
public interface SiteLogic {
    List<Site> getSites();

    String[] getSiteNames();

    Site getSite(int num);
}
