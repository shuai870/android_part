package com.example.lbstest;
import android.os.Looper;
import android.util.Log;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.model.LatLng;
import com.google.gson.*;
import okhttp3.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import android.os.Handler;

import java.util.logging.LogRecord;

public class DynamicJsonParser {
        private static final String TAG = "DynamicJsonParser";
        private static final String URL = "https://example.com/api/geofence"; // 替换为你的服务器地址
        private final OkHttpClient client = new OkHttpClient();
        private BaiduMap mBaiduMap;
        public DynamicJsonParser(BaiduMap baiduMap){
            this.mBaiduMap  = baiduMap;
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
        // 创建圆形区域
        CircleOptions circleOptions = new CircleOptions()
                .center(center)
                .radius(radius)
                .fillColor(0x5500FF00) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMap.addOverlay(circleOptions);
    }
    private void showPolygonFence(List<LatLng> points) {
        PolygonOptions polygonOptions = new PolygonOptions()
                .points(points)
                .fillColor(0x5500FF00) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMap.addOverlay(polygonOptions);
    }
    private void showRectangleFence(List<LatLng> points) {
        PolygonOptions polygonOptions = new PolygonOptions()
                .points(points)
                .fillColor(0x55FF0000) // 填充颜色
                .stroke(new Stroke(5, 0x5500FF00)); // 边框颜色和宽度
        // 添加到地图
        mBaiduMap.addOverlay(polygonOptions);
    }
}
