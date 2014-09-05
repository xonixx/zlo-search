package info.xonix.zlo.search.model;

/**
 * User: xonix
 * Date: 9/5/14
 * Time: 11:48 PM
 */
public enum ChartTypeStatus {
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
    READY,
    /**
     * data cleaned up for old chart to free space
     */
    OBSOLETE
}
