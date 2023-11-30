package ms_312.CheckMeBackend.ClassTests;

import ms_312.CheckMeBackend.Messages.Message;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class MessageClassTests {
    private Message createTestMessage(){
        return new Message("BaseballBob", "UnitTest", "Hello, World!", "Testing",LocalDateTime.parse("2000-01-01T00:00:00"));
    }

    @Test
    public void testGetSender(){
        Message testOn = createTestMessage();

        assertEquals("BaseballBob", testOn.getSender());
    }

    @Test
    public void testGetContents(){
        Message testOn = createTestMessage();

        assertEquals("Hello, World!", testOn.getContents());
    }

    @Test
    public void testGetSubject(){
        Message testOn = createTestMessage();

        assertEquals("Testing", testOn.getSubject());
    }

    @Test
    public void testGetSendTime(){
        Message testOn = createTestMessage();

        assertEquals(LocalDateTime.parse("2000-01-01T00:00:00"), testOn.getSendTime());
    }

    @Test
    public void testNullSubjectMessage(){
        Message testOn = new Message("BaseballBob", "UnitTest", "Hello, World!", LocalDateTime.parse("2000-01-01T00:00:00"));

        //Check that the message was successfully created by checking sender
        assertEquals("BaseballBob", testOn.getSender());
        assertNull(testOn.getSubject());
    }

    @Test
    public void testSetSender(){
        Message testOn = createTestMessage();
        testOn.setSender("HockeySteve");

        assertEquals("HockeySteve", testOn.getSender());
    }

    @Test
    public void testSetContents(){
        Message testOn = createTestMessage();
        testOn.setContents("I'm sorry Dave");

        assertEquals("I'm sorry Dave", testOn.getContents());
    }

    @Test
    public void testSetSubject(){
        Message testOn = createTestMessage();
        testOn.setSubject("Greeting");

        assertEquals("Greeting", testOn.getSubject());
    }


}
