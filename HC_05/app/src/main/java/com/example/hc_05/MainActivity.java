package com.example.hc_05;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private BluetoothAdapter mBluetoothAdater = null;
    private IntentFilter intentFilter;
    private MyReceiver myReceiver;

    public ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBluetoothAdater = BluetoothAdapter.getDefaultAdapter();

        Button startDiscovery = findViewById(R.id.startDiscovery);
        startDiscovery.setOnClickListener(this);

        Button clear = findViewById(R.id.clear);
        clear.setOnClickListener(this);
        //************************************************************************
        if (mBluetoothAdater == null) {
            finish();
        }

        requestPermission(); //权限申请方法 在本活动代码最后写了逻辑

        if (!mBluetoothAdater.isEnabled()) {

            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this,"没有授权连接蓝牙权限",Toast.LENGTH_SHORT).show();
            } else {
                mBluetoothAdater.enable();
                Toast.makeText(MainActivity.this, "蓝牙已开启", Toast.LENGTH_SHORT);
            }
        }
        //***************************************
        /**
         * 添加注册过滤器
         */
        intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothDevice.ACTION_FOUND);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        myReceiver = new MyReceiver();
        registerReceiver(myReceiver, intentFilter);

//************************************************************
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, myReceiver.deviceName);
        listView = findViewById(R.id.listview);

        listView.setAdapter(arrayAdapter);
//***********************************************************
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startDiscovery:
                startDiscovery();
                break;
            case R.id.clear:
                myReceiver.deviceOfall.clear();
                myReceiver.deviceName.clear();
                startDiscovery();
                break;
        }
    }
//*******************************************************************

    public void startDiscovery() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                   Toast.makeText(MainActivity.this,"扫描权限没有授予",Toast.LENGTH_SHORT).show();
                } else {
                    if (mBluetoothAdater.isDiscovering()) {
                        mBluetoothAdater.cancelDiscovery();
                    }
                    mBluetoothAdater.startDiscovery();
                    }
            }
        }).start();

    }

    /**
     * 权限申请的方法在最后面了
     *
     */

    private void requestPermission(){
        List<String> permissionList = new ArrayList<>();
        String[] permission = {
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.BLUETOOTH_ADVERTISE
        };
        for(String perm : permission){
            if(ActivityCompat.checkSelfPermission(MainActivity.this,perm) != PackageManager.PERMISSION_GRANTED){
                permissionList.add(perm);
            }
        }
        if(!permissionList.isEmpty()){
            String[] strings = new String[permissionList.size()];
            ActivityCompat.requestPermissions(MainActivity.this,permissionList.toArray(strings),5);
        }

    }

    //回调刚才的权限申请
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 5:
                 if(grantResults.length > 0){
                     for (int result : grantResults){
                         if(result != PackageManager.PERMISSION_GRANTED){
                             Toast.makeText(MainActivity.this,"权限未授权完整，部分功能不可用",Toast.LENGTH_SHORT).show();
                         }
                     }
                 }else{
                     Toast.makeText(MainActivity.this,"6 一个权限也不给，你清高 自己玩去吧",Toast.LENGTH_SHORT).show();
                 }
                 break;
        }
    }
}