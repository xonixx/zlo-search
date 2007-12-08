package org.xonix.zlo.search.config;

/**
 * Author: gubarkov
* Date: 11.09.2007
* Time: 17:56:07
*/
public enum ErrorMessage {
    ToDateInvalid("error.toDate"),
    FromDateInvalid("error.fromDate"),
    MustSelectCriterion("error.must.select.criterion"),

    NumParameterInvalid("error.num.param.invalid"),
    MessageNotFound("error.msg.not.found"),
    TooComplexSearch("error.too.complex.search"),

    InvalidQueryString("error.invalid.query.string"),

    DbError("error.db.connection"),

    UnknownError("error.unknown"),
    
    ;

    private String val;
    private String data = null;

    ErrorMessage(String key) {
        this.val = Config.getProp(key);
    }

    public String toString() {
        return val +
                (data == null
                        ? ""
                        : !Config.DEBUG
                        ? ""
                        : ":<br/> " + data);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
