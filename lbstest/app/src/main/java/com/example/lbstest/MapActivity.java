package com.example.lbstest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.common.BaiduMapSDKException;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;

import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;

import com.baidu.mapapi.model.LatLng;


import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

import com.baidu.trace.LBSTraceClient;
public class MapActivity extends AppCompatActivity{
    public MapView mMapView = null;
    private LocationClient mLocationClient = null;
    public BaiduMap mBaiduMap = null;
    private boolean is_first_locate =true;
    private static final int REQUEST_PERMISSION_RESULT = 99;
    private DynamicJsonParser dynamicJsonParser;
    private TextToSpeech tts;
    private boolean ttsReady = false;
    private boolean hasAlertedRecently = false;
    List<LatLng> newC = new ArrayList<>();
    private  TrackManager trackManager;
    private  Boolean trackOpen  = false;
    private PolygonOptions draw;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        LocationClient.setAgreePrivacy(true);
        try {
            mLocationClient= new LocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(mLocationClient != null) {
            agreePermission();  //申请权限并进行定位  申请权限的代码里有定位方法requestLocation()
        }
        mMapView =(MapView) findViewById(R.id.mMapView);
        mBaiduMap = mMapView.getMap();
        mBaiduMap.setMyLocationEnabled(true);
        dynamicJsonParser = new DynamicJsonParser(this,mBaiduMap);
        PhotoUploader photoUploader = new PhotoUploader(getApplicationContext());
        try {
            dynamicJsonParser.initTextToSpeech(getApplicationContext());
            // 1. 初始化 TTS，只执行一次
            tts = new TextToSpeech(getApplicationContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    int result = tts.setLanguage(Locale.CHINESE);
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        ttsReady = true;
                    }
                }
            });
        } catch(Exception e) {
        }
        /**
         * 光线传感器获取
         */
        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        SensorEventListener lightListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float lux = event.values[0];
                if (lux < 5 && ttsReady && !hasAlertedRecently) {
                    speak("触发主动告警");
                    AudioAlertRecorder.start(getApplicationContext());  // 开始录音
                    hasAlertedRecently = true;
                    // 5 秒内不再重复播报
                    new Handler().postDelayed(() -> hasAlertedRecently = false, 5000);
                    photoUploader.uploadLatestPhoto();
                }
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {}
        };
        sensorManager.registerListener(lightListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        /**
         * 同步地理围栏坐标
         */
        Button navigateTO = findViewById(R.id.navigateTO);
        navigateTO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dynamicJsonParser.fetchAndParseJson();
            }
        });
        Button warning = findViewById(R.id.warning);
        warning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawSimplePolyline();
            }
        });
        newC.add(new LatLng(34.159369,108.907082)); // 坐标点1
        newC.add(new LatLng(34.159373,108.907338)); // 坐标点1
        newC.add(new LatLng(34.159358,108.907468)); // 坐标
        newC.add(new LatLng(34.159362,108.907665)); // 坐标
        newC.add(new LatLng(34.159511,108.907661)); // 坐标
        newC.add(new LatLng(34.160165,108.907701)); // 坐
        newC.add(new LatLng(34.16056,108.907728)); // 坐标
        newC.add(new LatLng(34.160631,108.907724)); // 坐标
        newC.add(new LatLng(34.160837,108.907441)); // 坐标
        newC.add(new LatLng(34.154388,108.90736));
        newC.add(new LatLng(34.154389,108.90736));
        newC.add(new LatLng(34.154385,108.90736));
        newC.add(new LatLng(34.154387,108.90736));
        newC.add(new LatLng(34.154388,108.90736));
        newC.add(new LatLng(34.154387,108.90736));
        newC.add(new LatLng(34.154385,108.90736));
        newC.add(new LatLng(34.154374,108.90736));
        newC.add(new LatLng(34.154388,108.90736));
        newC.add(new LatLng(34.154374,108.90736));
        newC.add(new LatLng(34.159434,108.905184));

        trackManager = new TrackManager();

        trackManager.setDeviationListener(new TrackManager.DeviationListener() {
            @Override
            public void onOutOfTrack(LatLng point) {
                Toast.makeText(getApplicationContext(), "偏离轨迹", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onBackOnTrack(LatLng point) {
                Toast.makeText(getApplicationContext(), "回到轨迹", Toast.LENGTH_SHORT).show();
            }
        });
    }
    // 创建一个绘制折线的方法
    private void drawSimplePolyline() {
        PolylineOptions polylineOptions = new PolylineOptions()
                .points(newC)  // 设置折线的坐标点
                .width(10)       // 设置折线宽度
                .color(0x55FF0000); // 设置折线颜色（绿色）
        mBaiduMap.addOverlay(polylineOptions);
        for (LatLng point : newC) {
            addSpecialMarker(point); // 为特殊点添加标记
        }
        trackOpen  = true;
        mBaiduMap.addOverlay(draw);//把最新的两个点作为参考画出来
        trackManager.setRoute(newC);  // 设置轨迹点集合
        trackManager.setEllipseWidth(20.0);  // 设置容忍范围
    }
    private void addSpecialMarker(LatLng point) {
        // 创建标记并添加到地图上
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.end_icon); // 自定义图标
        OverlayOptions options = new MarkerOptions()
                .position(point)
                .icon(markerIcon)
                .zIndex(9); // 设置图层级别，确保标记在折线之上
        mBaiduMap.addOverlay(options);
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
            newC.add(new LatLng(locData.latitude,locData.longitude));  //加入newC

            trackManager.checkCurrentPosition(new LatLng(locData.latitude, locData.longitude));//偏离触发告警
            draw = RotatedEllipseUtil.getEllipseOverlay(newC.get(20), newC.get(19), 20);


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


    private void speak(String text) {
        if (tts != null) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "MAX_ALERT");
        }
    }

    /**
     * 动态申请权限
     */
    public void agreePermission(){
        List<String> permissionList = new ArrayList<String>();
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.CHANGE_WIFI_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.CHANGE_WIFI_STATE);
        }
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_WIFI_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
        }
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.FOREGROUND_SERVICE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.FOREGROUND_SERVICE);
        }
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if(ContextCompat.checkSelfPermission(MapActivity.this, Manifest.permission.RECORD_AUDIO)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.RECORD_AUDIO);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(Manifest.permission.READ_MEDIA_IMAGES);
            }
        }
        if(!permissionList.isEmpty()){
            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MapActivity.this,permissions,REQUEST_PERMISSION_RESULT);
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
        Log.d("Permission", "onRequestPermissionsResult called");
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_PERMISSION_RESULT:
                if(grantResults.length > 0){
                    for(int result : grantResults){
                        if(result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this, "请同意权限使用本程序", Toast.LENGTH_SHORT).show();
//                            finish();
                            return;
                        }
                    }
                    requestLocation();
                    Log.d("回调","回调");
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
        dynamicJsonParser.releaseTextToSpeech();
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
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
