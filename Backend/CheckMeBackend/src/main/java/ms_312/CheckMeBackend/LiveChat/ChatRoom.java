package ms_312.CheckMeBackend.LiveChat;
import jakarta.persistence.*;
import ms_312.CheckMeBackend.Users.Group;

import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name="CHATROOMS")
public class ChatRoom {

    private static final ArrayList<String> chatRooms = new ArrayList<>();

    @Id
    private int id;

    @ManyToMany
    @JoinColumn(name = "chat_id")
    private List<Chat> chats;

    @OneToOne
    @JoinColumn(name = "group_id")
    private Group group;

    public ChatRoom(Group group) {
        this.group = group;
        this.id = this.hashCode();
        this.chats = new ArrayList<>();
        chatRooms.add(this.group.getName());

    }

}
