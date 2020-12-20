package com.easycloudbooks.easycloudbooks.adapter;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.ContactFilterActivity;
import com.easycloudbooks.easycloudbooks.activity.ContectProfile;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.common.DataDateTime;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.model.ContactDetails;

import org.joda.time.DateTime;

import java.util.List;

public class ContactListAdapter extends RecyclerView.Adapter<ContactListAdapter.MyViewHolder> {

    List<ContactDetails> contactList;
    Context context;

    public ContactListAdapter(ContactFilterActivity contactFilterActivity, List<ContactDetails> contactList) {
        this.context = contactFilterActivity;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView  = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.contect_list_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        holder.txt_FN.setText(contactList.get(i).FN);
        holder.txt_MN.setText(contactList.get(i).MN);
        holder.txt_LN.setText(contactList.get(i).LN);

        holder.imgCardImage.setImageResource(R.drawable.bg_square);
        holder.imgCardImage.setColorFilter(null);
        String fname = contactList.get(i).FN.substring(0 ,1);
        String lname = contactList.get(i).LN.substring(0 ,1);;
        holder.icon_text.setText(fname+""+lname);
        holder.icon_text.setVisibility(View.VISIBLE);
        holder.company_profile_button.setVisibility(View.GONE);

        String email = contactList.get(i).email;
        final String phone = contactList.get(i).Ph;
        Log.e("TEST","Name Adapter NAME :"+contactList.get(i).FN+" "+contactList.get(i).LN);
        Log.e("TEST","Email :"+email);
        Log.e("TEST","Phone :"+phone);
        if(phone == null){
            holder.txt_email.setText(email);
        }else{
            holder.txt_email.setText(phone);
        }

    }


    @Override
    public int getItemCount() {
        return contactList.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView txt_FN,txt_MN,txt_LN,icon_text,txt_email;
        ImageView imgCardImage,company_profile_button;
        FButton whatApp_button,call_button;

        public MyViewHolder(View itemView) {
            super(itemView);
            txt_FN =itemView.findViewById(R.id.txt_FN);
            txt_MN =itemView.findViewById(R.id.txt_LN);
            txt_LN =itemView.findViewById(R.id.txt_LN);
            imgCardImage =itemView.findViewById(R.id.card_image);
            company_profile_button =itemView.findViewById(R.id.company_profile_button);
            icon_text =itemView.findViewById(R.id.icon_text);
            txt_email =itemView.findViewById(R.id.txt_email);
            whatApp_button =itemView.findViewById(R.id.whatApp_button);
            call_button =itemView.findViewById(R.id.call_button);
        }
    }
}
