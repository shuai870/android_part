package com.example.testdemo1;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor lightSensor;
    private static final int REQUEST_PERMISSION_CODE = 100;
    private FileWriter fileWriter;
    private boolean isCollectingData = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 请求权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION_CODE);
        }

        // 初始化传感器管理器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        // 启动或停止采集数据的按钮
        Button startButton = findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isCollectingData) {
                    startCollectingData();
                    startButton.setText("停止采集");
                } else {
                    stopCollectingData();
                    startButton.setText("开始采集");
                }
            }
        });
    }

    private void startCollectingData() {
        // 创建CSV文件
        File directory = new File(Environment.getExternalStorageDirectory(), "SensorData");
        if (!directory.exists()) {
            directory.mkdir();
        }
        File file = new File(directory, "light_data.csv");

        try {
            fileWriter = new FileWriter(file, true);

            // 如果是第一次写入，写入表头
            if (file.length() == 0) {
                fileWriter.append("Time,Light\n");  // CSV文件头
            }

            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
            isCollectingData = true;
            Toast.makeText(this, "数据采集已开始", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "文件创建失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopCollectingData() {
        sensorManager.unregisterListener(this);
        try {
            if (fileWriter != null) {
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        isCollectingData = false;
        Toast.makeText(this, "数据采集已停止", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            long timestamp = System.currentTimeMillis();
            float lightLevel = event.values[0];

            // 保存光线数据到CSV
            if (fileWriter != null) {
                try {
                    // 将时间戳和光线强度追加到CSV文件
                    fileWriter.append(timestamp + "," + lightLevel + "\n");
                    fileWriter.flush();  // 确保及时写入数据
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // 不处理
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isCollectingData) {
            stopCollectingData();
        }
    }
}
