package info.xonix.zlo.search.test.junit.xmlfp;

import info.xonix.forumsearch.xmlfp.XmlFpException;
import info.xonix.forumsearch.xmlfp.XmlFpFormatterOptions;
import info.xonix.forumsearch.xmlfp.jaxb_generated.Forum;
import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.domain.Message;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.xmlfp.XmlFpUtils;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Author: Vovan
 * Date: 13.08.2008
 * Time: 20:09:14
 */
public class TestMessageToXML {

    AppLogic appLogic = AppSpringContext.get(AppLogic.class);

    static {
        new XmlFpFormatterOptions("UTF-8", true);
    }

    @Test
    public void test1() throws XmlFpException {
        String forumId = "zlo";
        Message m = appLogic.getMessageByNumber(forumId, 3333333);
        System.out.println(m);

        System.out.println("======================");
        final String xml = XmlFpUtils.messageToXml(forumId, m);
        System.out.println(xml);

        System.out.println("======================");
        System.out.println("Now back...");
        System.out.println(XmlFpUtils.messageFromXml(xml));

        System.out.println("======================");
    }

    @Test
    public void test_m() throws IOException, XmlFpException {
        String path = "D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\m1.xml";

        final String msgXmlStr = FileUtils.readFileToString(new File(path), Config.UTF_8);

        System.out.println(XmlFpUtils.messageFromXml(msgXmlStr));
    }

    @Test
    public void test_m_nl() throws IOException, XmlFpException {
        String path = "D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\m1_nl.xml";

        final String msgXmlStr = FileUtils.readFileToString(new File(path), Config.UTF_8);

        final Message message = XmlFpUtils.messageFromXml(msgXmlStr);
        System.out.println(message.getBody());
        Assert.assertTrue(message.getBody().indexOf('\n') > 0);
    }

    @Test(expected = XmlFpException.class)
    public void test_m_err1() throws IOException, XmlFpException {
        String path = "D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\m_err1.xml";

        final String msgXmlStr = FileUtils.readFileToString(new File(path), Config.UTF_8);

        System.out.println(XmlFpUtils.messageFromXml(msgXmlStr));
    }

    @Test(expected = XmlFpException.class)
    public void test_m_err2() throws IOException, XmlFpException {
        String path = "D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\m_err2.xml";

        final String msgXmlStr = FileUtils.readFileToString(new File(path), Config.UTF_8);

        System.out.println(XmlFpUtils.messageFromXml(msgXmlStr));
    }

    @Test
    public void test_descr() throws IOException, XmlFpException {
        String path = "D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\descriptor2.xml";

        final String descrXmlStr = FileUtils.readFileToString(new File(path), Config.UTF_8);

        final Forum forum = XmlFpUtils.descriptorFromXml(descrXmlStr);
        Assert.assertEquals(1000, forum.getXmlfpUrls().getMessageListUrl().getMaxCount());
    }

    @Test
    public void test3() throws IOException, XmlFpException {
        System.out.println("Last num=");
        System.out.println(XmlFpUtils.lastMessageNumberFromXml(
                FileUtils.readFileToString(new File("D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\lastNum1.xml"), Config.UTF_8)
        ));
    }
}
