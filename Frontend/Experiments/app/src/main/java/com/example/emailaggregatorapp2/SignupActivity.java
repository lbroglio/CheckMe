package com.example.emailaggregatorapp2;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class SignupActivity extends AppCompatActivity {
    private static final String API_URL = "http://coms-309-047.class.las.iastate.edu:8080/user";

    private Button signUpButton;
    private EditText emailInput;
    private EditText usernameInput;
    private EditText passwordInput;
    private EditText passwordConfirm;

    private Button backbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signUpButton = (Button) findViewById(R.id.loginbut2);
        backbutton = (Button) findViewById(R.id.backbutton2);

        emailInput = (EditText) findViewById(R.id.emailInp);
        usernameInput = (EditText) findViewById(R.id.usernameInp);
        passwordInput = (EditText) findViewById(R.id.passInp);
        passwordConfirm = (EditText) findViewById(R.id.passConfirmInp);



        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!passwordInput.getText().toString().equals(passwordConfirm.getText().toString())){
                    Log.e("Password Mismatch", "Passwords do not match");
                    AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                    builder.setMessage("Passwords do not match")
                            .setPositiveButton("OK", null); // You can add additional buttons if needed
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    return;
                }


                String email = emailInput.getText().toString();
                String username = usernameInput.getText().toString();
                String password = passwordInput.getText().toString();

                makeJSONObjectRequest(username, email, password);
            }
        });

        backbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignupActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private void makeJSONObjectRequest(String username, String email, String password){
        JSONObject body = new JSONObject();
        try {
            body.put("username",username);
            body.put("email_address",email);
            body.put("password",password);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Handle the successful response here
                        Log.d("Response", response.toString());
                        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle any errors that occur
                        Log.e("ErrorLine106", error.toString());
                        if(error.networkResponse.statusCode == 409){
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                            builder.setMessage("Username or email already exists")
                                    .setPositiveButton("OK", null); // You can add additional buttons if needed
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignupActivity.this);
                            builder.setMessage("An error occurred")
                                    .setPositiveButton("OK", null); // You can add additional buttons if needed
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                    }
                }
        ){
            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return body.toString() == null ? null : body.toString().getBytes("utf-8");
                } catch (UnsupportedEncodingException uee) {
                    return null;
                }
            }
        };

        // Adding request to request queue
        VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

}