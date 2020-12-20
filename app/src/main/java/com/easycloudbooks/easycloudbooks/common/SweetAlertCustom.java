package com.easycloudbooks.easycloudbooks.common;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;

import com.easycloudbooks.sweetalert.SweetAlertDialog;



public class SweetAlertCustom {
    public static final int LongDuration = 5000;
    public Context context;
    public SweetAlertDialog LoadingDialog;

    public void onDestroy()
    {
        if(LoadingDialog != null)
            LoadingDialog.dismiss();
    }

    public SweetAlertCustom(Context context) {
        this.context = context;
    }

    public SweetAlertDialog CreatingLoadingDialog(String LoadingMesssage) {
        if (LoadingDialog == null) {
            LoadingDialog = new SweetAlertDialog(context, SweetAlertDialog.PROGRESS_TYPE);
            LoadingDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
            LoadingDialog.setCancelable(false);
        }
        LoadingDialog.setTitleText(LoadingMesssage);
        return LoadingDialog;
    }

    public void ShowLoading() {
        if (LoadingDialog != null ) {
            LoadingDialog.show();
        }
    }

    public void HideLoading() {
        if (LoadingDialog != null && LoadingDialog.isShowing()) {
            LoadingDialog.hide();
        }
    }

    public static SweetAlertDialog ShowMessage(Context context) {
        return ShowMessage(context, null, null);
    }

    public static SweetAlertDialog ShowMessage(Context context, DialogInterface.OnDismissListener dismissListener, DialogInterface.OnCancelListener cancelListener) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText("You clicked the button!");
        if (dismissListener != null) {
            pDialog.setOnDismissListener(dismissListener);
        }
        if (cancelListener != null)
            pDialog.setOnCancelListener(cancelListener);
        pDialog.show();
        return pDialog;
    }

       public static SweetAlertDialog ShowSuccessMessage(Context context,String Message,  DialogInterface.OnDismissListener dismissListener, DialogInterface.OnCancelListener cancelListener) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText(Message);
        if (dismissListener != null) {
            pDialog.setOnDismissListener(dismissListener);
        }
        if (cancelListener != null)
            pDialog.setOnCancelListener(cancelListener);
        pDialog.show();
        return pDialog;
    }

    public static SweetAlertDialog ShowWarningMessage(Context context,String Title,String Message,  DialogInterface.OnDismissListener dismissListener, DialogInterface.OnCancelListener cancelListener) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                .setTitleText(Title)
                .setContentText(Message);
        if (dismissListener != null) {
            pDialog.setOnDismissListener(dismissListener);
        }
        if (cancelListener != null)
            pDialog.setOnCancelListener(cancelListener);
        pDialog.show();
        return pDialog;
    }


    public static SweetAlertDialog ShowErrorMessage(Context context, String Message, DialogInterface.OnDismissListener dismissListener, DialogInterface.OnCancelListener cancelListener) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops!")
                .setContentText(Message);
        if (dismissListener != null) {
            pDialog.setOnDismissListener(dismissListener);
        }
        if (cancelListener != null)
            pDialog.setOnCancelListener(cancelListener);
        pDialog.show();
        return pDialog;
    }

    public void NormalMessage(String Message) {
        SweetAlertDialog sd = new SweetAlertDialog(context);
        sd.setContentText(Message);
        sd.setCancelable(true);
        sd.setCanceledOnTouchOutside(true);
        sd.show();
    }

    public void NormalMessage(Context context,String Message) {
        SweetAlertDialog sd = new SweetAlertDialog(context);
        sd.setContentText(Message);
        sd.setCancelable(true);
        sd.setCanceledOnTouchOutside(true);
        sd.show();
    }

    public void SuccessMessage(String Message) {
        Log.e("Mitul",context == null ? "1": "0");
        new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("Good job!")
                .setContentText(Message)
                .show();
    }

    public void ErrorMessage(String Message, String Title) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText(Title)
                .setContentText(Message)
                .show();
    }

    public void ErrorMessage(String Message) {
        new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Oops...")
                .setContentText(Message)
                .show();
    }

}
