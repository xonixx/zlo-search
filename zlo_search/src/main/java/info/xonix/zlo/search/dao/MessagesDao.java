package info.xonix.zlo.search.dao;

import info.xonix.zlo.search.domain.Message;
import info.xonix.zlo.search.domain.MessageShallow;
import info.xonix.zlo.search.domain.Topic;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

/**
 * User: Vovan
 * Date: 13.06.2010
 * Time: 1:19:55
 */
public interface MessagesDao {
    void saveMessagesFast(String forumId, List<Message> msgs);

    void saveMessagesFast(String forumId, List<Message> msgs, boolean updateIfExists);

    @Nullable
    Message getMessageByNumber(String forumId, int num);

    List<Message> getMessagesByRange(String forumId, int start, int end);

    List<Message> getMessages(String forumId, int[] nums);
    
    List<Message> getChildMessages(String forumId, long messageId);

    List<MessageShallow> getShallowMessages(String forumId, int[] nums);

    int getLastMessageNumber(String forumId);

    List<Topic> getTopicList(String forumId);

    void saveSearchTextForAutocomplete(String forumId, String text);

    List<String> autoCompleteText(String forumId, String text, int limit);

    int insertTopic(String forumId, String topic);

    List<Date> getMessageDatesByNicks(String forumId, List<String> nicks, Date start, Date end);

    List<Date> getMessageDatesByIds(String forumId, List<Long> ids, Date start, Date end);

    List<Integer> getMessageIdsByMissingParent(String forumId, int parentId, int limit);

    void updateParent(String forumId, int msgId, int parentId);
}
