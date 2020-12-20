package com.easycloudbooks.easycloudbooks.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.model.ContactDetails;

import java.util.List;

public class BottumListAdapter extends RecyclerView.Adapter<BottumListAdapter.MyViewHolder> {
    Context context;
    List<ContactDetails> bottumList;
    public BottumListAdapter(Context applicationContext, List<ContactDetails> bottumList) {
        this.context = applicationContext;
        this.bottumList = bottumList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contect_bottum_list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int i) {
        Log.e("TEST","Adapter Company Name :"+bottumList.get(i).cName);
        holder.txt_cName.setText(bottumList.get(i).cName);
        holder.txt_cIndustry.setText(bottumList.get(i).industryType);
        holder.txt_cPosition.setText(bottumList.get(i).position);
        holder.txt_cJoinDate.setText(bottumList.get(i).jdate);
    }

    @Override
    public int getItemCount() {
        return bottumList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView txt_cName,txt_cIndustry,txt_cPosition,txt_cJoinDate;
        ImageView imgCardImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txt_cName =itemView.findViewById(R.id.txt_cName);
            txt_cIndustry =itemView.findViewById(R.id.txt_cIndustry);
            txt_cPosition =itemView.findViewById(R.id.txt_cPosition);
            imgCardImage =itemView.findViewById(R.id.card_image);
            txt_cJoinDate =itemView.findViewById(R.id.txt_JoinDate);
        }
        }
}
