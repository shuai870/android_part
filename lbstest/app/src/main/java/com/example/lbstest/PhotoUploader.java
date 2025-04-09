package com.example.lbstest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class PhotoUploader {

    private static final String TAG = "PhotoUploader";
    private static  String UPLOAD_URL;

    private final Context context;
    private final OkHttpClient client;
    private  Handler mainHandler;
    public PhotoUploader(Context context) {
        this.context = context;
        this.client = new OkHttpClient();
    }

    public void uploadLatestPhoto() {
        String imagePath = getLatestPhotoPath();
        if (imagePath != null) {
            uploadImage(imagePath);
        } else {
            Log.e(TAG, "未找到最近照片");
        }
    }

    private String getLatestPhotoPath() {
        String[] projection = {MediaStore.Images.Media.DATA};
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    sortOrder
            );
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }
        } catch (Exception e) {
            Log.e(TAG, "获取照片失败: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
        }
        return null;
    }

    private void uploadImage(String imagePath) {
        File file = new File(imagePath);
        if (!file.exists()) {
            Log.e(TAG, "照片文件不存在: " + imagePath);
            return;
        }

        RequestBody fileBody = RequestBody.create(file, MediaType.parse("image/*"));
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("photo", file.getName(), fileBody)
                .build();
        UPLOAD_URL =Constants.BASE_URL + "photo/save";
        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "上传失败: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d(TAG, "上传成功");
                } else {
                    Log.e(TAG, "服务器返回错误: " + response.code());
                }
                response.close();
            }
        });
    }
}
