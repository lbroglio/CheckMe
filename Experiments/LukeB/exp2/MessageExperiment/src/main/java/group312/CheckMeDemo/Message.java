package group312.CheckMeDemo;

import java.time.LocalDateTime;

/**
 * Object that represents a single Message or email retrieved from a platform
 */
public class Message {
    /**
     * The name or identifier associated with who sent this message
     */
    private final String sender;
    /**
     * The text or the body of this message.
     */
    private final String contents;
    /**
     * Any subject or title associated with this message by the platform
     */
    private final String subject;
    /**
     * The time that this message was sent as reported by the platform it was retrieved from
     */
    private final LocalDateTime sendTime;
    /**
     * The messaging platform or service (Gmail Teams etc.) that this message was retrieved from
     */
    private final String platform;

    /**
     * Create a new message with the specified parameters.
     *
     * @param sender The name or identifier associated with this message
     * @param contents The contents or body of this message
     * @param subject The subject of this message.
     * @param sendTime The time this message was sent according to the platform it was retrieved from.
     * @param platform String with the name of the platform this message was retrieved from
     */
    public Message(String sender, String contents, String subject, LocalDateTime sendTime, String platform) {
        this.sender = sender;
        this.contents = contents;
        this.subject = subject;
        this.sendTime = sendTime;
        this.platform = platform;
    }

    @Override
    public String toString() {
        return "Message{" +
                "sender='" + sender + '\'' +
                ", contents='" + contents + '\'' +
                ", subject='" + subject + '\'' +
                ", sendTime=" + sendTime +
                ", platform='" + platform + '\'' +
                '}';
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
    public Message(String sender, String contents, LocalDateTime sendTime, String platform) {
        this.sender = sender;
        this.contents = contents;
        this.subject = null;
        this.sendTime = sendTime;
        this.platform = platform;
    }

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
}
