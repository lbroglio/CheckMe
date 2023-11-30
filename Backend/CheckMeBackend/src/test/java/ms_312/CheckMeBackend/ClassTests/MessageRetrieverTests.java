package ms_312.CheckMeBackend.ClassTests;

import ms_312.CheckMeBackend.Messages.Message;
import ms_312.CheckMeBackend.Messages.Retrievers.DemoRetriever;
import ms_312.CheckMeBackend.Messages.Retrievers.MessageRetriever;
import ms_312.CheckMeBackend.Users.User;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MessageRetrieverTests {

    private MessageRetriever createDemoRetriever(){
        return new DemoRetriever("https://example.com", new User("BaseballBob", "example@gmail.com", new byte[0], new byte[0]));
    }

    @Test
    public void testGetSource(){
        MessageRetriever testOn = createDemoRetriever();

        assertEquals("https://example.com", testOn.getSource());
    }

    @Test
    public void testGetOwner(){
        MessageRetriever testOn = createDemoRetriever();

        assertEquals(new User("BaseballBob", "example@gmail.com", new byte[0], new byte[0]), testOn.getOwner());
    }

    @Test
    public void testSetOwner(){
        MessageRetriever testOn = createDemoRetriever();
        testOn.setOwner(new User("HockeySteve", "example@gmail.com", new byte[0], new byte[0]));

        assertEquals(new User("HockeySteve", "example@gmail.com", new byte[0], new byte[0]), testOn.getOwner());
    }

    @Test
    public void testGetAll(){
        MessageRetriever testOn = createDemoRetriever();

        assertArrayEquals(new Message[0], testOn.getAll());
    }
}
