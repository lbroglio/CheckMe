package com.example.emailaggregatorapp2;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import android.view.View;
import android.widget.Toast;


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
        String authStr = UserLoginInfo.username + ':' + UserLoginInfo.password;

        //Encode auth str in Base64
        String encodedStr = Base64.encodeToString(authStr.getBytes(), Base64.DEFAULT);


        URI serverURI = new URI("wss://livechat/" + encodedStr +"/");

        webSocketClient = new WebSocketClient(serverURI) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                runOnUiThread(() -> showToast("WebSocket connection opened"));
            }

            @Override
            public void onMessage(String message) {
                runOnUiThread(() -> {
                    // Handle incoming messages
                    messageList.add(message);
                    messageAdapter.notifyDataSetChanged();
                });
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                runOnUiThread(() -> showToast("WebSocket connection closed"));
            }

            @Override
            public void onError(Exception ex) {
                runOnUiThread(() -> showToast("WebSocket connection error"));
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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webSocketClient != null) {
            webSocketClient.close();
        }
    }
}
