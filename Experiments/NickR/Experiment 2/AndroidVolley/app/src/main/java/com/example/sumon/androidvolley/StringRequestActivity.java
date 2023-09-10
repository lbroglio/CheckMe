package com.example.sumon.androidvolley;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.example.sumon.androidvolley.app.AppController;
import com.example.sumon.androidvolley.utils.Const;
import java.util.Scanner;

public class StringRequestActivity extends Activity {

    private String TAG = StringRequestActivity.class.getSimpleName();
    private Button btnStringReq;
    private TextView msgResponse;
    private ProgressDialog pDialog;

    // This tag will be used to cancel the request
    private String tag_string_req = "string_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.string_request);

        btnStringReq = (Button) findViewById(R.id.btnStringReq);
        msgResponse = (TextView) findViewById(R.id.msgResponse);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.setCancelable(false);

        btnStringReq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeStringReq();
            }
        });
    }

    private void showProgressDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideProgressDialog() {
        if (pDialog.isShowing())
            pDialog.hide();
    }

    /**
     * Making json object request
     * */
    private void makeStringReq() {
        showProgressDialog();

        StringRequest strReq = new StringRequest(Method.GET, Const.URL_STRING_REQ, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());

                String info = "";
                int spaces = 0;

                Scanner scan = new Scanner(response.toString());
                Scanner part;
                while(scan.hasNextLine())
                {
                    String line = scan.nextLine();
                    part = new Scanner(line);
                    String word = part.next();

                    if(!word.equals("")) {
                        if (!(word.substring(0, 1).equals("{") || word.substring(0, 1).equals("}"))) {
                            if (word.substring(1, 5).equals("name")) {
                                info += "name: ";

                                word = part.next();
                                String last = word.substring(1, word.length());
                                String first = word.substring(1, word.length() - 2);

                                info += (first + " " + last + "\n");
                            } else if (word.substring(1, 4).equals("add") || word.substring(1, 4).equals("com")) {
                                info += word.substring(1, word.length() - 2) + ":\n";
                                spaces = 1;
                            } else if (word.substring(1, 4).equals("lng")) {
                                info += "          " + word.substring(1, word.length() - 2) + ": ";
                                word = part.next();
                                info += word.substring(1, word.length() - 1) + "\n";
                                spaces = 0;
                            } else if (word.substring(1, 4).equals("geo")) {
                                info += "     geo:\n";
                                spaces = 2;
                            } else {
                                for (int i = 0; i < spaces; i++) {
                                    info += "     ";
                                }

                                info += word.substring(1, word.length() - 2) + ": ";

                                int start;
                                boolean first = true;
                                while (part.hasNext()) {
                                    word = part.next();
                                    if(!first)
                                    {
                                        info += " ";
                                    }

                                    if (word.substring(0, 1).equals("\"")) {
                                        info += word.substring(1, 2).toUpperCase();
                                        start = 2;
                                    } else
                                    {
                                        info += word.substring(0, 1).toUpperCase();
                                        start = 1;
                                    }

                                    if (word.substring(word.length() - 2, word.length()).equals("\",")) {
                                        info += word.substring(start, word.length() - 2);
                                    } else if (word.substring(word.length() - 1, word.length()).equals(",")) {
                                        info += word.substring(start, word.length() - 1);
                                    } else{
                                        info += word.substring(start, word.length());
                                    }

                                    first = false;
                                }

                                info += "\n";
                            }
                        }
                    }
                }

                msgResponse.setText(info);
                hideProgressDialog();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideProgressDialog();
            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);

    }
}