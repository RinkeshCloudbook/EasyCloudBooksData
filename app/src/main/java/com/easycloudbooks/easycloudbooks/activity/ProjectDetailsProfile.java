package com.easycloudbooks.easycloudbooks.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.animation.ViewAnimation;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.NoteCustomDialog;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.fragment.ContactFragment;
import com.easycloudbooks.easycloudbooks.model.ProjectJ;
import com.easycloudbooks.easycloudbooks.model.RoleJ;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.util.Helper;
import com.easycloudbooks.easycloudbooks.util.ImageHelper;
import com.easycloudbooks.easycloudbooks.util.Tools;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.List;

import info.androidhive.fontawesome.FontTextView;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_PROJECT_GET;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_PROJECT_GETDETAILS;

public class ProjectDetailsProfile extends AppCompatActivity {

    private static final String TAG = ProjectDetailsProfile.class.getSimpleName();
    public  CustomAuthRequest request;
    public View mView;
    ImageView company_image;
    TextView company_image_text,txt_DOF;
    LinearLayout pbHeaderProgress,lin_tab_info,lin_info,lin_service,lyt_expand_PC,lyt_expand_CC,
            lyt_expand_filling,lyt_expand_PC_other,lyt_expand_CC_group,lyt_expand_Group_CC,lyt_expand_CC_other
            ,lyt_expand_CC_group1,lyt_expand_CC_group2,lyt_expand_CC_filling_details;
    ImageButton bt_toggle_PC,bt_toggle_CC,bt_toggle_filling,bt_toggle_other,bt_toggle_group,bt_toggle_CC_group
            ,bt_toggle_CC_other,bt_toggle_group2,bt_toggle_filling_details;
    NestedScrollView nested_content;
    EditText edt_selectProject,edt_CC_selectProject,edt_DOF,edt_DOE,edt_DOB;
    String CRId;
    String nameCR;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_details_profile);

        company_image_text = (TextView)findViewById(R.id.company_image_text);
        company_image = (ImageView)findViewById(R.id.company_image);
        pbHeaderProgress = findViewById(R.id.pbHeaderProgress);
        lin_tab_info = findViewById(R.id.lin_tab_info);
        lin_info = findViewById(R.id.lin_info);
        //lin_service = findViewById(R.id.lin_service);
        bt_toggle_PC = findViewById(R.id.bt_toggle_PC);
        lyt_expand_PC = findViewById(R.id.lyt_expand_PC);
        bt_toggle_CC = findViewById(R.id.bt_toggle_CC);
        bt_toggle_filling = findViewById(R.id.bt_toggle_filling);
        lyt_expand_filling = findViewById(R.id.lyt_expand_filling);
        bt_toggle_other = findViewById(R.id.bt_toggle_other);
        lyt_expand_PC_other = findViewById(R.id.lyt_expand_PC_other);
        nested_content = findViewById(R.id.nested_content);
        edt_selectProject = findViewById(R.id.edt_selectProject);
        edt_CC_selectProject = findViewById(R.id.edt_CC_selectProject);
        lyt_expand_Group_CC = findViewById(R.id.lyt_expand_Group_CC);
        bt_toggle_CC_group = findViewById(R.id.bt_toggle_CC_group);
        bt_toggle_CC_other = findViewById(R.id.bt_toggle_CC_other);
        lyt_expand_CC_other = findViewById(R.id.lyt_expand_CC_other);
        lyt_expand_CC_group1 = findViewById(R.id.lyt_expand_CC_group1);
        bt_toggle_group2 = findViewById(R.id.bt_toggle_group2);
        lyt_expand_CC_group2 = findViewById(R.id.lyt_expand_CC_group2);
        bt_toggle_filling_details = findViewById(R.id.bt_toggle_filling_details);
        lyt_expand_CC_filling_details = findViewById(R.id.lyt_expand_CC_filling_details);
        edt_DOF = findViewById(R.id.edt_DOF);
        edt_DOE = findViewById(R.id.edt_DOE);
        edt_DOB = findViewById(R.id.edt_DOB);
        txt_DOF = findViewById(R.id.txt_DOF);

        Intent intent = getIntent();
        String pId = intent.getStringExtra("pId");
        String pService = intent.getStringExtra("pSe");
        String pstatus = intent.getStringExtra("pSt");

        if(pId != null){
            getProjectsDetails(pId);
        }else {

        }
        ((TextView) findViewById(R.id.txt_pService)).setText(pService);
        ((TextView) findViewById(R.id.txt_pStatus)).setText(pstatus);
        ((ImageView) findViewById(R.id.img_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        lin_tab_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.txt_info_count)).setTextColor(Color.parseColor("#1c84c6"));
                ((TextView) findViewById(R.id.txt_info)).setTextColor(Color.parseColor("#1c84c6"));
                ((TextView) findViewById(R.id.txtNote)).setTextColor(Color.parseColor("#999999"));

                lin_info.setVisibility(View.VISIBLE);
            }
        });

        ((LinearLayout) findViewById(R.id.contactList)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // String cId = String.valueOf(0);

            }
        });

        ((LinearLayout) findViewById(R.id.lin_projectNotes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) findViewById(R.id.txtNote)).setTextColor(Color.parseColor("#1c84c6"));
                ((TextView) findViewById(R.id.txt_info)).setTextColor(Color.parseColor("#999999"));
                ((LinearLayout) findViewById(R.id.lin_info)).setVisibility(View.GONE);
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_notes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteCustomDialog dialog = new NoteCustomDialog(ProjectDetailsProfile.this,nameCR);
                dialog.show();
            }
        });
        edt_DOF.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DD/MM/YYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("TEST","Date :"+s);
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    edt_DOF.setText(current);
                    edt_DOF.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_DOE.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DD/MM/YYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("TEST","Date :"+s);
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    edt_DOE.setText(current);
                    edt_DOE.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        edt_DOB.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DD/MM/YYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.e("TEST","Date :"+s);
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d.]|\\.", "");
                    String cleanC = current.replaceAll("[^\\d.]|\\.", "");

                    int cl = clean.length();
                    int sel = cl;
                    for (int i = 2; i <= cl && i < 6; i += 2) {
                        sel++;
                    }
                    //Fix for pressing delete next to a forward slash
                    if (clean.equals(cleanC)) sel--;

                    if (clean.length() < 8){
                        clean = clean + ddmmyyyy.substring(clean.length());
                    }else{
                        //This part makes sure that when we finish entering numbers
                        //the date is correct, fixing it otherwise
                        int day  = Integer.parseInt(clean.substring(0,2));
                        int mon  = Integer.parseInt(clean.substring(2,4));
                        int year = Integer.parseInt(clean.substring(4,8));

                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(Calendar.DATE))? cal.getActualMaximum(Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    edt_DOB.setText(current);
                    edt_DOB.setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        ((FontTextView) findViewById(R.id.img_contact)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nested_content.setVisibility(View.GONE);
                ((TextView) findViewById(R.id.txt_selectDirName)).setText("Contact");
                Bundle bundle = new Bundle();
                bundle.putString("contactId", CRId);
                Fragment fragment = new ContactFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_contact_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
            }
        });
        bt_toggle_PC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        bt_toggle_CC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection(v, lyt_expand_Group_CC);
            }
        });
        bt_toggle_filling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection(v, lyt_expand_filling);
            }
        });
        bt_toggle_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection(v, lyt_expand_PC_other);
            }
        });
        edt_selectProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryDialog(v);
            }
        });
        edt_CC_selectProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCountryDialog(v);
            }
        });

        bt_toggle_CC_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection(v, lyt_expand_CC_group1);
            }
        });
        bt_toggle_CC_other.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection(v, lyt_expand_CC_other);
            }
        });
        bt_toggle_group2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection(v, lyt_expand_CC_group2);
            }
        });
        bt_toggle_filling_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleSection(v, lyt_expand_CC_filling_details);
            }
        });
    }

    private void showCountryDialog(final View v) {
        final String[] array = new String[]{
                "Test 1", "Test 2", "Test 3"
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Project");
        builder.setSingleChoiceItems(array, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ((EditText) v).setText(array[i]);
                dialogInterface.dismiss();
            }
        });
        builder.show();
    }

    private void toggleSection(View v, final View lyt) {
        boolean show = toggleArrow(v);
        if(show){
            ViewAnimation.expand(lyt, new ViewAnimation.AnimListener() {
                @Override
                public void onFinish() {
                    Tools.nestedScrollTo(nested_content, lyt);
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

    private void getProjectsDetails(final String pId) {
        pbHeaderProgress.setVisibility(View.VISIBLE);
        request =  new CustomAuthRequest(Request.Method.POST, METHOD_PROJECT_GETDETAILS, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.w(TAG, response.toString() );
                        if (App.getInstance().authorizeSimple(response)) {
                            if(response.toString() != null){
                                pbHeaderProgress.setVisibility(View.GONE);
                                lin_info.setVisibility(View.VISIBLE);
                               // lin_service.setVisibility(View.VISIBLE);

                                try {
                                    JSONObject object = new JSONObject(response.toString());
                                    JSONArray jArr =object.getJSONArray("obj");
                                    for (int i = 0; i < jArr.length(); i++) {

                                        JSONObject obj = jArr.getJSONObject(i);
                                        String projectId = obj.getString("ProjectId");
                                        String title = obj.getString("Title");
                                        String status = obj.getString("Status");

                                        JSONObject serObj = obj.getJSONObject("Service");
                                        String serviceName = serObj.getString("ServiceName");

                                        JSONObject ObjCR = obj.getJSONObject("CR");
                                        CRId = ObjCR.getString("CRId");
                                        nameCR = ObjCR.getString("Name");

                                        company_image.setImageResource(R.drawable.bg_square);
                                        company_image.setColorFilter(null);
                                        company_image_text.setVisibility(View.VISIBLE);

                                        ((TextView) findViewById(R.id.txt_project_name)).setText(nameCR);
                                        if(serviceName != null){
                                            //((TextView) findViewById(R.id.txt_serviceName)).setText(serviceName);
                                            ((TextView) findViewById(R.id.txt_service)).setText(serviceName);
                                        }else {
                                            //((TextView) findViewById(R.id.txt_serviceName)).setText("No Service");
                                        }

                                        //((TextView) findViewById(R.id.txt_company)).setText(nameCR);
                                        //((TextView) findViewById(R.id.txt_timeperiod)).setText();
                                       // ((TextView) findViewById(R.id.txt_Stage)).setText(status);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }



                        } else {
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                            /*try {
                                if (response.getString("Message") != null)
                                    //SnakebarCustom.danger(getApplicationContext(), mView, response.getString("Message"), 5000);
                            } catch (JSONException ex) {
                            }*/
                        }
                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TEST","onErrorResponse");
                pbHeaderProgress.setVisibility(View.VISIBLE);
              //  SnakebarCustom.danger(getApplicationContext(), mView, "Unable to fetch Projects: " + error.getMessage(), 5000);
            }
        }) {

            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    JSONArray jsonArray = new JSONArray();
                   jsonArray.put(pId);

                    params.put("ProjectIds", jsonArray);

                  //  params.put("filterExpression", filterExpression);
                    return params;
                } catch (JSONException ex) {
                    DataGlobal.SaveLog(TAG, ex);
                    return null;
                }
            }

            @Override
            protected void onCreateFinished(CustomAuthRequest request) {
                int socketTimeout = 300000;//0 seconds - change to what you want
                RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                request.customRequest.setRetryPolicy(policy);
                App.getInstance().addToRequestQueue(request);
            }
        };
    }


}
