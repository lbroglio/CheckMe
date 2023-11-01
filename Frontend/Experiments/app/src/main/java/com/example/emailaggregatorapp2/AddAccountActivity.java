package com.example.emailaggregatorapp2;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class AddAccountActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_acount);



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
