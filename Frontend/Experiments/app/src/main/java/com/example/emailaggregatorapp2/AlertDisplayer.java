package com.example.emailaggregatorapp2;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AlertDialog;

public class AlertDisplayer {
    public static void dialog(String message, Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("OK", null); // You can add additional buttons if needed
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    public static void dialogIntent(String message, Context context, Intent nextActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        context.startActivity(nextActivity);
                    }
                });        // You can add additional buttons if needed
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
