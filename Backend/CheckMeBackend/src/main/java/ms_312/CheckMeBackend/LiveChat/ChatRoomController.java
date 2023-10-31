package ms_312.CheckMeBackend.LiveChat;


import jakarta.annotation.PostConstruct;
import ms_312.CheckMeBackend.Users.Group;
import ms_312.CheckMeBackend.Users.GroupRepository;

import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Messages.MessageRepository;

import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.util.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;


@SpringBootApplication
@RestController
@ComponentScan(basePackages = {"ms_312.CheckMeBackend"})
public class ChatRoomController {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @PostConstruct
    private void init(){
        // Create a chat room for each group
        List<Group> groups = groupRepository.findAll();
        for(Group group : groups){
            ChatRoom newChatRoom = new ChatRoom(group);
        }
    }


}
