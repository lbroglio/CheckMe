package ms_312.CheckMeBackend.Resources;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.kerberos.EncryptionKey;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Class which holds static method for performing cryptographic operations (Encryption and Decryption).
 * Handles encrypting a string and storing / retrieving keys and other related info.
 */
public class Crypto {
    /**
     * Store the path to the JSON file used for key storage.
     * Storing keys this was in insecure but necessary for development (for class)
     */
    private static final String KEY_STORAGE = "classpath:keys.json";

    /**
     * Encrypt a String using AES (Advanced Encryption Standard). Also handles storing the Key and IV associated with
     * a given reference name
     *
     * @param plaintext The string to encrypt
     * @param referenceName The string to be used for retrieving the key and Initialization Vector used to encrypt the
     * string.
     *
     * @throws ParseException Occurs when keys.json can not be parsed as JSON
     */
    //Unchecked casts are from interacting with unchecked casts
    @SuppressWarnings("unchecked")
    public static byte[] encryptStringAES(String plaintext, String referenceName) throws ParseException {
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
            c = Cipher.getInstance("AES");
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
        Store the encryption key and IV
        ## THIS METHOD OF STORAGE IS INSECURE BUT NECESSARY WHILE IN DEVELOPMENT(For class) ##
         */
        //Read in the JSON file containing the keys
        File jsonFile = new File(KEY_STORAGE);
        Scanner readFile;
        try{
            readFile = new Scanner(jsonFile);

        }
        //Not expected to ever occur since file is hardcode
        catch (FileNotFoundException e){
            throw new RuntimeException("Could not read keys.json");
        }

        //Reads the entries file in as a JSON string for the JSON Parser
        JSONParser parser = new JSONParser(readFile.useDelimiter("\\Z").next());
        readFile.close();

        // Parse the  JSON
        LinkedHashMap<String, LinkedHashMap<String, byte[]>> keysJSON = (LinkedHashMap<String, LinkedHashMap<String, byte[]>>) parser.parse();

        //Create a Map to store the data to be saved
        LinkedHashMap<String, byte[]> keyInfo = new LinkedHashMap<>();
        keyInfo.put("key", key.getEncoded());
        keyInfo.put("iv", ivArray);

        //Add the new key data to the parsed JSON associated with the given reference name
        keysJSON.put(referenceName, keyInfo);

        //Create A JSON Object to store the JSON information as a JSONObject instance
        JSONObject output = new JSONObject();
        //Add all the keysJSON to the new object
        for (String currKey : keysJSON.keySet()) {
            output.put(currKey, keysJSON.get(currKey));
        }

        //Save the JSON to the file
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
            writer.write(output.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return cipherText;
    }

    public static String decryptStringAES(byte[] ciphertext, String referenceName){
        // Retrieve the IV and keys for decryption from keys.json
        // Read in the file
        File keysFile = new File(KEY_STORAGE);
        Scanner readFile;
        try{
            readFile = new Scanner(keysFile);

        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not read keys.json. Root  Cause: " + e);
        }
        String fileContents = readFile.useDelimiter("\\Z").next();
        readFile.close();

        //Parse the file contents as JSON
        JSONParser parser = new JSONParser(fileContents);
        LinkedHashMap<Object, Object> keysJSON;

        try{
            keysJSON = (LinkedHashMap<Object, Object>) parser.parse();
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse keys.json. Root Cause: " + e);
        }

        // Get the key info for the given reference name from the parsed JSON
        LinkedHashMap<String, byte[]>  cipherInfo = (LinkedHashMap<String, byte[]>) keysJSON.get(referenceName);

        // Set up the key and the IV
        SecretKey key = new SecretKeySpec(cipherInfo.get("key"), "AES");
        IvParameterSpec iv = new IvParameterSpec(cipherInfo.get("iv"));

        // Create and Initialize and AES Cipher Object
        Cipher c;
        try {
            // Create and Initialize and AES Cipher Object
            c = Cipher.getInstance("AES");
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
