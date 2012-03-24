package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.model.Message;
import info.xonix.zlo.search.model.MessageShallow;
import info.xonix.zlo.search.model.Topic;

import java.util.List;

/**
 * User: Vovan
 * Date: 13.06.2010
 * Time: 1:19:55
 */
public interface MessagesDao {
    void saveMessagesFast(String forumId, List<Message> msgs);

    void saveMessagesFast(String forumId, List<Message> msgs, boolean updateIfExists);

    Message getMessageByNumber(String forumId, int num);

    List<Message> getMessagesByRange(String forumId, int start, int end);

    List<Message> getMessages(String forumId, int[] nums);

    List<MessageShallow> getShallowMessages(String forumId, int[] nums);

    int getLastMessageNumber(String forumId);

    List<Topic> getTopicList(String forumId);

    void saveSearchTextForAutocomplete(String forumId, String text);

    List<String> autoCompleteText(String forumId, String text, int limit);

    int insertTopic(String forumId, String topic);
}
