package info.xonix.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: gubarkov
 * Date: 01.05.12
 * Time: 21:34
 */
public class SafeUtilsTests {
    @Test
    public void test1() {
        final int[] arr = {1, 2, 3, 4, 5};

        assertEquals(1, SafeUtils.safeGet(arr, -100));
        assertEquals(1, SafeUtils.safeGet(arr, -1));
        assertEquals(1, SafeUtils.safeGet(arr, 0));
        assertEquals(3, SafeUtils.safeGet(arr, 2));
        assertEquals(5, SafeUtils.safeGet(arr, 4));
        assertEquals(1, SafeUtils.safeGet(arr, 5));
        assertEquals(1, SafeUtils.safeGet(arr, 555));

        assertEquals(777, SafeUtils.safeGet(arr, -100, 777));
        assertEquals(777, SafeUtils.safeGet(arr, -1, 777));
        assertEquals(1, SafeUtils.safeGet(arr, 0, 777));
        assertEquals(5, SafeUtils.safeGet(arr, 4, 777));
        assertEquals(777, SafeUtils.safeGet(arr, 5, 777));
        assertEquals(777, SafeUtils.safeGet(arr, 555, 777));
    }
}
