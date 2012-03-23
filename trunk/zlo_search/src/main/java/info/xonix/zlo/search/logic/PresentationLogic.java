package info.xonix.zlo.search.logic;

import info.xonix.zlo.search.model.SearchLog;
import info.xonix.zlo.search.utils.obscene.ObsceneUtils;

/**
 * User: gubarkov
 * Date: 23.03.12
 * Time: 18:42
 */
public class PresentationLogic {
    public static void unObscene(SearchLog searchLog, String defaultTxt) {
        searchLog.setSearchText(ObsceneUtils.unObscene(searchLog.getSearchText(), defaultTxt));
        searchLog.setSearchNick(ObsceneUtils.unObscene(searchLog.getSearchNick(), defaultTxt));
        searchLog.setSearchHost(ObsceneUtils.unObscene(searchLog.getSearchHost(), defaultTxt));
    }
}
