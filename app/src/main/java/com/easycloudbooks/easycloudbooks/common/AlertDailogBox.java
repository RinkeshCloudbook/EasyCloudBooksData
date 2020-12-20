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

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.CompanyProfileActivity;
import com.easycloudbooks.easycloudbooks.activity.ContectProfile;
import com.easycloudbooks.easycloudbooks.fragment.CompanyProfileFragment;
import com.easycloudbooks.easycloudbooks.util.MyDialogClickListener;

import org.w3c.dom.Text;

public class AlertDailogBox {
    AlertDialog dialog;
    public AlertDailogBox(final Context context, String header, final MyDialogClickListener myDialogClickListener){
        dialog=new AlertDialog.Builder(context).create();
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = layoutInflater.inflate(R.layout.custom_alert_dailogbox, null);

       LinearLayout lin_edtfile = layout.findViewById(R.id.lin_edtfile);
       LinearLayout lin_takeGallary = layout.findViewById(R.id.lin_takeGallary);
       Button bt_decline = layout.findViewById(R.id.bt_decline);
       Button bt_create = layout.findViewById(R.id.bt_create);
       TextView txt_takepic = layout.findViewById(R.id.txt_takepic);
       TextView txt_gallary = layout.findViewById(R.id.txt_gallary);
       TextView tvHeader = layout.findViewById(R.id.tvHeader);

        dialog.setView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        if(header.equalsIgnoreCase("Add Photo")){
            tvHeader.setText(header);
            lin_takeGallary.setVisibility(View.VISIBLE);
            bt_create.setVisibility(View.GONE);

            txt_takepic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
//                    ((ContectProfile)context).cameraIntent();
                    myDialogClickListener.onMyDialogClick(true);
                    //intefrace bna k krle

                }
            });
            txt_gallary.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    myDialogClickListener.onMyDialogClick(false);
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
