package ms_312.CheckMeBackend.ControllerTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import ms_312.CheckMeBackend.CheckMeBackendApplication;
import ms_312.CheckMeBackend.TestUtils.TestUtils;
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

import static ms_312.CheckMeBackend.TestUtils.TestUtils.logToFile;
import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = CheckMeBackendApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GroupTests {
    @LocalServerPort
    int port;

    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
    }

    public void createUser(String username, String email, String password){
        // Body to send for the request
        String requestBody = "{\n" +  "\t\"username\": \"" + username +"\",\n\t\"email_address\": \"" + email + "\",\n"
                + "\t\"password\": \"" + password + "\"\n}";

        // Send request and receive response
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body(requestBody).
                when().
                post("/user");
    }

    public void createBaseballBob(){
        createUser("BaseballBob", "cubsbob@yahoo.com","CubsGo123");
    }

    public void createCubsFans(){
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
    }


    @Test
    public void testCreateGroup(){
        createBaseballBob();

        // Body to send for the request
        String requestBody = """
                {
                    "name": "TestGroup"
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

        // Check that the username and email fields as expected
        assertEquals("Created Group: TestGroup",  response.body().asString());
    }

    @Test
    public void testGetGroupByName(){
        // Create the group to test on
        createCubsFans();

        //Authorization header to use for the endpoint
        String basicAuth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        // Use the see endpoint to get the Group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + basicAuth).
                when().
                get("/group/name/CubsFans");

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> group;
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /user/BaseballBob/groups as valid JSON. Root Cause: " + e);
        }

        // Get the first member from the Group
        ArrayList<Object> members = (ArrayList<Object>) group.get("members");
        LinkedHashMap<Object, Object> member = (LinkedHashMap<Object, Object>) members.get(0);


        // Check that the Group has expected fields
        assertEquals("CubsFans",  group.get("name"));
        assertEquals("BaseballBob",  member.get("name"));


    }



    @Test
    public void testGetGroupByJoinCode(){
        // Create the group to test on
        createCubsFans();

        //Authorization header to use for the endpoint
        String basicAuth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        // --- GET THE JOIN CODE BY GETTING THE GROUP BY NAME ---

        // Use the see endpoint to get the Group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + basicAuth).
                when().
                get("/group/name/CubsFans");

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> group;
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /group/name/CubsFans as valid JSON. Root Cause: " + e);
        }

        // Get the join code from the Group
        String joinCode = (String) group.get("joinCode");


        // --- TEST GETTING THE GROUP USING THE JOIN CODE ---

        // Use the see endpoint to get the Group
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + basicAuth).
                when().
                get("/group/code/" + joinCode);


        // Parse the response as JSON
        responseParser = new JSONParser(response.body().asString());
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /user/BaseballBob/groups as valid JSON. Root Cause: " + e);
        }

        // Get the first member from the Group
        ArrayList<Object> members = (ArrayList<Object>) group.get("members");
        LinkedHashMap<Object, Object> member = (LinkedHashMap<Object, Object>) members.get(0);

        // Check that the Group has expected fields
        assertEquals("CubsFans",  group.get("name"));
        assertEquals("BaseballBob",  member.get("name"));
        assertEquals(joinCode, group.get("joinCode"));

    }

    @Test
    public void testJoinGroup(){
        createCubsFans();
        createUser("HockeySteve", "wildsteve@gmail.com","WildGo123");



        // --- GET THE JOIN CODE  ---

        //Authorization header to use for the endpoint
        String basicAuth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        // Use the see endpoint to get the Group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + basicAuth).
                when().
                get("/group/name/CubsFans");

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> group;
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /group/name/CubsFans as valid JSON. Root Cause: " + e);
        }

        // Get the join code from the Group
        String joinCode = (String) group.get("joinCode");


        // --- JOIN THE GROUP AS STEVE  ---

        // Body to send for the request
        String requestBody = """
                {
                    "username": "HockeySteve",
                    "password": "WildGo123"
                }
                """;

        // Use the join endpoint
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                body(requestBody).
                when().
                put("/group/join/"+joinCode);

        //Test that the response is accurate
        assertEquals("Group Joined", response.body().asString());

        // -- VERIFY HockeySteve IS NOW A MEMBER OF THE  GROUP

        // Use the see endpoint to get the Group
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + basicAuth).
                when().
                get("/group/name/CubsFans");

        // Parse the response as JSON

        responseParser = new JSONParser(response.body().asString());
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /group/name/CubsFans as valid JSON. Root Cause: " + e);
        }


        //Verify there is a User in the group named HockeySteve
        ArrayList<Object> members = (ArrayList<Object>) group.get("members");
        boolean steveFound = false;
        for(int i=0; i < members.size(); i++){
            LinkedHashMap<Object, Object> currMember = (LinkedHashMap<Object, Object>) members.get(i);
            if(currMember.get("name").equals("HockeySteve")){
                steveFound =  true;
                i += members.size();
            }
        }

        // Confirm the steve was found as a member
        assertTrue(steveFound);

        // --- CONFIRM GROUP WAS ADDED TO HOCKEYSTEVE'S ACCOUNT

        // Use endpoint to see groups
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","WildGo123").
                when().
                get("/user/HockeySteve/groups");

        // Parse the response as JSON

        responseParser = new JSONParser(response.body().asString());
        ArrayList<Object> respJSON;
        try{
            respJSON = responseParser.parseArray();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /user/HockeySteve/groups as valid JSON. Root Cause: " + e);
        }

        // Get the group from the parsed ArrayList
       group = (LinkedHashMap<Object, Object>) respJSON.get(0);

        // Check that the Group has expected fields
        assertEquals("CubsFans",  group.get("name"));
    }

    @Test
    public void testPromoteMember() {
        createCubsFans();
        createUser("PROMOTE_TEST", "default@gmail.com", "Pass123");


        // --- GET THE JOIN CODE  ---

        //Authorization header to use for the endpoint
        String basicAuth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        // Use the see endpoint to get the Group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + basicAuth).
                when().
                get("/group/name/CubsFans");

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> group;
        try {
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse response from /group/name/CubsFans as valid JSON. Root Cause: " + e);
        }

        // Get the join code from the Group
        String joinCode = (String) group.get("joinCode");


        // --- JOIN THE GROUP AS A TEST USER  ---

        // Body to send for the request
        String requestBody = """
                {
                    "username": "PROMOTE_TEST",
                    "password": "Pass123"
                }
                """;

        // Use the join endpoint
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                body(requestBody).
                when().
                put("/group/join/" + joinCode);


        // --- PROMOTE THE TEST USER ---

        // Body to send for the request
         requestBody = """
                {
                    "toPromote": "PROMOTE_TEST"
                }
                """;

        // Use the promote endpoint
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + basicAuth).
                body(requestBody).
                when().
                put("/group/promote/CubsFans");

        // Test the response
        assertEquals("User PROMOTE_TEST added to admins of group CubsFans", response.body().asString());



        // --- CONFIRM TEST USER WAS ADDED TO LIST OF ADMINS ---

        // Use the see endpoint to get the Group
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + basicAuth).
                when().
                get("/group/name/CubsFans");

        // Parse the response as JSON

        responseParser = new JSONParser(response.body().asString());
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /group/name/CubsFans as valid JSON. Root Cause: " + e);
        }


        //Verify there is a User in the group named HockeySteve
        ArrayList<Object> admins = (ArrayList<Object>) group.get("admins");
        boolean userFound = false;
        for(int i=0; i < admins.size(); i++){
            if(admins.get(i).equals("PROMOTE_TEST")){
                userFound =  true;
                i += admins.size();
            }
        }

        // Confirm the steve was found as a member
        assertTrue(userFound);
    }

    @Test
    public void testRemoveMember() {
        createCubsFans();
        createUser("REMOVE_TEST", "default@gmail.com", "Pass123");


        // --- GET THE JOIN CODE  ---

        //Authorization header to use for the endpoint
        String basicAuth = Base64.getEncoder().encodeToString("BaseballBob:CubsGo123".getBytes());

        // Use the see endpoint to get the Group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + basicAuth).
                when().
                get("/group/name/CubsFans");

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> group;
        try {
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse response from /group/name/CubsFans as valid JSON. Root Cause: " + e);
        }

        // Get the join code from the Group
        String joinCode = (String) group.get("joinCode");


        // --- JOIN THE GROUP AS A TEST USER  ---

        // Body to send for the request
        String requestBody = """
                {
                    "username": "REMOVE_TEST",
                    "password": "Pass123"
                }
                """;

        // Use the join endpoint
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                body(requestBody).
                when().
                put("/group/join/" + joinCode);


        // --- PROMOTE THE TEST USER ---

        // Body to send for the request
        requestBody = """
                {
                    "toRemove": "REMOVE_TEST"
                }
                """;

        // Use the promote endpoint
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + basicAuth).
                body(requestBody).
                when().
                delete("/group/remove/CubsFans");

        // Test the response
        assertEquals("User REMOVE_TEST removed from the group CubsFans", response.body().asString());



        // --- CONFIRM TEST USER WAS ADDED TO LIST OF ADMINS ---

        // Use the see endpoint to get the Group
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + basicAuth).
                when().
                get("/group/name/CubsFans");

        // Parse the response as JSON

        responseParser = new JSONParser(response.body().asString());
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response from /group/name/CubsFans as valid JSON. Root Cause: " + e);
        }


        //Verify there is no User in the group named REMOVE_TEST
        ArrayList<Object> admins = (ArrayList<Object>) group.get("admins");
        boolean userFound = false;
        for(int i=0; i < admins.size(); i++){
            if(admins.get(i).equals("REMOVE_TEST")){
                userFound =  true;
                i += admins.size();
            }
        }

        // Confirm the steve was found as a member
        assertFalse(userFound);
    }


}