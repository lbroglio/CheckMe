package group.ms_312.Proxy;

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
}
