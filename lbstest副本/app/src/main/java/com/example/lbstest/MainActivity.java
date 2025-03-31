package com.example.lbstest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;


import com.baidu.trace.Trace;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.fence.OnFenceListener;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.analysis.OnAnalysisListener;
import com.baidu.trace.api.bos.OnBosListener;

public class MainActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private LocationClient mLocationClient = null;
    private LBSTraceClient mTraceClient = null;
    public BaiduMap mBaiduMap = null;
    private boolean is_first_locate =true;
    private static final int REQUEST_PERMISSION_RESULT = 99;

    // 轨迹服务ID
    long serviceId = 239155;
    // 设备标识
    String entityName = "myTrace";
    // 是否需要对象存储服务，默认为：false，关闭对象存储服务。注：鹰眼 Android SDK v3.0以上版本支持随轨迹上传图像等对象数据，若需使用此功能，该参数需设为 true，且需导入bos-android-sdk-1.0.2.jar。
    boolean isNeedObjectStorage = false;
    // 初始化轨迹服务

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationClient.setAgreePrivacy(true);
        LBSTraceClient.setAgreePrivacy(getApplicationContext(),true);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        mMapView =(MapView) findViewById(R.id.mMapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        // 初始化轨迹服务
        Trace mTrace = new Trace(serviceId, entityName, isNeedObjectStorage);
        try {
            mLocationClient= new LocationClient(getApplicationContext());
//            mTraceClient =new LBSTraceClient(MainActivity.this);
        } catch(Exception e) {
        }
        if(mLocationClient != null) {
            agreePermission();  //申请权限并进行定位  申请权限的代码里有定位方法requestLocation()
        }
    }
    /**
     * 定位监听器
     * 定位完成则执行下面的功能
     */
    private class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation bdLocation) {

//        StringBuilder currentPosition = new StringBuilder();
//        currentPosition.append("纬度：").append(bdLocation.getLatitude()).append("\n");
//        currentPosition.append("经度：").append(bdLocation.getLongitude()).append("\n");
//        currentPosition.append("国家：").append(bdLocation.getCountry()).append("\n");
//        currentPosition.append("省：").append(bdLocation.getProvince()).append("\n");
//        currentPosition.append("市：").append(bdLocation.getCity()).append("\n");
//        currentPosition.append("区：").append(bdLocation.getDistrict()).append("\n");
//        currentPosition.append("村镇：").append(bdLocation.getTown()).append("\n");
//        currentPosition.append("街道：").append(bdLocation.getStreet()).append("\n");
//        currentPosition.append("地址：").append(bdLocation.getAddrStr()).append("\n");
//        currentPosition.append("定位方式：");
//        if(bdLocation.getLocType() == BDLocation.TypeGpsLocation){
//            currentPosition.append("GPS");
//        } else if (bdLocation.getLocType() == BDLocation.TypeNetWorkLocation) {
//            currentPosition.append("网络");
//        }
            //mapView 销毁后不在处理新接收的位置
            if (bdLocation == null || mMapView == null){
                return;
            }
            if(is_first_locate){
                LatLng ll = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
                MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
                mBaiduMap.animateMapStatus(update);
                update = MapStatusUpdateFactory.zoomTo(16f);
                mBaiduMap.animateMapStatus(update);
                is_first_locate = false;
            }
                MyLocationData locData = new MyLocationData.Builder()
                        .accuracy(bdLocation.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(bdLocation.getDirection())
                        .latitude(bdLocation.getLatitude()).longitude(bdLocation.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);
        }
    }
    /**
     * 请求定位的方法，先进行初始化操作，然后再启动线程
     */
    private void requestLocation(){
        initLocation();
        mLocationClient.registerLocationListener(new MyLocationListener());
        mLocationClient.start();
    }
    private void initLocation(){
        LocationClientOption locationOption = new LocationClientOption();
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        locationOption.setOpenGps(true); // 打开gps
//可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("bd09ll");
//可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
//可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
//可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
//可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
//可选，默认false，设置是否当卫星定位有效时按照1S1次频率输出卫星定位结果
        locationOption.setLocationNotify(true);
//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
//可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
//可选，默认false，设置是否开启卫星定位
        locationOption.setOpenGnss(true);
//可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
//设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        mLocationClient.setLocOption(locationOption);
    }
        /**
         * 动态申请权限
         */
        public void agreePermission(){

            List<String> permissionList = new ArrayList<String>();
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CHANGE_WIFI_STATE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.CHANGE_WIFI_STATE);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_WIFI_STATE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.FOREGROUND_SERVICE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.FOREGROUND_SERVICE);
            }
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.READ_PHONE_STATE);
            }
            if(!permissionList.isEmpty()){
                String [] permissions = permissionList.toArray(new String[permissionList.size()]);
                ActivityCompat.requestPermissions(MainActivity.this,permissions,REQUEST_PERMISSION_RESULT);
            }else{
                requestLocation();
            }
        }
        /**
         *
         *
         * 动态申请权限之后，用户的界面出现弹窗，
         * 无论是否点击同意，都会在这个方法中进行判断
         * 逐一核对，
         * 有一个不符就退出程序，
         * 实际上比较流氓的做法，
         * 正确的做法应该是，用到什么功能才进行申请而不是全申请了在运行
         */
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            switch (requestCode){
                case REQUEST_PERMISSION_RESULT:
                    if(grantResults.length > 0){
                        for(int result : grantResults){
                            if(result != PackageManager.PERMISSION_GRANTED){
                                Toast.makeText(this, "请同意权限使用本程序", Toast.LENGTH_SHORT).show();
                                finish();
                                return;
                            }
                        }
                        requestLocation();
                    }else{
                        Toast.makeText(this, "在申请权限回调时granResult.length错误", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    break;
            }
        }
    @Override
    protected void onDestroy() {
        mLocationClient.stop();
        mBaiduMap.setMyLocationEnabled(false);
        mMapView.onDestroy();
        mMapView = null;
        super.onDestroy();
    }
    @Override
    protected void onPause() {
        mMapView.onPause();
        super.onPause();
    }
    @Override
    protected void onResume() {
        mMapView.onResume();
        super.onResume();
    }
}