package ms_312.CheckMeBackend.ControllerTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import ms_312.CheckMeBackend.CheckMeBackendApplication;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CheckMeBackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTests {
    private static final Logger log = Logger.getLogger(GroupTests.class.getName());
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    public void createBaseballBob(){
        // Body to send for the request
        String requestBody = """
                {
                    "username": "BaseballBob",
                    "email_address": "cubsbob@yahoo.com",
                    "password": "CubsGo123"
                }
                """;


        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body(requestBody).
                when().
                post("/user");
    }



    @Test
    public void testAddUser(){
        // Body to send for the request
        String requestBody = """
                {
                    "username": "TestUser",
                    "email_address": "test@yahoo.com",
                    "password": "Test123"
                }
                """;


        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body(requestBody).
                when().
                post("/user");

        // Check that the response is correct
        assertEquals("Created new user: TestUser", response.body().asString());
 ;
        // Use dev endpoint to see the  User
         response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                when().
                get("/dev/user/TestUser");

        // Parse the response as JSON
        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> respJSON;
        try{
            respJSON = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /dev/user/TestUser as valid JSON. Root Cause: " + e);
        }


        // Check that the username and email fields as expected
        assertEquals("TestUser",  respJSON.get("name"));
        assertEquals("test@yahoo.com",  respJSON.get("email"));
    }

    @Test
    public void testSeeUser(){
        createBaseballBob();

        // Use the see endpoint to see the  User
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","CubsGo123").
                when().
                get("/user/BaseballBob");

        // Parse the response as JSON
        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> respJSON;
        try{
            respJSON = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /user/BaseballBob as valid JSON. Root Cause: " + e);
        }


        // Check that the username and email fields as expected
        assertEquals("BaseballBob",  respJSON.get("name"));
        assertEquals("cubsbob@yahoo.com",  respJSON.get("email"));
    }

    @Test
    public void testUserAccountSettings() {
        createBaseballBob();

        // --- TEST SET ENDPOINT ---


        // Body to send for the request
        String requestBody = """
                {
                    "Test-Setting": "Testing"
                }
                """;

        // Use the see endpoint to see the  User
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","CubsGo123").
                when().
                body(requestBody).
                put("/user/BaseballBob/account_settings");

        // Check that the response is correct
        assertEquals("Updated User: BaseballBob", response.body().asString());

        // --- TEST GET ENDPOINT ---


        // Use the see endpoint to see the  User
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","CubsGo123").
                when().
                get("/user/BaseballBob/account_settings");

        // Parse the response as JSON
        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> respJSON;
        try{
            respJSON = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /user/BaseballBob/account_settings as valid JSON. Root Cause: " + e);
        }

        // Check that the JSON returned as expected
        assertEquals("Testing",  respJSON.get("Test-Setting"));
    }

    @Test
    public void testGetUserGroups() {
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
                header("charset","utf-8").
                header("Authorization","Basic " + basicAuth).
                body(requestBody).
                when().
                post("/group");

        // Use endpoint to see groups
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","CubsGo123").
                when().
                get("/user/BaseballBob/groups");

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        ArrayList<Object> respJSON;
        try{
            respJSON = responseParser.parseArray();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /user/BaseballBob/groups as valid JSON. Root Cause: " + e);
        }

        // Get the group from the parsed ArrayList
        LinkedHashMap<Object, Object> group = (LinkedHashMap<Object, Object>) respJSON.get(0);

        // Get the first member from the Group
        ArrayList<Object> members = (ArrayList<Object>) group.get("members");
        LinkedHashMap<Object, Object> member = (LinkedHashMap<Object, Object>) members.get(0);


        // Check that the Group has expected fields
        assertEquals("CubsFans",  group.get("name"));
        assertEquals("BaseballBob",  member.get("name"));
    }

    @Test
    public void testLoginSuccess() {
        createBaseballBob();

        // Body to send for the request
        String requestBody = """
                {
                    "username": "BaseballBob",
                    "password": "CubsGo123"
                }
                """;

        // Send request to create a group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                body(requestBody).
                when().
                post("/user/login");


        // Parse the response as a bool
        boolean responseBool = Boolean.parseBoolean(response.body().asString());

        // Assert that the boolean is true
        assertTrue(responseBool);

    }


    @Test
    public void testLoginFailure() {
        createBaseballBob();

        // Body to send for the request
        String requestBody = """
                {
                    "username": "BaseballBob",
                    "password": "WrongPassword"
                }
                """;

        // Send request to create a group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                body(requestBody).
                when().
                post("/user/login");


        // Parse the response as a bool
        boolean responseBool = Boolean.parseBoolean(response.body().asString());

        // Assert that the boolean is true
        assertFalse(responseBool);
    }
}
