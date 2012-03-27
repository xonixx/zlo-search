package info.xonix.utils;

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
        if (obj == null) {
            throw new IllegalArgumentException(objName + " is not set!");
        }
    }

    public static void isSet(Object obj) {
        Check.isSet(obj, "property");
    }
}
