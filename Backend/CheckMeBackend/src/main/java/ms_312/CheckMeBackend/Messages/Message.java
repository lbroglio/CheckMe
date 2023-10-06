package ms_312.CheckMeBackend.Messages;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import jakarta.persistence.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ms_312.CheckMeBackend.Users.User;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Object that represents a single Message or email retrieved from a platform
 */
@Entity
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
    private String platform;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * Create a new message with the specified parameters.
     *
     * @param sender The name or identifier associated with this message
     * @param recipient The name
     * @param contents The contents or body of this message
     * @param subject The subject of this message.
     * @param sendTime The time this message was sent according to the platform it was retrieved from.
     * @param platform String with the name of the platform this message was retrieved from
     */
    public Message(String sender, String recipient, String contents, String subject, LocalDateTime sendTime, String platform) {
        this.sender = sender;
        this.recipient = recipient;
        this.contents = contents;
        this.subject = subject;
        this.sendTime = sendTime;
        this.platform = platform;
        this.ID = this.hashCode();
    }

    /**
     * Create a new message with the specified parameters. This constructor is for messages with no subject.
     * subject will be set to null.
     *
     * @param sender The name or identifier associated with this message
     * @param contents The contents or body of this message
     * @param sendTime The time this message was sent according to the platform it was retrieved from.
     * @param platform String with the name of the platform this message was retrieved from
     */
    public Message(String sender, String recipient, String contents, LocalDateTime sendTime, String platform) {
        this.sender = sender;
        this.recipient = recipient;
        this.contents = contents;
        this.subject = null;
        this.sendTime = sendTime;
        this.platform = platform;
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
     * Retrieve the time that this message was sent as reported by the platform it was retrieved from
     * as a Java {@link LocalDateTime} object.
     */
    public void setSendTime(LocalDateTime sendTime) {
        this.sendTime = sendTime;
    }
    /**
     * Retrieve the messaging platform or service (Gmail Teams etc.) that this message was retrieved from as a String
     */
    public void setPlatform(String platform) {
        this.platform = platform;
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
     * Retrieve the time that this message was sent as reported by the platform it was retrieved from
     * as a Java {@link LocalDateTime} object.
     */
    public LocalDateTime getSendTime() {
        return sendTime;
    }
    /**
     * Retrieve the messaging platform or service (Gmail Teams etc.) that this message was retrieved from as a String
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * Retrieve the ID for this message. A unique identifier created from the Has of its sender, contents, sendTime, and platform
     */
    public int getID(){return ID;}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return Objects.equals(sender, message.sender) && Objects.equals(contents, message.contents) && Objects.equals(sendTime, message.sendTime) && Objects.equals(platform, message.platform);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sender, contents, sendTime, platform);
    }


    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", recipient='" + recipient + '\'' +
                ", contents='" + contents + '\'' +
                ", subject='" + subject + '\'' +
                ", sendTime=" + sendTime +
                ", platform='" + platform + '\'' +
                '}';
    }

}
