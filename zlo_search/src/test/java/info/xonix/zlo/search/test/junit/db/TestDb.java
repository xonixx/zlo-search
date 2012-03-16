package info.xonix.zlo.search.test.junit.db;

import info.xonix.zlo.search.dao.DbDict;
import info.xonix.zlo.search.dao.MessagesDao;
import info.xonix.zlo.search.spring.AppSpringContext;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * User: gubarkov
 * Date: 16.03.12
 * Time: 16:06
 */
public class TestDb {
    private MessagesDao messagesDao;
    private DbDict dbDict;
    private String forumId;

    @Before
    public void setUp() throws Exception {
//        site = Site.forName("zlo");
        forumId = "zlo";
        messagesDao = AppSpringContext.get(MessagesDao.class);
        dbDict = AppSpringContext.get(DbDict.class);
    }
    @Test
    public void testGetTopics() {
        String[] topics = messagesDao.getTopics(forumId);
        assertEquals("без темы", topics[0]);
        assertEquals("Учеба", topics[1]);
        assertEquals("Работа", topics[2]);
        assertEquals("Temp", topics[18]);
        assertEquals(20, topics.length);
    }

    @Test
    public void testDbDict() {
        dbDict.setInt(forumId, "name1", null);
        assertEquals(null, dbDict.getInt(forumId, "name1"));
        dbDict.setStr(forumId, "s", "Hello");
        assertEquals("Hello", dbDict.getStr(forumId, "s"));
        Date d = new Date();
        dbDict.setDate(forumId, "d", d);
        Date dd = dbDict.getDate(forumId, "d");
        System.out.println(d + " " + dd + " " + d.getTime() + " " + dd.getTime());
        assertEquals(true, d.getTime() - dd.getTime() < 1000);

        dbDict.remove(forumId, "name1");
        dbDict.remove(forumId, "s");
        dbDict.remove(forumId, "d");
        assertEquals(null, dbDict.getStr(forumId, "s"));
    }
}
