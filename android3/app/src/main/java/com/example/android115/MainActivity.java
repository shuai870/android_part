package com.example.android115;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private String[] Data = {"a","b","c","d","e","f","g","h","i","j","k","l","m"};
    private List<Text> textList = new ArrayList<>();
    private ListView list_view;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAllText();
        TextAdapter adapter = new TextAdapter(MainActivity.this,R.layout.text_item,textList);
       // ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1,Data);
        list_view = (ListView)findViewById(R.id.list_view);
        list_view.setAdapter(adapter);
        list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Text text_v = textList.get(i);
                Log.d("MainActivity",text_v.getName());
            }
        });
    }
    private void initAllText(){
//        Text a = new Text("a",R.drawable.ic_launcher_background);
//        textList.add(a);
        String x;
        for(int i = 0 ; i < Data.length ; i++){
            x=Data[i];
            Text text_v = new Text(x,R.drawable.ic_launcher_background);
            textList.add(text_v);
        }

    }
}