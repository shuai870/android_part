package com.example.lbstest;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
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
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.os.Handler;
import com.baidu.trace.LBSTraceClient;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
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
    private boolean isListeningE  = false;
    List<LatLng> Edata = new ArrayList<>();
    private MediaPlayer mediaPlayer;
    private static  String PHOTO_URL;

    private boolean check;
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
        Button navigateTOs = findViewById(R.id.navigates);
        Button aqqqqq = findViewById(R.id.aqqqqq);
        PHOTO_URL = Constants.BASE_URL + "photo/latest";

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
        BaiduMap.OnMapClickListener listenerE = new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.drawable.img);
                OverlayOptions option = new MarkerOptions().position(latLng).icon(bitmap);
                mBaiduMaps.addOverlay(option);
                Edata.add(latLng);
            }
            @Override
            public void onMapPoiClick(MapPoi mapPoi) {
                Log.d("mapPoi","点击了坐标");
            }
        };
        navigateTOs.setOnClickListener(v ->{
            if(isListeningE){
                showPolygonFence(Edata);
                sendDataServer(Edata);
                Edata.clear();
                mBaiduMaps.setOnMapClickListener(null);
                isListeningE = false;
            }else{
                mBaiduMaps.setOnMapClickListener(listenerE);
                isListeningE =true;
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
                }, 999);
            }
        });

          drawSimplePolyline();

        LatLng userLocation = new LatLng(34.159369,108.907082);
        mBaiduMaps.setMapStatus(MapStatusUpdateFactory.zoomTo(16));
        mBaiduMaps.setMapStatus(MapStatusUpdateFactory.newLatLng(userLocation));
        //构建Marker图标
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.one);
        //构建MarkerOption，用于在地图上添加Marker
        OverlayOptions option = new MarkerOptions()
                .position(userLocation)
                .icon(bitmap);
        //在地图上添加Marker，并显示
        mBaiduMaps.addOverlay(option);

        aqqqqq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(check == false) {
                    showOutOfBoundsAlert();
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.two);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions()
                            .position(userLocation)
                            .icon(bitmap);
                    //在地图上添加Marker，并显示
                    mBaiduMaps.addOverlay(option);
                    check = true;
                }else{
                    BitmapDescriptor bitmap = BitmapDescriptorFactory
                            .fromResource(R.drawable.one);
                    //构建MarkerOption，用于在地图上添加Marker
                    OverlayOptions option = new MarkerOptions()
                            .position(userLocation)
                            .icon(bitmap);
                    //在地图上添加Marker，并显示
                    mBaiduMaps.addOverlay(option);
                    check = false;
                }
            }
        });
    }
    private void showCoordinateDialog() {
        // 创建一个自定义布局的 AlertDialog
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_layout, null);
        // 获取布局中的控件
        Button audio_b  = dialogView.findViewById(R.id.GetAudio);
        TextView coordinateInfo = dialogView.findViewById(R.id.coordinate_info);
        ImageView imageView = dialogView.findViewById(R.id.image_view);
        Glide.with(this)
                .load(PHOTO_URL)
                .diskCacheStrategy(DiskCacheStrategy.NONE) // 不使用磁盘缓存
                .skipMemoryCache(true)  // 跳过内存缓存
                .into(imageView);
        // 设置坐标点信息
        coordinateInfo.setText("纬度：34.163308\n经度：108.908002");
        audio_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchAudioUrlAndPlay();
            }
        });
        // 创建弹窗
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("用户触发告警");
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
    private void showPolygonFence(List<LatLng> points) {
        mBaiduMaps.clear();
        PolygonOptions polygonOptions = new PolygonOptions()
                .points(points)
                .fillColor(0x5500FF00) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMaps.addOverlay(polygonOptions);
    }
    private void sendDataToServer(LatLng latLng, int number) {
        // 服务器 URL
        String url = Constants.BASE_URL + "getdata/insertdata";

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
    private void sendDataServer(List<LatLng> Edata){
        // 服务器 URL
        String url = Constants.BASE_URL + "getdata/insertlist";

        // 构造 JSON 数据

        JSONArray jsonArray = new JSONArray();
        for (LatLng latLng : Edata) {
            JSONObject jsonObjectlist = new JSONObject();
            try {
                jsonObjectlist.put("lat", latLng.latitude);
                jsonObjectlist.put("lng", latLng.longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            jsonArray.put(jsonObjectlist);
        }

        RequestBody requestBodylist = RequestBody.create(
                jsonArray.toString(),
                MediaType.parse("application/json; charset=utf-8")
        );

        // 构建 HTTP 请求
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBodylist)
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

    private void fetchAudioUrlAndPlay() {
        String requestUrl =Constants.BASE_URL + "upload/latest";
        new Thread(() -> {
            try {
                URL url = new URL(requestUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setConnectTimeout(5000);

                InputStream inputStream = conn.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                reader.close();
                inputStream.close();

                String audioUrl = Constants.BASE_URL + new JSONObject(result.toString()).getString("url");
                runOnUiThread(() -> playAudioFromUrl(audioUrl));

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("AudioFetch", "获取音频 URL 失败：" + e.getMessage());
            }
        }).start();
    }
    private void playAudioFromUrl(String audioUrl) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();

            mediaPlayer.setAudioAttributes(
                    new AudioAttributes.Builder()
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .build());
            mediaPlayer.setDataSource(audioUrl);

            mediaPlayer.setOnPreparedListener(MediaPlayer::start);
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Log.e("AudioPlay", "播放出错 what=" + what + ", extra=" + extra);
                Toast.makeText(getApplicationContext(), "音频播放失败", Toast.LENGTH_SHORT).show();
                return true;
            });
            mediaPlayer.prepareAsync();
            Log.d("AudioPlay", "准备播放: " + audioUrl);
        } catch (IOException e) {
            Log.e("AudioPlay", "IOException: " + e.getMessage());
        }
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