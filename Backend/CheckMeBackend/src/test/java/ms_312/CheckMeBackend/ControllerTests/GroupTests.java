package ms_312.CheckMeBackend.ControllerTests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import ms_312.CheckMeBackend.CheckMeBackendApplication;
import ms_312.CheckMeBackend.TestUtils.TestUtils;
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

import static ms_312.CheckMeBackend.TestUtils.TestUtils.getTimeStamp;
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

    public UserStorage createUser(String username, String email, String password){
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

        return new UserStorage(username,  password);
    }


    public UserStorage createTestUser(){
        return createUser("TestUser"+getTimeStamp(), "testing@gmail.com", "TestPass");
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


    @Test
    public void testCreateGroup(){
        UserStorage user =  createTestUser();

        // Body to send for the request
        String name = "TestGroup" + getTimeStamp();
        String requestBody = "{\n\t \"name\": \"" + name + "\"\n}";

        // Send request to create a group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + user.auth).
                body(requestBody).
                when().
                post("/group");

        // Check that the username and email fields as expected
        assertEquals("Created Group: "+ name,  response.body().asString());
    }

    @Test
    public void testGetGroupByName(){
        // Create the group to test on
        UserStorage usr = createTestUser();
        String groupName = createTestGroup(usr);

        // Use the see endpoint to get the Group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + usr.auth).
                when().
                get("/group/name/" + groupName);

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
        assertEquals(groupName,  group.get("name"));
        assertEquals(usr.username,  member.get("name"));

    }



    @Test
    public void testGetGroupByJoinCode(){
        // Create the group to test on
        UserStorage usr = createTestUser();
        String groupName = createTestGroup(usr);

        // --- GET THE JOIN CODE BY GETTING THE GROUP BY NAME ---

        // Use the see endpoint to get the Group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + usr.auth).
                when().
                get("/group/name/" + groupName);

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> group;
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response as valid JSON. Root Cause: " + e);
        }

        // Get the join code from the Group
        String joinCode = (String) group.get("joinCode");


        // --- TEST GETTING THE GROUP USING THE JOIN CODE ---

        // Use the see endpoint to get the Group
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + usr.auth).
                when().
                get("/group/code/" + joinCode);


        // Parse the response as JSON
        responseParser = new JSONParser(response.body().asString());
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response as valid JSON. Root Cause: " + e);
        }

        // Get the first member from the Group
        ArrayList<Object> members = (ArrayList<Object>) group.get("members");
        LinkedHashMap<Object, Object> member = (LinkedHashMap<Object, Object>) members.get(0);

        // Check that the Group has expected fields
        assertEquals(groupName,  group.get("name"));
        assertEquals(usr.username,  member.get("name"));
        assertEquals(joinCode, group.get("joinCode"));

    }

    @Test
    public void testJoinGroup(){
        // Create the group to test on
        UserStorage usr = createTestUser();
        String groupName = createTestGroup(usr);
        UserStorage joiner = createUser("JoinTestUser"+getTimeStamp(), "joining@gmail.com","JT123");



        // --- GET THE JOIN CODE  ---

        // Use the see endpoint to get the Group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + usr.auth).
                when().
                get("/group/name/" + groupName);

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> group;
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response as valid JSON. Root Cause: " + e);
        }

        // Get the join code from the Group
        String joinCode = (String) group.get("joinCode");


        // --- JOIN THE GROUP AS STEVE  ---

        // Body to send for the request
        String requestBody = "{\n" + "\t\"username\": \"" + joiner.username + "\",\n" + "\t\"password\": \"" + joiner.password + "\"\n}";


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
                header("Authorization","Basic " + usr.auth).
                when().
                get("/group/name/"+groupName);

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
        boolean memFound = false;
        for(int i=0; i < members.size(); i++){
            LinkedHashMap<Object, Object> currMember = (LinkedHashMap<Object, Object>) members.get(i);
            if(currMember.get("name").equals(joiner.username)){
                memFound =  true;
                i += members.size();
            }
        }

        // Confirm the steve was found as a member
        assertTrue(memFound);

        // --- CONFIRM GROUP WAS ADDED TO HOCKEYSTEVE'S ACCOUNT

        // Use endpoint to see groups
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization",joiner.password).
                when().
                get("/user/" + joiner.username + "/groups");

        // Parse the response as JSON

        responseParser = new JSONParser(response.body().asString());
        ArrayList<Object> respJSON;
        try{
            respJSON = responseParser.parseArray();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response as valid JSON. Root Cause: " + e);
        }

        // Get the group from the parsed ArrayList
       group = (LinkedHashMap<Object, Object>) respJSON.get(0);

        // Check that the Group has expected fields
        assertEquals(groupName,  group.get("name"));
    }

    @Test
    public void testPromoteMember() {
        UserStorage creator = createTestUser();
        String name = createTestGroup(creator);
        UserStorage toPromote = createUser("PROMOTE_TEST"+getTimeStamp(), "default@gmail.com", "Pass123");


        // --- GET THE JOIN CODE  ---


        // Use the see endpoint to get the Group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + creator.auth).
                when().
                get("/group/name/" + name);

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> group;
        try {
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse response as valid JSON. Root Cause: " + e);
        }

        // Get the join code from the Group
        String joinCode = (String) group.get("joinCode");


        // --- JOIN THE GROUP AS A TEST USER  ---

        // Body to send for the request
        String requestBody = "{\n" + "\t\"username\": \"" + toPromote.username + "\",\n" + "\t\"password\": \"" + toPromote.password + "\"\n}";


        // Use the join endpoint
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                body(requestBody).
                when().
                put("/group/join/" + joinCode);


        // --- PROMOTE THE TEST USER ---

        // Body to send for the request
         requestBody = "{ \n\"toPromote\": \"" + toPromote.username + "\"\n}";

        // Use the promote endpoint
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + creator.auth).
                body(requestBody).
                when().
                put("/group/promote/"+name);

        // Test the response
        assertEquals("User " + toPromote.username + " added to admins of group " + name, response.body().asString());



        // --- CONFIRM TEST USER WAS ADDED TO LIST OF ADMINS ---

        // Use the see endpoint to get the Group
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + creator.auth).
                when().
                get("/group/name/"+name);

        // Parse the response as JSON

        responseParser = new JSONParser(response.body().asString());
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response as valid JSON. Root Cause: " + e);
        }


        //Verify there is a User in the group named HockeySteve
        ArrayList<Object> admins = (ArrayList<Object>) group.get("admins");
        boolean userFound = false;
        for(int i=0; i < admins.size(); i++){
            if(admins.get(i).equals(toPromote.username)){
                userFound =  true;
                i += admins.size();
            }
        }

        // Confirm the steve was found as an admin
        assertTrue(userFound);
    }

    @Test
    public void testRemoveMember() {
        UserStorage usr = createTestUser();
        String name = createTestGroup(usr);
        UserStorage toRemove = createUser("REMOVE_TEST"+getTimeStamp(), "default@gmail.com", "Pass123");


        // --- GET THE JOIN CODE  ---

        // Use the see endpoint to get the Group
        Response response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + usr.auth).
                when().
                get("/group/name/"+name);

        // Parse the response as JSON

        JSONParser responseParser = new JSONParser(response.body().asString());
        LinkedHashMap<Object, Object> group;
        try {
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        } catch (ParseException e) {
            throw new RuntimeException("Could not parse response as valid JSON. Root Cause: " + e);
        }

        // Get the join code from the Group
        String joinCode = (String) group.get("joinCode");


        // --- JOIN THE GROUP AS A TEST USER  ---

        // Body to send for the request
        String requestBody = "{\n" + "\t\"username\": \"" + toRemove.username + "\",\n" + "\t\"password\": \"" + toRemove.password + "\"\n}";


        // Use the join endpoint
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                body(requestBody).
                when().
                put("/group/join/" + joinCode);


        // --- PROMOTE THE TEST USER ---

        // Body to send for the request
        requestBody = "{ \n\"toRemove\": \"" + toRemove.username + "\"\n}";


        // Use the remove endpoint
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset", "utf-8").
                header("Authorization", "Basic " + usr.auth).
                body(requestBody).
                when().
                delete("/group/remove/"+name);

        // Test the response
        assertEquals("User " + toRemove.username + " removed from the group " + name, response.body().asString());



        // --- CONFIRM TEST USER WAS ADDED TO LIST OF ADMINS ---

        // Use the see endpoint to get the Group
        response = RestAssured.given().
                header("Content-Type", "text/plain").
                header("charset","utf-8").
                header("Authorization","Basic " + usr.auth).
                when().
                get("/group/name/"+name);

        // Parse the response as JSON

        responseParser = new JSONParser(response.body().asString());
        try{
            group = (LinkedHashMap<Object, Object>) responseParser.parse();
        }
        catch (ParseException e){
            throw new RuntimeException("Could not parse response  as valid JSON. Root Cause: " + e);
        }


        //Verify there is no User in the group named REMOVE_TEST
        ArrayList<Object> members = (ArrayList<Object>) group.get("members");
        boolean userFound = false;
        for(int i=0; i < members.size(); i++){
            if(members.get(i).equals(toRemove.username)){
                userFound =  true;
                i += members.size();
            }
        }

        // Confirm the steve was found as a member
        assertFalse(userFound);
    }


}