package org.xonix.zlo.search.db;

import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.dao.DB;

/**
 * Author: Vovan
 * Date: 13.01.2008
 * Time: 4:23:03
 */
public class DbManagerSource {
    private Site site;

    public DbManagerSource(Site site) {
        this.site = site;
    }

    public Site getSite() {
        return site;
    }

    public DbManager getDbManager() {
        return DbManager.forSite(site);
    }

    private DB db;
    public DB getDB() {
        if (db == null) {
            db = new DB(site);
        }
        return db;
    }
}
