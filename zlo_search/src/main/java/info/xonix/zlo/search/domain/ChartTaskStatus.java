package info.xonix.zlo.search.domain;

/**
 * User: xonix
 * Date: 9/5/14
 * Time: 11:48 PM
 */
public enum ChartTaskStatus {
    /**
     * right after creation
     */
    NEW,
    /**
     * daemon building chart started
     */
    STARTED,
    /**
     * daemon processed
     */
    READY
}
