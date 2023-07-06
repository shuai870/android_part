package com.example.hc_05;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.ArrayList;


class MyReceiver extends BroadcastReceiver {
        public ArrayList<BluetoothDevice> deviceOfall = new ArrayList<>();  //用来存放蓝牙设备
        public ArrayList<String> deviceName = new ArrayList<>();             //蓝牙名称和地址

        @Override
        public void onReceive(Context context, Intent intent) {
           if( intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
               Toast.makeText(context, "开始搜寻设备", Toast.LENGTH_SHORT).show();
           } else if (intent.getAction().equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
               Toast.makeText(context, "搜寻停止了", Toast.LENGTH_SHORT).show();
           }else if(intent.getAction().equals(BluetoothDevice.ACTION_FOUND)){
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            for(int i = 0;i < deviceOfall.size();i++){
                if(deviceOfall.get(i).getAddress().equals(device.getAddress()))
                {
                    return;
                }
            }
            deviceOfall.add(device);
            deviceName.add("地址 "+device.getAddress()+"\n"+"名称： "+device.getName());
           }

        }
}

