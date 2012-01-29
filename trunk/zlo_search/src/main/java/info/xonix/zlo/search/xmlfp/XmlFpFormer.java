package info.xonix.zlo.search.xmlfp;

import info.xonix.zlo.search.domainobj.Site;
import info.xonix.zlo.search.logic.AppLogic;
import info.xonix.zlo.search.model.Message;

/**
 * User: gubarkov
 * Date: 29.01.12
 * Time: 22:56
 */
public class XmlFpFormer {
//    @Autowired
    private AppLogic appLogic;

    public void setAppLogic(AppLogic appLogic) {
        this.appLogic = appLogic;
    }

    public String getMessage(Site site, int num) {
        Message m = appLogic.getMessageByNumber(site, num);

        return ZloJaxb.zloMessageToXml(m);
    }

    public String lastMessageNumber(Site site){
        return ZloJaxb.lastMessageNumberToXml(
                appLogic.getLastSavedMessageNumber(site));
    }
}
