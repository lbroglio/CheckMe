package com.example.emailaggregatorapp2;

import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import android.widget.TextView;
import android.widget.ToggleButton;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MessagesActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        //TODO - Rework for Message frontend


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

    private void makeJSONObjectPostRequest(String sender, String recipient, String subject, String contents){
        JSONObject body = new JSONObject();
        try {
            body.put("sender",sender);
            body.put("recipient",recipient);
            body.put("contents",contents);
            body.put("subject",subject);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                "API_URL_POST",
                body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the successful response here
                        System.out.println(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        System.out.println("Error: " + error.toString());
                    }
                }
        );

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }


    private void makeJSONObjectGetRequest(String user){
        JSONObject body = new JSONObject();

        StringRequest jsonObjectRequest = new StringRequest(
                Request.Method.GET,
                ("API_URL_GET" + user),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        System.out.println("Error: " + error.toString());
                    }
                }
        );


        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }
}
