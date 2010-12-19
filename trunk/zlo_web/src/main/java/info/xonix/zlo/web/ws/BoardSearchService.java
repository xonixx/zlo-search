package info.xonix.zlo.web.ws;

import info.xonix.zlo.search.dao.MessagesDao;
import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.logic.SearchException;
import info.xonix.zlo.search.logic.SearchLogic;
import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.web.ws.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.jws.WebMethod;
import javax.jws.WebService;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Vovan
 * Date: 12.12.10
 * Time: 18:53
 */
@WebService
public class BoardSearchService {

    @Autowired
    private AppLogic appLogic;

    @Autowired
    private SiteLogic siteLogic;

    @Autowired
    private SearchLogic searchLogic;

    @Autowired
    private MessagesDao messagesDao;

    @Nonnull
    private Site site(int siteId) throws ServiceException {
        final Site site = siteLogic.getSite(siteId);
        if (site == null) {
            throw new ServiceException("Wrong siteId: " + siteId);
        }
        return site;
    }

    private Message fromMessageModel(info.xonix.zlo.search.model.Message messageModel) {
        return new Message(
                messageModel.getNum(),
                messageModel.getNick(),
                messageModel.getHost(),
                messageModel.isReg(),
                messageModel.getTopic(),
                messageModel.getTitle(),
                messageModel.getBody(),
                messageModel.getDate(),
                messageModel.isHasUrl(),
                messageModel.isHasImg());
    }

    @WebMethod
    public int getLastSavedMsgNumber(int siteId) throws ServiceException {
        return appLogic.getLastSavedMessageNumber(site(siteId));
    }

    @WebMethod
    public int getLastIndexedMsgNumber(int siteId) throws ServiceException {
        return appLogic.getLastIndexedNumber(site(siteId));
    }

    @WebMethod
    public Message getMessage(int siteId, int msgId) throws ServiceException {
        return fromMessageModel(appLogic.getMessageByNumber(site(siteId), msgId));
    }

    @WebMethod
    public List<Message> search(int siteId, String searchString, int limit) throws ServiceException {
        final Site site = site(siteId);

        final int[] resultIds;

        try {
            resultIds = searchLogic.search(site, searchString, limit);
        } catch (SearchException e) {
            throw new ServiceException("Search error", e);
        }

        final List<info.xonix.zlo.search.model.Message> messages = messagesDao.getMessages(site, resultIds);

        List<Message> resultMessages = new ArrayList<Message>(messages.size());

        for (info.xonix.zlo.search.model.Message message : messages) {
            resultMessages.add(fromMessageModel(message));
        }

        return resultMessages;
    }
}
