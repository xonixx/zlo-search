package org.xonix.zlo.search.config;

/**
 * Author: gubarkov
* Date: 11.09.2007
* Time: 17:56:07
*/
public enum ErrorMsgs {
    ToDateInvalid("error.toDate"),
    FromDateInvalid("error.fromDate"),
    MustSelectCriterion("error.must.select.criterion");

    private String val;

    ErrorMsgs(String key) {
        this.val = Config.getProp(key);
    }

    public String toString() {
        return val;
    }
}
