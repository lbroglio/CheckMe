package group.ms_312.Proxy;

import group.ms_312.Proxy.Providers.AuthMapper;
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
    public void testAuthMapperSize(){
        AuthMapper testOn = new AuthMapper();
        testOn.put("1", "one");
        testOn.put("2", "two");
        testOn.put("3", "three");

        assertEquals(3, testOn.size());
    }

    @Test
    public void testAuthMapperForwardsAccess(){
        AuthMapper testOn = new AuthMapper();
        testOn.put("1", "one");
        testOn.put("2", "two");
        testOn.put("3", "three");

        assertEquals("one", testOn.get("1"));
        assertEquals("two", testOn.get("2"));
        assertEquals("three", testOn.get("3"));

    }

    @Test
    public void testAuthMapperBackwardsAccess(){
        AuthMapper testOn = new AuthMapper();
        testOn.put("1", "one");
        testOn.put("2", "two");
        testOn.put("3", "three");

        assertEquals("1", testOn.getKey("one"));
        assertEquals("2", testOn.getKey("two"));
        assertEquals("3", testOn.getKey("three"));
    }

    @Test
    public void testAuthMapperRemove(){
        AuthMapper testOn = new AuthMapper();
        testOn.put("1", "one");
        testOn.put("2", "two");
        testOn.put("3", "three");

        testOn.remove(3L);

        assertNull(testOn.get(3L));
    }

    @Test
    public void testAuthMapperPutAll(){
        HashMap<String, String> buildFrom = new HashMap<>();
        buildFrom.put("1", "one");
        buildFrom.put("2", "two");
        buildFrom.put("3", "three");

        AuthMapper testOn = new AuthMapper();

        testOn.putAll(buildFrom);

        assertEquals("one", testOn.get("1"));
        assertEquals("two", testOn.get("2"));
        assertEquals("three", testOn.get("3"));

        assertEquals("1", testOn.getKey("one"));
        assertEquals("2", testOn.getKey("two"));
        assertEquals("3", testOn.getKey("three"));
    }

}
