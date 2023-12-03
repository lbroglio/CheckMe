package com.example.emailaggregatorapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

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

public class ReplyActivity extends AppCompatActivity {

    private final String API_URL = "http://coms-309-047.class.las.iastate.edu:8080/user/" + UserLoginInfo.username + "/reply";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);

        Button sendButton = (Button)  findViewById(R.id.replySend);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText replyBox = (EditText) findViewById(R.id.replyContent);
                String replyContents = String.valueOf(replyBox.getText());

                sendReplyRequest(replyContents);
            }
        });
    }

    private void sendReplyRequest(String contents){
        JSONObject body = new JSONObject();

        try{
            body.put("reply-contents", contents);
            body.put("reply-to", SelectedMessageInfo.selectedMsg);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        Log.e("LOOK HERE",body.toString());

        JsonRequest<String> jsonObjectRequest = new JsonRequest<String>(
                Request.Method.POST,
                API_URL,
                body.toString(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Intent intent = new Intent(ReplyActivity.this, MessagesActivity.class);
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
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);

    }

}
