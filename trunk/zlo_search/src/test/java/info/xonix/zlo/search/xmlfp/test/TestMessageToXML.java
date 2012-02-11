package info.xonix.zlo.search.xmlfp.test;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.xmlfp.XmlFp;
import info.xonix.zlo.search.xmlfp.XmlFpException;
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
    XmlFp xmlFp = AppSpringContext.get(XmlFp.class);

    @Test
    public void test1() throws XmlFpException {
        Site site = Site.forName("zlo");
        Message m = appLogic.getMessageByNumber(site, 3333333);
        System.out.println(m);

        System.out.println("======================");
        final String xml = xmlFp.messageToXml(m);
        System.out.println(xml);

        System.out.println("======================");
        System.out.println("Now back...");
        System.out.println(xmlFp.messageFromXml(xml));

        System.out.println("======================");
    }

    @Test
    public void test2() throws IOException, XmlFpException {
        String path = "D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\m1.xml";

        final String msgXmlStr = FileUtils.readFileToString(new File(path), Config.UTF_8);

        System.out.println(xmlFp.messageFromXml(msgXmlStr));
    }
}
