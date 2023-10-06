package com.example.emailaggregatorapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private static final String API_URL = "http://data.jsontest.com/";
    private TextView responseTextView;
    private Button loginbutton;
    private EditText usernameEditText;
    private EditText passwordEditText;
    public static String username;
    public static String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //responseTextView = (TextView) findViewById(R.id.jsonresponse);
        loginbutton = (Button) findViewById(R.id.loginbut2);
        usernameEditText = (EditText) findViewById(R.id.loginusernameedittext);
        passwordEditText = (EditText) findViewById(R.id.loginpasswordedittext);

        loginbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                username = usernameEditText.getText().toString();
                password = passwordEditText.getText().toString();
                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                startActivity(intent);


            }
        });
    }

    /**
    private void makeJsonObjectRequest() {
        responseTextView = (TextView) findViewById(R.id.jsonresponse);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                API_URL,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Handle the successful response here
                        responseTextView.setText(response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        responseTextView.setText("Error: " + error.toString());
                    }
                }
        );
     */
}