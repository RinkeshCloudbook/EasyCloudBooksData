package com.easycloudbooks.easycloudbooks.common;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.AppCompatButton;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.animation.ViewAnimation;
import com.easycloudbooks.easycloudbooks.util.Tools;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class NoteCustomDialog {
    Dialog dialog;
    DatePicker picker;
    Context context;
    TextView et_date,txt_nameNotes;
    ImageButton bt_toggle_reminder;
    LinearLayout expand_remider;
    public NoteCustomDialog(final Context context,String noteName){
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.note_dialog_add_review);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText et_post = (EditText) dialog.findViewById(R.id.et_post);
        et_date = (TextView) dialog.findViewById(R.id.et_date);
        txt_nameNotes = (TextView) dialog.findViewById(R.id.txt_nameNotes);
        bt_toggle_reminder = dialog.findViewById(R.id.bt_toggle_reminder);
        expand_remider = dialog.findViewById(R.id.expand_remider);

        final InputMethodManager mImm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);;
        et_post.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener() {

            @Override
            public void onFocusChange(final View v, boolean hasFocus) {

                mImm.showSoftInput(et_post, InputMethodManager.SHOW_FORCED);
            }

        });

        txt_nameNotes.setText(noteName);
        et_date.setText(getDateTime());
        et_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogDatePickerLight((TextView) v);

            }
        });
        bt_toggle_reminder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection(v, expand_remider);
            }
        });
        ((AppCompatButton) dialog.findViewById(R.id.bt_submit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String review = et_post.getText().toString().trim();
                if (review.isEmpty()) {
                    Toast.makeText(context, "Please fill review text", Toast.LENGTH_SHORT).show();
                } else {

                }

                dialog.dismiss();
                Toast.makeText(context, "Post Added", Toast.LENGTH_SHORT).show();
            }
        });
        ((AppCompatButton) dialog.findViewById(R.id.bt_cancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               dialog.dismiss();
            }
        });

        /*dialog.show();
        dialog.getWindow().setAttributes(lp);*/

    }

    private void toggleSection(View v, View lyt) {
        boolean show = toggleArrow(v);
        if(show){
            ViewAnimation.expand(lyt, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    //Tools.nestedScrollTo(nested_content, lyt);
                }
            });
        }else {
            ViewAnimation.collapse(lyt);
        }
    }

    private boolean toggleArrow(View v) {
        if(v.getRotation() == 0){
            v.animate().setDuration(200).rotation(180);
            return true;
        }else {
            v.animate().setDuration(200).rotation(0);
            return false;
        }
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("d/M/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void dialogDatePickerLight(TextView v) {
        Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                et_date.setText(dayOfMonth + "/" + (month + 1) + "/" + year);
            }
        },mYear, mMonth, mDay);

        datePickerDialog.show();
    }


    public void show() {
        dialog.show();
    }
    public void hide() {
        dialog.dismiss();
    }


}
