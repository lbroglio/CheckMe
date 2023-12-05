package com.example.emailaggregatorapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

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

import android.widget.TextView;

public class GroupActivity extends AppCompatActivity {

    private final String USER_API_ENDPOINT ="http://coms-309-047.class.las.iastate.edu:8080/user/"+UserLoginInfo.username+"/groups";

    private final String GROUP_API_ENDPOINT ="http://coms-309-047.class.las.iastate.edu:8080/group/name/";

    private String selectedGroup = null;

    private int numButtons = 0;

    // Used to assign created TextViews with ids
    private int numMessages = 1;

    private ArrayList<String> userGroups = new ArrayList<>();

    private ArrayList<String> messages = new ArrayList<>();

    private void addMessages(){

        // Get the layout to add the button to
        LinearLayout messageAddTo = (LinearLayout) findViewById(R.id.groupMessageLayout);


        // Add a button for every group in the list
        for(int i=0; i < messages.size(); i++){

            // Create a new button and set its id
            TextView newMessage = new TextView(this);
            newMessage.setId(numMessages * 1000);

            // Increment the next id to set
            numMessages++;

            // Set the text for the new button to be the name of its corresponding group
            String messageContent = messages.get(i);
            newMessage.setText(messageContent);
            Log.d("MESSAGE" + i, messageContent);

            // Add Message TextView to layout
            newMessage.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            messageAddTo.addView(newMessage);


        }
    }


    private void addGroupButtons(){

        // Get the layout to add the button to
        LinearLayout addTo = (LinearLayout) findViewById(R.id.groupButtonLayout);


        // Add a button for every group in the list
        for(int i=0; i < userGroups.size(); i++){

            // Create a new button and set its id
            Button newButton = new Button(this);
            newButton.setId(numButtons);

            // Increment the next id to set
            numButtons++;

            // Set the text for the new button to be the name of its corresponding group
            String groupTitle = userGroups.get(i);
            newButton.setText(groupTitle);
            Log.d("GROUP" + i, groupTitle);

            // Create a listener for when the new button is clicked
            newButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    // Set the Title text to be the current group name
                    TextView groupHeader = (TextView)  findViewById(R.id.groupTitle);
                    groupHeader.setText(groupTitle);
                    selectedGroup = groupTitle;
                    UserSelectedGroup.groupname = groupTitle;
                    // Remove existing messages
                    LinearLayout messageLayout = (LinearLayout) findViewById(R.id.groupMessageLayout);
                    messageLayout.removeAllViews();
                    messages = new ArrayList<>();

                    // Add new messages
                    makeMessageRequest();
                }
            });

            // Add button to layout
            newButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            addTo.addView(newButton);

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);

        // Populate the list of Groups
        makeUserGroupRequest();

        // Add a Button for all Groups the User is in

        // Get the layout to add the button to
        LinearLayout addTo = (LinearLayout) findViewById(R.id.groupButtonLayout);

        // Setup button for making a new Group
        // Create a new button and set its id
        Button newButton = new Button(this);
        newButton.setId(numButtons);

        // Increment the next id to set
        numButtons++;

        // Set the text for the new button
        String buttonText = "New Group";
        newButton.setText(buttonText);

        // Create a listener for when the new button is clicked
        newButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Go to the screen for making a new group
                Intent intent = new Intent(GroupActivity.this, NewGroupActivity.class);
                startActivity(intent);
            }
        });

        // Add button to layout
        newButton.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        addTo.addView(newButton);

        //Setup back button
        Button backButton = (Button) findViewById(R.id.backButtonGroupActiv);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Return to Message Screen
                Intent intent = new Intent(GroupActivity.this, MessagesActivity.class);
                startActivity(intent);
            }
        });

        //Setup Add account button
        Button addAccountButton = (Button) findViewById(R.id.addAccountGroups);
        addAccountButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // If a group has been selected
                if(selectedGroup != null){
                    // Setup for adding for groups
                    AddAccountActivity.targetGroup = selectedGroup;
                    Intent intent = new Intent(GroupActivity.this, AddAccountActivity.class);
                    startActivity(intent);
                }

            }
        });

        //Setup Live chat account button
        Button liveChatButton = (Button) findViewById(R.id.groupChatButton);
        liveChatButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // If a group has been selected
                if(selectedGroup != null){
                    Intent intent = new Intent(GroupActivity.this, LiveChatActivity.class);
                    startActivity(intent);
                }

            }
        });




    }

    private void makeUserGroupRequest(){

        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                (USER_API_ENDPOINT),
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //Add all Groups in the response to the Group list
                        for(int i=0; i < response.length(); i++){
                            // Get the item at the current index
                            JSONObject curr;
                            try{
                                // Unchecked cast is from interacting with JSON API
                                curr = (JSONObject) response.get(i);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            // Get the name of the current group
                            String name;
                            try {
                                name = (String) curr.get("name");
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                            //Add the current group name to the list
                            userGroups.add(name);

                        }

                        addGroupButtons();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        Log.e("Volley Error", error.toString());
                    }
                }
        ){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");
                params.put("Authorization", UserLoginInfo.password);
                return params;
            }

        };


        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    private void makeMessageRequest() {
        JsonArrayRequest jsonObjectRequest = new JsonArrayRequest(
                Request.Method.GET,
                GROUP_API_ENDPOINT + selectedGroup + "/messages",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Go through every retrieved Message
                        for (int i = 0; i < response.length(); i++) {
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
                                        + msg.getString("subject") + "\nBody: " + msg.getString("contents")
                                        + "\nTime: " + msg.getString("sendTime") + "\n\n";
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
        ) {
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
