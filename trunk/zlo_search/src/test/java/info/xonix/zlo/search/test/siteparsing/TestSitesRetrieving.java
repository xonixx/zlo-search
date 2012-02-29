package info.xonix.zlo.search.test.siteparsing;


import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.search.logic.site.PageParseException;
import info.xonix.zlo.search.logic.site.RetrieverException;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageStatus;
import info.xonix.zlo.search.spring.AppSpringContext;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Author: Vovan
 * Date: 22.03.2008
 * Time: 20:17:05
 */
public class TestSitesRetrieving {

    private static Site velo = null;
    private static Site dev = null;
    private static Site zlo = null;
    private static Site takeoff = null;
    private static Site anime = null;
    private static Site np = null;
    private static Site x = null;
    private static Site dolgopa = null;

    private static SiteLogic siteLogic = AppSpringContext.get(SiteLogic.class);

    @BeforeClass
    public static void setUp() {
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
        final String forumId = velo;

        int lmn = siteLogic.getLastMessageNumber(forumId);
        System.out.println("lmn: " + lmn);

        Message m = siteLogic.getMessageByNumber(forumId, 19490);
        System.out.println(m);

        Assert.assertEquals(19490, m.getNum());
        Assert.assertEquals("sim", m.getNick());
        Assert.assertTrue(m.isReg());
        Assert.assertEquals("gw.zunet.ru", m.getHost());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));

        m = siteLogic.getMessageByNumber(forumId, 19580);
        System.out.println(m);

        Assert.assertEquals(19580, m.getNum());
        Assert.assertEquals("bull", m.getNick());
        Assert.assertTrue(!m.isReg());
        Assert.assertEquals("ppp85-140-32-253.pppoe.mtu-net.ru", m.getHost());
        Assert.assertTrue(StringUtils.isEmpty(m.getBody()));

        m = siteLogic.getMessageByNumber(forumId, 18869);
        System.out.println(m);

        Assert.assertEquals("а многие собирают себе титановые ригиды на ХТЯ.....", m.getTitle());
        Assert.assertEquals("Tormentor", m.getNick());
        Assert.assertTrue(StringUtils.isEmpty(m.getBody()));
        Assert.assertTrue(m.isReg());

        m = siteLogic.getMessageByNumber(forumId, 25597);
        System.out.println(m);
        Assert.assertEquals("Велотуризм", m.getTopic());
        Assert.assertEquals("Отчет по походу по Карелии, который обещал выложить", m.getTitle());
        Assert.assertEquals("timm", m.getNick());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));
        Assert.assertTrue(m.isHasUrl());
        Assert.assertEquals("localhost.localdomain", m.getHost());

        checkMsgNotExists(forumId);
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
        final String forumId = dolgopa;

        int lmn = siteLogic.getLastMessageNumber(forumId);
        System.out.println(lmn);

        Message m = siteLogic.getMessageByNumber(forumId, 138080);

        System.out.println(m);
        Assert.assertEquals("Rook", m.getNick());
        Assert.assertEquals("", m.getHost());
        Assert.assertEquals("Не, ну чо тут сказать? Как обычно: Слава ЕР! И ныне и присно и во веки веков!", m.getTitle());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));
        Assert.assertEquals("И да не кончатся богом данные бюллетени в ее поддержку!", m.getBody());

        m = siteLogic.getMessageByNumber(forumId, 138026);

        System.out.println(m);
        Assert.assertEquals("@LuCiFeRsHa@", m.getNick());
        Assert.assertEquals("", m.getHost());
        Assert.assertEquals("Спасибо!!!!!!!!!!!!!!!!", m.getTitle());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(StringUtils.isEmpty(m.getBody()));

        m = siteLogic.getMessageByNumber(forumId, 138034);

        System.out.println(m);
        Assert.assertEquals("\\/", m.getNick());
        Assert.assertEquals("", m.getHost());
        Assert.assertEquals("пожелаем им скорейшего ВЫЗДОРОВЛЕНИЯ, а будут выёживацо-пусть роют себе свой коллектор", m.getTitle());
        Assert.assertTrue(!m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));

        m = siteLogic.getMessageByNumber(forumId, 138004);

        System.out.println(m);
        Assert.assertEquals("FIPS", m.getNick());
        Assert.assertEquals("", m.getHost());
        Assert.assertEquals("А кинологи в окресностях ближайших есть?", m.getTitle());
        Assert.assertEquals("Вопрос", m.getTopic());
        Assert.assertTrue(!m.isReg());
        Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));
        Assert.assertEquals("С овчаркой позаниматься.", m.getBody());

        m = siteLogic.getMessageByNumber(forumId, 199);
        System.out.println(m);
        Assert.assertEquals("<P>Люди кто нить знает что за проводок кинули с Лих 4 на Чайку уж не сетку ли?????", m.getBody());

        checkMsgNotExists(forumId);
    }

    private void checkMsgNotExists(String forumId) throws RetrieverException, PageParseException {
        final Message m = siteLogic.getMessageByNumber(forumId, 999999999);

        System.out.println(m);

        Assert.assertEquals(null, m.getNick());
        Assert.assertEquals(null, m.getHost());
        Assert.assertEquals(null, m.getBody());
        Assert.assertEquals(MessageStatus.DELETED, m.getStatus());
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

        m = siteLogic.getMessageByNumber(zlo, 7787566);

        Assert.assertEquals("vilfred", m.getNick());
        Assert.assertEquals("77.51.192.172", m.getHost());
        Assert.assertTrue(m.isReg());
        Assert.assertTrue(m.isHasImg(zlo));
        Assert.assertTrue("Сообщения в этом потоке".equals(m.getTitle()));
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
    public void testAnime() throws RetrieverException, PageParseException {
        int lmn = siteLogic.getLastMessageNumber(anime);

        System.out.println(lmn);

        Message m = siteLogic.getMessageByNumber(anime, 16825);
        System.out.println(m);

        Assert.assertTrue(m.isReg());
        Assert.assertEquals("bestation", m.getNick());
        Assert.assertEquals("10.55.110.140", m.getHost());
        Assert.assertTrue(!m.isHasImg(anime));
        Assert.assertTrue(!m.isHasUrl());
        Assert.assertEquals("В качестве бонуса могу выдать батч скаченный на 54.2% с полностью скаченной первой серией, оп и ед.", m.getBody());
        Assert.assertEquals("Ну, что? Кто в локалке возмется кланнад 1-5 скачать?", m.getTitle());

        m = siteLogic.getMessageByNumber(anime, 16376);
        System.out.println(m);

        Assert.assertTrue(m.isHasImg(anime));
        Assert.assertTrue(m.isHasUrl());
        Assert.assertTrue(m.isReg());
        Assert.assertEquals("fth", m.getNick());
        Assert.assertEquals("10.55.103.181", m.getHost());
        Assert.assertEquals("С наступающим Новым Годом!", m.getTitle());

        m = siteLogic.getMessageByNumber(anime, 16799);
        System.out.println(m);

        Assert.assertFalse(m.isReg());
        Assert.assertEquals("zuzzik_", m.getNick());
        Assert.assertEquals("Фотки и отчет будут? Ж)", m.getTitle());

        m = siteLogic.getMessageByNumber(anime, 2);
        System.out.println(m);
        Assert.assertFalse(m.isOk());
        Assert.assertEquals(MessageStatus.DELETED, m.getStatus());
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
