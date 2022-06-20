package com.example.lbstest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import android.Manifest;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;


public class MainActivity extends AppCompatActivity {

    TextView locationInfo;

    public LocationClient mLocationClient = null;
    private boolean isPermissionRequested;
    //private MyLocationListener myListener = new MyLocationListener();
    MapView mMapView ;

    BaiduMap mbaiduMap = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.setAgreePrivacy(getApplicationContext(), true);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        locationInfo = findViewById(R.id.locationInfo);
        LocationClient.setAgreePrivacy(true);
        try{mLocationClient = new LocationClient(getApplicationContext());}
        catch (Exception e){
            e.printStackTrace();
        }
        if(mLocationClient != null) {
        mLocationClient.registerLocationListener(new MyLocationListener());}
        mMapView =  findViewById(R.id.bmapView);
        mbaiduMap = mMapView.getMap();
        mbaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);// 指地图类型
        mbaiduMap.setMyLocationEnabled(true);
        requestPermission();
        requestLocation();
      //  requestLocation();//请求定位——个人定义的方法--下文定义 权限完成后在下面的else里了
//        List<String> permissionList = new ArrayList<String>();//权限需要--下文写入
//        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
//            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
//        }
//        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)!=PackageManager.PERMISSION_GRANTED){
//            permissionList.add(Manifest.permission.READ_PHONE_STATE);
//        }
//
//        if(ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
//            permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        if(!permissionList.isEmpty()){
//            String [] permissions = permissionList.toArray(new String[permissionList.size()]);
//            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
//        }else{
//            requestLocation();
//        }

    }
    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= 23 && !isPermissionRequested) {
            isPermissionRequested = true;
            ArrayList<String> permissionsList = new ArrayList<>();
            String[] permissions = {
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE,
            };
            for (String perm : permissions) {
                if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this,perm)) {
                   // requestLocation();
                }else {
                    permissionsList.add(perm);

                }
            }

            if (!permissionsList.isEmpty()) {
                String[] strings = new String[permissionsList.size()];
                requestPermissions(permissionsList.toArray(strings), 0);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 0:
                if(grantResults.length>0){
                    for(int result:grantResults){
                        if(result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"同意权限才可以用本定位demo",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                    requestLocation();
                }else{
                    Toast.makeText(this,"有错误",Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void requestLocation(){
        initLocation(); //下文定义
        mLocationClient.start();
    }
    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
//可选，设置定位模式，默认高精度
//LocationMode.Hight_Accuracy：高精度；
//LocationMode. Battery_Saving：低功耗；
//LocationMode. Device_Sensors：仅使用设备；
//LocationMode.Fuzzy_Locating, 模糊定位模式；v9.2.8版本开始支持，可以降低API的调用频率，但同时也会降低定位精度；
        option.setCoorType("BD09ll");
//可选，设置返回经纬度坐标类型，默认GCJ02
//GCJ02：国测局坐标；
//BD09ll：百度经纬度坐标；
//BD09：百度墨卡托坐标；
//海外地区定位，无需设置坐标类型，统一返回WGS84类型坐标
        option.setScanSpan(3000);
//可选，设置发起定位请求的间隔，int类型，单位ms
//如果设置为0，则代表单次定位，即仅定位一次，默认为0
//如果设置非0，需设置1000ms以上才有效
        option.setOpenGps(true);
//可选，设置是否使用gps，默认false
//使用高精度和仅用设备两种定位模式的，参数必须设置为true
        option.setLocationNotify(false);
//可选，设置是否当GPS有效时按照1S/1次频率输出GPS结果，默认false
        option.setIgnoreKillProcess(false);
//可选，定位SDK内部是一个service，并放到了独立进程。
//设置是否在stop的时候杀死这个进程，默认（建议）不杀死，即setIgnoreKillProcess(true)
        option.SetIgnoreCacheException(false);
//可选，设置是否收集Crash信息，默认收集，即参数为false
        option.setWifiCacheTimeOut(5*60*1000);
//可选，V7.2版本新增能力
//如果设置了该接口，首次启动定位时，会先判断当前Wi-Fi是否超出有效期，若超出有效期，会先重新扫描Wi-Fi，然后定位
        option.setEnableSimulateGps(false);
//可选，设置是否需要过滤GPS仿真结果，默认需要，即参数为false
        option.setNeedNewVersionRgc(true);
//可选，设置是否需要最新版本的地址信息。默认需要，即参数为true
        option.setIsNeedAddress(true);//  国家 地区 城市
        mLocationClient.setLocOption(option);

//mLocationClient为第二步初始化过的LocationClient对象
//需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
//更多LocationClientOption的配置，请参照类参考中LocationClientOption类的详细说明

    }
    private class MyLocationListener extends BDAbstractLocationListener{   //监听类
        @Override
        public void onReceiveLocation(BDLocation Location) {

            navigateTo(Location);

            StringBuilder currentPosition = new StringBuilder();
            currentPosition.append("纬度").append(Location.getLatitude()).append("\n");
            currentPosition.append("经度").append(Location.getLongitude()).append("\n");
            currentPosition.append("国家").append(Location.getCountry()).append("\n");
            currentPosition.append("省").append(Location.getProvince()).append("\n");
            currentPosition.append("市").append(Location.getCity()).append("\n");
            currentPosition.append("区").append(Location.getDistrict()).append("\n");
            currentPosition.append("村镇").append(Location.getTown()).append("\n");
            currentPosition.append("街道").append(Location.getStreet()).append("\n");
            currentPosition.append("地址").append(Location.getAddrStr()).append("\n");
            currentPosition.append("定位方式：");
            if (Location.getLocType() == BDLocation.TypeGpsLocation){
                currentPosition.append("GPS");
            }else if(Location.getLocType() == BDLocation.TypeNetWorkLocation){
                currentPosition.append("网络");
            }
            if (Location == null || mMapView == null){
                return;
            }
            MyLocationData locData = new MyLocationData.Builder()
                    .accuracy(Location.getRadius())
                    .direction(Location.getDirection()).latitude(Location.getLatitude())
                    .longitude(Location.getLongitude()).build();
            mbaiduMap.setMyLocationData(locData);
            locationInfo.setText(currentPosition);  //打印上面的内容
        }
    }
   private void navigateTo(BDLocation Location){
        LatLng ll = new LatLng(Location.getLatitude(),Location.getLongitude());
        MapStatusUpdate update = MapStatusUpdateFactory.newLatLng(ll);
        mbaiduMap.animateMapStatus(update);
//       update = MapStatusUpdateFactory.zoomTo(16f);
//       mbaiduMap.animateMapStatus(update);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        mbaiduMap.setMyLocationEnabled(false);
        mLocationClient.stop();
        mMapView = null;
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }
}
