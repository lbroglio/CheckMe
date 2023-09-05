package group312.CheckMeDemo;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;

/**
 * Retrieves a list of messages stored in a JSON file on the local machine and provides it as an array of {@link Message}
 * objects
 */
public class MessageRetriever {
    private final String messageLoc;

    /**
     * Create a new MessageRetriever that will load messages from the JSON file at the given file location
     *
     * @param messageLoc A string containing the path to the JSON file to retrieve messages from
     */
    public MessageRetriever(String messageLoc) {
        this.messageLoc = messageLoc;
    }


    /**
     * Retrieve all the messages stored in this retrievers target location and return them in an array.
     *
     * @return An array of {@link Message} objects built from the retrieved message
     *
     * @throws FileNotFoundException Will occur when this retriever's target file cannot be found in the file system
     * @throws ParseException Will occur when the contents of the target file cannot be parsed as JSON
     */
    // These need to be suppressed because of the nature of the JSON API
    @SuppressWarnings("unchecked")
    public Message[] getAllMessages() throws FileNotFoundException, ParseException {
        //Loads the entire JSON file containing the books into a string
        String booksJson = new Scanner(new File(messageLoc)).useDelimiter("\\Z").next();

        //Parse the read JSON
        JSONParser parser = new JSONParser(booksJson);
        ArrayList<Object> parsedArr = parser.parseArray();

        //Create an array to return the messages
        Message[] toReturn = new Message[parsedArr.size()];

        //For every entry in the parse JSON
        for (int i =0;  i < parsedArr.size(); i++) {
            LinkedHashMap<Object, Object> entry = (LinkedHashMap<Object, Object>) parsedArr.get(i);

            String sender = (String) entry.get("sender");
            String contents = (String) entry.get("contents");
            String subject = (String) entry.get("subject");

            //Take in the send time as a String and use it to construct an object
            String timeString = (String) entry.get("sendTime");
            LocalDateTime sendTime = LocalDateTime.parse(timeString);

            String platform = (String) entry.get("platform");

            toReturn[i] = new Message(sender, contents, subject, sendTime, platform);
        }

        return toReturn;
    }

    /**
     * Returns an array containing the messages retrieved by this container that are from the given platform.
     *
     * @param platformName The name of the messaging platform to get messages for
     *
     * @return An array of {@link Message} objects built from the retrieved message
     *
     * @throws FileNotFoundException Will occur when this retriever's target file cannot be found in the file system
     * @throws ParseException Will occur when the contents of the target file cannot be parsed as JSON
     */
    public Message[] getMessagesForPlatform(String platformName) throws FileNotFoundException, ParseException {
        //  The platform name put into lowercase for comparing with the Messages property
        String compPlatform = platformName.toLowerCase();
        Message[] allMessages = getAllMessages();
        ArrayList<Message> foundMessages = new ArrayList<>();

        // For every message
        for (Message currMessage : allMessages) {
            //If the platform name matches the provided one add it to the list of found messages
            if(currMessage.getPlatform().toLowerCase().equals(compPlatform)){
                foundMessages.add(currMessage);
            }
        }

        return foundMessages.toArray(new Message[0]);
    }

}