package info.xonix.zlo.search.xmlfp;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

/**
 * User: gubarkov
 * Date: 29.01.12
 * Time: 22:56
 */
public class XmlFpFormer {
    @Autowired
    private AppLogic appLogic;

    public String getMessage(Site site, int num) {
        Message m;
        try {
            m = appLogic.getMessageByNumber(site, num);
        } catch (EmptyResultDataAccessException e) {
            m = Message.withStatus(null);// TODO: should mean NOT EXISTS
        }

        return XmlFpUtils.messageToXml(m);
    }

    public String lastMessageNumber(Site site){
        return XmlFpUtils.lastMessageNumberToXml(
                appLogic.getLastSavedMessageNumber(site));
    }
}
