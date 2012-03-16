package info.xonix.zlo.web.ws;

import info.xonix.zlo.search.config.forums.GetForum;
import info.xonix.zlo.search.dao.MessagesDao;

import info.xonix.zlo.search.logic.*;
import info.xonix.zlo.web.ws.dto.Message;
import info.xonix.zlo.web.ws.dto.MessageShallow;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vovan
 * Date: 12.12.10
 * Time: 18:53
 */
@WebService
@SuppressWarnings("unused")
public class BoardSearchService {
    private static final Logger log = Logger.getLogger(BoardSearchService.class);

    @Autowired
    private AppLogic appLogic;

    @Autowired
    private SiteLogic siteLogic;

    @Autowired
    private SearchLogic searchLogic;

    @Autowired
    private MessagesDao messagesDao;

    @Nonnull
    private String forumId(int siteId) throws ServiceException {
        final String forumId = GetForum.descriptor(siteId).getForumId();// TODO: <<<--- fix!!!
        if (forumId == null) {
            throw new ServiceException("Wrong siteId: " + siteId);
        }
        return forumId;
    }

    private Message fromMessageModel(String forumId, info.xonix.zlo.search.model.Message messageModel) {
        return new Message(
                messageModel.getNum(),
                messageModel.getNick(),
                messageModel.getHost(),
                messageModel.isReg(),
                messageModel.getTopic(),
                messageModel.getTitle(),
                messageModel.getBody(),
                messageModel.getDate(),
                MessageLogic.hasUrl(messageModel),
                MessageLogic.hasImg(messageModel,forumId));
    }

    private MessageShallow fromMessageModelShallow(info.xonix.zlo.search.model.MessageShallow message) {
        return new MessageShallow(
                message.getNum(),
                message.getNick(),
                message.getHost(),
                message.isReg(),
                message.getDate(),
                message.getTopic(),
                message.getTitle());
    }

    @WebMethod
    public int getLastSavedMsgNumber(@WebParam(name = "siteId") int siteId) throws ServiceException {
        log.info("getLastSavedMsgNumber(" + siteId + ")");

        return appLogic.getLastSavedMessageNumber(forumId(siteId));
    }

    @WebMethod
    public int getLastIndexedMsgNumber(@WebParam(name = "siteId") int siteId) throws ServiceException {
        log.info("getLastIndexedMsgNumber(" + siteId + ")");

        return appLogic.getLastIndexedNumber(forumId(siteId));
    }

    @WebMethod
    public Message getMessage(
            @WebParam(name = "siteId") int siteId,
            @WebParam(name = "msgId") int msgId) throws ServiceException {
        log.info("getMessage(" + siteId + ", " + msgId + ")");

        final String forumId = forumId(siteId);
        return fromMessageModel(forumId, appLogic.getMessageByNumber(forumId, msgId));
    }

    @WebMethod
    public List<Message> search(
            @WebParam(name = "siteId") int siteId,
            @WebParam(name = "searchString") String searchString,
            @WebParam(name = "skip") int skip,
            @WebParam(name = "limit") int limit) throws ServiceException {
        log.info("search(" + siteId + ", \"" + searchString + "\", " + skip + ", " + limit + ")");

        final String forumId = forumId(siteId);

        final int[] resultIds = search(forumId, searchString, skip, limit);

        final List<info.xonix.zlo.search.model.Message> messages = messagesDao.getMessages(forumId, resultIds);

        List<Message> resultMessages = new ArrayList<Message>(messages.size());

        for (info.xonix.zlo.search.model.Message message : messages) {
            resultMessages.add(fromMessageModel(forumId, message));
        }

        return resultMessages;
    }

    @WebMethod
    public List<MessageShallow> searchShallow(
            @WebParam(name = "siteId") int siteId,
            @WebParam(name = "searchString") String searchString,
            @WebParam(name = "skip") int skip,
            @WebParam(name = "limit") int limit) throws ServiceException {
        log.info("searchShallow(" + siteId + ", \"" + searchString + "\", " + skip + ", " + limit + ")");

        final String forumId = forumId(siteId);

        final int[] resultIds = search(forumId, searchString, skip, limit);

        final List<info.xonix.zlo.search.model.MessageShallow> messages = messagesDao.getShallowMessages(forumId, resultIds);

        List<MessageShallow> resultMessages = new ArrayList<MessageShallow>(messages.size());

        for (info.xonix.zlo.search.model.MessageShallow message : messages) {
            resultMessages.add(fromMessageModelShallow(message));
        }

        return resultMessages;
    }

    private int[] search(String forumId, String searchString, int skip, int limit) throws ServiceException {
        final int[] resultIds;

        try {
            resultIds = searchLogic.search(forumId, searchString, skip, limit);
        } catch (SearchException e) {
            throw new ServiceException("Search error:" + e, e);
        }

        return resultIds;
    }
}
