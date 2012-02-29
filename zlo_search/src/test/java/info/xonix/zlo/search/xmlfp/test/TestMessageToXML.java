package info.xonix.zlo.search.xmlfp.test;

import info.xonix.zlo.search.config.Config;

import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.xmlfp.*;
import org.apache.commons.io.FileUtils;
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

    @Test
    public void test1() throws XmlFpException {
        String forumId = Site.forName("zlo");
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
    public void test2() throws IOException, XmlFpException {
        String path = "D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\m1.xml";

        final String msgXmlStr = FileUtils.readFileToString(new File(path), Config.UTF_8);

        System.out.println(XmlFpUtils.messageFromXml(msgXmlStr));
    }

    @Test
    public void test3() throws IOException, XmlFpException {
        System.out.println("Last num=");
        System.out.println(XmlFpUtils.lastMessageNumberFromXml(
                FileUtils.readFileToString(new File("D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\lastNum1.xml"), Config.UTF_8)
        ));
    }

    @Test
    public void test4() throws XmlFpException {
        final XmlFpUrls xmlFpUrls = new XmlFpUrls(
                "http://localhost:8080/xmlfp/xmlfp.jsp?xmlfp=lastMessageNumber&site=0",
                "http://localhost:8080/xmlfp/xmlfp.jsp?xmlfp=message&site=0&num=" + XmlFpUrlsSubstitutions.MESSAGE_ID);

        ForumAccessor forumAccessor = new ForumAccessor(xmlFpUrls);

        final long lastMessageNumber = forumAccessor.getLastMessageNumber();

        System.out.println("Last num: " + lastMessageNumber);
        System.out.println("Msg: " + forumAccessor.getMessage(lastMessageNumber));
    }

    @Test
    public void test5() throws XmlFpException {
        ForumAccessor forumAccessor = new ForumAccessor("http://localhost:8080/xmlfp/xmlfp.jsp?xmlfp=descriptor");

        final long lastMessageNumber = forumAccessor.getLastMessageNumber();

        System.out.println("Last num: " + lastMessageNumber);
        System.out.println("Msg: " + forumAccessor.getMessage(lastMessageNumber));
    }
}
