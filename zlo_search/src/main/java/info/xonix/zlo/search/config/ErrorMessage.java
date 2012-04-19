package info.xonix.zlo.search.config;

import info.xonix.zlo.search.spring.AppSpringContext;

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

    Banned("error.banned");

    private Config config = AppSpringContext.get(Config.class);

    private String val;
    private String data = null;

    ErrorMessage(String key) {
        this.val = config.getProp(key);
    }

    public String toString() {
        return val +
                (data == null
                        ? ""
                        : !config.isDebug()
                        ? ""
                        : ":<br/> " + data);
    }

    public String getData() {
        return data;
    }

    /*
     * TODO: this is not OK : enum values should be immutable
     */
    public void setData(String data) {
        this.data = data;
    }
}
