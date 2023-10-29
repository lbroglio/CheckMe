package ms_312.CheckMeBackend.Messages;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ms_312.CheckMeBackend.Users.User;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Objects;

/**
 * Object that represents a single Message or email retrieved from a platform
 */
@Entity
@Table(name="MESSAGES")
public class Message {

    /**
     * The ID of this message. The ID is created from the Hash of the sender, contents, send time, and platform
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ID;
    private String sender;
    private String recipient;
    private String contents;
    private String subject;

    private LocalDateTime sendTime;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Message(String sender, String recipient, String contents, String subject, LocalDateTime sendTime) {
        this.sender = sender;
        this.recipient = recipient;
        this.contents = contents;
        this.subject = subject;
        this.sendTime = sendTime;
        this.ID = this.hashCode();
    }

    /**
     * Create a new message with the specified parameters. This constructor is for messages with no subject.
     * subject will be set to null.
     *
     * @param sender The name or identifier associated with this message
     * @param contents The contents or body of this message
     */
    public Message(String sender, String recipient, String contents, LocalDateTime sendTime) {
        this.sender = sender;
        this.recipient = recipient;
        this.contents = contents;
        this.subject = null;
        this.sendTime = sendTime;
        this.ID = this.hashCode();
    }

    /**
     * Create a new Message from a LinkedHashMap holding the fields.Used for creating Messages from JSON
     *
     * @param jsonObj The LinkedHashMap containing the fields for the message
     */
    public Message(LinkedHashMap<Object, Object> jsonObj){
        this.sender =  (String) jsonObj.get("sender");
        this.recipient =  (String) jsonObj.get("recipient");
        this.contents =  (String) jsonObj.get("contents");
        this.subject =  (String) jsonObj.get("subject");
        this.sendTime =  LocalDateTime.parse((String) jsonObj.get("sendTime")) ;
        this.ID = this.hashCode();
    }


    public Message() {}

    /**
     * Retrieve the name or identifier associated with who sent this message as a string
     */
    public void setSender(String sender) {
        this.sender = sender;
    }

    /**
     * Retrieve the text or the body of this message as a string.
     */
    public void setContents(String contents) {
        this.contents = contents;
    }

    /**
     * Retrieve any subject or title associated with this message by the platform as String.
     * Can be null if no subject exist
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Retrieve the ID for this message. A unique identifier created from the Has of its sender, contents, sendTime, and platform
     */
    public void setID(int ID){this.ID = ID;}

    //Getters for each field


    /**
     * Retrieve the name or identifier associated with who sent this message as a string
     */

    public String getSender() {
        return sender;
    }

    /**
     * Retrieve the text or the body of this message as a string.
     */
    public String getContents() {
        return contents;
    }

    /**
     * Retrieve any subject or title associated with this message by the platform as String.
     * Can be null if no subject exist
     *
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Retrieve the ID for this message. A unique identifier created from the Has of its sender, contents, sendTime, and platform
     */
    public int getID(){return ID;}

    /**
     * @return The time that this Message was sent as a {@link LocalDateTime} object
     */
    public LocalDateTime getSendTime() {
        return sendTime;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(sender, message.sender) && Objects.equals(recipient, message.recipient) && Objects.equals(contents, message.contents);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, contents);
    }


    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", contents='" + contents + '\'' +
                ", subject='" + subject + '\'' +
                '}';
    }

}
