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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AddAccountActivity extends AppCompatActivity{
    public static String targetGroup = null;

    private final String API_URL = "http://10.0.2.2:8080/";

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



        chaosButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Make Groups visible or gone depending on if there necessary
                if (urlEnter.getVisibility() == View.GONE) {
                    urlEnter.setVisibility(View.VISIBLE);
                }

                if (tokenEnter.getVisibility() == View.GONE) {
                    tokenEnter.setVisibility(View.VISIBLE);
                }

                if (accountInfoEnter.getVisibility() == View.VISIBLE) {
                    accountInfoEnter.setVisibility(View.GONE);
                }

                targetService = "chaos";
            }
        });

        crewsButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Make Groups visible or gone depending on if there necessary
                if (urlEnter.getVisibility() == View.GONE) {
                    urlEnter.setVisibility(View.VISIBLE);
                }

                if (tokenEnter.getVisibility() == View.VISIBLE) {
                    tokenEnter.setVisibility(View.GONE);
                }

                if (accountInfoEnter.getVisibility() == View.GONE) {
                    accountInfoEnter.setVisibility(View.VISIBLE);
                }

                targetService = "crews";
            }
        });

        cmailButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // Make Groups visible or gone depending on if there necessary
                if (urlEnter.getVisibility() == View.GONE) {
                    urlEnter.setVisibility(View.VISIBLE);
                }

                if (tokenEnter.getVisibility() == View.VISIBLE) {
                    tokenEnter.setVisibility(View.GONE);
                }

                if (accountInfoEnter.getVisibility() == View.GONE) {
                    accountInfoEnter.setVisibility(View.VISIBLE);
                }

                targetService = "cmail";
            }
        });

        // Setup add account button
        Button addAccount = (Button) findViewById(R.id.addAccountButton);
        addAccount.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    // Get all entered text
                    String givenUrl = String.valueOf(urlEnterTB.getText());
                    String givenToken = String.valueOf(tokenEnterTB.getText());
                    String givenUsername = String.valueOf(usernameEnter.getText());
                    String givenPassword = String.valueOf(passwordEnter.getText());


                    // If target hasn't been set send request for user
                    if(targetGroup == null){
                        // For Chaos service
                        if(targetService.equals("chaos")){
                            try {
                                addAccountRequestUser(givenUrl, null, givenToken);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        // For crews or cmail service
                        else{
                            try {
                                addAccountRequestUser(givenUrl, givenUsername, givenPassword);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                    // Send request for Group
                    else{
                        // For Chaos service
                        if(targetService.equals("chaos")){
                            try {
                                addAccountRequestGroup(givenUrl, null, givenToken);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        // For crews or cmail service
                        else{
                            try {
                                addAccountRequestGroup(givenUrl, givenUsername, givenPassword);
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }

                }
        });

        // Setup back button
        Button backButton = (Button) findViewById(R.id.addActBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                // R return to correct page
                if(targetGroup == null ){
                    Intent intent = new Intent(AddAccountActivity.this, MessagesActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(AddAccountActivity.this, GroupActivity.class);
                    startActivity(intent);
                }
            }
        });


    }

    // AUTH Parameter can be a password or a token
    private void addAccountRequestUser(String url, String username, String auth) throws JSONException {
        // Configure body based on current service
        JSONObject body = new JSONObject();
        // All providers need URL and service
        body.put("service-url", url);
        body.put("message-service", targetService);
        // Setup for chaos
        if(Objects.equals(targetService, "chaos")){

            body.put("chaos-token", auth);
        }
        //Setup for crews
        else if(Objects.equals(targetService, "crews")){
            body.put("crews-username", username);
            body.put("crews-password", auth);

        }
        //Setup for cmail
        else if(Objects.equals(targetService, "cmail")){
            body.put("cmail-username", username);
            body.put("cmail-password", auth);

        }


        JsonRequest<String> jsonObjectRequest = new JsonRequest<String>(
                Request.Method.PUT,
                API_URL + "user/" + UserLoginInfo.username + "/connect-account",
                body.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Intent intent = new Intent(AddAccountActivity.this, MessagesActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        Log.e("Volley Error", error.toString());

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

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return Response.success(response.toString(), HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

    // AUTH Parameter can be a password or a token
    private void addAccountRequestGroup(String url, String username, String auth) throws JSONException {
        // Configure body based on current service
        JSONObject body = new JSONObject();
        // All providers need URL and service
        body.put("service-url", url);
        body.put("message-service", targetService);
        // Setup for chaos
        if(Objects.equals(targetService, "chaos")){

            body.put("chaos-token", auth);
        }
        //Setup for crews
        else if(Objects.equals(targetService, "crews")){
            body.put("crews-username", username);
            body.put("crews-password", auth);

        }
        //Setup for cmail
        else if(Objects.equals(targetService, "cmail")){
            body.put("cmail-username", username);
            body.put("cmail-password", auth);

        }


        JsonRequest<String> jsonObjectRequest = new JsonRequest<String>(
                Request.Method.PUT,
                API_URL + "group/" +targetGroup + "/connect-account",
                body.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Intent intent = new Intent(AddAccountActivity.this, GroupActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        Log.e("Volley Error", error.toString());

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

            @Override
            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                return Response.success(response.toString(), HttpHeaderParser.parseCacheHeaders(response));
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

}
