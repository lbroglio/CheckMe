package group_312.WebSocketExperiment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.bind.annotation.RestController;

@EnableAutoConfiguration
@SpringBootApplication
@ComponentScan(basePackages = {"group_312.WebSocketExperiment"})
public class WebSocketExperimentApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebSocketExperimentApplication.class, args);
	}

}
