package info.xonix.zlo.search.utils;

import org.springframework.util.Assert;

/**
 * User: gubarkov
 * Date: May 27, 2010
 * Time: 2:18:30 PM
 */
public final class Check {
    /**
     * checks whether obj is null or not<br/>
     * in case of obj==null - throws IllegalArgumentException
     *
     * @param obj     the object to check
     * @param objName the object name
     * @throws IllegalArgumentException
     */
    public static void isSet(Object obj, String objName) {
        Assert.notNull(obj, objName + " is not set!");
    }

    public static void isSet(Object obj) {
        Assert.notNull(obj, "property is not set!");
    }
}
