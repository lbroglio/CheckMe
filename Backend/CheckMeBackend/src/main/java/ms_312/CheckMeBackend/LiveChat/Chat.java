package ms_312.CheckMeBackend.LiveChat;
import jakarta.persistence.*;
import ms_312.CheckMeBackend.Users.User;

@Entity
@Table(name="CHATS")
public class Chat {

    @Id
    private int ID;


    @OneToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;


    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String sender;

    public Chat(ChatRoom chatRoom, User user) {
        this.chatRoom = chatRoom;
        this.user = user;
        sender = user.getName();
        this.ID = this.hashCode();
    }
    public void setID(int id) {
        this.ID = id;
    }

    public int getID() {
        return ID;
    }
}

