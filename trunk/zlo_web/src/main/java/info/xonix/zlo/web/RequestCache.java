package info.xonix.zlo.web;

import info.xonix.zlo.search.domainobj.SearchResult;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Author: Vovan
 * Date: 24.04.2008
 * Time: 2:35:31
 */
public class RequestCache {
    public static int MAX_ENTRIES = 10;
    private Map<Integer, SearchResult> cache = Collections.synchronizedMap(new LinkedHashMap<Integer, SearchResult>(MAX_ENTRIES + 1) {
        protected boolean removeEldestEntry(Map.Entry<Integer, SearchResult> eldest) {
            return size() > MAX_ENTRIES;
        }

        public SearchResult get(Object key) {
            SearchResult o = remove(key);
            put((Integer) key, o);
            return o;
        }
    });

    public SearchResult get(Integer key) {
        return cache.get(key);
    }

    public SearchResult put(Integer key, SearchResult value) {
        cache.put(key, value);
        return value;
    }

    public String toString() {
        return cache.toString();
    }
}
