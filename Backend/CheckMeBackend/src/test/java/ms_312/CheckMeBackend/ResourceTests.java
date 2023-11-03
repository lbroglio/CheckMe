package ms_312.CheckMeBackend;

import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Resources.Crypto;

import ms_312.CheckMeBackend.Resources.Sorting;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class ResourceTests {

    @Test
    public void testEncryptDecrypt() {
        // Encrypt a string
        byte[] ciphertext = Crypto.encryptStringAES("UnitTestCrypto", "unit-test");

        // Decrypt the string
        String plaintext = Crypto.decryptStringAES(ciphertext,"unit-test");

        //Assert that the decrypted string is accurate
        assertEquals("UnitTestCrypto", plaintext);
    }

    private class DoubleTestComparator implements Comparator<Double> {

        @Override
        public int compare(Double o1, Double o2) {
            return  o1.compareTo(o2);
        }
    }

    @Test
    public void testArrayMerge(){
        // Two arrays to merge
        Double[] test1 = {10.0, 9.0, 8.0, 7.0, 6.0, 5.0, 4.0, 3.0, 2.0, 1.0};
        Double[] test2 = {9.5, 9.0, 8.5, 7.5, 6.5, 5.5, 4.5, 3.5, 2.5, 1.5};

        // Manually merged arrays to compare the merged array to
        Double[] compAgainst = {10.0, 9.5, 9.0, 9.0, 8.5, 8.0, 7.5, 7.0, 6.5, 6.0, 5.5, 5.0, 4.5, 4.0 , 3.5, 3.0, 2.5, 2.0, 1.5, 1.0};


        // Merge the two arrays
        Double[] mergeInto = new Double[test1.length + test2.length];

        Sorting.mergeSortedArrays(test1, test2, mergeInto, new DoubleTestComparator());

        assertArrayEquals(compAgainst, mergeInto);

    }

    @Test
    public void testArrayMergeDifferentSizes(){
        // Two arrays to merge
        Double[] test1 = {10.0};
        Double[] test2 = {9.5, 9.0};

        // Manually merged arrays to compare the merged array to
        Double[] compAgainst = {10.0, 9.5, 9.0};


        // Merge the two arrays
        Double[] mergeInto = new Double[test1.length + test2.length];

        Sorting.mergeSortedArrays(test1, test2, mergeInto, new DoubleTestComparator());

        assertArrayEquals(compAgainst, mergeInto);

    }



}
