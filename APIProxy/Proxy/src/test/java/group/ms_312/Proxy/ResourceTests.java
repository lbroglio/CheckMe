package group.ms_312.Proxy;

import group.ms_312.Proxy.Providers.TokenBased.TokenMapper;
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
    public void testTokenMapperSize(){
        TokenMapper testOn = new TokenMapper();
        testOn.put(1L, "one");
        testOn.put(2L, "two");
        testOn.put(3L, "three");

        assertEquals(3, testOn.size());
    }

    @Test
    public void testTokenMapperForwardsAccess(){
        TokenMapper testOn = new TokenMapper();
        testOn.put(1L, "one");
        testOn.put(2L, "two");
        testOn.put(3L, "three");

        assertEquals("one", testOn.get(1L));
        assertEquals("two", testOn.get(2L));
        assertEquals("three", testOn.get(3L));

    }

    @Test
    public void testTokenMapperBackwardsAccess(){
        TokenMapper testOn = new TokenMapper();
        testOn.put(1L, "one");
        testOn.put(2L, "two");
        testOn.put(3L, "three");

        assertEquals(1L, testOn.getKey("one"));
        assertEquals(2L, testOn.getKey("two"));
        assertEquals(3L, testOn.getKey("three"));
    }

    @Test
    public void testTokenMapperRemove(){
        TokenMapper testOn = new TokenMapper();
        testOn.put(1L, "one");
        testOn.put(2L, "two");
        testOn.put(3L, "three");

        testOn.remove(3L);

        assertNull(testOn.get(3L));
    }

    @Test
    public void testTokenMapperPutAll(){
        HashMap<Long, String> buildFrom = new HashMap<>();
        buildFrom.put(1L, "one");
        buildFrom.put(2L, "two");
        buildFrom.put(3L, "three");

        TokenMapper testOn = new TokenMapper();

        testOn.putAll(buildFrom);

        assertEquals("one", testOn.get(1L));
        assertEquals("two", testOn.get(2L));
        assertEquals("three", testOn.get(3L));

        assertEquals(1L, testOn.getKey("one"));
        assertEquals(2L, testOn.getKey("two"));
        assertEquals(3L, testOn.getKey("three"));
    }

}
