package com.example.lbstest;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
import com.baidu.trace.LBSTraceClient;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
                SDKInitializer.setAgreePrivacy(this, true);

        try {
             SDKInitializer.initialize(this);
        } catch (BaiduMapSDKException e) {

        }
             SDKInitializer.setCoordType(CoordType.BD09LL);

    }
}
