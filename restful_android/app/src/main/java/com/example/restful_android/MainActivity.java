package com.example.restful_android;

import static android.os.Build.VERSION.SDK_INT;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Button send_request;
    TextView response_text;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         send_request =  findViewById(R.id.send_request);
         response_text = findViewById(R.id.response_text);
         send_request.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.send_request){
            sendRequestWithHttpConnection();
        }
    }
    private void sendRequestWithHttpConnection(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    OkHttpClient client = new OkHttpClient();
                    JSONObject jsondata_1 = new JSONObject();
//                    jsondata_1.put("usernumber","1");
//                    jsondata_1.put("latitude","17");
//                    jsondata_1.put("longitude","77");
//                    jsondata_1.put("time","1");
//                    jsondata_1.put("warntype","2");
//                    jsondata_1.put("heartrate","2");
//                    jsondata_1.put("groupnumber","2");
                    jsondata_1.put("usernumber",1);
                    jsondata_1.put("latitude",17);
                    jsondata_1.put("longitude",77);
                    jsondata_1.put("time",1);
                    jsondata_1.put("warntype",2);
                    jsondata_1.put("heartrate",2);
                    jsondata_1.put("groupnumber",2);
                    RequestBody requestBody = RequestBody.create(JSON, String.valueOf(jsondata_1));
                    Request request = new Request.Builder()
                            .url("http://101.43.205.134:8089/onelocation/insert")
                            .post(requestBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();

                    showResponse(responseData);
                }catch(Exception e){
                    e.printStackTrace();
               }
            }
        }).start();
    }
    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                response_text.setText(response);
            }
        });
    }
}