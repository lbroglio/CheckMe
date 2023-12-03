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

    private final String API_URL = "http://10.0.2.2:8080/";

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

        btnCreateUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AdminActivity.this, AdminCreateUserActivity.class));
            }
        });

        btnDeleteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement user deletion logic here
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


}
