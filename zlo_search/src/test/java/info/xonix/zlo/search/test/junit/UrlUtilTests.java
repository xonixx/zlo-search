package info.xonix.zlo.search.test.junit;

import info.xonix.zlo.search.xmlfp.utils.UrlUtil;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: gubarkov
 * Date: 18.02.12
 * Time: 17:07
 */
public class UrlUtilTests {
    @Test
    public void test1() {
        assertEquals("http://url/123", UrlUtil.combineUrls("http://aaa.bb/", "http://url/123"));
        assertEquals("https://url/123", UrlUtil.combineUrls("http://aaa.bb/", "https://url/123"));
        assertEquals("http://aaa.bb/url/123", UrlUtil.combineUrls("http://aaa.bb/", "url/123"));
        assertEquals("http://aaa.bb/url/123", UrlUtil.combineUrls("http://aaa.bb/", "/url/123"));
        assertEquals("http://aaa.bb/url/123", UrlUtil.combineUrls("http://aaa.bb/ccc/ddd", "/url/123"));
        assertEquals("http://aaa.bb/url/123?fff", UrlUtil.combineUrls("http://aaa.bb/ccc/ddd?eee", "/url/123?fff"));
        assertEquals("http://aaa.bb/ccc/url/123?fff", UrlUtil.combineUrls("http://aaa.bb/ccc/ddd?eee", "url/123?fff"));

        assertEquals("http://aaa.bb/ccc/ddd?fff",
                UrlUtil.combineUrls("http://aaa.bb/ccc/ddd", "?fff"));

        assertEquals("http://aaa.bb/ccc/ddd?fff",
                UrlUtil.combineUrls("http://aaa.bb/ccc/ddd?eee", "?fff"));
    }

    @Test
    public void testUrlWithoutSchema() {
       assertEquals("aaa/", UrlUtil.urlWithoutSchema("http://aaa/"));
       assertEquals("aaa/", UrlUtil.urlWithoutSchema("https://aaa/"));
       assertEquals("aaa/bbb", UrlUtil.urlWithoutSchema("https://aaa/bbb"));
       assertEquals("aaa/bbb?ddd=eee", UrlUtil.urlWithoutSchema("https://aaa/bbb?ddd=eee"));
       assertEquals("aaa/bbb?ddd=eee", UrlUtil.urlWithoutSchema("ftp://aaa/bbb?ddd=eee"));
       assertEquals("aaa/bbb?ddd=eee", UrlUtil.urlWithoutSchema("HTTP://aaa/bbb?ddd=eee"));
       assertEquals("aaa/bbb?ddd=eee", UrlUtil.urlWithoutSchema("ZzZzZzZz://aaa/bbb?ddd=eee"));
       assertEquals("zlo.rt.mipt.ru:7500/search", UrlUtil.urlWithoutSchema("http://zlo.rt.mipt.ru:7500/search"));
    }
}
