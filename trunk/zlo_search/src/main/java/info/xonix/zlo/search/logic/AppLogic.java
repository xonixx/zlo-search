package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.model.Message;

import java.util.Date;
import java.util.List;

/**
 * User: Vovan
 * Date: 21.06.2010
 * Time: 0:22:50
 */
public interface AppLogic {
    void setLastIndexedNumber(String forumId, int num);

    int getLastIndexedNumber(String forumId);

    Date getLastIndexedDate(String forumId);

    void setLastSavedDate(String forumId, Date d);

    Date getLastSavedDate(String forumId);

    void saveMessages(String forumId, List<Message> msgs);

    /**
     * get message, saved in DB
     *
     * @param forumId forumId
     * @param num  message id
     * @return message obj
     */
    Message getMessageByNumber(String forumId, int num);

    /**
     * get messages, saved in DB for site in given range
     *
     * @param forumId forumId
     * @param start start index
     * @param end   end index
     * @return result messages
     */
    List<Message> getMessages(String forumId, int start, int end);

    int getLastSavedMessageNumber(String forumId);

    void saveSearchTextForAutocomplete(String forumId, String text);

    List<String> autoCompleteText(String forumId, String text, int limit);
}
