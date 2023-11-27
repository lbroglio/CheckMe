package ms_312.CheckMeBackend.ControllerTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import ms_312.CheckMeBackend.CheckMeBackendApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Base64;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CheckMeBackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageTests {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    public void createUser(String username, String email, String password) {
        // Body to send for the request
        String requestBody = "{\n" + "\t\"username\": \"" + username + "\",\n\t\"email_address\": \"" + email + "\",\n"
                + "\t\"password\": \"" + password + "\"\n}";

        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                body(requestBody).
                when().
                post("/user");
    }

    public void createBaseballBob() {
        createUser("BaseballBob", "cubsbob@yahoo.com", "CubsGo123");
    }

    public void createCubsFans() {
        createBaseballBob();

        // Body to send for the request
        String requestBody = """
                {
                    "name": "CubsFans"
                }
                """;

        //Authorization header to use for the group creating endpoint
        String basicAuth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        // Send request to create a group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + basicAuth).
                body(requestBody).
                when().
                post("/group");
    }


}