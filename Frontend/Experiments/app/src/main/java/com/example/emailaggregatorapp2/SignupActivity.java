package com.example.emailaggregatorapp2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class SignupActivity extends AppCompatActivity {
    private static final String API_URL = "http://coms-309-047.class.las.iastate.edu:8080/user";

    private Button signUpButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpButton = (Button) findViewById(R.id.loginbut2);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeJSONObjectRequest(null, null, null);

                Intent intent = new Intent(SignupActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void makeJSONObjectRequest(String username, String email, String password){
        JSONObject body = new JSONObject();
        try {
            body.put("username","HockeySteve");
            body.put("email_address","knightssteve@gmail.com");
            body.put("password","GoldenKnightsGo123");
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                API_URL,
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

}