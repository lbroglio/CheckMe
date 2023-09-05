package group312.CheckMeDemo;

import org.json.JSONArray;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.io.FileWriter;

public class dumpJSON {

    public static void backup(String[] args) throws IOException {
        Message[] messages = new Message[3];

        LocalDateTime send1 =  LocalDateTime.of(LocalDate.of(2023,8,31), LocalTime.of(8, 31, 0));
        LocalDateTime send2 =  LocalDateTime.of(LocalDate.of(2023,8,29), LocalTime.of(13, 0, 0));
        LocalDateTime send3 =  LocalDateTime.of(LocalDate.of(2023,8,30), LocalTime.of(12, 8, 14));


        messages[0] =  new Message("Mike","Hello want to hangout",null,send1, "discord");
        messages[1] =  new Message("Will","Want to meet later","Meeting",send2, "Gmail");
        messages[2] =  new Message("Lizzie","Can you help me with a project",null,send3, "Teams");

        JSONArray output = new JSONArray(messages);

        FileWriter outputFile = new FileWriter("messages.json");
        outputFile.write(output.toString());

        outputFile.close();


    }
}
