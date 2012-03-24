package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.config.Config;
import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.logic.forum_adapters.ForumAccessException;
import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.utils.Check;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * User: Vovan
 * Date: 12.06.2010
 * Time: 22:00:33
 */
public class ForumLogicImpl implements ForumLogic, InitializingBean {
    private static final Logger log = Logger.getLogger(ForumLogicImpl.class);

    @Autowired
    private Config config;

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(config, "config");
    }

    @Override
    public Message getMessageByNumber(String forumId, int num) throws ForumAccessException {
        log.debug(forumId + " - Receiving from site: " + num);

        return GetForum.adapter(forumId).getMessage(forumId, num);
    }

    @Override
    public List<Message> getMessages(String forumId, int from, int to) throws ForumAccessException {
        log.info(forumId + " - Downloading messages from " + from + " to " + to + "...");
        long begin = System.currentTimeMillis();

        List<Message> msgs = GetForum.adapter(forumId).getMessages(forumId, (long) from, (long) to);

        float durationSecs = (System.currentTimeMillis() - begin) / 1000f;
        log.info(forumId + " - Downloaded " + msgs.size() + " messages in " + (int) durationSecs + "secs. Rate: " + ((float) msgs.size()) / durationSecs + "mps.");

        return msgs;
    }

    @Override
    public int getLastMessageNumber(String forumId) throws ForumAccessException {
        return (int) GetForum.adapter(forumId).getLastMessageNumber(forumId);
    }
}
