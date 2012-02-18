import info.xonix.zlo.search.xmlfp.utils.UrlUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: gubarkov
 * Date: 18.02.12
 * Time: 17:07
 */
public class UrlUtilTests {
    @Test
    public void test1() {
        Assert.assertEquals("http://url/123", UrlUtil.combineUrls("http://aaa.bb/", "http://url/123"));
        Assert.assertEquals("https://url/123", UrlUtil.combineUrls("http://aaa.bb/", "https://url/123"));
        Assert.assertEquals("http://aaa.bb/url/123", UrlUtil.combineUrls("http://aaa.bb/", "url/123"));
        Assert.assertEquals("http://aaa.bb/url/123", UrlUtil.combineUrls("http://aaa.bb/", "/url/123"));
        Assert.assertEquals("http://aaa.bb/url/123", UrlUtil.combineUrls("http://aaa.bb/ccc/ddd", "/url/123"));
        Assert.assertEquals("http://aaa.bb/url/123?fff", UrlUtil.combineUrls("http://aaa.bb/ccc/ddd?eee", "/url/123?fff"));
        Assert.assertEquals("http://aaa.bb/ccc/url/123?fff", UrlUtil.combineUrls("http://aaa.bb/ccc/ddd?eee", "url/123?fff"));
    }
}
