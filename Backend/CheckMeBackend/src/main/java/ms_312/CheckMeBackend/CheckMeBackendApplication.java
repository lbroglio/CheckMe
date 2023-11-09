package ms_312.CheckMeBackend;


import jakarta.annotation.PostConstruct;
import ms_312.CheckMeBackend.LiveChat.ChatRepository;
import ms_312.CheckMeBackend.Users.Group;
import ms_312.CheckMeBackend.Users.GroupRepository;

import ms_312.CheckMeBackend.Messages.MessageRepository;

import ms_312.CheckMeBackend.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.*;

import java.util.*;


@SpringBootApplication
@RestController
@ComponentScan(basePackages = {"ms_312.CheckMeBackend"})
public class CheckMeBackendApplication {
	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	MessageRepository messageRepository;

	@Autowired
	ChatRepository chatRepository;

	@PostConstruct
	private void rebuildStatics(){
		List<Group> allGroups = groupRepository.findAll();

		for (Group allGroup : allGroups) {
			allGroup.fillCodeList();
		}

	}


	public static void main(String[] args) {
		SpringApplication.run(CheckMeBackendApplication.class, args);
	}


}
