package ms_312.CheckMeBackend.TestUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class TestUtils {
    static int logsMade = 0;

    public static void logToFile(String messageToLog){
        File logTo = new File("CheckMeTestLog.txt");
        try{
            FileWriter writer = new FileWriter(logTo, true);
            writer.append(Integer.toString(logsMade++)).append(": ").append(messageToLog).append("\n");
            writer.close();
        }
        catch (IOException e){
            throw new RuntimeException("Could not log to file. Root Cause: " + e);
        }
    }

    public static String getTimeStamp(){
        LocalDateTime now = LocalDateTime.now();
        return now.toString().replace(":","").replace("-","").replace(".","");
    }
}
