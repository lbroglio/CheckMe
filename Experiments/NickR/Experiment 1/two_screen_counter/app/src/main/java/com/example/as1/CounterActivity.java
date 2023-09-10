package com.example.as1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CounterActivity extends AppCompatActivity {

    Button backBtn;
    TextView numberTxt;
    Button add1;
    Button add2;
    Button add5;
    Button add10;
    Button add100;
    Button sub1;
    Button sub2;
    Button sub5;
    Button sub10;
    Button sub100;
    Button mult2;
    Button mult3;
    Button mult5;
    Button mult10;
    Button mult100;
    Button div2;
    Button div3;
    Button div5;
    Button div10;
    Button div100;

    int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter);

        add1 = findViewById(R.id.add1Btn);
        add2 = findViewById(R.id.add2Btn);
        add5 = findViewById(R.id.add5Btn);
        add10 = findViewById(R.id.add10Btn);
        add100 = findViewById(R.id.add100Btn);
        sub1 = findViewById(R.id.sub1Btn);
        sub2 = findViewById(R.id.sub2Btn);
        sub5 = findViewById(R.id.sub5Btn);
        sub10 = findViewById(R.id.sub10Btn);
        sub100 = findViewById(R.id.sub100Btn);
        mult2 = findViewById(R.id.mult2Btn);
        mult3 = findViewById(R.id.mult3Btn);
        mult5 = findViewById(R.id.mult5Btn);
        mult10 = findViewById(R.id.mult10Btn);
        mult100 = findViewById(R.id.mult100Btn);
        div2 = findViewById(R.id.div2Btn);
        div3 = findViewById(R.id.div3Btn);
        div5 = findViewById(R.id.div5Btn);
        div10 = findViewById(R.id.div10Btn);
        div100 = findViewById(R.id.div100Btn);
        backBtn = findViewById(R.id.backBtn);
        numberTxt = findViewById(R.id.number);

        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter++;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter += 2;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        add5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter += 5;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        add10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter += 10;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        add100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter += 100;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        sub1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter--;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        sub2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter -= 2;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        sub5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter -= 5;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        sub10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter -= 10;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        sub100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter -= 100;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        mult2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter *= 2;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        mult3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter *= 3;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        mult5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter *= 5;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        mult10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter *= 10;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        mult100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter *= 100;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        div2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter /= 2;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        div3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter /= 3;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        div5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter /= 5;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        div10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter /= 10;
                numberTxt.setText(String.valueOf(counter));
            }
        });
        div100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                counter /= 100;
                numberTxt.setText(String.valueOf(counter));
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(CounterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


    }
}