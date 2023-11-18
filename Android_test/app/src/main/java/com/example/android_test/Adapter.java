package com.example.android_test;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {


    private List<Part> partList;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_item,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Part part = partList.get(position);
        List<Integer> pushABCD = new ArrayList<>();
        holder.ID.setText(String.valueOf(part.getID()));
        holder.a.setText(String.valueOf(part.getA()));
        holder.b.setText(String.valueOf(part.getB()));
        holder.c.setText(String.valueOf(part.getC()));
        holder.d.setText(String.valueOf(part.getD()));
        holder.ph.setText(String.valueOf(part.getPh()));
        holder.wo.setText(String.valueOf(part.getWo()));
        Button push = holder.itemView.findViewById(R.id.push);
        Button work = holder.itemView.findViewById(R.id.work);
        CheckBox A = holder.itemView.findViewById(R.id.A);
        CheckBox B = holder.itemView.findViewById(R.id.B);
        CheckBox C = holder.itemView.findViewById(R.id.C);
        CheckBox D = holder.itemView.findViewById(R.id.D);
        push.setOnClickListener(new View.OnClickListener() {
            Boolean worked = false;
            @Override
            public void onClick(View view) {

                if(worked == false) {
                    pushABCD.add(part.getID());
                    if (A.isChecked()) {
                        pushABCD.add(1);
                    }
                    if (B.isChecked()) {
                        pushABCD.add(2);
                    }
                    if (C.isChecked()) {
                        pushABCD.add(3);
                    }
                    if (D.isChecked()) {
                        pushABCD.add(4);
                    }
                    Log.d("Mainactivity", pushABCD.toString());


                    //发送到服务器  toString
                    push.setText("撤销");
                    push.setBackgroundColor(Color.GRAY);
                    worked = true;
                } else if (worked == true) {
                    //写个路由 传进去 删除序号
                    pushABCD.add(part.getID());
                    push.setText("再发");
                    worked = false;
                }
            }
        });
        work.setOnClickListener(new View.OnClickListener() {
            Boolean change = false;
            @Override
            public void onClick(View view) {
                if(change == false){
                    //写好路由 发个1
                    pushABCD.add(part.getID());
                    pushABCD.add(1);
                    work.setText("撤销");
                    work.setBackgroundColor(Color.GRAY);
                    change = true;
                } else if (change == true) {
                    //修改了
                    pushABCD.add(part.getID());
                    pushABCD.add(0);
                    work.setText("再做");
                    change = false;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return partList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView ID;
        TextView a;
        TextView b;
        TextView c;
        TextView d;
        TextView ph;
        TextView wo;
        public ViewHolder(View view){
            super(view);
            ID = view.findViewById(R.id.ID);
            a = view.findViewById(R.id.a);
            b = view.findViewById(R.id.b);
            c = view.findViewById(R.id.c);
            d = view.findViewById(R.id.d);
            ph = view.findViewById(R.id.ph);
            wo = view.findViewById(R.id.wo);
        }
    }
    public Adapter(List<Part> partlist){
        partList = partlist;
    }
}
