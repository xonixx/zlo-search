package info.xonix.zlo.search.domainobj;

import info.xonix.zlo.search.utils.factory.Factory;

/**
 * Author: Vovan
 * Date: 11.01.2008
 * Time: 22:30:46
 * TODO: we should rename this to smth like WwwconfForumParams
 */
public class Site extends SiteConfiguration {
    private static Factory<String, Site> siteFactory = new Factory<String, Site>() {
        @Override
        protected Site create(String siteName) {
            return new Site(siteName);
        }
    };

//    TODO: merge this with SiteConfiguration to single class!!!
    public Site(String siteName) {
        super(siteName);
    }

    public static Site forName(String siteName) {
        return siteFactory.get(siteName);
    }

    public String toString() {
        return "Site(" + getName() + ")";
    }
}
