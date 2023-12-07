package com.example.emailaggregatorapp2;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

public class AlertDisplayer {
    public static void dialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("OK", null); // You can add additional buttons if needed
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
