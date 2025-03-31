package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;



import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

    public class MainActivity extends AppCompatActivity{

        private EditText nameInput, ageInput, locationInput;
        private ImageView photoImage;
        private Uri photoUri;

        private ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        photoUri = result.getData().getData();
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), photoUri);
                            photoImage.setImageBitmap(bitmap);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            nameInput = findViewById(R.id.name_input);
            ageInput = findViewById(R.id.age_input);
            locationInput = findViewById(R.id.location_input);
            photoImage = findViewById(R.id.photo_image);

            Button uploadButton = findViewById(R.id.upload_button);
            Button submitButton = findViewById(R.id.submit_button);

            uploadButton.setOnClickListener(v -> openImagePicker());
            submitButton.setOnClickListener(v -> submitReport());
        }

        private void openImagePicker() {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        }

        private void submitReport() {
            String name = nameInput.getText().toString().trim();
            String age = ageInput.getText().toString().trim();
            String location = locationInput.getText().toString().trim();

            if (name.isEmpty() || age.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "请填写所有信息", Toast.LENGTH_SHORT).show();
            } else {
                // 上传数据到服务器的逻辑
                Toast.makeText(this, "报备成功", Toast.LENGTH_SHORT).show();
                // 你可以选择跳转到另一个界面或者清空字段
            }
        }
    }

