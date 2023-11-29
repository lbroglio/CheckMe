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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Base64;
import java.util.LinkedHashMap;

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
        // Create BaseballBob and calculate his authn
        createBaseballBob();
        String auth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        Response resp = connectTSTACTChaos("/user/BaseballBob", auth);

        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testConnectCrewsUser(){
        // Create BaseballBob and calculate his authn
        createBaseballBob();
        String auth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        Response resp = connectTSTACTCrews("/user/BaseballBob", auth);

        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testConnectCMailsUser(){
        // Create BaseballBob and calculate his authn
        createBaseballBob();
        String auth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        Response resp = connectTSTACTCMail("/user/BaseballBob", auth);

        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testConnectChaosGroup(){
        // Create BaseballBob and calculate his authn
        createCubsFans();
        String auth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        Response resp = connectTSTACTChaos("/group/CubsFans", auth);

        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testConnectCrewsGroup(){
        // Create BaseballBob and calculate his authn
        createBaseballBob();
        String auth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        Response resp = connectTSTACTCrews("/group/CubsFans", auth);

        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testConnectCMailsGroup(){
        // Create BaseballBob and calculate his auth
        createBaseballBob();
        String auth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        Response resp = connectTSTACTCMail("/group/CubsFans", auth);
        //Assert that the response was as expected
        assertEquals("Retriever Added", resp.body().asString());
    }

    @Test
    public void testGetMessagesUser(){
        // Create BaseballBob and calculate its auth
        createUser("msgTest", "default@gmail.com", "TestPass");
        String auth = Base64.getEncoder().encodeToString("msgTest:TestPass".getBytes());

        // Connect retrievers
        connectTSTACTChaos("/user/msgTest", auth);
        connectTSTACTCrews("/user/msgTest", auth);
        connectTSTACTCMail("/user/msgTest", auth);

        // Get messages for the user
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + auth).
                when().
                get("/user/msgTest/messages");

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
        // Create BaseballBob and calculate its auth
        createUser("msgTest", "default@gmail.com", "TestPass");
        String auth = Base64.getEncoder().encodeToString("msgTest:TestPass".getBytes());

        // Connect retrievers
        connectTSTACTChaos("/user/msgTest", auth);
        connectTSTACTCrews("/user/msgTest", auth);
        connectTSTACTCMail("/user/msgTest", auth);

        // Clear the retrievers
        RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + auth).
                when().
                delete("/user/msgTest/clear-retrievers");

        // Get messages for the user
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + auth).
                when().
                get("/user/msgTest/messages");

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