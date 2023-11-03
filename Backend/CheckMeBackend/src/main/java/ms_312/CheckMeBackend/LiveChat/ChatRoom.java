package ms_312.CheckMeBackend.LiveChat;
import jakarta.persistence.*;
import jakarta.websocket.Session;
import ms_312.CheckMeBackend.Users.Group;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@Entity
@Table(name="CHATROOMS")
public class ChatRoom {


    private static final ArrayList<String> chatRooms = new ArrayList<>();

    @Transient
    private final List<Session> sessions = new CopyOnWriteArrayList<>();
    @Id
    private int id;

    @ManyToMany
    @JoinColumn(name = "chat_id")
    private List<Chat> chats;

    private String groupName;


    public ChatRoom(){
        this.id = this.hashCode();
        this.chats = new ArrayList<>();
    }


    public ChatRoom(String group) {
        this.groupName = group;
        this.id = this.hashCode();
        this.chats = new ArrayList<>();
        chatRooms.add(this.groupName);

    }

    public static ArrayList<String> getChatRooms() {
        return chatRooms;
    }

    public void addSession(Session session) {
        this.sessions.add(session);
    }

    public void removeSession(Session session) {
        this.sessions.remove(session);
    }

    public List<Session> getSessions() {
        return sessions;
    }


    public void addChat(Chat chat) {
        this.chats.add(chat);
    }

    public List<Chat> getChats() {
        return chats;
    }


}
