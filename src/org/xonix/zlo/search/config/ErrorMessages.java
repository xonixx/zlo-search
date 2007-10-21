package org.xonix.zlo.search.config;

/**
 * Author: gubarkov
* Date: 11.09.2007
* Time: 17:56:07
*/
public enum ErrorMessages {
    ToDateInvalid("error.toDate"),
    FromDateInvalid("error.fromDate"),
    MustSelectCriterion("error.must.select.criterion"),

    NumParameterInvalid("error.num.param.invalid"),
    MessageNotFound("error.msg.not.found"),
    TooComplexSearch("error.too.complex.search"),

    InvalidQueryString("error.invalid.query.string"),

    DbError("error.db.connection");
    
    ;

    private String val;

    ErrorMessages(String key) {
        this.val = Config.getProp(key);
    }

    public String toString() {
        return val;
    }
}
