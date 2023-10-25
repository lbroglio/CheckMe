package ms_312.CheckMeBackend.Messages.Retrievers;

import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Users.RetrieverOwner;
import org.apache.tomcat.util.json.JSONParser;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.io.File;
import java.io.FileNotFoundException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Scanner;

/**
 * A MessageRetriever which retrieves information from the Chaos Service within the Proxy Applications
 */
public class ChaosRetriever extends MessageRetriever{



    /**
     * Create a new ChaosRetriever for a specific User.
     *
     * @param source The URL of the endpoint to retrieve messages.
     * @param chaosAPIToken The API token needed to authenticate with the Chaos service
     * @param owner The {@link RetrieverOwner} object which owns this Retriever
     *
     */
    public ChaosRetriever(String source, String chaosAPIToken, RetrieverOwner owner) {
        super(source, owner);

        //Encrypt the BearerToken using AES
    }

    @Override
    public Message[] getAll() {
        return new Message[0];
    }
}
