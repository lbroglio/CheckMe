package ms_312.CheckMeBackend.LiveChat;
import jakarta.persistence.*;
import ms_312.CheckMeBackend.Users.User;


import java.util.Objects;

@Entity
@Table(name="CHATS")
public class Chat {

    @Id
    private int id;


    @OneToOne
    @JoinColumn(name = "chatroom_id")
    private ChatRoom chatRoom;


    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;


    public Chat(ChatRoom chatRoom, User user) {
        this.chatRoom = chatRoom;
        this.user = user;
        this.id = this.hashCode();
    }
    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}

