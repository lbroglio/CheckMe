package ms_312.CheckMeBackend.ControllerTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import ms_312.CheckMeBackend.CheckMeBackendApplication;
import ms_312.CheckMeBackend.TestUtils.UserStorage;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.logging.Logger;

import static ms_312.CheckMeBackend.TestUtils.TestUtils.getTimeStamp;
import static ms_312.CheckMeBackend.TestUtils.TestUtils.logToFile;
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

    public UserStorage createTestUser(){
        return createUser("TestUser"+ getTimeStamp(), "testing@gmail.com", "TestPass");
    }

    public UserStorage createUser(String username, String email, String password) {
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

        return new UserStorage(username, password);
    }



    @Test
    public void testAddUser(){
        String username = "TestUser" + getTimeStamp();
        String email = "testing@gmail.com";
        String password = "TestPass";

        String requestBody = "{\n" + "\t\"username\": \"" + username + "\",\n\t\"email_address\": \"" + email + "\",\n"
                + "\t\"password\": \"" + password + "\"\n}";


        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body(requestBody).
                when().
                post("/user");

        // Check that the response is correct
        assertEquals("Created new user: " + username, response.body().asString());
 ;
        // Use dev endpoint to see the  User
         response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                when().
                get("/dev/user/"+username);

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
        assertEquals(username,  respJSON.get("name"));
        assertEquals(email,  respJSON.get("email"));
    }

    @Test
    public void testSeeUser(){
        UserStorage usr = createTestUser();

        // Use the see endpoint to see the  User
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization",usr.password).
                when().
                get("/user/"+usr.username);

        // Parse the response as JSON
        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> respJSON;
        try{
            respJSON = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response  as valid JSON. Root Cause: " + e);
        }


        // Check that the username and email fields as expected
        assertEquals(usr.username,  respJSON.get("name"));
        assertEquals("testing@gmail.com",  respJSON.get("email"));
    }

    @Test
    public void testUserAccountSettings() {
        UserStorage usr = createTestUser();

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
                header("Authorization",usr.password).
                when().
                body(requestBody).
                put("/user/"+usr.username+"/account_settings");

        // Check that the response is correct
        assertEquals("Updated User: " + usr.username, response.body().asString());

        // --- TEST GET ENDPOINT ---


        // Use the see endpoint to see the  User
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization",usr.password).
                when().
                get("/user/"+usr.username+"/account_settings");

        // Parse the response as JSON
        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> respJSON;
        try{
            respJSON = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response as valid JSON. Root Cause: " + e);
        }

        // Check that the JSON returned as expected
        assertEquals("Testing",  respJSON.get("Test-Setting"));
    }

    @Test
    public void testGetUserGroups() {
        UserStorage usr = createTestUser();

        String name = "TestGroup" + getTimeStamp();
        // Body to send for the request
        String requestBody = "{\n\t \"name\": \"" + name + "\"\n}";

        // Send request to create a group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + usr.auth).
                body(requestBody).
                when().
                post("/group");

        String joinCode = response.body().asString();
        requestBody = "{\n" + "\t\"username\": \"" + usr.username + "\",\n" + "\t\"password\": \"" + usr.password + "\"\n}";

        // Use the join endpoint to add the user to the group
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body(requestBody).
                when().
                put("/group/join/"+joinCode);

        // Use endpoint to see groups
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization",usr.password).
                when().
                get("/user/" + usr.username + "/groups");

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        ArrayList<Object> respJSON;
        try{
            respJSON = responseParser.parseArray();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response as valid JSON. Root Cause: " + e);
        }

        // Get the group from the parsed ArrayList
        LinkedHashMap<Object, Object> group = (LinkedHashMap<Object, Object>) respJSON.get(0);

        // Get the first member from the Group
        ArrayList<Object> members = (ArrayList<Object>) group.get("members");
        LinkedHashMap<Object, Object> member = (LinkedHashMap<Object, Object>) members.get(0);


        // Check that the Group has expected fields
        assertEquals(name,  group.get("name"));
        assertEquals(usr.username,  member.get("name"));
    }

    @Test
    public void testLoginSuccess() {
        UserStorage usr = createTestUser();

        //Create request body
        String requestBody = "{\n" + "\t\"username\": \"" + usr.username + "\",\n" + "\t\"password\": \"" + usr.password + "\"\n}";

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
        UserStorage usr = createTestUser();

        //Create request body
        String requestBody = "{\n" + "\t\"username\": \"" + usr.username + "\",\n" + "\t\"password\": \"" + "WrongPasswor"+ "\"\n}";


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
