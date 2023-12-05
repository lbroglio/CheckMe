package com.example.emailaggregatorapp2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminActivity extends AppCompatActivity{
    public static String targetGroup = null;

    private final String API_URL = "http://coms-309-047.class.las.iastate.edu:8080/";

    private String targetService = "";

    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        Button btnCreateUser = findViewById(R.id.btnCreateUser);
        Button btnDeleteUser = findViewById(R.id.btnDeleteUser);
        Button btnGroupManagement = findViewById(R.id.btnGroupManagement);
        Button btnBack = findViewById(R.id.btnBack);
        final LinearLayout layoutUserInput = findViewById(R.id.layoutUserInput);
        final Button btnConfirmDelete = findViewById(R.id.btnConfirmDelete);
        final TextInputEditText editTextUserToDelete = findViewById(R.id.editTextUserToDelete);


        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, AdminCreateUserActivity.class));
            }
        });

        btnConfirmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform the delete operation using the entered username
                String usernameToDelete = editTextUserToDelete.getText().toString();
                deleteUser(usernameToDelete);
            }
        });

        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutUserInput.setVisibility(View.VISIBLE);
                btnConfirmDelete.setVisibility(View.VISIBLE);
            }
        });

        btnGroupManagement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement group management logic here
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

    private void deleteUser(String username){
        username = username.trim();
        StringRequest stringRequest = new StringRequest(
                Request.Method.DELETE,
                API_URL + "user/delete/" + username,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Response", response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        Log.e("Error", error.toString());
                    }
                }
        ){
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
        };
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

}


}
