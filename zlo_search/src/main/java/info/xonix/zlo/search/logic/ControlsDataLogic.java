package info.xonix.zlo.search.logic;

import java.util.Map;

/**
 * User: gubarkov
 * Date: 24.03.12
 * Time: 21:23
 */
public interface ControlsDataLogic {

    Map<Integer, String> getTopics(String forumId);

    Map<String, Integer> getTopicsReversedMap(String forumId);

    int addNewTopic(String forumId, String topic);
}
