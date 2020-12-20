package com.easycloudbooks.easycloudbooks.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.Retrofit.ContectExample;
import com.easycloudbooks.easycloudbooks.Retrofit.FileUploadService;
import com.easycloudbooks.easycloudbooks.Retrofit.UserFileUpload;
import com.easycloudbooks.easycloudbooks.adapter.BottumListAdapter;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.common.AlertDailogBox;
import com.easycloudbooks.easycloudbooks.common.DataDateTime;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.NoteCustomDialog;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.fragment.CompanyFragment;
import com.easycloudbooks.easycloudbooks.fragment.ProjectFragment;
import com.easycloudbooks.easycloudbooks.model.CompanyRelationJ;
import com.easycloudbooks.easycloudbooks.model.ContactDetails;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.util.DataText;
import com.easycloudbooks.easycloudbooks.util.GenericFileProvider;
import com.easycloudbooks.easycloudbooks.util.MyDialogClickListener;
import com.easycloudbooks.easycloudbooks.util.RealPathUtil;
import com.easycloudbooks.easycloudbooks.util.Utility;
import com.easycloudbooks.easycloudbooks.view.siv.CircularImageView;
import com.google.android.gms.maps.model.Circle;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.GsonBuilder;

import org.joda.time.DateTime;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import info.androidhive.fontawesome.FontTextView;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_CONTACT_PROFILE;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_CONTACT_PROJECT_FILTER;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_UPLOAD_COMPANY_PROFILE;
import static com.easycloudbooks.easycloudbooks.fragment.CompanyProfileFragment.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;

public class ContectProfile extends AppCompatActivity implements MyDialogClickListener {
    private static final String TAG = ContectProfile.class.getSimpleName();

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private RecyclerView bottumList_rv;
    LinearLayout pbHeaderProgress;
    public CustomAuthRequest request;
    List<ContactDetails> bottumList = new ArrayList<>();
    ContactDetails cd;
    ImageView company_image;
    public String photoFileName = "photo.jpg";
    File photoFile;
    public final String APP_TAG = "MyCustomAppContect";
    ProgressBar pr_imageLoder;
    Retrofit retrofit;
    String contectId, email, phone, L1, T, L2, city, state, country,zip,landMark, userId, name;
    public MenuItem searchMenu=null;

    public  long[] Filter_CRIds ;
    public boolean IsFiltered()
    {
        if(Filter_CRIds != null && Filter_CRIds.length > 0)
        {
            return  true;
        }
        return  false;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contect_profile);

        pr_imageLoder = findViewById(R.id.pr_imageLoder);
       // bottom_sheet = findViewById(R.id.bottom_sheet);
        company_image = findViewById(R.id.company_image);
        pbHeaderProgress = findViewById(R.id.pbHeaderProgress);
        pbHeaderProgress.setVisibility(View.GONE);
        ((ImageView) findViewById(R.id.img_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            userId = bundle.getString("D");
            String name = bundle.getString("N");
            getcontactDetail(userId);
        }
        company_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contectId  = cd.userId;
                selectImage();
            }
        });
        ((LinearLayout) findViewById(R.id.lin_companyList)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showBottomSheetDialog();
                ((NestedScrollView)findViewById(R.id.nested_content)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.txt_selectDirName)).setText("Company");
                Bundle bundle = new Bundle();
                bundle.putString("params", cd.userId);
                Fragment fragment = new CompanyFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
            }
        });
        ((FButton) findViewById(R.id.mail_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:"+email));
                startActivity(Intent.createChooser(emailIntent, "Send feedback"));
            }
        });
        ((FButton) findViewById(R.id.call_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ContectProfile.this,
                            new String[]{Manifest.permission.CALL_PHONE},
                            Config.MY_PERMISSIONS_REQUEST_CALL_PHONE);
                } else {
                    CallPhoneNumber();
                }
            }
        });
        ((FButton) findViewById(R.id.whatApp_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" +"+91"+phone;
                try {
                    PackageManager pm = getPackageManager();
                    pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setType("text/plain");
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } catch (PackageManager.NameNotFoundException e) {
                    Toast.makeText(ContectProfile.this, "Whatsapp app not installed in your phone", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        });
        ((FButton) findViewById(R.id.navigation_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address = L1+","+L2+","+landMark+","+zip+","+city+","+state+","+country;
                convertAddress(address);
                /*Intent searchAddress = new  Intent(Intent.ACTION_VIEW,Uri.parse("geo:0,0?q="+add));
                startActivity(searchAddress);*/
               // getLocationFromAddress(address);

            }
        });
        ((FontTextView) findViewById(R.id.img_company)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NestedScrollView)findViewById(R.id.nested_content)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.txt_selectDirName)).setText("Company");
                Bundle bundle = new Bundle();
                bundle.putString("params", cd.userId);
                Fragment fragment = new CompanyFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();
            }
        });
        ((FontTextView) findViewById(R.id.img_project)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NestedScrollView)findViewById(R.id.nested_content)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.txt_selectDirName)).setText("Project");
                Bundle bundle = new Bundle();
                bundle.putString("params", userId);
                Fragment fragment = new ProjectFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.project_fragment_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

            }
        });
        ((LinearLayout) findViewById(R.id.lin_contactInfo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) findViewById(R.id.lin_info_details)).setVisibility(View.VISIBLE);
                ((TextView) findViewById(R.id.txtconInfo)).setTextColor(Color.parseColor("#1c84c6"));
                ((TextView) findViewById(R.id.txtconNotes)).setTextColor(Color.parseColor("#999999"));
            }
        });
        ((LinearLayout) findViewById(R.id.lin_contact_notes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) findViewById(R.id.lin_info_details)).setVisibility(View.GONE);
                ((TextView) findViewById(R.id.txtconInfo)).setTextColor(Color.parseColor("#999999"));
                ((TextView) findViewById(R.id.txtconNotes)).setTextColor(Color.parseColor("#1c84c6"));
            }
        });

        ((FloatingActionButton) findViewById(R.id.fab_notes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteCustomDialog dialog = new NoteCustomDialog(ContectProfile.this,name);
                dialog.show();
            }
        });

        ((EditText) findViewById(R.id.edt_DOB)).addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DD/MM/YYYY";
            private android.icu.util.Calendar cal = android.icu.util.Calendar.getInstance();
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
                        cal.set(android.icu.util.Calendar.MONTH, mon-1);
                        year = (year<1900)?1900:(year>2100)?2100:year;
                        cal.set(android.icu.util.Calendar.YEAR, year);
                        // ^ first set year for the line below to work correctly
                        //with leap years - otherwise, date e.g. 29/02/2012
                        //would be automatically corrected to 28/02/2012

                        day = (day > cal.getActualMaximum(android.icu.util.Calendar.DATE))? cal.getActualMaximum(android.icu.util.Calendar.DATE):day;
                        clean = String.format("%02d%02d%02d",day, mon, year);
                    }

                    clean = String.format("%s/%s/%s", clean.substring(0, 2),
                            clean.substring(2, 4),
                            clean.substring(4, 8));

                    sel = sel < 0 ? 0 : sel;
                    current = clean;
                    ((EditText) findViewById(R.id.edt_DOB)).setText(current);
                    ((EditText) findViewById(R.id.edt_DOB)).setSelection(sel < current.length() ? sel : current.length());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

  /*  @Override
    public boolean onCreateOptionsMenu(Menu menu) {

            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.searchmenu, menu);
            searchMenu = menu.findItem(R.id.menu_search);
         *//*   MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.searchmenu, menu);

            getSupportActionBar().setTitle("Contacts");
            *//*
            return true; // false
        }

*/


    public void convertAddress(String getAddress) {
        if (getAddress != null && !getAddress.isEmpty()) {
            try {
                Geocoder coder = new Geocoder(this);
                List<Address> addressList = coder.getFromLocationName(getAddress, 1);
                if (addressList != null && addressList.size() > 0) {
                    double lat = addressList.get(0).getLatitude();
                    double lng = addressList.get(0).getLongitude();

                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                            Uri.parse("http://maps.google.com/maps?daddr="+lat+","+lng+""));
                    startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } // end catch
        } // end if
    }

    private void CallPhoneNumber() {
        DateTime NowTime = DataDateTime.Now().minusMinutes(10);
        if (NowTime.isBefore(CallPhoneTime)) {

            if (phone != null && !TextUtils.isEmpty(phone)) {
                DialNumber(phone);
            } else {
                //SnakebarCustom.danger(getApplicationContext(), _thisFragment.getView(), "No Valid Phone Number found to Call", 1000);
            }
        }
    }

    public DateTime CallPhoneTime;

    private void DialNumber(String phone) {
        DateTime NowTime = DataDateTime.Now().minusMinutes(10);
        if (NowTime.isBefore(CallPhoneTime)) {
            //CompanyRelationJ message = CallPhoneMessage;
            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                //SnakebarCustom.success(getApplication(), ContectProfile.this.getCurrentFocus(), "Calling" + "(" + phone + ")", 1000);
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + phone));
                startActivity(intent);
                //CallPhoneMessage = null;
            }
        }
    }

    private void selectImage() {
        AlertDailogBox dailogBox = new AlertDailogBox(ContectProfile.this,"Add Photo",this);
        dailogBox.show();
        /*final CharSequence[] items = { "Take Photo", "Choose from Gallary",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(ContectProfile.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(ContectProfile.this);
                if (items[item].equals("Take Photo")) {
                    //userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Gallary")) {
                     //userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();*/
    }

    public void cameraIntent() {

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        photoFileName= Calendar.getInstance().getTimeInMillis()+".jpg";
        photoFile = getPhotoFileUri(photoFileName);
        Uri fileProvider = GenericFileProvider.getUriForFile(ContectProfile.this, "com.easycloudbooks.easycloudbooks.util.GenericFileProvider", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        if (intent.resolveActivity(ContectProfile.this.getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

        }
    }

    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),111);
    }

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(ContectProfile.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        return file;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String  Token = App.getInstance().getAccessToken();
        long ParseACId = App.getInstance().getCurrentACId();
        final String parsedurl = App.getInstance().GetCustonDomain(ParseACId,METHOD_UPLOAD_COMPANY_PROFILE);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 111){
                String realPath= RealPathUtil.getRealPath(ContectProfile.this,data.getData());

                uploadImage(parsedurl,realPath,Token);
            }
            else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
                String realPath=photoFile.getAbsolutePath();
                uploadImage(parsedurl,realPath,Token);
            }
        }
    }

    private void uploadImage(final String parsedurl, final String realPath, final String token) {
        pr_imageLoder.setVisibility(View.VISIBLE);
        File N_file = new File(realPath);
        StringBuilder sb3 = new StringBuilder();
        // sb3.append(getPackageName());
        sb3.append(".provider");
        // image_path = String.valueOf(FileProvider.getUriForFile(this, sb3.toString(), new File(file_name)));
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(parsedurl+"/").client(client).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create())).build();
//        RequestBody open_time = RequestBody.create(MediaType.parse("text/plain"), "pass any data like id,name ");
        okhttp3.RequestBody requestBodyx = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), N_file);

        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", new File(realPath).getName(), requestBodyx);
        okhttp3.RequestBody companyimage =  okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "contactimage");
        okhttp3.RequestBody filepath =  okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "");
        okhttp3.RequestBody coid =  okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), String.valueOf(cd.userId));

        FileUploadService apiService = retrofit.create(FileUploadService.class);
        Call<ContectExample> memberCall = apiService.contectuploadImage(parsedurl,"Bearer "+token,fileToUpload, companyimage,filepath,coid);
        memberCall.enqueue(new Callback<ContectExample>() {

            @Override
            public void onResponse(Call<ContectExample> call, retrofit2.Response<ContectExample> response) {
                if(response.body() != null && response.body().toString().length() > 0){
                    if(response.body().getMessage().equalsIgnoreCase("OK")){
                        pr_imageLoder.setVisibility(View.GONE);
                        Glide.with(ContectProfile.this).load(realPath)
                                .thumbnail(0.5f)
                                .transition(withCrossFade())
                                .apply(RequestOptions.circleCropTransform())
                                .into(company_image);
                    }
                }
            }
            @Override
            public void onFailure(Call<ContectExample> call, Throwable t) {
                pr_imageLoder.setVisibility(View.VISIBLE);
                Log.e(">>>", "onFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void getcontactDetail(final String id){
        Log.e("TEST","Caontact Id :"+id);
        pbHeaderProgress.setVisibility(View.VISIBLE);
        request = new CustomAuthRequest(Request.Method.POST, METHOD_CONTACT_PROFILE, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //showMessage(getText(R.string.label_empty_list).toString());
                        Log.w(TAG, response.toString() );
                        pbHeaderProgress.setVisibility(View.VISIBLE);

                        String strResponse = response.toString();
                        if(strResponse != null){
                            try {
                                pbHeaderProgress.setVisibility(View.GONE);
                                cd = new ContactDetails();
                                JSONObject obj = new JSONObject(response.toString());
                                JSONObject sys  = obj.getJSONObject("obj");
                                name = sys.getString("FN")+" "+sys.getString("LN");
                                cd.FN = sys.getString("FN")+" "+sys.getString("LN");
                                cd.imageUrl = sys.getString("I");
                                String imageURL = sys.getString("I");
                                String uniqueId = sys.getString("UId");
                                cd.userId = sys.getString("Id");

                                ((TextView) findViewById(R.id.txt_uniqueId)).setText(uniqueId);
                                ((TextView) findViewById(R.id.txt_profile_name)).setText(name);

                                String firstLater = sys.getString("FN").substring(0,1).toUpperCase();
                                String secondLater = sys.getString("LN").substring(0,1).toUpperCase();
                                String combinstion = firstLater+secondLater;
                                if(imageURL == "null"){
                                    ((ImageView) findViewById(R.id.company_image)).setImageResource(R.drawable.bg_circle);
                                    ((ImageView) findViewById(R.id.company_image)).setColorFilter(null);
                                    ((TextView) findViewById(R.id.company_image_text)).setText(combinstion);
                                    ((TextView) findViewById(R.id.company_image_text)).setVisibility(View.VISIBLE);
                                }else{
                                   // Glide.with(context).load(DataText.GetImagePath(imageURL)).into(holder.imgCardImage);

                                    Glide.with(ContectProfile.this).load(DataText.GetImagePath(imageURL))
                                            .thumbnail(0.5f)
                                            .transition(withCrossFade())
                                            .apply(RequestOptions.circleCropTransform())
                                            .into(company_image);

                                    //Glide.with(ContectProfile.this).load(DataText.GetImagePath(imageURL)).into((ImageView) findViewById(R.id.company_image));
                                    ((TextView) findViewById(R.id.company_image_text)).setVisibility(View.GONE);
                                }
                              //  Glide.with(ContectProfile.this).load(DataText.GetImagePath(imageURL)).into((ImageView) findViewById(R.id.image));

                                JSONArray jsonArray = sys.getJSONArray("Es");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject emailobj = jsonArray.getJSONObject(i);
                                     email = emailobj.getString("E");
                                     if(email != null){
                                    ((LinearLayout) findViewById(R.id.lin_email)).setVisibility(View.VISIBLE);
                                    ((TextView) findViewById(R.id.txt_email)).setText(email);
                                     }
                                }

                                JSONArray phoneArray = sys.getJSONArray("PHs");
                                for (int k = 0; k < phoneArray.length(); k++) {
                                    JSONObject phoneobj = phoneArray.getJSONObject(k);
                                    phone = phoneobj.getString("Ph");
                                    Log.e("TEST","Contact Phone number :"+phone);
                                    if(phone != null) {
                                        ((LinearLayout) findViewById(R.id.lin_phone)).setVisibility(View.VISIBLE);
                                        ((LinearLayout) findViewById(R.id.lin_phone_whatapp)).setVisibility(View.VISIBLE);
                                        ((TextView) findViewById(R.id.txt_phone)).setText(phone);
                                        ((TextView) findViewById(R.id.txt_phone_whatsApp)).setText(phone);
                                    }
                                }
                                JSONArray addsArray = sys.getJSONArray("Adds");
                                for (int m = 0; m < addsArray.length(); m++) {
                                    JSONObject addsobj = addsArray.getJSONObject(m);
                                    String L1,T,L2,city,landMark,state,country,zip,aL1,aT,aL2,acity,alandMark,astate,acountry,azip;

                                    L1 = addsobj.getString("L1");
                                    T = addsobj.getString("T");
                                    L2 = addsobj.getString("L2");
                                    city = addsobj.getString("C");
                                    landMark = addsobj.getString("A");
                                    state = addsobj.getString("S");
                                    country = addsobj.getString("CR");
                                    zip = addsobj.getString("Zip");

                                   /* L1 = addsobj.getString("L1")+",";
                                    T = " "+addsobj.getString("T")+",";
                                    L2 = " "+addsobj.getString("L2")+",";
                                    city = " "+addsobj.getString("C")+",";
                                    landMark = " "+addsobj.getString("A")+",";
                                    state = " "+addsobj.getString("S")+",";
                                    country = " "+addsobj.getString("CR")+",";
                                    zip = " "+addsobj.getString("Zip")+",";*/

                                    if(!L1.equals("")){
                                        aL1 = addsobj.getString("L1")+",";
                                    }else {
                                        aL1 = "";
                                    }
                                    if(!T.equals("")){
                                        aT = " "+addsobj.getString("T")+",";
                                    }else{
                                        aT = "";
                                    }
                                    if(!L2.equals("")){
                                        aL2 = " " +L2+",";
                                    }else{
                                        aL2 = "";
                                    }
                                    if(!city.equals("")){
                                        acity = " "+addsobj.getString("C")+",";
                                    }else{
                                        acity = "";
                                    }
                                    if(!landMark.equals("")){
                                        alandMark = " "+addsobj.getString("A")+",";
                                    }else{
                                        alandMark = "";
                                    }
                                    if(!state.equals("")){
                                        astate = " "+addsobj.getString("S")+",";
                                    }else{
                                        astate = "";
                                    }
                                    if(!country.equals("")){
                                        acountry = " "+addsobj.getString("CR")+",";
                                    }else{
                                        acountry = "";
                                    }
                                    if(!zip.equals("")){
                                        azip = " "+addsobj.getString("Zip")+",";
                                    }else{
                                        azip = "";
                                    }

                                    String add =aL1+""+aL2+""+alandMark+""+acity+""+astate+""+acountry+""+azip;
                                    add = add.substring(0, add.length() - 1);

                                    if(!add.equals("")) {
                                        ((LinearLayout) findViewById(R.id.lin_address)).setVisibility(View.VISIBLE);
                                        aT = aT.substring(0, aT.length() - 1);

                                        ((TextView) findViewById(R.id.txt_addType)).setText(aT);
                                        ((TextView) findViewById(R.id.txt_address)).setText(add);
                                    }
                                   // ((TextView) findViewById(R.id.txt_postal)).setText(zip);
                                }
                                JSONArray campanyArr = sys.getJSONArray("CRUs");
                                for (int n = 0; n < campanyArr.length(); n++) {
                                    String count = String.valueOf(campanyArr.length());
                                    ((TextView) findViewById(R.id.txt_count)).setText(count);
                                    JSONObject companyobj = campanyArr.getJSONObject(n);
                                    //cd.userId = companyobj.getString("CRUId");
                                    JSONArray posArr = companyobj.getJSONArray("Ps");
                                    for (int i = 0; i < posArr.length(); i++) {
                                        JSONObject posObj = posArr.getJSONObject(i);
                                        String position = posObj.getString("P");
                                        cd.position = posObj.getString("P");
                                        cd.jdate = posObj.getString("CD");
                                        String posJdate = posObj.getString("CD");
                                        String dt = posJdate.replace("T", " ");
                                        SimpleDateFormat changeDate = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                                        try {
                                            Date dateFrom = changeDate.parse(dt);
                                            android.text.format.DateFormat df = new android.text.format.DateFormat();
                                            cd.jdate = df.format("MMM dd,yyyy HH:mm", dateFrom).toString();
                                            //((TextView) findViewById(R.id.txt_dob)).setText(df.format("dd MMM yyyy HH:mm", dateFrom).toString());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    JSONObject jobjcO = companyobj.getJSONObject("CO");
                                    cd.industryType = jobjcO.getString("Ind");
                                    cd.cId = jobjcO.getString("CRId");
                                    cd.cName = jobjcO.getString("Name");

                                    bottumList.add(cd);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.e("TEST","GET Exception :"+e);
                            }
                        }else {
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                        }


                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbHeaderProgress.setVisibility(View.VISIBLE);
                /*SnakebarCustom.danger(mContext, mView, "Unable to fetch Companies: " + error.getMessage(), 5000);
                swipeRefreshLayout.setRefreshing(false);*/
            }
        }) {

            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("ContactId", id);
                    JSONObject filterExpression = new JSONObject();
                    try {
                        filterExpression.put("Status","All");
                        if(Filter_CRIds != null)
                        {
                            JSONArray CRIds = new JSONArray();
                            for (Long CRId: Filter_CRIds) {
                                CRIds.put(CRId);
                            }
                            if(CRIds.length() > 0)
                            {
                                filterExpression.put("CRIds",CRIds);
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    params.put("filterExpression", filterExpression);
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


    @Override
    public void onMyDialogClick(boolean isCameraClick) {
        if (isCameraClick)
            cameraIntent();
        else
            galleryIntent();

    }
}
