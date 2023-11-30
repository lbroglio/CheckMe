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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;

import static ms_312.CheckMeBackend.TestUtils.TestUtils.getTimeStamp;
import static ms_312.CheckMeBackend.TestUtils.TestUtils.logToFile;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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

    public UserStorage createTestUser(){
        return createUser("TestUser"+ getTimeStamp(), "testing@gmail.com", "TestPass");
    }

    public String createTestGroup(UserStorage creatingUser){

        String name = "TestGroup" + getTimeStamp();
        // Body to send for the request
        String requestBody = "{\n\t \"name\": \"" + name + "\"\n}";

        // Send request to create a group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + creatingUser.auth).
                body(requestBody).
                when().
                post("/group");

        return name;
    }

    public Response connectTSTACTChaos(String endpointStart, String auth){
        // Body for making the request to setup the account
        String requestBody = """
            {
                "message-service": "chaos",
                "service-url": "http://coms-309-047.class.las.iastate.edu:8443/chaos/messages/ATSTACT",
                "chaos-token": "6831972300431632"
            }
            """;

        // Send request to create a group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + auth).
                body(requestBody).
                when().
                put(endpointStart + "/connect-account");

        return response;
    }

    public Response connectTSTACTCrews(String endpointStart, String auth){
        // Body for making the request to setup the account
        String requestBody = """
            {
                "message-service": "crews",
                "service-url": "http://coms-309-047.class.las.iastate.edu:8443/crews/messages",
                "crews-username": "ATSTACT",
                "crews-password": "TSTPASS"
            }
            """;

        // Send request to create a group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + auth).
                body(requestBody).
                when().
                put(endpointStart + "/connect-account");

        return response;
    }

    public Response connectTSTACTCMail(String endpointStart, String auth){
        // Body for making the request to setup the account
        String requestBody = """
            {
                "message-service": "cmail",
                "service-url": "http://coms-309-047.class.las.iastate.edu:8443/cmail/messages",
                "cmail-username": "ATSTACT",
                "cmail-password": "TSTPASS"
            }
            """;

        // Send request to create a group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + auth).
                body(requestBody).
                when().
                put(endpointStart + "/connect-account");

        return response;
    }

    @Test
    public void testConnectChaosUser(){
        // Create the test user
        UserStorage usr = createTestUser();

        Response resp = connectTSTACTChaos("/user/" + usr.username, usr.auth);

        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testConnectCrewsUser(){
        // Create the test user
        UserStorage usr = createTestUser();

        Response resp = connectTSTACTCrews("/user/"+usr.username, usr.auth);

        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testConnectCMailsUser(){
        // Create the test user
        UserStorage usr = createTestUser();

        Response resp = connectTSTACTCMail("/user/"+usr.username, usr.auth);

        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testConnectChaosGroup(){
        // Create BaseballBob and calculate his authn
        UserStorage usr = createTestUser();
        String group = createTestGroup(usr);

        Response resp = connectTSTACTChaos("/group/"+group, usr.auth);

        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testConnectCrewsGroup(){
        // Create BaseballBob and calculate his authn
        UserStorage usr = createTestUser();
        String group = createTestGroup(usr);

        Response resp = connectTSTACTCrews("/group/"+group, usr.auth);

        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testConnectCMailsGroup(){
        // Create BaseballBob and calculate his authn
        UserStorage usr = createTestUser();
        String group = createTestGroup(usr);

        Response resp = connectTSTACTCMail("/group/"+group, usr.auth);
        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testGetMessagesUser(){
        // Create the TestUser
        UserStorage usr = createTestUser();

        // Connect retrievers
        connectTSTACTChaos("/user/" + usr.username, usr.auth);
        connectTSTACTCrews("/user/" + usr.username, usr.auth);
        connectTSTACTCMail("/user/" + usr.username, usr.auth);

        // Get messages for the user
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + usr.auth).
                when().
                get("/user/" + usr.username + "/messages");

        // Parse the response as JSON
        JSONParser parser = new JSONParser(response.body().asString());
        ArrayList<Object> msgList;
        try {
            msgList = parser.parseArray();
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse list of messages as JSON. Root Cause: " + e);
        }

        // For every message
        boolean hasChaosMsg = false;
        boolean hasCrewsMsg = false;
        boolean hasCmailMsg = false;

        for (Object o : msgList) {
            // Get the current message as a linked hash map
            LinkedHashMap<Object, Object> currMsg = (LinkedHashMap<Object, Object>) o;

            // Check if the body of the message is one of the expected three
            String contents = (String) currMsg.get("contents");
            switch (contents) {
                case "TestingChaos" -> hasChaosMsg = true;
                case "TestingCrews" -> hasCrewsMsg = true;
                case "TestingCmail" -> hasCmailMsg = true;
            }
        }

        //  Assert that all three messages were found
        assertTrue(hasChaosMsg);
        assertTrue(hasCrewsMsg);
        assertTrue(hasCmailMsg);

    }

    @Test
    public void testGetMessagesGroup(){
        // Create the TestUser and group
        UserStorage usr = createTestUser();
        String group = createTestGroup(usr);


        // Connect retrievers
        connectTSTACTChaos("/group/" + group, usr.auth);
        connectTSTACTCrews("/group/" + group, usr.auth);
        connectTSTACTCMail("/group/" + group, usr.auth);

        // Get messages for the group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + usr.auth).
                when().
                get("/group/" + group + "/messages");

        // Parse the response as JSON
        JSONParser parser = new JSONParser(response.body().asString());
        ArrayList<Object> msgList;
        try {
            msgList = parser.parseArray();
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse list of messages as JSON. Root Cause: " + e);
        }

        // For every message
        boolean hasChaosMsg = false;
        boolean hasCrewsMsg = false;
        boolean hasCmailMsg = false;

        for (Object o : msgList) {
            // Get the current message as a linked hash map
            LinkedHashMap<Object, Object> currMsg = (LinkedHashMap<Object, Object>) o;

            // Check if the body of the message is one of the expected three
            String contents = (String) currMsg.get("contents");
            switch (contents) {
                case "TestingChaos" -> hasChaosMsg = true;
                case "TestingCrews" -> hasCrewsMsg = true;
                case "TestingCmail" -> hasCmailMsg = true;
            }
        }

        //  Assert that all three messages were found
        assertTrue(hasChaosMsg);
        assertTrue(hasCrewsMsg);
        assertTrue(hasCmailMsg);

    }


    @Test
    public void testClearRetrievers(){
        // Create the TestUser
        UserStorage usr = createTestUser();

        // Connect retrievers
        connectTSTACTChaos("/user/" + usr.username, usr.auth);
        connectTSTACTCrews("/user/" + usr.username, usr.auth);
        connectTSTACTCMail("/user/" + usr.username, usr.auth);

        // Clear the retrievers
        RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + usr.auth).
                when().
                delete("/user/" + usr.username + "/clear-retrievers");

        // Get messages for the user
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + usr.auth).
                when().
                get("/user/" + usr.username + "/messages");

        // Parse the response as JSON
        JSONParser parser = new JSONParser(response.body().asString());
        ArrayList<Object> msgList;
        try {
            msgList = parser.parseArray();
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse list of messages as JSON. Root Cause: " + e);
        }

        // Assert that no messages were returned
        assertEquals(0, msgList.size());
    }



}