package info.xonix.zlo.search.xmlfp.test;

import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.model.Site;
import info.xonix.zlo.search.model.ZloMessage;
import info.xonix.zlo.search.xmlfp.ZloJaxb;
import org.junit.Test;

/**
 * Author: Vovan
 * Date: 13.08.2008
 * Time: 20:09:14
 */
public class TestMessageToXML {

    @Test
    public void test1() throws DAOException {
        Site s = Site.forName("zlo");
        ZloMessage m = s.getDB().getMessageByNumber(3333333);
        System.out.println(m);
        System.out.println("======================");
        System.out.println(ZloJaxb.zloMessageToXml(m));
    }
}
