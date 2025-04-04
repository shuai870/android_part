package com.example.lbstest;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.Handler;
import com.baidu.trace.LBSTraceClient;

import org.json.JSONException;
import org.json.JSONObject;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class BossActivity extends AppCompatActivity {
    private MapView mMapView = null;
    private LocationClient mLocationClient = null;
    public BaiduMap mBaiduMaps = null;
    private boolean isListening = false;


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
        Button numbers = findViewById(R.id.numbers);




        BaiduMap.OnMapClickListener listener = new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.img);
                OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);
                mBaiduMaps.addOverlay(option);
                set_rs(latLng);
            }
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                Log.d("mapPoi","点击了坐标");
            }
        };

        numbers.setOnClickListener(v -> {
            if (isListening) {
                mBaiduMaps.setOnMapClickListener(null); // 取消监听
                isListening = false;
                Log.d("MapClickListener", "已关闭监听");
            } else {
                mBaiduMaps.setOnMapClickListener(listener); // 开启监听
                isListening = true;
                Log.d("MapClickListener", "已开启监听");
            }
        });

        showDialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showCoordinateDialog();
                    }
                }, 3000); // 3秒延迟
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
    private void set_rs(LatLng latLng) {
        // 创建一个自定义布局的 AlertDialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout_set, null);
        Button button_send = dialogView.findViewById(R.id.send_set);
        // 获取布局中的控件
        EditText editText_set = dialogView.findViewById(R.id.set_rs_number);
        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = editText_set.getText().toString().trim(); // 获取输入内容，去除前后空格
                if (!inputText.isEmpty()) { // 防止空输入导致崩溃
                    int number = Integer.parseInt(inputText); // 转换为整数
                    Log.d("UserInput", "输入的数值: " + number);
                    showCircleFence(latLng,number);
                    sendDataToServer(latLng,number);
                } else {
                    Log.d("UserInput", "输入为空");
                }
            }
        });
        // 创建弹窗
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("创建具体的界面大小");
        builder.setView(dialogView);
        // 设置关闭按钮
        builder.setPositiveButton("关闭", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mBaiduMaps.clear();
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
    private void showCircleFence(LatLng center,int radius) {
        mBaiduMaps.clear();
        // 创建圆形区域
        CircleOptions circleOptions = new CircleOptions()
                .center(center)
                .radius(radius)
                .fillColor(0x5500FF00) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMaps.addOverlay(circleOptions);
    }
    private void showPolygonFence() {
        mBaiduMaps.clear();
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
        mBaiduMaps.addOverlay(polygonOptions);
    }
    private void sendDataToServer(LatLng latLng, int number) {
        // 服务器 URL
        String url = "https://your-server.com/api/upload";

        // 构造 JSON 数据
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("lat", latLng.latitude);
            jsonObject.put("lng", latLng.longitude);
            jsonObject.put("radius", number);
        } catch (JSONException e) {
            Log.e("JSONError", "JSON 生成错误", e);
            return;
        }

        // 创建请求体 (JSON)
        RequestBody requestBody = RequestBody.create(
                jsonObject.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        // 构建 HTTP 请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();

        // 异步发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("ServerError", "请求失败", e);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("ServerResponse", "成功: " + response.body().string());
                } else {
                    Log.e("ServerResponse", "失败: " + response.code());
                }
            }
        });
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