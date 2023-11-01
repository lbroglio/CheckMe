package com.example.emailaggregatorapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class NewGroupActivity extends AppCompatActivity {

    private final String JOIN_GROUP_ENDPOINT = "http://coms-309-047.class.las.iastate.edu:8080/group/join/";

    private final String CREATE_GROUP_ENDPOINT = "http://coms-309-047.class.las.iastate.edu:8080/group";

    // Track if the join group request was successful
    private boolean joinGroupRequestSuccess = false;

    // Track if the create group request was successful
    private boolean createGroupRequestSuccess = false;

    private TextView joinGroupError;

    private TextView createGroupError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);

        // Setup error TextViews
        joinGroupError =  (TextView) findViewById(R.id.joinGroupError);
        createGroupError =  (TextView) findViewById(R.id.createGroupError);


        // Setup join group button
        Button groupJoinButton = (Button) findViewById(R.id.joinGroupButton);
        groupJoinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Get the current text in the join code text box
                EditText joinCodeEntry = (EditText) findViewById(R.id.joinGroupTB);
                String joinCode = String.valueOf(joinCodeEntry.getText());

                // Make API request
                try {
                    makeJoinGroupRequest(joinCode);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // If the request was successful return to group screen
                if(joinGroupRequestSuccess){
                    Intent intent = new Intent(NewGroupActivity.this, GroupActivity.class);
                    startActivity(intent);
                }

            }
        });

        // Setup create group button
        Button groupCreateButton = (Button) findViewById(R.id.createGroupButton);
        groupJoinButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Get the current text in the join code text box
                EditText groupNameEntry = (EditText) findViewById(R.id.createGroupTB);
                String groupName = String.valueOf(groupNameEntry.getText());

                // Make API request
                try {
                    makeCreateGroupRequest(groupName);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                // If the request was successful return to group screen
                if(createGroupRequestSuccess){
                    Intent intent = new Intent(NewGroupActivity.this, GroupActivity.class);
                    startActivity(intent);
                }

            }
        });


        // Setup back button
        Button backButton = (Button) findViewById(R.id.backButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Return to Group screen
                Intent intent = new Intent(NewGroupActivity.this, GroupActivity.class);
                startActivity(intent);
            }
        });

    }

    private void makeJoinGroupRequest(String joinCode) throws JSONException {
        // Setup request body
        JSONObject body = new JSONObject();
        body.put("username", UserLoginInfo.username);
        body.put("password", UserLoginInfo.password);

        JsonObjectRequest joinGroupRequest = new JsonObjectRequest(
                Request.Method.PUT,
                (JOIN_GROUP_ENDPOINT + joinCode),
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        joinGroupRequestSuccess = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log error
                        Log.e("Volley Error", error.toString());

                        //Display error to user
                        if(joinGroupError.getVisibility() == View.INVISIBLE){
                            joinGroupError.setVisibility(View.VISIBLE);
                        }
                        String errorMsg = "Could not Join Group. Cause: " + error.toString();
                        joinGroupError.setText(errorMsg);

                    }
                }
        );
        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(joinGroupRequest);
    }

    private void makeCreateGroupRequest(String groupName) throws JSONException {
        // Setup request body
        JSONObject body = new JSONObject();
        body.put("name", groupName);

        JsonObjectRequest createGroupRequest = new JsonObjectRequest(
                Request.Method.PUT,
                (CREATE_GROUP_ENDPOINT),
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        createGroupRequestSuccess = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Log error
                        Log.e("Volley Error", error.toString());

                        //Display error to user
                        if(createGroupError.getVisibility() == View.INVISIBLE){
                            createGroupError.setVisibility(View.VISIBLE);
                        }
                        String errorMsg = "Could not Create Group. Cause: " + error.toString();
                        createGroupError.setText(errorMsg);

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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(createGroupRequest);
    }



}
