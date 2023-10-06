package com.example.emailaggregatorapp2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

public class MessageActivity extends AppCompatActivity{
    private static final String API_URL_POST = "http://coms-309-047.class.las.iastate.edu:8080/message";
    private static final String API_URL_GET = "http://coms-309-047.class.las.iastate.edu:8080/message/user/";

    private Button postSendButton;
    private Button getSendButton;
    private EditText senderInput;
    private EditText toInput;
    private EditText subjectInput;
    private EditText contentInput;

    private EditText retrieveInput;
    private TextView messageDisplay;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        postSendButton = (Button) findViewById(R.id.button);
        getSendButton = (Button) findViewById(R.id.button3);

        senderInput = (EditText) findViewById(R.id.editTextText);
        toInput = (EditText) findViewById(R.id.editTextText3);
        subjectInput = (EditText) findViewById(R.id.editTextText4);
        contentInput = (EditText) findViewById(R.id.editTextText5);

        retrieveInput = (EditText) findViewById(R.id.editTextText6);
        messageDisplay = (TextView) findViewById(R.id.textView8);


        postSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String from = senderInput.getText().toString();
                String to = toInput.getText().toString();
                String subject = subjectInput.getText().toString();
                String contents = contentInput.getText().toString();

                makeJSONObjectPostRequest(from,to,subject,contents);

            }
        });


        getSendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String user = retrieveInput.getText().toString();

                makeJSONObjectGetRequest(user);

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
                API_URL_POST,
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
                (API_URL_GET + user),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        messageDisplay.setText(response);
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
