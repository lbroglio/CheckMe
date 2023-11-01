package ms_312.CheckMeBackend;


import jakarta.annotation.PostConstruct;
import ms_312.CheckMeBackend.Users.Group;
import ms_312.CheckMeBackend.Users.GroupRepository;

import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Messages.MessageRepository;

import ms_312.CheckMeBackend.Users.UserRepository;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


import java.security.NoSuchAlgorithmException;

import java.util.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;


@SpringBootApplication
@RestController
public class CheckMeBackendApplication {
	@Autowired
	UserRepository userRepository;

	@Autowired
	GroupRepository groupRepository;

	@Autowired
	MessageRepository messageRepository;

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
