package com.example.android_test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
private List<Part> partList = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initPart();
        RecyclerView recyclerView = findViewById(R.id.recycle_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Adapter adapter = new Adapter(partList);
        recyclerView.setAdapter(adapter);
    }
    private void initPart(){
        //传回来几个item
        int max = 0;
            max = 15;//把这个作为行数
        for(int i = 0; i < max; i++){
            Part part = new Part();
            part.setID(i);
            partList.add(part);
        }
    }
}