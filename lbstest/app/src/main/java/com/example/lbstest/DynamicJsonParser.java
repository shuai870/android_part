package com.example.lbstest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import com.baidu.geofence.GeoFenceListener;
import com.baidu.geofence.model.DPoint;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.*;
import okhttp3.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.os.Handler;
import android.widget.Toast;

import com.baidu.geofence.GeoFence;
import com.baidu.geofence.GeoFenceClient;
import com.baidu.geofence.PoiItem;

public class DynamicJsonParser {
        private static final String TAG = "DynamicJsonParser";
        private static final String URL = "http://192.168.124.11:9090/getdata";
        private final OkHttpClient client = new OkHttpClient();

        private BaiduMap mBaiduMap;
        private GeoFenceClient mGeoFenceClient;
        private Context context;
        // 定义接收的action字符串
        public static final String GEOFENCE_BROADCAST_ACTION = "com.example.lbstest.GEOFENCE_BROADCAST";
        private TextToSpeech mTextToSpeech;
        private Vibrator mVibrator;
        private Handler mHandler = new Handler(Looper.getMainLooper());
        public DynamicJsonParser(Context context, BaiduMap baiduMap){


            this.context=context;
            this.mBaiduMap  = baiduMap;
            this.mGeoFenceClient = new GeoFenceClient(context.getApplicationContext());
            // 创建并设置PendingIntent
            mGeoFenceClient.createPendingIntent(GEOFENCE_BROADCAST_ACTION);
            setupGeoFenceListener();
            registerGeoFenceReceiver();
        }
        public void fetchAndParseJson() {

            MediaType mediaType = MediaType.parse("text/plain");

            RequestBody requestBody = RequestBody.create(mediaType, "a");

            Request request = new Request.Builder().url(URL)
                    .post(requestBody)
                    .build();
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Request failed: " + e.getMessage());
                }
                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Unexpected response: " + response);
                        return;
                    }
                    String jsonResponse = response.body().string();
                    new Handler(Looper.getMainLooper()).post(() -> parseGeoFenceJson(jsonResponse));
                }
            });
        }
    private void parseGeoFenceJson(String json) {
        try {
            JsonElement jsonElement = JsonParser.parseString(json);

            if (jsonElement.isJsonArray()) {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                parseFenceData(jsonArray);
            } else {
                Log.w(TAG, "Invalid JSON format: " + json);
            }
        } catch (Exception e) {
            Log.e(TAG, "JSON parsing error: " + e.getMessage());
            Log.d(TAG,json);
        }
    }

    private void parseFenceData(JsonArray jsonArray) {
        if (jsonArray.size() < 2) {
            Log.e(TAG, "Invalid fence data, missing required elements.");
            return;
        }

        String firstValue = jsonArray.get(0).getAsString();
        String secondValue = jsonArray.get(1).getAsString();

        if (!"F".equals(firstValue)) {
            Log.w(TAG, "Not a fence data format: " + jsonArray);
            return;
        }

        if ("c".equals(secondValue)) {
            Modelc modelc =new Modelc();
            modelc = parseCircularFence(jsonArray);
            showCircleFence(modelc.getRadius(),modelc.getCenter());

        } else if ("e".equals(secondValue)) {
            showPolygonFence(parsePolygonFence(jsonArray));
        } else {
            Log.w(TAG, "Unknown fence type: " + secondValue);
        }
    }
    private Modelc parseCircularFence(JsonArray jsonArray) {
            Modelc modelc = new Modelc();
        if (jsonArray.size() < 3 || !jsonArray.get(2).isJsonArray()) {
            Log.e(TAG, "Invalid circular fence format.");
            return modelc;
        }
        JsonArray circleData = jsonArray.get(2).getAsJsonArray();
        if (circleData.size() != 3) {
            Log.e(TAG, "Circular fence should contain [latitude, longitude, radius]");
            return modelc;
        }
        double latitude = circleData.get(0).getAsDouble();
        double longitude = circleData.get(1).getAsDouble();
        int radius = circleData.get(2).getAsInt();
        // 定义圆形围栏的中心和半径
        LatLng center = new LatLng(latitude, longitude);
        modelc.setRadius(radius);
        modelc.setCenter(center);
        return modelc;
    }

    private List<LatLng> parsePolygonFence(JsonArray jsonArray) {
        List<LatLng> polygonVertices = new ArrayList<>();
        if (jsonArray.size() < 3 || !jsonArray.get(2).isJsonArray()) {
            Log.e(TAG, "Invalid polygon fence format.");
            return polygonVertices;
        }

        JsonArray polygonData = jsonArray.get(2).getAsJsonArray();
        Log.d(TAG, "Parsed Polygon Fence with " + polygonData.size() + " vertices:");

        for (JsonElement element : polygonData) {
            if (element.isJsonArray()) {
                JsonArray point = element.getAsJsonArray();
                if (point.size() == 2) {
                    double latitude = point.get(0).getAsDouble();
                    double longitude = point.get(1).getAsDouble();
                    LatLng vertex = new LatLng(latitude, longitude);
                    polygonVertices.add(vertex);
                }
            }
        }
        return polygonVertices;
    }
    private void showCircleFence(int radius , LatLng center) {
        mGeoFenceClient.removeGeoFence();
        mBaiduMap.clear();
        // 创建圆形区域
        CircleOptions circleOptions = new CircleOptions()
                .center(center)
                .radius(radius)
                .fillColor(0x5500FF00) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMap.addOverlay(circleOptions);
        DPoint dPoint = new DPoint(center.latitude,center.longitude);
        mGeoFenceClient.addGeoFence (dPoint,GeoFenceClient.BD09LL,radius,"业务ID");
    }
    private void showPolygonFence(List<LatLng> points) {
        mGeoFenceClient.removeGeoFence();
        mBaiduMap.clear();
        PolygonOptions polygonOptions = new PolygonOptions()
                .points(points)
                .fillColor(0x5500FF00) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMap.addOverlay(polygonOptions);
    }
    private void setupGeoFenceListener() {
        mGeoFenceClient.setActivateAction(GeoFenceClient.GEOFENCE_IN |
                GeoFenceClient.GEOFENCE_OUT |
                GeoFenceClient.GEOFENCE_STAYED);
        GeoFenceListener fenceListener = new GeoFenceListener() {
            @Override
            public void onGeoFenceCreateFinished(List<GeoFence> list, int i, String s) {
                if (i == GeoFence.ADDGEOFENCE_SUCCESS) {
                    Log.d(TAG, "围栏添加成功! 总数: " + list.size());
                } else {
                    Log.e(TAG, "围栏");
                }
            }
        };
        mGeoFenceClient.setGeoFenceListener(fenceListener);

    }
    public void registerGeoFenceReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(GEOFENCE_BROADCAST_ACTION);
        context.registerReceiver(mGeoFenceReceiver, filter);
    }

    // 创建广播接收器，监听围栏触发事件
    private BroadcastReceiver mGeoFenceReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(GEOFENCE_BROADCAST_ACTION)) {
                Bundle bundle = intent.getExtras();
                if (bundle == null) return;

                // 获取围栏状态（进入、离开、停留）
                int status = bundle.getInt(GeoFence.BUNDLE_KEY_FENCESTATUS);
                String customId = bundle.getString(GeoFence.BUNDLE_KEY_CUSTOMID); // 自定义围栏标识
                String fenceId = bundle.getString(GeoFence.BUNDLE_KEY_FENCEID); // 围栏ID
                GeoFence fence = bundle.getParcelable(GeoFence.BUNDLE_KEY_FENCE); // 获取当前触发的围栏对象
                // 解析围栏状态
                String statusText = "";
                switch (status) {
                    case GeoFence.STATUS_IN:
                        statusText = "进入围栏";
                        break;
                    case GeoFence.STATUS_OUT:
                        statusText = "离开围栏";
                        break;
                    case GeoFence.STATUS_STAYED:
                        statusText = "在围栏内停留";
                        Toast.makeText(context, "在安全范围内 轨迹记录关闭", Toast.LENGTH_SHORT).show();
                        break;
                    case GeoFence.INIT_STATUS_OUT:
                        statusText = "在围栏外";
                        triggerAlert(context, "已离开安全区域"); // 触发震动 & 语音播报
                        break;
                    case GeoFence.INIT_STATUS_IN:
                        statusText = "在围栏内";
                        break;
                    default:
                        statusText = "未知状态";
                        break;
                }
                Log.d("GeoFenceReceiver", "围栏触发: " + statusText + " | 围栏ID: " + fenceId);
            }
        }
    };

    /**
     * 触发震动 & 语音播报
     */
    private void triggerAlert(Context context, String message) {
        // 在主线程执行震动 & 语音播报
        mHandler.post(() -> {
            Toast.makeText(context, "请注意环境 不在围栏之内", Toast.LENGTH_SHORT).show();
            // 震动 1 秒
            if (mVibrator == null) {
                mVibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            }
            if (mVibrator != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    mVibrator.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                }
            }
            // 语音播报
            if (mTextToSpeech != null) {
                mTextToSpeech.speak(message, TextToSpeech.QUEUE_FLUSH, null, null);
            }

        });
    }
    /**
     * 在应用启动时初始化 TextToSpeech
     */
    public void initTextToSpeech(Context context) {
        if (mTextToSpeech == null) {
            mTextToSpeech = new TextToSpeech(context.getApplicationContext(), status -> {
                if (status == TextToSpeech.SUCCESS) {
                    mTextToSpeech.setLanguage(Locale.CHINESE);
                }
            });
        }
    }

    /**
     * 在应用销毁时释放 TextToSpeech 资源
     */
    public void releaseTextToSpeech() {
        if (mTextToSpeech != null) {
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
        }
    }
    private void showRectangleFence(List<LatLng> points) {
        mBaiduMap.clear();
        PolygonOptions polygonOptions = new PolygonOptions()
                .points(points)
                .fillColor(0x55FF0000) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMap.addOverlay(polygonOptions);
        ArrayList<DPoint> dPointList = new ArrayList<>();
        for (LatLng point : points) {
            DPoint dPoint = new DPoint(point.latitude,point.longitude);
            dPointList.add(dPoint);
        }
        mGeoFenceClient.addGeoFence(dPointList,GeoFenceClient.BD09LL,"业务ID" );
    }

}
