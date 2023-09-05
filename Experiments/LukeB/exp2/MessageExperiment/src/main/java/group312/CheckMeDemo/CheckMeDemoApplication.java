package group312.CheckMeDemo;

import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import java.io.FileNotFoundException;

@SpringBootApplication
@RestController
public class CheckMeDemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(CheckMeDemoApplication.class, args);
	}

	@RequestMapping("/")
	public ModelAndView welcome() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("explainer.html");
		return modelAndView;
	}

	@GetMapping("/messages")
	public String getAllMessages() throws FileNotFoundException, ParseException {
		MessageRetriever getAll = new MessageRetriever("../../shared/messages.json");

		return new JSONArray(getAll.getAllMessages()).toString();
	}

	@RequestMapping("/messages/{platform}")
	public String getAllMessages(@PathVariable String platform) throws FileNotFoundException, ParseException {
		MessageRetriever getAll = new MessageRetriever("../../shared/messages.json");

		return new JSONArray(getAll.getMessagesForPlatform(platform)).toString();
	}


}
