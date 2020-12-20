package com.easycloudbooks.easycloudbooks.common;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.fragment.CompanyProfileFragment;

public class Frag_AlertDailogBox {
    AlertDialog dialog;
    public Frag_AlertDailogBox(final Context context, String header){
        dialog=new AlertDialog.Builder(context).create();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.custom_alert_dailogbox, null);

        LinearLayout lin_edtfile = layout.findViewById(R.id.lin_edtfile);
        LinearLayout lin_takeGallary = layout.findViewById(R.id.lin_takeGallary);
        Button bt_decline = layout.findViewById(R.id.bt_decline);
        Button bt_create = layout.findViewById(R.id.bt_create);
        TextView txt_takepic = layout.findViewById(R.id.txt_takepic);
        TextView txt_gallary = layout.findViewById(R.id.txt_gallary);

        dialog.setView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(header.equalsIgnoreCase("Add Photo")){
            Log.e("TEST","header :"+header);
            lin_takeGallary.setVisibility(View.VISIBLE);
            bt_create.setVisibility(View.GONE);

            txt_takepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //((CompanyProfileFragment)).cameraIntent();
                }
            });
            txt_gallary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                   // ((CompanyProfileFragment)context).galleryIntent();
                }
            });
            bt_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }else if(header.equalsIgnoreCase("Add Photo")){

        }


    }
    public void show() {
        dialog.show();
    }
    public void hide() {
        dialog.dismiss();
    }
}

