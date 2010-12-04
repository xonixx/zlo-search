package info.xonix.zlo.search.test.siteparsing;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.search.logic.site.PageParseException;
import info.xonix.zlo.search.logic.site.RetrieverException;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.spring.AppSpringContext;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * Author: Vovan
 * Date: 22.03.2008
 * Time: 20:17:05
 */
public class TestSitesRetrieving {

    private Site velo = null;
    private Site dev = null;
    private Site zlo = null;
    private Site takeoff = null;
    private Site anime = null;
    private Site np = null;
    private Site x = null;
    private Site dolgopa = null;

    private SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);

    @Before
    public void setUp() {
        velo = Site.forName("velo");

        dev = Site.forName("dev");

        zlo = Site.forName("zlo");

        takeoff = Site.forName("takeoff");

        anime = Site.forName("anime");

//        np = Site.forName("np");
        x = Site.forName("x");

        dolgopa = Site.forName("dolgopa");
    }

    @Test
    public void testVelo() throws RetrieverException, PageParseException {
        int lmn = siteLogic.getLastMessageNumber(velo);
        System.out.println("lmn: " + lmn);

        Message m = siteLogic.getMessageByNumber(velo, 19490);

        System.out.println(m);

        Assert.assertEquals(19490, m.getNum());
        Assert.assertEquals("sim", m.getNick());
        Assert.assertTrue(m.isReg());
        Assert.assertEquals("gw.zunet.ru", m.getHost());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));

        m = siteLogic.getMessageByNumber(velo, 19580);

        System.out.println(m);

        Assert.assertEquals(19580, m.getNum());
        Assert.assertEquals("bull", m.getNick());
        Assert.assertTrue(!m.isReg());
        Assert.assertEquals("ppp85-140-32-253.pppoe.mtu-net.ru", m.getHost());
        Assert.assertTrue(StringUtils.isEmpty(m.getBody()));

        m = siteLogic.getMessageByNumber(velo, 18869);

        System.out.println(m);

        Assert.assertTrue(StringUtils.isEmpty(m.getBody()));
        Assert.assertTrue(m.isReg());

        System.out.println(siteLogic.getMessageByNumber(velo, 33));


    }

    @Test
    public void testDev() throws RetrieverException, PageParseException {
        int lmn = siteLogic.getLastMessageNumber(dev);
        System.out.println(lmn);

        Message m = siteLogic.getMessageByNumber(dev, 9404);

        Assert.assertEquals(9404, m.getNum());
        Assert.assertEquals("Berk", m.getNick());
        Assert.assertEquals("post.mirantis.ru", m.getHost());
        Assert.assertTrue(m.isReg());
        Assert.assertEquals("Unix/Linux", m.getTopic());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));

        System.out.println(m);

        m = siteLogic.getMessageByNumber(dev, 9374);

        Assert.assertEquals(9374, m.getNum());
        Assert.assertEquals("arfix.", m.getNick());
        Assert.assertTrue(StringUtils.isEmpty(m.getBody()));
        Assert.assertTrue(!m.isReg());

        System.out.println(m);

        m = siteLogic.getMessageByNumber(dev, 10153);

        Assert.assertEquals(10153, m.getNum());
        Assert.assertEquals("Рыбак", m.getNick());
        Assert.assertEquals("ppp91-77-164-91.pppoe.mtu-net.ru", m.getHost());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));
        Assert.assertTrue(!m.isReg());

        System.out.println(m);
    }

    @Test
    public void testX() throws RetrieverException, PageParseException {
        int lmn = siteLogic.getLastMessageNumber(x);
        System.out.println(lmn);

        Message m = siteLogic.getMessageByNumber(x, 55177);

        Assert.assertEquals("uberdude", m.getNick());
        Assert.assertEquals("wimax-client.yota.ru", m.getHost());
        Assert.assertEquals("Почему?", m.getTitle());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));
        System.out.println(m);

        m = siteLogic.getMessageByNumber(x, 55182);

        Assert.assertEquals("Митя", m.getNick());
        Assert.assertEquals("ip-46-73-158-249.bb.netbynet.ru", m.getHost());
        Assert.assertEquals("+ к", m.getTitle());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isEmpty(m.getBody()));
        System.out.println(m);

        m = siteLogic.getMessageByNumber(x, 55207);

        Assert.assertEquals("demerzel", m.getNick());
        Assert.assertEquals("93.175.15.182", m.getHost());
        Assert.assertEquals("+", m.getTitle());
        Assert.assertTrue(!m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));
        System.out.println(m);

        m = siteLogic.getMessageByNumber(x, 999999999);

        Assert.assertEquals(null, m.getNick());
        Assert.assertEquals(null, m.getHost());
        Assert.assertEquals(null, m.getBody());
        Assert.assertEquals(MessageStatus.DELETED, m.getStatus());
        System.out.println(m);
    }

    @Test
    public void testDolgopa() throws RetrieverException, PageParseException {
        int lmn = siteLogic.getLastMessageNumber(x);
        System.out.println(lmn);

        Message m = siteLogic.getMessageByNumber(x, 55177);

        Assert.assertEquals("uberdude", m.getNick());
        Assert.assertEquals("wimax-client.yota.ru", m.getHost());
        Assert.assertEquals("Почему?", m.getTitle());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));
        System.out.println(m);

        m = siteLogic.getMessageByNumber(x, 55182);

        Assert.assertEquals("Митя", m.getNick());
        Assert.assertEquals("ip-46-73-158-249.bb.netbynet.ru", m.getHost());
        Assert.assertEquals("+ к", m.getTitle());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isEmpty(m.getBody()));
        System.out.println(m);

        m = siteLogic.getMessageByNumber(x, 55207);

        Assert.assertEquals("demerzel", m.getNick());
        Assert.assertEquals("93.175.15.182", m.getHost());
        Assert.assertEquals("+", m.getTitle());
        Assert.assertTrue(!m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));
        System.out.println(m);

        m = siteLogic.getMessageByNumber(x, 999999999);

        Assert.assertEquals(null, m.getNick());
        Assert.assertEquals(null, m.getHost());
        Assert.assertEquals(null, m.getBody());
        Assert.assertEquals(MessageStatus.DELETED, m.getStatus());
        System.out.println(m);
    }

    @Test
    public void testZlo() throws RetrieverException, PageParseException {
        int lmn = siteLogic.getLastMessageNumber(zlo);
        System.out.println(lmn);

        Message m = siteLogic.getMessageByNumber(zlo, 4093778);

        Assert.assertEquals("QDiesel", m.getNick());
        Assert.assertEquals("nokia.7ka.mipt.ru", m.getHost());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));
        System.out.println(m);

        m = siteLogic.getMessageByNumber(zlo, 4093788);

        Assert.assertEquals("Loki", m.getNick());
        Assert.assertEquals("loki.3ka.mipt.ru", m.getHost());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isEmpty(m.getBody()));
        System.out.println(m);

        m = siteLogic.getMessageByNumber(zlo, 405573);

        Assert.assertEquals("Demoney", m.getNick());
        Assert.assertEquals("morgue.7ka.mipt.ru", m.getHost());
        Assert.assertTrue(!m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));
        System.out.println(m);

        m = siteLogic.getMessageByNumber(zlo, 999999999);

        Assert.assertEquals(null, m.getNick());
        Assert.assertEquals(null, m.getHost());
        Assert.assertEquals(null, m.getBody());
        Assert.assertEquals(MessageStatus.DELETED, m.getStatus());
        System.out.println(m);
    }

    @Test
    public void testTakeoff() throws RetrieverException, PageParseException {
        int lmn = siteLogic.getLastMessageNumber(takeoff);

        System.out.println(lmn);

        Message m = siteLogic.getMessageByNumber(takeoff, 13996);

        Assert.assertEquals("Слава", m.getNick());
        Assert.assertEquals("gluk.2ka.mipt.ru", m.getHost());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));

        System.out.println(m);

        m = siteLogic.getMessageByNumber(takeoff, 14003);

        Assert.assertEquals(14003, m.getNum());
        Assert.assertEquals("mitrich", m.getNick());
        Assert.assertEquals("83.229.152.73", m.getHost());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isEmpty(m.getBody()));

        System.out.println(m);

        m = siteLogic.getMessageByNumber(takeoff, 1729);

        Assert.assertEquals(1729, m.getNum());
        Assert.assertEquals("shpagin&stalker", m.getNick());
        Assert.assertEquals("stalker.4ka.mipt.ru", m.getHost());
        Assert.assertTrue(!m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));

        System.out.println(m);
    }

    @Test
    public void testAnime() throws RetrieverException {
        int lmn = siteLogic.getLastMessageNumber(anime);
        System.out.println(lmn);
    }

//    @Test

    public void testNp() throws RetrieverException, PageParseException {
        int lmn = siteLogic.getLastMessageNumber(np);
        System.out.println(lmn);

        Message m = siteLogic.getMessageByNumber(np, 96119);
        System.out.println(m);

        Assert.assertEquals("там сочинение на страницу или больше", m.getTitle());
        Assert.assertEquals("а я почему-то могу писать или писать только иногда, когда настроение", m.getBody());
        Assert.assertEquals("без темы", m.getTopic());

        m = siteLogic.getMessageByNumber(np, 95933);
        System.out.println(m);

        Assert.assertEquals("проверим", m.getTitle());
        Assert.assertEquals("Кто здесь?", m.getTopic());
        Assert.assertEquals("", m.getBody());
    }
}
