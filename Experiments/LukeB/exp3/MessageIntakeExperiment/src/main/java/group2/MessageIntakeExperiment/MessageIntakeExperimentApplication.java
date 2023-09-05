package group2.MessageIntakeExperiment;

import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SpringBootApplication
@RestController
public class MessageIntakeExperimentApplication {
	public static void main(String[] args) {
		SpringApplication.run(MessageIntakeExperimentApplication.class, args);
	}

	@PostMapping("/add")
	public void intakeMessage(@RequestBody String newMessage) throws IOException, ParseException {
		System.out.println(newMessage);

		//Load in JSON file
		String messages = new Scanner(new File("../../shared/messages.json")).useDelimiter("\\Z").next();

		// Convert the string into a usable array object
		JSONArray  asJson = new JSONArray(messages);

		// Add the new message to the array
		JSONParser jParser = new JSONParser(newMessage);
		asJson.put(jParser.parse());

		//Overwrite the file with the new object
		File outFile = new File("../../shared/messages.json");
		BufferedWriter writer = new BufferedWriter(new FileWriter(outFile));
		writer.write(asJson.toString());

		writer.close();

	}

}
