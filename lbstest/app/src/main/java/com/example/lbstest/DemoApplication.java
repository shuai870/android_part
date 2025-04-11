package com.example.lbstest;
import android.app.Application;
import android.util.Log;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DemoApplication", "onCreate: initializing Baidu SDK");
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        SDKInitializer.setCoordType(CoordType.BD09LL);
        try {
            SDKInitializer.initialize(getApplicationContext());
            Log.d("DemoApplication", "SDKInitializer initialized");
        } catch (BaiduMapSDKException e) {
            e.printStackTrace();
            Log.e("DemoApplication", "SDK Initialization failed", e);
        }
    }
}
