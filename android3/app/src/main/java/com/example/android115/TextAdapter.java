package com.example.android115;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TextAdapter extends ArrayAdapter<Text> {
    private int resourceID;

    public TextAdapter(@NonNull Context context, int resource, @NonNull List<Text> objects) {
        super(context, resource, objects);
        resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Text text = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceID,parent,false);
        ImageView textImage = view.findViewById(R.id.text_image);
        TextView textName = view.findViewById(R.id.text_name);
        textImage.setImageResource(text.getImageID());
        textName.setText(text.getName());
        return view;
    }
}

