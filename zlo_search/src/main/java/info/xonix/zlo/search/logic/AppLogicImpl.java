package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.dao.DbDict;
import info.xonix.zlo.search.dao.DbDictFields;
import info.xonix.zlo.search.dao.MessagesDao;
import info.xonix.zlo.search.model.Message;
import info.xonix.utils.Check;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;

import java.util.Date;
import java.util.List;

/**
 * User: Vovan
 * Date: 21.06.2010
 * Time: 0:23:00
 */
public class AppLogicImpl implements AppLogic, InitializingBean {
    private final static Logger log = Logger.getLogger(AppLogicImpl.class);

    private DbDict dbDict;
    private MessagesDao messagesDao;

    public void setDbDict(DbDict dbDict) {
        this.dbDict = dbDict;
    }

    public void setMessagesDao(MessagesDao messagesDao) {
        this.messagesDao = messagesDao;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Check.isSet(dbDict, "dbDict");
        Check.isSet(messagesDao, "messagesDao");
    }

    @Override
    public void setLastIndexedNumber(String forumId, int num) {
        dbDict.setInt(forumId, DbDictFields.DB_DICT_LAST_INDEXED_DOUBLE, num);
        dbDict.setDate(forumId, DbDictFields.DB_DICT_LAST_INDEXED_DOUBLE_DATE, new Date());
    }

    @Override
    public int getLastIndexedNumber(String forumId) {
        return dbDict.getInt(forumId, DbDictFields.DB_DICT_LAST_INDEXED_DOUBLE, 0);
    }

    @Override
    public Date getLastIndexedDate(String forumId) {
        return dbDict.getDate(forumId, DbDictFields.DB_DICT_LAST_INDEXED_DOUBLE_DATE, new Date(0));
    }

    @Override
    public void setLastSavedDate(String forumId, Date d) {
        dbDict.setDate(forumId, DbDictFields.DB_DICT_LAST_SAVED_DATE, d);
    }

    @Override
    public Date getLastSavedDate(String forumId) {
        return dbDict.getDate(forumId, DbDictFields.DB_DICT_LAST_SAVED_DATE, new Date(0));
    }

    // messages

    @Override
    public void saveMessages(String forumId, List<Message> msgs) {
        log.info(forumId + " - Saving (" + msgs.get(0).getNum() + " - " + msgs.get(msgs.size() - 1).getNum() + ") msgs to DB...");
        messagesDao.saveMessagesFast(forumId, msgs);
        log.info(forumId + " - Successfully saved " + msgs.size() + " msgs to DB.");
    }

    @Override
    public Message getMessageByNumber(String forumId, int num) {
        return messagesDao.getMessageByNumber(forumId, num);
    }

    @Override
    public List<Message> getMessages(String forumId, int start, int end) {
        return messagesDao.getMessagesByRange(forumId, start, end);
    }

    @Override
    public int getLastSavedMessageNumber(String forumId) {
        return messagesDao.getLastMessageNumber(forumId);
    }

    @Override
    public void saveSearchTextForAutocomplete(String forumId, String text) {
        if (StringUtils.isEmpty(text)) {
            throw new IllegalArgumentException("text is empty");
        }

        messagesDao.saveSearchTextForAutocomplete(forumId, text.trim().toLowerCase());
    }

    @Override
    public List<String> autoCompleteText(String forumId, String text, int limit) {
        return messagesDao.autoCompleteText(forumId, text, limit);
    }
}
