package info.xonix.zlo.search.xmlfp.test;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.spring.AppSpringContext;
import info.xonix.zlo.search.xmlfp.ZloJaxb;
import org.junit.Test;

/**
 * Author: Vovan
 * Date: 13.08.2008
 * Time: 20:09:14
 */
public class TestMessageToXML {

    AppLogic appLogic = AppSpringContext.get(AppLogic.class);

    @Test
    public void test1() {
        Site site = Site.forName("zlo");
        Message m = appLogic.getMessageByNumber(site, 3333333);
        System.out.println(m);
        System.out.println("======================");
        System.out.println(ZloJaxb.zloMessageToXml(m));
    }
}
