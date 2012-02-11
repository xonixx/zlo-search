package info.xonix.zlo.search.xmlfp.test;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.xmlfp.Convert;
import info.xonix.zlo.search.xmlfp.XmlFp;
import info.xonix.zlo.search.xmlfp.utils.MarshalUtils;
import info.xonix.zlo.search.xmlfp.utils.XmlFpMarshalException;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.FileReader;
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
    public void test1() {
        Site site = Site.forName("zlo");
        Message m = appLogic.getMessageByNumber(site, 3333333);
        System.out.println(m);
        System.out.println("======================");
        System.out.println(xmlFp.messageToXml(m));
    }

    @Test
    public void test2() throws IOException, JAXBException, XmlFpMarshalException {
        String path = "D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\m1.xml";

        final String msgXmlStr = IOUtils.toString(new FileReader(path));

        JAXBContext jaxbContext = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.xjccompiled.message");
        final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

        final info.xonix.zlo.search.xmlfp.xjccompiled.message.Message jaxbMessage = MarshalUtils.unmarshal(unmarshaller, msgXmlStr);
        System.out.println(Convert.fromJaxbMessage(jaxbMessage));
    }
}
