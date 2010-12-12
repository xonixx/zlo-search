package info.xonix.zlo.web.ws;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.logic.SiteLogic;
import info.xonix.zlo.web.ws.dto.Message;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nullable;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * User: Vovan
 * Date: 12.12.10
 * Time: 18:53
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC, use = SOAPBinding.Use.LITERAL)
public class BoardSearchService {

    @Autowired
    private AppLogic appLogic;

    @Autowired
    private SiteLogic siteLogic;

    @Nullable
    private Site site(int siteId) {
        return siteLogic.getSite(siteId);
    }

    @WebMethod
    public int getLastSavedMsgNumber(int siteId) {
        final Site site = site(siteId);
        if (site != null) {
            return appLogic.getLastSavedMessageNumber(site);
        } else {
            return -1;
        }
    }

    @WebMethod
    public int getLastIndexedMsgNumber(int siteId) {
        final Site site = site(siteId);
        if (site != null) {
            return appLogic.getLastIndexedNumber(site);
        } else {
            return -1;
        }
    }

    @WebMethod
    public Message getMessage(int siteId, int msgId) {
        final Site site = site(siteId);
        if (site != null) {
            final info.xonix.zlo.search.model.Message messageModel = appLogic.getMessageByNumber(site, msgId);

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
        } else {
            return null;
        }
    }
}
