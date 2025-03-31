package com.example.lbstest;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_main);
        /**
         * 获取输入框
         */
        EditText name_input = findViewById(R.id.name_input);
        EditText age_input = findViewById(R.id.age_input);
        EditText location_input = findViewById(R.id.location_input);
        EditText location_inpuset = findViewById(R.id.location_inpuset);
        /**
         * 获取到按钮
         */
        Button btn_guardian_mode = findViewById(R.id.btn_guardian_mode);
        Button btn_elder_mode = findViewById(R.id.btn_elder_mode);

        btn_elder_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入并去除首尾空格
                String input1 = name_input.getText().toString().trim();
                String input2 = age_input.getText().toString().trim();
                String input3 = location_input.getText().toString().trim();
                String input4 = location_inpuset.getText().toString().trim();

                // 判断是否有空输入
                if (!input1.isEmpty() && !input2.isEmpty() && !input3.isEmpty() && !input4.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, MapActivity.class);
                    startActivity(intent);
                } else {
                    // 其中一个或多个输入框为空，提示用户
                    Toast.makeText(MainActivity.this, "请填写所有输入框", Toast.LENGTH_SHORT).show();
                }
            }
        });
        btn_guardian_mode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 获取输入并去除首尾空格
                String input1 = name_input.getText().toString().trim();
                String input2 = age_input.getText().toString().trim();
                String input3 = location_input.getText().toString().trim();
                String input4 = location_inpuset.getText().toString().trim();

                // 判断是否有空输入
                if (!input1.isEmpty() && !input2.isEmpty() && !input3.isEmpty() && !input4.isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, BossActivity.class);
                    startActivity(intent);
                } else {
                    // 其中一个或多个输入框为空，提示用户
                    Toast.makeText(MainActivity.this, "请填写所有输入框", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onResume() {
        super.onResume();
    }

}