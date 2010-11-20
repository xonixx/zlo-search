package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.domainobj.Site;

/**
 * User: Vovan
 * Date: 21.06.2010
 * Time: 0:16:10
 */
public interface IndexerLogic {
    void index(Site site, int from, int to) throws IndexerException;
}
