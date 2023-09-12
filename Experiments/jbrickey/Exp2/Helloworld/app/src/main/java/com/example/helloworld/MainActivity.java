package com.example.helloworld;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button hello_button = (Button) findViewById(R.id.hellobutton);

        TextView hello_world = (TextView) findViewById(R.id.hellotext);
        Button hellobutton = (Button) findViewById(R.id.hellobutton);

        hellobutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hello_world.setText("Hello World");
            }
        });



    }

}