package com.example.lbstest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
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
import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
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

public class BossActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private LocationClient mLocationClient = null;
    public BaiduMap mBaiduMaps = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LocationClient.setAgreePrivacy(true);
        LBSTraceClient.setAgreePrivacy(getApplicationContext(),true);
        setContentView(R.layout.activity_boss);
        mMapView =(MapView) findViewById(R.id.mMapViews);
        mBaiduMaps = mMapView.getMap();
        mBaiduMaps.setMyLocationEnabled(true);
        Button showDialogButton = findViewById(R.id.show_dialog_buttons);
        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCoordinateDialog();
            }
        });

//        // 显示圆形围栏
//        showCircleFence();

//        // 显示矩形围栏
//        showRectangleFence();
//
//        // 显示多边形围栏
//        showPolygonFence();
//        showOutOfBoundsAlert();
          drawSimplePolyline();
    }
    private void showCoordinateDialog() {
        // 创建一个自定义布局的 AlertDialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        // 获取布局中的控件
        TextView coordinateInfo = dialogView.findViewById(R.id.coordinate_info);
        ImageView imageView = dialogView.findViewById(R.id.image_view);
        // 设置坐标点信息
        coordinateInfo.setText("纬度：39.963175\n经度：116.400244");
        // 设置图片（这里你可以使用自己需要的图片资源）
        imageView.setImageResource(R.drawable.sample_image);  // 替换为你的图片资源
        // 创建弹窗
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("MAX告警，相关文件已下载");
        builder.setView(dialogView);
        // 设置关闭按钮
        builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        // 显示弹窗
        builder.show();
    }
    // 创建一个绘制折线的方法
    private void drawSimplePolyline() {
        // 定义一组折线的坐标点
        List<LatLng> points = new ArrayList<>();
        points.add(new LatLng(34.159369,108.907082)); // 坐标点1
        points.add(new LatLng(34.159373,108.907338)); // 坐标点1
        points.add(new LatLng(34.159358,108.907468)); // 坐标
        points.add(new LatLng(34.159362,108.907665)); // 坐标
        points.add(new LatLng(34.159511,108.907661)); // 坐标
        points.add(new LatLng(34.160165,108.907701)); // 坐
        points.add(new LatLng(34.16056,108.907728)); // 坐标
        points.add(new LatLng(34.160631,108.907724)); // 坐标
        points.add(new LatLng(34.160837,108.907441)); // 坐标
        points.add(new LatLng(34.154388,108.90736));
        points.add(new LatLng(34.154389,108.90736));
        points.add(new LatLng(34.154385,108.90736));
        points.add(new LatLng(34.154387,108.90736));
        points.add(new LatLng(34.154388,108.90736));
        points.add(new LatLng(34.154387,108.90736));
        points.add(new LatLng(34.154385,108.90736));
        points.add(new LatLng(34.154374,108.90736));
        points.add(new LatLng(34.154388,108.90736));
        points.add(new LatLng(34.154374,108.90736));
        points.add(new LatLng(34.154388,108.90736));
        // 创建 PolylineOptions 对象来绘制折线
        PolylineOptions polylineOptions = new PolylineOptions()
                .points(points)  // 设置折线的坐标点
                .width(10)       // 设置折线宽度
                .color(0x55FF0000); // 设置折线颜色（绿色）
        mBaiduMaps.addOverlay(polylineOptions);
        for (LatLng point : points) {
            addSpecialMarker(point); // 为特殊点添加标记
        }
    }
    private void addSpecialMarker(LatLng point) {
        // 创建标记并添加到地图上
        BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromResource(R.drawable.end_icon); // 自定义图标
        OverlayOptions options = new MarkerOptions()
                .position(point)
                .icon(markerIcon)
                .zIndex(9); // 设置图层级别，确保标记在折线之上
        mBaiduMaps.addOverlay(options);
    }
    // 写入定位数据到文件
    private void showOutOfBoundsAlert() {
        Toast.makeText(this, "对方已越界，定位已开启", Toast.LENGTH_SHORT).show();
    }
    private void showCircleFence() {
        BaiduMap mBaiduMap = mMapView.getMap();
        // 定义圆形围栏的中心和半径
        LatLng center = new LatLng(34.155721, 108.905559); // 北京
        int radius = 300; // 半径为500米
        // 创建圆形区域
        CircleOptions circleOptions = new CircleOptions()
                .center(center)
                .radius(radius)
                .fillColor(0x5500FF00) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMap.addOverlay(circleOptions);
    }
    private void showRectangleFence() {
        BaiduMap mBaiduMap = mMapView.getMap();
        // 定义矩形围栏的四个角点
        LatLng point1 = new LatLng(34.155721, 108.905559); // 左上角
        LatLng point2 = new LatLng(34.155721, 108.907153); // 右上角
        LatLng point3 = new LatLng(34.155389, 108.907153); // 右下角
        LatLng point4 = new LatLng(34.155389, 108.905559); // 左下角
        // 创建矩形区域
        List<LatLng> points = Arrays.asList(point1, point2, point3, point4);
        PolygonOptions polygonOptions = new PolygonOptions()
                .points(points)
                .fillColor(0x55FF0000) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMap.addOverlay(polygonOptions);
    }
    private void showPolygonFence() {
        BaiduMap mBaiduMap = mMapView.getMap();
        // 定义多边形围栏的多个顶点
        LatLng point1 = new LatLng(34.156841, 108.905123);
        LatLng point2 = new LatLng(34.156692, 108.906848);
        LatLng point3 = new LatLng(34.155475, 108.907297);
        LatLng point4 = new LatLng(34.156053, 108.905828);
        LatLng point5 = new LatLng(34.156602, 108.904935); // 添加一个额外的点来形成多边形
        // 创建多边形区域
        List<LatLng> points = Arrays.asList(point1, point2, point3, point4, point5);
        PolygonOptions polygonOptions = new PolygonOptions()
                .points(points)
                .fillColor(0x5500FF00) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMap.addOverlay(polygonOptions);
    }
    @Override
    protected void onDestroy() {
        mBaiduMaps.setMyLocationEnabled(false);
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