package ms_312.CheckMeBackend.LiveChat;
import jakarta.persistence.*;
import ms_312.CheckMeBackend.Users.Group;

import java.util.List;


public class ChatRoom {

    @Id
    private int id;

    @ManyToMany
    @JoinColumn(name = "chat_id")
    private List<Chat> chats;

    @ManyToMany
    @JoinColumn(name = "group_id")
    private Group group;

    public ChatRoom(Group group) {
        this.group = group;
        this.id = this.hashCode();

    }


}
