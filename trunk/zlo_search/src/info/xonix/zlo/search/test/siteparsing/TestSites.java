package info.xonix.zlo.search.test.siteparsing;

import info.xonix.zlo.search.dao.DAOException;
import info.xonix.zlo.search.dao.Site;
import info.xonix.zlo.search.model.ZloMessage;
import junit.framework.Assert;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Author: Vovan
 * Date: 22.03.2008
 * Time: 20:17:05
 */
public class TestSites {

    Site velo;

    @Before
    public void setUp() {
        velo = Site.forName("velo");
        velo.setDB_VIA_CONTAINER(false);
    }

    @Test
    public void testVelo() {
        try {
            int lmn = velo.getLastMessageNumber();
            System.out.println("lmn: " + lmn);

            ZloMessage m = velo.getMessage(19490);

            System.out.println(m);

            Assert.assertEquals(19490, m.getNum());
            Assert.assertEquals("sim", m.getNick());
            Assert.assertTrue(m.isReg());
            Assert.assertEquals("gw.zunet.ru", m.getHost());
            Assert.assertTrue(StringUtils.isNotEmpty(m.getBody()));

            m = velo.getMessage(19580);

            System.out.println(m);

            Assert.assertEquals(19580, m.getNum());
            Assert.assertEquals("bull", m.getNick());
            Assert.assertTrue(!m.isReg());
            Assert.assertEquals("ppp85-140-32-253.pppoe.mtu-net.ru", m.getHost());
            Assert.assertTrue(StringUtils.isEmpty(m.getBody()));

            m = velo.getMessage(18869);

            System.out.println(m);

            Assert.assertTrue(StringUtils.isEmpty(m.getBody()));
            Assert.assertTrue(m.isReg());

            System.out.println(velo.getMessage(33));


        } catch (DAOException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
