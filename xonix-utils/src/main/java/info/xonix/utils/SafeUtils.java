package info.xonix.utils;

/**
 * User: gubarkov
 * Date: 01.05.12
 * Time: 21:32
 */
public final class SafeUtils {
    /**
     * @param arr array
     * @param idx index
     * @return either array[idx] of array[0] if idx is out of bounds
     */
    public static int safeGet(int[] arr, int idx) {
        return safeGet(arr, idx, arr[0]);
    }

    public static int safeGet(int[] arr, int idx, int defaultVal) {
        if (idx < 0 || idx >= arr.length) {
            return defaultVal;
        }

        return arr[idx];
    }
}
