package com.example.emailaggregatorapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String API_URL = "http://coms-309-047.class.las.iastate.edu:8080/user/login";
    private TextView responseTextView;
    private TextView failureMsg;
    private Button loginbutton;
    private Button backbutton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    public static String username;
    public static String password;

    private boolean goToNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //responseTextView = (TextView) findViewById(R.id.jsonresponse);
        loginbutton = (Button) findViewById(R.id.loginbut2);
        backbutton = (Button) findViewById(R.id.backbutton);

        usernameEditText = (EditText) findViewById(R.id.loginusernameedittext);
        passwordEditText = (EditText) findViewById(R.id.loginpasswordedittext);

        failureMsg = (TextView) findViewById(R.id.failureMsg);
        Log.d("HERE", failureMsg.toString());
        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                makeJsonObjectRequest(username, password);
                checkAdmin();
                if(goToNext){
                    Intent intent = new Intent(LoginActivity.this, MessagesActivity.class);
                    startActivity(intent);
                }


            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void makeJsonObjectRequest(String username, String password) {
        JSONObject body = new JSONObject();
        try {
            body.put("username",username);
            body.put("password",password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JsonRequest<Boolean> jsonObjectRequest = new JsonRequest<Boolean>(
                Request.Method.POST,
                API_URL,
                body.toString(),
                new Response.Listener<Boolean>() {
                    @Override
                    public void onResponse(Boolean response) {
                        //  Set the flag to go to the next screen and save the login info
                        UserLoginInfo.username = username;
                        UserLoginInfo.password = password;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        Log.i("Volley error", error.toString());
                        failureMsg.setVisibility(View.VISIBLE);
                    }
                }
        ) {
            @Override
            protected Response<Boolean> parseNetworkResponse(NetworkResponse response) {
                return Response.success(Boolean.parseBoolean(response.toString()), HttpHeaderParser.parseCacheHeaders(response));

            }
        };
        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);
    }

    private void checkAdmin(){
        StringRequest jsonObjectRequest = new StringRequest(
                Request.Method.GET,
                (API_URL + "/isAdmin"),

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //  Set the flag to go to the next screen and save the login info'
                        Log.d("IS ADMIN", response);
                        if(response.equals("true")){
                            UserLoginInfo.isAdmin = true;
                        }
                        else{
                            UserLoginInfo.isAdmin = false;
                        }
                        goToNext = true;
                        Intent intent = new Intent(LoginActivity.this, MessagesActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        UserLoginInfo.isAdmin = false;
                        Log.e("Volley Error", error.toString());

                    }
                }
        ){

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json; charset=UTF-8");

                //Setup Basic Auth
                String authStr = username + ':' + password;
                Log.d("Auth String", authStr);
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