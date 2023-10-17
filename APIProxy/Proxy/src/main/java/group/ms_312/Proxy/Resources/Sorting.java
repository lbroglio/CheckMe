package group.ms_312.Proxy.Resources;

import java.util.Comparator;

public class Sorting {
    /**
     * Place the item in arr at index i at index j
     * and
     * place the item in arr at index j at index i
     *
     * @param i The index of the first item to swap
     * @param j The index of second item to swap
     * @param arr The array to operate within
     */
    private static void swap(int i, int j, Object[] arr){
        Object temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }

    /**
     * Partition method for quick sort implementation
     *
     * @param toSort The array to operate on
     * @param low The low bounding index of the section of the array to operate on
     * @param high The high bounding index of the section of the array to operate on
     * @param compareWith The comparator for sorting with toSort
     * @param <T> Type of object stored in toSort
     */
    private static <T> int partition(T[] toSort, int low, int high, Comparator<T> compareWith){
        // Set the pivot object
        T pivot = toSort[high];

        //Set the low position
        int lowPos = low -1;

        // Move through the array
        for(int i = low; i < high; i++){
            // If the current element if less than the pivot
            if(compareWith.compare(toSort[i], pivot) < 0){
                // Increment low index
                lowPos++;
                // Swap the current element to the low index
                swap(lowPos, i, toSort);
            }
        }
        swap(lowPos + 1, high, toSort);

        //Return the space above the low index
        return lowPos +1;
    }

    /**
     * Quick sort implementation for sorting an array
     *
     * @param toSort The array to operate on
     * @param low The low bounding index of the section of the array to operate on
     * @param high The high bounding index of the section of the array to operate on
     * @param compareWith The comparator for sorting with toSort
     * @param <T> Type of object stored in toSort
     */
    public static <T> void qSort(T[] toSort, int low, int high, Comparator<T> compareWith){

        if (low < high) {

            // Partition the array
            int newBound = partition(toSort, low, high, compareWith);

            //Recursively call for each section of the array
            qSort(toSort, low, newBound - 1, compareWith);
            qSort(toSort, newBound + 1, high, compareWith);
        }

    }
}
