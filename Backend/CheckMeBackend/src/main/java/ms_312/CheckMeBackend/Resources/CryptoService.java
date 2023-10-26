package ms_312.CheckMeBackend.Resources;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.transaction.Transactional;
import ms_312.CheckMeBackend.Messages.Message;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.*;

/**
 * Class which holds static method for performing cryptographic operations (Encryption and Decryption).
 * Handles encrypting a string and storing / retrieving keys and other related info.
 */
@Service
public class CryptoService {
    @Autowired
    private static AESKeyRepository keyRepository;

    /**
     * Class used for storing the information associated with a cipher (key and iv) in the database.
     */
    @Entity
    private static class AESKey {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private long ID;

        public String referenceName;

        /**
         * AES cipher key for this AESKey
         */
        public byte[] key;

        /**
         * Initialization Vector for this cryptokey
         */
        public byte[] iv;

        /**
         * Create a new AESKey
         *
         * @param referenceName The name to reference this key by. -- Must be unique
         * @param key The key to store in this object
         * @param iv The iv to store in this object.
         */
        public AESKey(String referenceName, byte[] key, byte[] iv){
            this.referenceName = referenceName;
            this.key = Arrays.copyOf(key, key.length);
            this.iv = Arrays.copyOf(iv, iv.length);

        }

        /**
         * Private constructor used by JPA
         */
        private AESKey(){}

    }

    /**
     * Interface used by the JPA for persisting AESKey objects
     */
    private interface AESKeyRepository extends JpaRepository<AESKey, Long> {
        AESKey findByID(int ID);
        AESKey findByReferenceName(String referenceName);
    }



    /**
     * Encrypt a String using AES (Advanced Encryption Standard). Also handles storing the Key and IV associated with
     * a given reference name
     *
     * @param plaintext The string to encrypt
     * @param referenceName The string to be used for retrieving the key and Initialization Vector used to encrypt the
     * string.
     *
     */
    //Unchecked casts are from interacting with unchecked casts
    @SuppressWarnings("unchecked")
    public static byte[] encryptStringAES(String plaintext, String referenceName)  {
        //Generate an encryption key
        KeyGenerator keyGen;
        try {
            // Create and Initialize and AES Cipher Object
            keyGen = KeyGenerator.getInstance("AES");
        }
        //Not expected to ever occur since algorithm is hardcode
        catch (Exception e){
            throw new IllegalStateException("Failed to create KeyGenerator Object. Root Cause: " + e.getMessage());
        }

        SecretKey key = keyGen.generateKey();

        //Generate an initialization vector
        SecureRandom secRan = new SecureRandom();
        byte[] ivArray = new byte[16];
        secRan.nextBytes(ivArray);
        IvParameterSpec iv = new IvParameterSpec(ivArray);

        Cipher c;
        try {
            // Create and Initialize and AES Cipher Object
            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE, key, iv);
        }
        //Not expected to ever occur since algorithm is hardcode
        catch (Exception e){
            throw new IllegalStateException("Failed to create or initialize cipher object. Root Cause: " + e.getMessage());
        }

        //Encrypt the String
        byte[] cipherText;
        try{
            cipherText = c.doFinal(plaintext.getBytes());

        }
        // The exceptions thrown do not apply in this context so this should never occur
        catch (Exception e){
            throw new IllegalStateException("Failed to encrypt string");
        }

        /*
        Store the encryption key and IV in the database
        ## THIS METHOD OF STORAGE IS INSECURE BUT NECESSARY WHILE IN DEVELOPMENT(For class) ##
         */

        //Create a new AESKey object to store the data to be saved
        AESKey toStore = new AESKey(referenceName, key.getEncoded(), ivArray);

        //Save the new AESKey to the database
        keyRepository.save(toStore);

        return cipherText;
    }

    /**
     * Decrypt a given ciphertext (Stored as a byte array) using AES (Advanced Encryption standard)
     * Handles retrieving the key and IV stored associated with the given reference name.
     *
     * @param ciphertext The cipher to decrypt
     * @param referenceName The name used to retrieve the key and IV for the cipher.
     *
     * @return The decrypted cipher as a String
     */
    //Unchecked casts are from interacting with unchecked casts
    @SuppressWarnings("unchecked")
    public static String decryptStringAES(byte[] ciphertext, String referenceName){
        // Retrieve the IV and keys for decryption from the database
        AESKey cipherInfo = keyRepository.findByReferenceName(referenceName);

        // Set up the key and the IV
        SecretKey key = new SecretKeySpec(cipherInfo.key, "AES");
        IvParameterSpec iv = new IvParameterSpec(cipherInfo.iv);

        // Create and Initialize and AES Cipher Object
        Cipher c;
        try {
            // Create and Initialize and AES Cipher Object
            c = Cipher.getInstance("AES/CBC/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE, key, iv);
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e){
            throw new RuntimeException("Could not setup AES cipher object. Root Cause: " + e);
        }

        // Decrypt the cipher
        String plaintext;
        try{
            plaintext = new String(c.doFinal(ciphertext));
        }
        catch (IllegalBlockSizeException | BadPaddingException e){
            throw new RuntimeException("Could not decrypt ciphertext. Root Cause: " +  e);
        }

        return plaintext;
    }


}
