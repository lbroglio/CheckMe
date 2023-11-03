package ms_312.CheckMeBackend.Resources;

import java.util.Comparator;

/**
 * Holds Static functions used for performing sorting operations
 */
public class Sorting {

    /**
     * Merge two arrays sorted in descending order into a single array in descending order
     *
     * @param arr1 The first sorted array to merge
     * @param arr2 The second sorted array to merge
     * @param sortInto The array to add the sorted elements into
     * @param comparator Object used for comparing eleements in the two array
     * @param <T> Type of Objects stored in arr1 and arr2
     */
    public static <T> void mergeSortedArrays(T[] arr1, T[] arr2, T[] sortInto, Comparator<T> comparator){
        if(sortInto.length <  arr1.length + arr2.length){
            throw new IllegalArgumentException("sortInto Array does not have enough space for the elements");
        }

        int i = 0;
        int j = 0;
        int k = 0;

        // Go through both arrays and add the larger element until one array is empty
        while (i < arr1.length && j < arr2.length) {
            if (comparator.compare(arr1[i], arr2[j]) > 0){
                sortInto[k++] = arr1[i++];
            }
            else{
                sortInto[k++] = arr2[j++];
            }
        }
        // Add the remaining elements in arr1
        while (i < arr1.length){
            sortInto[k++] = arr1[i++];
        }
        // Add the remaining elements in arr2
        while (j < arr2.length){
            sortInto[k++] = arr2[j++];

        }
    }
}
