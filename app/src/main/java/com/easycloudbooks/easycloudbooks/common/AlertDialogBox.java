package com.easycloudbooks.easycloudbooks.common;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.fragment.DocumentUpload;
import com.easycloudbooks.easycloudbooks.util.MyDialogClickListenerString;


public class AlertDialogBox {
    AlertDialog dialog;

    public AlertDialogBox(final FragmentActivity activity, String header, final MyDialogClickListenerString myDialogClickListener) {
        dialog=new AlertDialog.Builder(activity).create();
        LayoutInflater layoutInflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.custom_alert_document_dailogbox, null);

        LinearLayout lin_edtfile = layout.findViewById(R.id.lin_edtfile);
        LinearLayout lin_takeGallary = layout.findViewById(R.id.lin_takeGallary);
        Button bt_decline = layout.findViewById(R.id.bt_decline);
        Button bt_create = layout.findViewById(R.id.bt_create);
        TextView txt_takepic = layout.findViewById(R.id.txt_takepic);
        TextView txt_gallary = layout.findViewById(R.id.txt_gallary);
        TextView txt_folder = layout.findViewById(R.id.txt_folder);

        dialog.setView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Log.e("TEST","header :"+header);

        if(header.equalsIgnoreCase("Add Photo")){
            Log.e("TEST","header :"+header);
            lin_takeGallary.setVisibility(View.VISIBLE);
            bt_create.setVisibility(View.GONE);

            txt_takepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //((DocumentUpload)activity).cameraIntent();
                    myDialogClickListener.onMyDialogClick("cam","","");
                    //intefrace bna k krle

                }
            });
            txt_gallary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //((DocumentUpload)activity).galleryIntent();
                    myDialogClickListener.onMyDialogClick("","gal","");
                }
            });
            txt_folder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    //((DocumentUpload)activity).galleryIntent();
                    myDialogClickListener.onMyDialogClick("","","folder");
                }
            });
            bt_decline.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }else if(header.equalsIgnoreCase("Camera")){

        }


    }

    public void show() {
        dialog.show();
    }
    public void hide() {
        dialog.dismiss();
    }

}
