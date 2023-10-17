package group.ms_312.Proxy;

import group.ms_312.Proxy.Messages.Message;
import group.ms_312.Proxy.Providers.MessageProvider;
import group.ms_312.Proxy.Providers.MessageProviderRepository;
import group.ms_312.Proxy.Providers.TokenBasedProvider;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.LinkedHashMap;

@SpringBootApplication
public class ProxyApplication {

	@Autowired
	MessageProviderRepository providerRepository;

	private void loadTokenBasedMessage(LinkedHashMap<Object, Object> messageJSON, String username){
		// Get the token based provider
		MessageProvider tokenBasedProvider = providerRepository.findByID(101L);

		//If the token based provided doesn't exist create one
		if(tokenBasedProvider == null){
			tokenBasedProvider = new TokenBasedProvider();
		}

		//Add the message
		tokenBasedProvider.loadMessage(new Message(messageJSON),username);

		//Save the provider
		providerRepository.save(tokenBasedProvider);
	}


	public static void main(String[] args) {
		SpringApplication.run(ProxyApplication.class, args);
	}

	@PostMapping("/messages/load")
	public ResponseEntity<String> loadMessages(@RequestBody String toLoad){
		//Parse the request body as JSON
		JSONParser parser = new JSONParser(toLoad);
		ArrayList<Object> messagesToLoad;
		try{
			messagesToLoad = parser.parseArray();
		}
		catch (ParseException e){
			//Return 400 is the body cannot be parsed
			return new ResponseEntity<>("Could not parse request body as JSON array", HttpStatus.BAD_REQUEST)
		}

		// Go through every object in the Array
		for(int i=0; i < messagesToLoad.size(); i++){
			// Get the current object and cast it to a LinkedHashMap


		}





	}


}
