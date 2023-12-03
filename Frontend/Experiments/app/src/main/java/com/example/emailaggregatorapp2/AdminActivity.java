package com.example.emailaggregatorapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity{
    public static String targetGroup = null;

    private final String API_URL = "http://coms-309-047.class.las.iastate.edu:8080/";

    private String targetService = "";

    TextView errorText;
    private boolean requestComplete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_acount);

        errorText = (TextView) findViewById(R.id.addActError);

        // Vars for text boxes
        EditText urlEnterTB = (EditText) findViewById(R.id.enterTargetURL);
        EditText tokenEnterTB = (EditText) findViewById(R.id.enterTokenTB);
        EditText usernameEnter = (EditText) findViewById(R.id.enterServiceUsernameTB);
        EditText passwordEnter = (EditText) findViewById(R.id.enterServicePasswordTB);


        // Groups used for information
        Group urlEnter = (Group) findViewById(R.id.enterUrlGroup);
        Group tokenEnter = (Group) findViewById(R.id.enterTokenGroup);
        Group accountInfoEnter = (Group) findViewById(R.id.accountInfoGroup);

        // Setup the three Account type buttons
        Button chaosButton = (Button) findViewById(R.id.chasoButton);
        Button crewsButton = (Button) findViewById(R.id.crewsButton);
        Button cmailButton = (Button) findViewById(R.id.cmailButton);




        // Setup back button
        Button backButton = (Button) findViewById(R.id.addActBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // R return to correct page
                if(targetGroup == null ){
                    Intent intent = new Intent(AdminActivity.this, MessagesActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(AdminActivity.this, GroupActivity.class);
                    startActivity(intent);
                }
            }
        });


    }


}
