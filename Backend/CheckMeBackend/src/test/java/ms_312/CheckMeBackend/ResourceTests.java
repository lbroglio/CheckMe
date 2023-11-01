package ms_312.CheckMeBackend;

import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Resources.Crypto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;





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



}
