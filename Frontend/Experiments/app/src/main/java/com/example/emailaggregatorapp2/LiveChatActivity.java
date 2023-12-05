package com.example.emailaggregatorapp2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import java.net.URI;
import java.util.ArrayList;
import android.view.View;

import org.java_websocket.drafts.Draft_6455;


import java.net.URISyntaxException;



public class LiveChatActivity extends AppCompatActivity {
    private WebSocketClient webSocketClient;
    private ListView chatMessageListView;
    private EditText messageInput;
    private Button sendButton;
    private ArrayAdapter<String> messageAdapter;
    private ArrayList<String> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_livechat);

        chatMessageListView = findViewById(R.id.chatMessageListView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);

        messageList = new ArrayList<>();
        messageAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, messageList);
        chatMessageListView.setAdapter(messageAdapter);

        try {
            // Initialize and connect to the WebSocket server
            connectToWebSocketServer();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
    }

    private void connectToWebSocketServer() throws URISyntaxException {
        Draft[] drafts = {
                new Draft_6455()
        };
        String authStr = UserLoginInfo.username + ':' + UserLoginInfo.password;

        //Encode auth str in Base64
        String encodedStr = Base64.encodeToString(authStr.getBytes(), Base64.DEFAULT).trim();

        URI serverURI = new URI((String)("ws://coms-309-047.class.las.iastate.edu:8080/livechat/" + encodedStr +"/"+UserSelectedGroup.groupname+"/").trim());

        Log.d("URI", serverURI.toString());

        webSocketClient = new WebSocketClient(serverURI, (Draft) drafts[0]) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                Log.d("OPEN", "run() returned: " + "is connecting");
            }

            @Override
            public void onMessage(String message) {
                    // Handle incoming messages
                    messageList.add(message);
                    messageAdapter.notifyDataSetChanged();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                Log.d("CLOSE", "onClose() returned: " + reason);
            }

            @Override
            public void onError(Exception e) {
                Log.d("Exception:", e.toString());
            }
        };
        webSocketClient.connect();
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            webSocketClient.send(message);
            messageInput.setText("");
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
