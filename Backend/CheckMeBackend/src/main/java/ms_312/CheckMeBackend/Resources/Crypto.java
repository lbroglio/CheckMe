package ms_312.CheckMeBackend.Resources;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;

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
public class Crypto {
    /**
     * Read in the contents of the JSON file used for storing keys and ivs <br/>
     * !! THIS METHOD OF KEY STORAGE IS NOT SECURE BUT IS NECESSARY WHILE IN DEVELOPMENT (FOR CLASS)
     *
     * @return A {@link LinkedHashMap} containing the JSON data parsed from the file
     */
    //Unchecked casts are from interacting with unchecked casts
    @SuppressWarnings("unchecked")
    private static LinkedHashMap<String, LinkedHashMap<String, ArrayList<Integer>>> readKeysJSON(){
        // Get the file for the keys JSON
        File keysFile = new File(System.getProperty("user.home") + "/CheckMe/keys.json");

        //Scan in the file
        String fileContents;
        try{
            Scanner readFile = new Scanner(keysFile);
            fileContents = readFile.useDelimiter("\\Z").next();
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not read contents of keys.json file. Root Cause: " + e);
        }

        // Parse the file contents as JSON and cast it
        JSONParser parser = new JSONParser(fileContents);
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<Integer>>> keysJSON;
        try{
            keysJSON = (LinkedHashMap<String, LinkedHashMap<String, ArrayList<Integer>>>) parser.parse();
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse contents of keys.json. Root Cause: " + e);
        }

        //Return the parse JSON
        return keysJSON;
    }

    private static void storeKey(String referenceName, byte[] key, byte[] iv){
        // Get the existing keys.json information
        LinkedHashMap<String, LinkedHashMap<String, ArrayList<Integer>>> existing = readKeysJSON();

        //Create an object storing the new key info
        LinkedHashMap<String, ArrayList<Integer>> newInfo = new LinkedHashMap<>();

        // Build ArrayLists (of ints) for the key and iv
        ArrayList<Integer> keyList = new ArrayList<>(key.length);
        for (byte currByte : key) {
            keyList.add((int) currByte);
        }
        ArrayList<Integer> ivList = new ArrayList<>(key.length);
        for (byte currByte : iv) {
            ivList.add((int) currByte);
        }

        //Associate the new info with there type (key or iv) in the map
        newInfo.put("key", keyList);
        newInfo.put("iv", ivList);

        //Associate the new map with its reference name in the new map
        existing.put(referenceName, newInfo);

        // Create a JSONObject from the map
        JSONObject toSave = new JSONObject(existing);

        //Save the JSON to the keys.json file
        File keysFile = new File(System.getProperty("user.home") + "/CheckMe/keys.json");

        // Write the JSON to the file
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(keysFile));
            writer.write(toSave.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not write to keys.json. Root Cause: " + e);
        }
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
        Store the encryption key and IV in a file
        ## THIS METHOD OF STORAGE IS INSECURE BUT NECESSARY WHILE IN DEVELOPMENT(For class) ##
         */
        storeKey(referenceName, key.getEncoded(),  ivArray);

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
    public static String decryptStringAES(byte[] ciphertext, String referenceName){
        // Retrieve the IV and keys for decryption from the keys.JSON file
        LinkedHashMap<String, ArrayList<Byte>> cipherInfo = readKeysJSON().get(referenceName);

        //Unbox the ArrayLists for the key and iv
        ArrayList<Byte> keyList = cipherInfo.get("key");
        byte[] keyArray = new byte[keyList.size()];
        for(int i=0; i<keyList.size(); i++){
            Byte currWrapper = keyList.get(i);
            keyArray[i] = currWrapper;
        }
        ArrayList<Byte> ivList = cipherInfo.get("iv");
        byte[] ivArray = new byte[ivList.size()];
        for(int i=0; i<ivList.size(); i++){
            Byte currWrapper = ivList.get(i);
            ivArray[i] = currWrapper;
        }

        // Set up the key and the IV
        SecretKey key = new SecretKeySpec(keyArray, "AES");
        IvParameterSpec iv = new IvParameterSpec(ivArray);

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
