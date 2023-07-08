package com.example.activity_lifecycle;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {
    Button back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent back_intent = new Intent();
                back_intent.putExtra("back","下一个活动返回来的");
                setResult(RESULT_OK,back_intent);
                finish();
            }
        });
        Intent intent = getIntent();
        String Data = intent.getStringExtra("extra_data");
        Log.d("MainActivity2",Data);

    }
}