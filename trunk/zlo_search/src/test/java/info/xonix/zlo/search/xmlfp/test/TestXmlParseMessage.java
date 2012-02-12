package info.xonix.zlo.search.xmlfp.test;

import info.xonix.zlo.search.xmlfp.jaxb_generated.Message;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Author: Vovan
 * Date: 11.08.2008
 * Time: 23:34:17
 */
public class TestXmlParseMessage {
    public static void main(String[] args) {
        try {
            JAXBContext jc = JAXBContext.newInstance("info.xonix.zlo.search.xmlfp.jaxb_generated");
            Unmarshaller unmarshaller = jc.createUnmarshaller();
            Marshaller marshaller = jc.createMarshaller();
            Message msg = (Message) unmarshaller.unmarshal(new File("D:\\stuff\\test\\java\\zlo-search\\zlo_search\\forum_xml_protocol\\tst\\m1.xml"));
            System.out.println(msg.getAuthor().getName());
            System.out.println(msg.getContent().getBody());
            System.out.println(msg.getContent().getTags().getTag().get(0));
            msg.getContent().setTitle("Тест... Тест.. Тест");
            msg.getContent().setBody("<h1>HELLO\n</h1>");
            System.out.println("==========================");
            marshaller.setProperty("jaxb.encoding", "UTF-8");
            marshaller.marshal(msg, System.out);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
}
