package com.example.activity_lifecycle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    Button next_button;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 5:
                if(resultCode == RESULT_OK){
                    String back = data.getStringExtra("back");
                    Log.d("MainActivity",back);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String Data = "进入第二个活动界面";
        next_button = findViewById(R.id.next_button);
        next_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,MainActivity2.class);
                intent.putExtra("extra_data",Data);
                startActivity(intent);
                startActivityForResult(intent,5);
            }
        });

    }
}