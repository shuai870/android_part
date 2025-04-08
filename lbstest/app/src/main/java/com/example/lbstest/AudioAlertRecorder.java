package com.example.lbstest;
import android.content.Context;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AudioAlertRecorder {
    private static MediaRecorder recorder;
    private static File audioFile;
    private static final String TAG = "AudioAlertRecorder";

    // 开始录音，录音时长10秒
    public static void start(Context context) {
        // 获取存储路径
        File outputDir = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC);
        if (outputDir == null) {
            Log.e(TAG, "无法获取保存录音的路径");
            return;
        }
        audioFile = new File(outputDir, "alert_audio_" + System.currentTimeMillis() + ".mp3");
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setOutputFile(audioFile.getAbsolutePath());
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.prepare();
            recorder.start();
            Log.d(TAG, "录音开始: " + audioFile.getAbsolutePath());

            // 录音时长设为10秒
            new Handler().postDelayed(() -> stop(), 5000);  // 10秒后停止录音

        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, "录音失败: " + e.getMessage());
        }
    }

    // 停止录音
    public static void stop() {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
                Log.d(TAG, "录音已停止: " + audioFile.getAbsolutePath());

                // 录音停止后，在这里添加上传逻辑，比如上传到服务器
                 uploadAudioFile(audioFile);

            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "停止录音失败: " + e.getMessage());
        }
    }

    // 上传录音文件（你可以根据需要实现这个功能）
    private static void uploadAudioFile(File file) {
        RequestBody fileBody = RequestBody.create(file, MediaType.parse("audio/mp4"));
        MultipartBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("audio", file.getName(), fileBody)
                .build();

        Request request = new Request.Builder()
                .url("http://192.168.124.11:9090/upload/audio") // 替换为实际上传接口
                .post(requestBody)
                .build();

        // 发起上传请求
        OkHttpClient client = new OkHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "上传失败: " + e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "上传成功");
                }
            }
        });
    }
}
