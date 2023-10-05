package ms_312.CheckMeBackend.Messages;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @Autowired
    MessageRepository messageRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @GetMapping(path="/messages")
    List<Message> getAllMessages(){return messageRepository.findAll();}

    @GetMapping(path="/messages/{id}")
    List<Message> getMessageById(@PathVariable int id){return getMessageById(id);}

    @PostMapping(path="/messages")
    String createMessage(Message message){
        if (message == null) return failure;
    messageRepository.save(message);
    return  success;
    }


}
