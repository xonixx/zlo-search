package info.xonix.zlo.search.logic;

import info.xonix.utils.factory.StringFactory;
import info.xonix.zlo.search.dao.MessagesDao;
import info.xonix.zlo.search.model.Topic;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * User: gubarkov
 * Date: 24.03.12
 * Time: 21:23
 */
public class ControlsDataLogicImpl implements ControlsDataLogic {
    @Autowired
    private MessagesDao messagesDao;

    private StringFactory<Map<Integer, String>> topicsMapFactory = new StringFactory<Map<Integer, String>>() {
        @Override
        protected Map<Integer, String> create(String forumId) {
            List<Topic> topicList = messagesDao.getTopicList(forumId);
            Map<Integer, String> topics = new LinkedHashMap<Integer, String>();

            for (Topic topic : topicList) {
                topics.put(topic.getId(), topic.getName());
            }

            return topics;
        }
    };

    private StringFactory<Map<String, Integer>> topicsReverseMapFactory = new StringFactory<Map<String, Integer>>() {
        @Override
        protected Map<String, Integer> create(String forumId) {
            List<Topic> topicList = messagesDao.getTopicList(forumId);
            Map<String, Integer> topicsHashMap = new HashMap<String, Integer>();

            for (Topic topic : topicList) {
                topicsHashMap.put(topic.getName(), topic.getId());
            }

            return topicsHashMap;
        }
    };

    @Override
    public Map<Integer, String> getTopics(String forumId) {
        return topicsMapFactory.get(forumId);
    }

    @Override
    public Map<String, Integer> getTopicsReversedMap(String forumId) {
        return topicsReverseMapFactory.get(forumId);
    }

    @Override
    public int addNewTopic(String forumId, String topic) {
        // insert
        int id = messagesDao.insertTopic(forumId, topic);

        // invalidate caches
        topicsMapFactory.reset(forumId);
        topicsReverseMapFactory.reset(forumId);

        return id;
    }
}
