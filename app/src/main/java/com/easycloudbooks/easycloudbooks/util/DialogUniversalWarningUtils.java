package com.easycloudbooks.easycloudbooks.util;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.easycloudbooks.easycloudbooks.R;

public class DialogUniversalWarningUtils {

    private Dialog mDialog;

    private Context mContext;
    private String mTitle;
    private String mDescription;
    private TextView mDialogTitle;
    private TextView mDialogText;
    private TextView mDialogOKButton;
    private TextView mDialogCancelButton;

    public DialogUniversalWarningUtils(Context context,String Title, String Description) {
        this.mContext = context;
        if(Title.equals("") || Title == null) Title = "Warning";
        if(Description.equals("") || Description == null) Description = "";
        mTitle = Title;
        mDescription = Description;
    }

    public Dialog getDialog() {
        return mDialog;
    }

    public TextView getDialogOKButton() {
        return mDialogOKButton;
    }

    public TextView getDialogCancelButton() {
        return mDialogCancelButton;
    }

    public void showDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(mContext,
                    R.style.CustomDialogTheme);
        }
        mDialog.setContentView(R.layout.dialog_universal_warning);
        mDialog.setCancelable(true);
        mDialog.show();

        mDialogTitle = (TextView) mDialog.findViewById(R.id.dialog_universal_warning_title);
        mDialogText = (TextView) mDialog.findViewById(R.id.dialog_universal_warning_text);
        mDialogTitle.setText(mTitle);
        mDialogText.setText(mDescription);
        mDialogOKButton = (TextView) mDialog.findViewById(R.id.dialog_universal_warning_ok);
        mDialogCancelButton = (TextView) mDialog.findViewById(R.id.dialog_universal_warning_cancel);
    }

    private void initDialogButtons() {

        mDialogOKButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });

        mDialogCancelButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                mDialog.dismiss();
            }
        });
    }

    public void dismissDialog() {
        mDialog.dismiss();
    }
}
