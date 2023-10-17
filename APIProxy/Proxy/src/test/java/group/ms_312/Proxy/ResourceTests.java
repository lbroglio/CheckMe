package group.ms_312.Proxy;

import group.ms_312.Proxy.Resources.Bimap;
import group.ms_312.Proxy.Resources.Sorting;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class ResourceTests {
    static class intComp implements Comparator<Integer>{

        @Override
        public int compare(Integer o1, Integer o2) {
            return o1.compareTo(o2);
        }
    }


    @Test
    public void testQuickSort(){
        Integer[] toSort = {10, 8, 5, 1, 3, 2, 4, 0, 7, 6, 9};
        Integer[] sorted = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        Sorting.qSort(toSort, 0, toSort.length -1, new intComp());
        assertArrayEquals(sorted, toSort);

    }

    @Test
    public void testBimapSize(){
        Bimap<String, Integer> testOn = new Bimap<>();
        testOn.put("one", 1);
        testOn.put("two", 2);
        testOn.put("three", 3);

        assertEquals(3, testOn.size());
    }

    @Test
    public void testBimapForwardsAccess(){
        Bimap<String, Integer> testOn = new Bimap<>();
        testOn.put("one", 1);
        testOn.put("two", 2);
        testOn.put("three", 3);

        assertEquals(1, testOn.get("one"));
        assertEquals(2, testOn.get("two"));
        assertEquals(3, testOn.get("three"));
    }

    @Test
    public void testBimapBackwardsAccess(){
        Bimap<String, Integer> testOn = new Bimap<>();
        testOn.put("one", 1);
        testOn.put("two", 2);
        testOn.put("three", 3);

        assertEquals("one", testOn.getKey(1));
        assertEquals("two", testOn.getKey(2));
        assertEquals("three", testOn.getKey(3));
    }

    @Test
    public void testBimapRemove(){
        Bimap<String, Integer> testOn = new Bimap<>();
        testOn.put("one", 1);
        testOn.put("two", 2);
        testOn.put("three", 3);

        testOn.remove("three");

        assertNull(testOn.get("three"));
    }

    @Test
    public void testBimapPutAll(){
        HashMap<String, Integer> buildFrom = new HashMap<>();
        buildFrom.put("one", 1);
        buildFrom.put("two", 2);
        buildFrom.put("three", 3);

        Bimap<String, Integer> testOn = new Bimap<>();

        testOn.putAll(buildFrom);

        assertEquals(1, testOn.get("one"));
        assertEquals(2, testOn.get("two"));
        assertEquals(3, testOn.get("three"));

        assertEquals("one", testOn.getKey(1));
        assertEquals("two", testOn.getKey(2));
        assertEquals("three", testOn.getKey(3));
    }

}
