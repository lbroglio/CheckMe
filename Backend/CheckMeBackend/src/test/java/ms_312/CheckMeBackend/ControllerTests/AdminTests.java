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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.LinkedHashMap;

import static ms_312.CheckMeBackend.TestUtils.TestUtils.getTimeStamp;
import static ms_312.CheckMeBackend.TestUtils.TestUtils.logToFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CheckMeBackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminTests {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    public UserStorage createTestAdmin(){
        String username = "TestingAdmin";
        String email = "testing@gmail.com";
        String password = "TstPass";

        // Body to send for the request
        String requestBody = "{\n" + "\t\"username\": \"" + username + "\",\n\t\"email_address\": \"" + email + "\",\n"
                + "\n\t\"user_type\": \"ADMIN\",\n"
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
    public void testIsAdmin(){
        UserStorage testAdmin = createTestAdmin();

        // Send request to create a group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + testAdmin.auth).
                when().
                get("/user/login/isAdmin");


        // Parse the response as a bool
        boolean responseBool = Boolean.parseBoolean(response.body().asString());

        // Assert that the boolean is true
        assertTrue(responseBool);
    }

    @Test
    public void testDeleteUser(){
        UserStorage testAdmin = createTestAdmin();
        UserStorage testUser = createTestUser();

        // Delete the test user
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + testAdmin.auth).
                when().
                delete("/user/delete/" +  testUser.username);

        // Confirm that the User returns 404 when trying to get it

        // Use the see endpoint to see the  User
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization",testUser.password).
                when().
                get("/user/"+testUser.username);

       assertEquals(404, response.statusCode());

    }

    @Test
    public void testAdminCreateUser(){
        UserStorage testAdmin = createTestAdmin();

        String username = "TestUser" + getTimeStamp();
        String email = "testing@gmail.com";
        String password = "TestPass";

        String requestBody = "{\n" + "\t\"username\": \"" + username + "\",\n\t\"email_address\": \"" + email + "\",\n"
                + "\n\t\"user_type\": \"DEFAULT\",\n"
                + "\t\"password\": \"" + password + "\"\n}";


        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization", testAdmin.auth).
                body(requestBody).
                when().
                post("/create");

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
            throw new RuntimeException("Could not parse response as valid JSON. Root Cause: " + e);
        }


        // Check that the username and email fields as expected
        assertEquals(username,  respJSON.get("name"));
        assertEquals(email,  respJSON.get("email"));

    }

    @Test
    public void testPromoteUser(){
        UserStorage testAdmin = createTestAdmin();
        UserStorage usr = createTestUser();



        // Use dev endpoint to see the  User
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", testAdmin.auth).
                when().
                put("/promote/" +  usr.username);

        // Test that the user is now an admin
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + usr.auth).
                when().
                get("/user/login/isAdmin");


        // Parse the response as a bool
        boolean responseBool = Boolean.parseBoolean(response.body().asString());

        // Assert that the boolean is true
        assertTrue(responseBool);

    }


}
