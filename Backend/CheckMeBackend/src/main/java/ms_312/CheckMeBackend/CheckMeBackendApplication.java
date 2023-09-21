package ms_312.CheckMeBackend;

import ms_312.CheckMeBackend.Messages.MessageRetriever;
import ms_312.CheckMeBackend.Users.User;
import ms_312.CheckMeBackend.Users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static ms_312.CheckMeBackend.Messages.PlatformName.*;

@SpringBootApplication
@RestController
public class CheckMeBackendApplication {
	@Autowired
	UserRepository userRepository;

	public static void main(String[] args) {
		SpringApplication.run(CheckMeBackendApplication.class, args);
	}

	@PostMapping("/new-user")
	public String createUser(){
		User dummyUser = new User("Bob123","p@ssword");
		dummyUser.newMessageSource("www.does-not-exist.com");
		userRepository.save(dummyUser);

		return "Created";
	}

	@GetMapping("/user")
	public List<MessageRetriever> seeUser(){
		User temp = userRepository.findByUsername("Bob123");
		System.out.println(temp.getMessageRetrievers());
		return temp.getMessageRetrievers();
	}



}
