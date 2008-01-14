package org.xonix.zlo.search.db;

import org.xonix.zlo.search.dao.Site;
import org.xonix.zlo.search.dao.DB;
import org.xonix.zlo.search.site.SiteSource;

/**
 * Author: Vovan
 * Date: 13.01.2008
 * Time: 4:23:03
 */
public class DbManagerSource extends SiteSource {
    public DbManagerSource(Site site) {
        super(site);
    }

    public DbManager getDbManager() {
        return DbManager.forSite(getSite());
    }

    private DB db;
    public DB getDB() {
        if (db == null) {
            db = new DB(getSite());
        }
        return db;
    }
}
