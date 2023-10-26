package ms_312.CheckMeBackend;

import ms_312.CheckMeBackend.Resources.CryptoService;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.ResourceUtils;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ResourceTests {

    @Test
    //Unchecked casts are from interacting with unchecked casts
    @SuppressWarnings("unchecked")
    public void testEncryptDecrypt() {
        // Encrypt a string
        byte[] ciphertext = CryptoService.encryptStringAES("UnitTestCrypto", "unit-test");

        // Decrypt the string
        String plaintext = CryptoService.decryptStringAES(ciphertext,"unit-test");

        //Assert that the decrypted string is accurate
        assertEquals("UnitTestCrypto", plaintext);
    }


}
