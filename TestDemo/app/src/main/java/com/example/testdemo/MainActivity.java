package com.example.testdemo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer, gyroscope;

    private FileWriter fileWriter;
    private String currentActivity = "Walking";
    private String currentPhonePosition = "Handheld";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化传感器
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        // 注册传感器监听器
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);

        // 初始化Spinner选择器
        Spinner activitySpinner = findViewById(R.id.activity_spinner);
        Spinner positionSpinner = findViewById(R.id.position_spinner);

        ArrayAdapter<CharSequence> activityAdapter = ArrayAdapter.createFromResource(this,
                R.array.activities_array, android.R.layout.simple_spinner_item);
        activityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        activitySpinner.setAdapter(activityAdapter);

        ArrayAdapter<CharSequence> positionAdapter = ArrayAdapter.createFromResource(this,
                R.array.positions_array, android.R.layout.simple_spinner_item);
        positionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        positionSpinner.setAdapter(positionAdapter);

        activitySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentActivity = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        positionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentPhonePosition = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // 保存数据按钮
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> stopDataRecording());
    }
    private float[] accelerometerData = new float[3]; // 缓存加速度计数据
    private float[] gyroscopeData = new float[3]; // 缓存陀螺仪数据

    @Override
    public void onSensorChanged(SensorEvent event) {
        long timestamp = System.currentTimeMillis();

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelerometerData = event.values.clone();
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroscopeData = event.values.clone();
        }

        // 将加速度计和陀螺仪的数据写入同一行
        String data = String.format(Locale.getDefault(),
                "%d,%.3f,%.3f,%.3f,%.3f,%.3f,%.3f,%s,%s\n",
                timestamp,
                accelerometerData[0], accelerometerData[1], accelerometerData[2],
                gyroscopeData[0], gyroscopeData[1], gyroscopeData[2],
                currentActivity, currentPhonePosition);

        try {
            if (fileWriter != null) {
                fileWriter.append(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private void stopDataRecording() {
        try {
            if (fileWriter != null) {
                fileWriter.close();
                fileWriter = null;
                Toast.makeText(this, "Data saved successfully!", Toast.LENGTH_SHORT).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        createFileForData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopDataRecording();
    }

    private void createFileForData() {
        try {
            File folder = new File(getExternalFilesDir(null), "SensorData");
            if (!folder.exists()) folder.mkdirs();

            String fileName = "sensor_data_" +
                    new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";
            File file = new File(folder, fileName);

            fileWriter = new FileWriter(file, true);
            fileWriter.append("Timestamp,X,Y,Z,a,b,c,Activity,PhonePosition\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
