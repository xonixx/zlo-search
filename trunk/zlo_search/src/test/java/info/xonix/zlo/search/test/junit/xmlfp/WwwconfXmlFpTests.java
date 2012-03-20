package info.xonix.zlo.search.test.junit.xmlfp;

import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.xmlfp.ForumAccessor;
import info.xonix.zlo.search.xmlfp.XmlFpException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * User: gubarkov
 * Date: 20.03.12
 * Time: 14:50
 */
public class WwwconfXmlFpTests {
    private ForumAccessor forumAccessor;
    public static final int N = 10;

    @Before
    public void setup() throws XmlFpException {
        forumAccessor = ForumAccessor.fromDescriptorUrl(
                "http://votalka.campus.mipt.ru/?xmlfp");
    }

    @Test
    public void test_descriptor() throws XmlFpException {
        assertEquals("http://votalka.campus.mipt.ru/?index", forumAccessor.getForumUrl());
        assertEquals("свободное™ общение", forumAccessor.getTitle());
        assertEquals("диктатура свободного™ общения без модерации", forumAccessor.getDescription());
        assertEquals(100, forumAccessor.getMessageListMaxDelta());
    }

    @Test
    public void test_list_last_N_msgs() throws XmlFpException {
        final long lastMessageNumber = forumAccessor.getLastMessageNumber();

        for (long i = 0; i < N; i++) {
            System.out.println(forumAccessor.getMessage(lastMessageNumber - i));
        }
    }

    @Test
    public void test_list_last_N_msgs_bunch() throws XmlFpException {
        final long lastMessageNumber = forumAccessor.getLastMessageNumber();

        final List<Message> messageList = forumAccessor.getMessageList(lastMessageNumber - N + 1, lastMessageNumber);
        assertEquals(N, messageList.size());

        for (Message message : messageList) {
            System.out.println(message);
        }
    }
}
