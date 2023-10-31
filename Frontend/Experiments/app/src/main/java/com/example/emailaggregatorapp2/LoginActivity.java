package com.example.emailaggregatorapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;

import org.json.JSONException;
import org.json.JSONObject;

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
                        goToNext = true;
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
}