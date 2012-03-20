package info.xonix.zlo.search.test.junit.xmlfp;

import info.xonix.zlo.search.xmlfp.ForumAccessor;
import info.xonix.zlo.search.xmlfp.XmlFpException;
import org.junit.Assert;
import org.junit.Test;

/**
 * User: gubarkov
 * Date: 20.03.12
 * Time: 14:50
 */
public class WwwconfXmlFpTests {
    @Test
    public void test1() throws XmlFpException {
        final ForumAccessor forumAccessor = ForumAccessor.fromDescriptorUrl(
                "http://votalka.campus.mipt.ru/?xmlfp");

        Assert.assertEquals("http://votalka.campus.mipt.ru/?index", forumAccessor.getForumUrl());
        Assert.assertEquals("свободное™ общение", forumAccessor.getTitle());
        Assert.assertEquals("диктатура свободного™ общения без модерации", forumAccessor.getDescription());
    }
}
