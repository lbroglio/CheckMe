package com.example.emailaggregatorapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MessagesActivity extends AppCompatActivity{

    private final String API_URL = "http://coms-309-047.class.las.iastate.edu:8080/user/" +  UserLoginInfo.username + "/messages";

    private ArrayList<String> messages = new ArrayList<>();

    // Used to assign created TextViews with ids
    private int numMessages =0;


    private void addMessages(){

        // Get the layout to add the button to
        LinearLayout addTo = (LinearLayout) findViewById(R.id.messageListLayout);


        // Add a button for every group in the list
        for(int i=0; i < messages.size(); i++){

            // Create a new button and set its id
            TextView newMessage = new TextView(this);
            newMessage.setId(numMessages);

            // Increment the next id to set
            numMessages++;

            // Set the text for the new button to be the name of its corresponding group
            String messageContent = messages.get(i);
            newMessage.setText(messageContent);
            Log.d("MESSAGE" + i, messageContent);

            // Add Message TextView to layout
            newMessage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            addTo.addView(newMessage);


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        // Get the User's messages from the API and add them to the screen
        makeMessageRequest();


        // Navigation Bar

        //Group for all Navbar Objects
        Group navBar = (Group)  findViewById(R.id.NavBarGroup);

        // Button for toggling Visibility
        ToggleButton visButton = (ToggleButton) findViewById(R.id.navBarToggle);

        // Button for going to accounts page
        Button accountButton = (Button) findViewById(R.id.TPAccountsGoTo);

        //Button for going to groups page
        Button groupsButton = (Button) findViewById(R.id.groupButton);

        // Toggle navbar visibility
        visButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(navBar.getVisibility() == View.GONE){
                    navBar.setVisibility(View.VISIBLE);
                }
                else{
                    navBar.setVisibility(View.GONE);

                }
            }
        });

        // Go to third party accounts Screen
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Configure for adding for the user
                AddAccountActivity.targetGroup = null;
                Intent intent = new Intent(MessagesActivity.this, AddAccountActivity.class);
                startActivity(intent);
            }
        });

        // Go to groups Screen
        groupsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MessagesActivity.this, GroupActivity.class);
                startActivity(intent);
            }
        });


    }

    private void makeMessageRequest(){
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                       // Go through every retrieved Message
                        for(int i=0; i <  response.length(); i++){
                            // Get the Message as a JSON object
                            JSONObject msg;
                            try {
                                 msg = response.getJSONObject(i);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            // Use the object to create a string with the message contents
                            String msgStr;
                            try {
                                msgStr = "From: " + msg.getString("sender") + "\nSubject: "
                                        + msg.getString("subject") +"\nBody: " + msg.getString("contents")
                                        + "\nTime: " + msg.getString("sendTime")+"\n\n";
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                            Log.d("HERE", msgStr);
                            // Add the Message to the list
                            messages.add(msgStr);

                        }

                        // Put messages on screen
                        addMessages();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        System.out.println("Error: " + error.toString());
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");

                //Setup Basic Auth
                String authStr = UserLoginInfo.username + ':' + UserLoginInfo.password;

                //Encode auth str in Base64
                String encodedStr = Base64.encodeToString(authStr.getBytes(), Base64.DEFAULT);

                // Set up auth header
                params.put("Authorization", "Basic " + encodedStr);
                return params;
            }
            
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }
}
