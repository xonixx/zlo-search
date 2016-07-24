package info.xonix.zlo.search.test.junit.siteparsing;


import info.xonix.zlo.search.logic.ForumLogic;
import info.xonix.zlo.search.logic.MessageLogic;
import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.domain.Message;
import info.xonix.zlo.search.domain.MessageStatus;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.utils.HtmlUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Author: Vovan
 * Date: 22.03.2008
 * Time: 20:17:05
 */
public class TestSitesRetrieving {

/*    private static Site velo = null;
    private static Site dev = null;
    private static Site zlo = null;
    private static Site takeoff = null;
    private static Site anime = null;
    private static Site np = null;
    private static Site x = null;
    private static Site dolgopa = null;*/

    private static ForumLogic forumLogic = AppSpringContext.get(ForumLogic.class);

    @BeforeClass
    public static void setUp() {
/*        velo = Site.forName("velo");

        dev = Site.forName("dev");

        zlo = Site.forName("zlo");

        takeoff = Site.forName("takeoff");

        anime = Site.forName("anime");

//        np = Site.forName("np");
        x = Site.forName("x");

        dolgopa = Site.forName("dolgopa");*/
    }

    @Test
    public void testVelo() throws ForumAccessException {
        final String forumId = "velo";

        int lmn = forumLogic.getLastMessageNumber(forumId);
        System.out.println("lmn: " + lmn);

        Message m = forumLogic.getMessageByNumber(forumId, 19490);
        System.out.println(m);

        assertEquals(19490, m.getNum());
        assertEquals("sim", m.getNick());
        assertTrue(m.isReg());
        assertEquals("gw.zunet.ru", m.getHost());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));

        m = forumLogic.getMessageByNumber(forumId, 19580);
        System.out.println(m);

        assertEquals(19580, m.getNum());
        assertEquals("bull", m.getNick());
        assertTrue(!m.isReg());
        assertEquals("ppp85-140-32-253.pppoe.mtu-net.ru", m.getHost());
        assertTrue(StringUtils.isEmpty(m.getBody()));

        m = forumLogic.getMessageByNumber(forumId, 18869);
        System.out.println(m);

        assertEquals("а многие собирают себе титановые ригиды на ХТЯ.....", m.getTitle());
        assertEquals("Tormentor", m.getNick());
        assertTrue(StringUtils.isEmpty(m.getBody()));
        assertTrue(m.isReg());

        m = forumLogic.getMessageByNumber(forumId, 25597);
        System.out.println(m);
        assertEquals("Велотуризм", m.getTopic());
        assertEquals("Отчет по походу по Карелии, который обещал выложить", m.getTitle());
        assertEquals("timm", m.getNick());
        assertTrue(m.isReg());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));
        assertTrue(MessageLogic.hasUrl(m));
        assertEquals("localhost.localdomain", m.getHost());

        checkMsgNotExists(forumId);
    }

    @Test
    public void testDev() throws ForumAccessException {
        String dev = "dev";
        int lmn = forumLogic.getLastMessageNumber(dev);
        System.out.println(lmn);

        Message m = forumLogic.getMessageByNumber(dev, 9404);

        assertEquals(9404, m.getNum());
        assertEquals("Berk", m.getNick());
        assertEquals("post.mirantis.ru", m.getHost());
        assertTrue(m.isReg());
        assertEquals("Unix/Linux", m.getTopic());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));

        System.out.println(m);

        m = forumLogic.getMessageByNumber(dev, 9374);

        assertEquals(9374, m.getNum());
        assertEquals("arfix.", m.getNick());
        assertTrue(StringUtils.isEmpty(m.getBody()));
        assertTrue(!m.isReg());

        System.out.println(m);

        m = forumLogic.getMessageByNumber(dev, 10153);

        assertEquals(10153, m.getNum());
        assertEquals("Рыбак", m.getNick());
        assertEquals("ppp91-77-164-91.pppoe.mtu-net.ru", m.getHost());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));
        assertTrue(!m.isReg());

        System.out.println(m);
    }

//    @Test
//    dead
    public void testX() throws ForumAccessException {
        String x = "x";
        int lmn = forumLogic.getLastMessageNumber(x);
        System.out.println(lmn);

        Message m = forumLogic.getMessageByNumber(x, 55177);

        assertEquals("uberdude", m.getNick());
        assertEquals("wimax-client.yota.ru", m.getHost());
        assertEquals("Почему?", m.getTitle());
        assertTrue(m.isReg());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));
        System.out.println(m);

        m = forumLogic.getMessageByNumber(x, 55182);

        assertEquals("Митя", m.getNick());
        assertEquals("ip-46-73-158-249.bb.netbynet.ru", m.getHost());
        assertEquals("+ к", m.getTitle());
        assertTrue(m.isReg());
        assertTrue(StringUtils.isEmpty(m.getBody()));
        System.out.println(m);

        m = forumLogic.getMessageByNumber(x, 55207);

        assertEquals("demerzel", m.getNick());
        assertEquals("93.175.15.182", m.getHost());
        assertEquals("+", m.getTitle());
        assertTrue(!m.isReg());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));
        System.out.println(m);

        m = forumLogic.getMessageByNumber(x, 999999999);

        assertEquals(null, m.getNick());
        assertEquals(null, m.getHost());
        assertEquals(null, m.getBody());
        assertEquals(MessageStatus.DELETED, m.getStatus());
        System.out.println(m);
    }

//    @Test
    public void testDolgopa() throws ForumAccessException {
        final String forumId = "dolgopa";

        int lmn = forumLogic.getLastMessageNumber(forumId);
        System.out.println(lmn);

        Message m = forumLogic.getMessageByNumber(forumId, 138080);

        System.out.println(m);
        assertEquals("Rook", m.getNick());
        assertEquals("", m.getHost());
        assertEquals("Не, ну чо тут сказать? Как обычно: Слава ЕР! И ныне и присно и во веки веков!", m.getTitle());
        assertTrue(m.isReg());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));
        assertEquals("И да не кончатся богом данные бюллетени в ее поддержку!", m.getBody());

        m = forumLogic.getMessageByNumber(forumId, 138026);

        System.out.println(m);
        assertEquals("@LuCiFeRsHa@", m.getNick());
        assertEquals("", m.getHost());
        assertEquals("Спасибо!!!!!!!!!!!!!!!!", m.getTitle());
        assertTrue(m.isReg());
        assertTrue(StringUtils.isEmpty(m.getBody()));

        m = forumLogic.getMessageByNumber(forumId, 138034);

        System.out.println(m);
        assertEquals("\\/", m.getNick());
        assertEquals("", m.getHost());
        assertEquals("пожелаем им скорейшего ВЫЗДОРОВЛЕНИЯ, а будут выёживацо-пусть роют себе свой коллектор", m.getTitle());
        assertTrue(!m.isReg());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));

        m = forumLogic.getMessageByNumber(forumId, 138004);

        System.out.println(m);
        assertEquals("FIPS", m.getNick());
        assertEquals("", m.getHost());
        assertEquals("А кинологи в окресностях ближайших есть?", m.getTitle());
        assertEquals("Вопрос", m.getTopic());
        assertTrue(!m.isReg());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));
        assertEquals("С овчаркой позаниматься.", m.getBody());

        m = forumLogic.getMessageByNumber(forumId, 199);
        System.out.println(m);
        assertEquals("<P>Люди кто нить знает что за проводок кинули с Лих 4 на Чайку уж не сетку ли?????", m.getBody());

        checkMsgNotExists(forumId);
    }

    private void checkMsgNotExists(String forumId) throws ForumAccessException {
        final Message m = forumLogic.getMessageByNumber(forumId, 999999999);

        System.out.println(m);

        assertEquals(null, m.getNick());
        assertEquals(null, m.getHost());
        assertEquals(null, m.getBody());
        assertEquals(MessageStatus.DELETED, m.getStatus());
    }

    @Test
    public void testZlo() throws ForumAccessException {
        String zlo = "zlo";
        int lmn = forumLogic.getLastMessageNumber(zlo);
        System.out.println(lmn);

        Message m = forumLogic.getMessageByNumber(zlo, 4093778);

        assertEquals(4093772, m.getParentNum());
        assertEquals("QDiesel", m.getNick());
        assertEquals("nokia.7ka.mipt.ru", m.getHost());
        assertTrue(m.isReg());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));
        System.out.println(m);

        m = forumLogic.getMessageByNumber(zlo, 4093772);
        assertEquals(0, m.getParentNum());
        assertEquals("HEMP", m.getNick());

        m = forumLogic.getMessageByNumber(zlo, 4093788);

        assertEquals(4093785, m.getParentNum());
        assertEquals("Loki", m.getNick());
        assertEquals("loki.3ka.mipt.ru", m.getHost());
        assertTrue(m.isReg());
        assertTrue(StringUtils.isEmpty(m.getBody()));
        System.out.println(m);

        m = forumLogic.getMessageByNumber(zlo, 405573);

        assertEquals(405555, m.getParentNum());
        assertEquals("Demoney", m.getNick());
        assertEquals("morgue.7ka.mipt.ru", m.getHost());
        assertTrue(!m.isReg());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));
        System.out.println(m);

        m = forumLogic.getMessageByNumber(zlo, 7787566);

        assertEquals(7787551, m.getParentNum());
        assertEquals("vilfred", m.getNick());
        assertEquals("77.51.192.172", m.getHost());
        assertTrue(m.isReg());
        assertTrue(MessageLogic.hasImg(m, zlo));
        assertTrue("Сообщения в этом потоке".equals(m.getTitle()));
        System.out.println(m);

        m = forumLogic.getMessageByNumber(zlo, 999999999);

        assertEquals(-10, m.getParentNum());
        assertEquals(null, m.getNick());
        assertEquals(null, m.getHost());
        assertEquals(null, m.getBody());
        assertEquals(MessageStatus.DELETED, m.getStatus());
        System.out.println(m);

        m = forumLogic.getMessageByNumber(zlo, 9338324);
        assertTrue(MessageLogic.hasImg(m, zlo));
        assertEquals(5, HtmlUtils.extractImgUrls(m.getBody(), "zlo.rt.mipt.ru", 5).size());
        assertEquals(64, HtmlUtils.extractImgUrls(m.getBody(), "zlo.rt.mipt.ru", -1).size());
        assertEquals(64, HtmlUtils.extractImgUrls(m.getBody(), "zlo.rt.mipt.ru").size());
        assertEquals(64, HtmlUtils.extractImgUrls(m.getBody(), "zlo.rt.mipt.ru", 100).size());
        assertEquals("http://cs543105.vk.me/v543105862/4b006/nVnMYwGVEso.jpg",
                HtmlUtils.extractImgUrls(m.getBody(), "zlo.rt.mipt.ru", 100).get(2));
        assertEquals("http://cs7001.vk.me/v7001128/23567/HQpKymOJ9A8.jpg",
                HtmlUtils.extractImgUrls(m.getBody(), "zlo.rt.mipt.ru", 100).get(63));
    }

//    @Test
//    engine changed
    public void testTakeoff() throws ForumAccessException {
        String takeoff = "takeoff";
        int lmn = forumLogic.getLastMessageNumber(takeoff);

        System.out.println(lmn);

        Message m = forumLogic.getMessageByNumber(takeoff, 13996);

        assertEquals("Слава", m.getNick());
        assertEquals("gluk.2ka.mipt.ru", m.getHost());
        assertTrue(m.isReg());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));

        System.out.println(m);

        m = forumLogic.getMessageByNumber(takeoff, 14003);

        assertEquals(14003, m.getNum());
        assertEquals("mitrich", m.getNick());
        assertEquals("83.229.152.73", m.getHost());
        assertTrue(m.isReg());
        assertTrue(StringUtils.isEmpty(m.getBody()));

        System.out.println(m);

        m = forumLogic.getMessageByNumber(takeoff, 1729);

        assertEquals(1729, m.getNum());
        assertEquals("shpagin&stalker", m.getNick());
        assertEquals("stalker.4ka.mipt.ru", m.getHost());
        assertTrue(!m.isReg());
        assertTrue(StringUtils.isNotEmpty(m.getBody()));

        System.out.println(m);
    }

    @Test
    public void testAnime() throws ForumAccessException {
        String anime = "anime";
        int lmn = forumLogic.getLastMessageNumber(anime);

        System.out.println(lmn);

        Message m = forumLogic.getMessageByNumber(anime, 16825);
        System.out.println(m);

        assertTrue(m.isReg());
        assertEquals("bestation", m.getNick());
        assertEquals("10.55.110.140", m.getHost());
        assertTrue(!MessageLogic.hasImg(m, anime));
        assertTrue(!MessageLogic.hasUrl(m));
        assertEquals("В качестве бонуса могу выдать батч скаченный на 54.2% с полностью скаченной первой серией, оп и ед.", m.getBody());
        assertEquals("Ну, что? Кто в локалке возмется кланнад 1-5 скачать?", m.getTitle());

        m = forumLogic.getMessageByNumber(anime, 16376);
        System.out.println(m);

        assertTrue(MessageLogic.hasImg(m, anime));
        assertTrue(MessageLogic.hasUrl(m));
        assertTrue(m.isReg());
        assertEquals("fth", m.getNick());
        assertEquals("10.55.103.181", m.getHost());
        assertEquals("С наступающим Новым Годом!", m.getTitle());

        m = forumLogic.getMessageByNumber(anime, 16799);
        System.out.println(m);

        Assert.assertFalse(m.isReg());
        assertEquals("zuzzik_", m.getNick());
        assertEquals("Фотки и отчет будут? Ж)", m.getTitle());

        m = forumLogic.getMessageByNumber(anime, 2);
        System.out.println(m);
        Assert.assertFalse(m.isOk());
        assertEquals(MessageStatus.DELETED, m.getStatus());
    }

//    @Test

    public void testNp() throws ForumAccessException {
        String np = "np";
        int lmn = forumLogic.getLastMessageNumber(np);
        System.out.println(lmn);

        Message m = forumLogic.getMessageByNumber(np, 96119);
        System.out.println(m);

        assertEquals("там сочинение на страницу или больше", m.getTitle());
        assertEquals("а я почему-то могу писать или писать только иногда, когда настроение", m.getBody());
        assertEquals("без темы", m.getTopic());

        m = forumLogic.getMessageByNumber(np, 95933);
        System.out.println(m);

        assertEquals("проверим", m.getTitle());
        assertEquals("Кто здесь?", m.getTopic());
        assertEquals("", m.getBody());
    }
}
