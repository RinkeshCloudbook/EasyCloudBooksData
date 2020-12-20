package com.easycloudbooks.easycloudbooks.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.Preference;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import com.easycloudbooks.easycloudbooks.Retrofit.FileUploadService;
import com.easycloudbooks.easycloudbooks.Retrofit.UserFileUpload;
import com.easycloudbooks.easycloudbooks.activity.ContactFilterActivity;
import com.easycloudbooks.easycloudbooks.activity.ContectProfile;
import com.easycloudbooks.easycloudbooks.activity.ProjectFilterActivity;
import com.easycloudbooks.easycloudbooks.adapter.Company.CompanyServiceListAdapter;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.AlertDailogBox;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.DataText;
import com.easycloudbooks.easycloudbooks.common.NoteCustomDialog;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.constants.Constants;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.model.CompanyRelationJ;
import com.easycloudbooks.easycloudbooks.model.CompanyRelationSubServiceJ;
import com.easycloudbooks.easycloudbooks.model.ContactDetails;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.util.GenericFileProvider;
import com.easycloudbooks.easycloudbooks.util.Helper;
import com.easycloudbooks.easycloudbooks.util.MyDialogClickListener;
import com.easycloudbooks.easycloudbooks.util.RealPathUtil;
import com.easycloudbooks.easycloudbooks.util.Tools;
import com.easycloudbooks.easycloudbooks.util.Utility;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.GsonBuilder;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
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
public class CompanyProfileFragment extends BaseExampleFragment implements Constants,
        CompanyServiceListAdapter.AdapterListener, MyDialogClickListener {

    private static String TAG = CompanyProfileFragment.class.getSimpleName();
    private Preference logoutPreference, aboutPreference, itemTerms, itemPrivacy;
    private final String[] REQUIRED_PERMISSIONS = new String[]{"android.permission.CAMERA", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private int REQUEST_CODE_PERMISSIONS = 101;
    private ProgressDialog pDialog;
    private Activity CurrentActivity;
    private CompanyProfileFragment _this;
    private CompanyServiceListAdapter mSearchResultsAdapter;
    private Context mContext;
    public View mView;
    Retrofit retrofit;
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    public String photoFileName = "photo.jpg";
    File photoFile;
    private int CAMREA_CODE = 1;

    LinearLayout aboutDialogContent;
    TextView aboutDialogAppName, aboutDialogAppVersion, aboutDialogAppCopyright;

    private Boolean loading = false;
    private  CompanyRelationJ CR;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        initpDialog();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_companyprofile, container, false);
        CurrentActivity = this.getActivity();
        mView = rootView;

        mContext = CurrentActivity.getApplicationContext();
        Bundle intentBundle =   CurrentActivity.getIntent().getExtras();
        if(intentBundle != null)
        {
            CR =  (CompanyRelationJ)intentBundle.getSerializable("CR");
        }
              return rootView;
    }
    TextView company_name,company_image_text,company_sub_detail,company_service_count,company_project_count
            ,mMessage,contact_count,txt_companyAddress,txt_addType,txt_serviceInfo;
    ImageView company_image;
    FontTextView img_project,img_contact;
    ImageButton img_camera;
    RecyclerView mSearchResultsList;
    ArrayList<CompanyRelationSubServiceJ> list;
    LinearLayout company_project_container,company_contact_calling;
    LinearLayoutManager mlinearLayoutManager;
    LinearLayout pbHeaderProgress,lin_company_service,lin_company_info,lin_serviceInfo,lin_comNote;
    ProgressBar pr_imageLoder;
    String cName,contactCount;
    NestedScrollView nested_content;
    CardView cv_info;
    String address;


    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        _this = this;
        Context mContext = CurrentActivity;
        super.onViewCreated(view, savedInstanceState);
        //mSearchResultsList = (RecyclerView) view.findViewById(R.id.recycler_view);

        if(allPermissionsGranted()){
            //cameraIntent(); //start camera if permission has been granted by user
        } else{
            ActivityCompat.requestPermissions(getActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

       /* public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        if(userChoosenTask.equals("Take Photo"))
                            cameraIntent();
                        else if(userChoosenTask.equals("Choose from Library"))
                            galleryIntent();
                    } else {
                        //code for deny
                    }
                    break;
            }
        }*/

        company_name = (TextView)view.findViewById(R.id.company_name);
        company_image_text = (TextView)view.findViewById(R.id.company_image_text);
        company_image = (ImageView)view.findViewById(R.id.company_image);
        company_sub_detail = (TextView)view.findViewById(R.id.company_sub_detail);
        company_service_count = (TextView)view.findViewById(R.id.company_service_count);
        company_project_count = (TextView)view.findViewById(R.id.company_project_count);
        mMessage = (TextView) view.findViewById(R.id.no_service_message);
        pr_imageLoder = view.findViewById(R.id.pr_imageLoder);
        company_project_container = (LinearLayout) view.findViewById(R.id.company_project_container);
        img_project = view.findViewById(R.id.img_project);
        company_contact_calling = (LinearLayout) view.findViewById(R.id.company_contact_calling);
        img_contact = view.findViewById(R.id.img_contact);
        pbHeaderProgress = view.findViewById(R.id.pbHeaderProgress);
        contact_count = view.findViewById(R.id.contact_count);
        img_camera = view.findViewById(R.id.img_camera);
        nested_content = view.findViewById(R.id.nested_content);
        lin_company_service = view.findViewById(R.id.lin_company_service);
        lin_company_info = view.findViewById(R.id.lin_company_info);
        txt_companyAddress = view.findViewById(R.id.txt_companyAddress);
        txt_addType = view.findViewById(R.id.txt_addType);
        cv_info = view.findViewById(R.id.cv_info);
        txt_serviceInfo = view.findViewById(R.id.txt_serviceInfo);
        lin_serviceInfo = view.findViewById(R.id.lin_serviceInfo);
        lin_comNote = view.findViewById(R.id.lin_comNote);


        company_name.setText(CR.Name);
        company_sub_detail.setText(CR.LS + " / " + CR.Ind);
        company_service_count.setText(String.valueOf(CR.SC));
        if (!TextUtils.isEmpty(CR.I)) {
            Glide.with(mContext).load(DataText.GetImagePath(CR.I))
                    .thumbnail(0.5f)
                    .transition(withCrossFade())
                    .apply( new RequestOptions().transform(new RoundedCorners(20)).diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(company_image);
              company_image.setColorFilter(null);
              company_image_text.setVisibility(View.GONE);
        } else {
            company_image.setImageResource(R.drawable.bg_square);
            company_image.setColorFilter(CR.getColor());
            company_image_text.setVisibility(View.VISIBLE);
            company_image_text.setText(Helper.getShortName(CR.Name));
        }

        lin_company_service.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) view.findViewById(R.id.lin_infoDelails)).setVisibility(View.GONE);
                ((CardView) view.findViewById(R.id.cv_service)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.txt_cmpinfo)).setTextColor(Color.parseColor("#999999"));
                ((TextView) view.findViewById(R.id.txt_cmpNotes)).setTextColor(Color.parseColor("#999999"));
                ((TextView) view.findViewById(R.id.txt_cmpService)).setTextColor(Color.parseColor("#1c84c6"));
            }
        });
        lin_company_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CardView) view.findViewById(R.id.cv_service)).setVisibility(View.GONE);
                ((LinearLayout) view.findViewById(R.id.lin_infoDelails)).setVisibility(View.VISIBLE);
                ((TextView) view.findViewById(R.id.txt_cmpinfo)).setTextColor(Color.parseColor("#1c84c6"));
                ((TextView) view.findViewById(R.id.txt_cmpNotes)).setTextColor(Color.parseColor("#999999"));
                ((TextView) view.findViewById(R.id.txt_cmpService)).setTextColor(Color.parseColor("#999999"));

            }
        });
        lin_comNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CardView) view.findViewById(R.id.cv_service)).setVisibility(View.GONE);
                ((LinearLayout) view.findViewById(R.id.lin_infoDelails)).setVisibility(View.GONE);
                ((TextView) view.findViewById(R.id.txt_cmpinfo)).setTextColor(Color.parseColor("#999999"));
                ((TextView) view.findViewById(R.id.txt_cmpNotes)).setTextColor(Color.parseColor("#1c84c6"));
                ((TextView) view.findViewById(R.id.txt_cmpService)).setTextColor(Color.parseColor("#999999"));

            }
        });
        company_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        img_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        ((FloatingActionButton) view.findViewById(R.id.fab_notes)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoteCustomDialog dialog = new NoteCustomDialog(getActivity(),CR.Name);
                dialog.show();
            }
        });

        mlinearLayoutManager = new LinearLayoutManager(CurrentActivity);
        mSearchResultsList = (RecyclerView) CurrentActivity.findViewById(R.id.recycler_view_services);

        RecyclerView.LayoutManager recyce = new GridLayoutManager(getActivity(),2);
        mSearchResultsList.setLayoutManager(recyce);

        list = new ArrayList<CompanyRelationSubServiceJ>();
        mSearchResultsAdapter = new CompanyServiceListAdapter(mContext, list, this);
        mSearchResultsList.setAdapter(mSearchResultsAdapter);
        mSearchResultsList.setLayoutManager(recyce);
        mSearchResultsList.addItemDecoration(new SpacingItemDecoration(2, Tools.dpToPx(this, 8), true));
        mSearchResultsList.setHasFixedSize(true);
        mSearchResultsList.setNestedScrollingEnabled(false);
        //mSearchResultsList.setHasFixedSize(false);
        final String cRid = String.valueOf(CR.CRId);

        company_project_container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(CurrentActivity, ProjectFilterActivity.class);
                i.putExtra("CRIds",new long[]{ CR.CRId});
                i.putExtra("cId",cRid);
                CurrentActivity.startActivity(i);
            }
        });
        img_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(CurrentActivity, ProjectFilterActivity.class);
                i.putExtra("CRIds",new long[]{ CR.CRId});
                i.putExtra("cId",cRid);
                CurrentActivity.startActivity(i);
            }
        });
        getCompanyContact(String.valueOf(CR.CRId));
        company_contact_calling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent i = new Intent(CurrentActivity, ContactFilterActivity.class);
                String cId = String.valueOf(CR.CRId);
                nested_content.setVisibility(View.GONE);
               // ((TextView) view.findViewById(R.id.txt_selectDirName)).setText("ContactCCCCompany");
                Bundle bundle = new Bundle();
                bundle.putString("contactId", cId);
                Fragment fragment = new ContactFragment();
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_contact_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

               /* Log.e("TEST","Contact User Id :"+cId);
                i.putExtra("CRIds", cId);
                i.putExtra("cName", cName);
                CurrentActivity.startActivity(i);*/
            }
        });
        ((FButton) view.findViewById(R.id.navigation_button)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                convertAddress(address);
            }
        });
        img_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Intent i = new Intent(CurrentActivity, ContactFilterActivity.class);
                Log.e("TEST","CRID :"+CR.CRId);
                String cId = String.valueOf(CR.CRId);
                nested_content.setVisibility(View.GONE);
                // ((TextView) view.findViewById(R.id.txt_selectDirName)).setText("ContactCCCCompany");
                Bundle bundle = new Bundle();
                bundle.putString("contactId", cId);
                Fragment fragment = new ContactFragment();
                fragment.setArguments(bundle);
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_contact_frame, fragment, fragment.getClass().getSimpleName()).addToBackStack(null).commit();

               /* Log.e("TEST","Contact User Id :"+cId);
                i.putExtra("CRIds", cId);
                i.putExtra("cName", cName);
                CurrentActivity.startActivity(i);*/
            }
        });

        getCompanyDetail(true,0,"");
    }

    private void convertAddress(String getAddress) {
        if (getAddress != null && !getAddress.isEmpty()) {
            try {
                Geocoder coder = new Geocoder(getActivity());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        String  Token = App.getInstance().getAccessToken();
        long ParseACId = App.getInstance().getCurrentACId();
        final String parsedurl = App.getInstance().GetCustonDomain(ParseACId,METHOD_UPLOAD_COMPANY_PROFILE);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 111){
                String realPath= RealPathUtil.getRealPath(getActivity(),data.getData());
                uploadImage(parsedurl,realPath,Token);
            }
            else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){
                    String realPath=photoFile.getAbsolutePath();
                    uploadImage(parsedurl,realPath,Token);
            }
        }
    }

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(APP_TAG, "failed to create directory");
        }
        File file = new File(mediaStorageDir.getPath() + File.separator + photoFileName);
        return file;
    }

    private void selectImage() {
        AlertDailogBox dailogBox = new AlertDailogBox(getActivity(),"Add Photo",this);
        dailogBox.show();
        /*final CharSequence[] items = { "Take Photo", "Choose from Gallary",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(getActivity());
                if (items[item].equals("Take Photo")) {
                    //userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Gallary")) {
                   // userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();*/
    }

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),111);
    }

    private void cameraIntent() {

        if (permissionAlreadyGranted()) {
            //Toast.makeText(getContext(), "Permission is already granted!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFileName= Calendar.getInstance().getTimeInMillis()+".jpg";
            photoFile = getPhotoFileUri(photoFileName);
            Uri fileProvider = GenericFileProvider.getUriForFile(getActivity(), "com.easycloudbooks.easycloudbooks.util.GenericFileProvider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            }
            return;
        }
        requestPermission();


    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMREA_CODE);
    }

    private boolean permissionAlreadyGranted() {
        int result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA);
        if (result == PackageManager.PERMISSION_GRANTED)
        {
            /* result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED)
            { result = ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (result == PackageManager.PERMISSION_GRANTED)*/
            return true;
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMREA_CODE) {

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(getContext(), "Permission granted successfully", Toast.LENGTH_SHORT).show();
            } else {
                //Toast.makeText(getContext(), "Permission is denied!", Toast.LENGTH_SHORT).show();
                boolean showRationale = shouldShowRequestPermissionRationale( Manifest.permission.CAMERA );
                if (! showRationale) {
                    openSettingsDialog();
                }
            }
        }
    }

    private void openSettingsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Required Permissions");
        builder.setMessage("This app require permission to use awesome feature. Grant them in app settings.");
        builder.setPositiveButton("Take Me To SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", "com.easycloudbooks.ecb_client", null);
                intent.setData(uri);
                startActivityForResult(intent, 101);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

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
        okhttp3.RequestBody companyimage =  okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "companyimage");
        okhttp3.RequestBody filepath =  okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), "");
        okhttp3.RequestBody crid =  okhttp3.RequestBody.create(okhttp3.MediaType.parse("text/plain"), String.valueOf(CR.CRId));
//        MultipartBody.Part UploadedFolder = MultipartBody.Part.createFormData("mode", "companyimage");
//        MultipartBody.Part UploadedFolder = MultipartBody.Part.createFormData("filepath", "");
//        MultipartBody.Part UploadedFolder = MultipartBody.Part.createFormData("crid", "92343");
        FileUploadService apiService = retrofit.create(FileUploadService.class);
        Call<UserFileUpload> memberCall = apiService.uploadImage(parsedurl,"Bearer "+token,fileToUpload, companyimage,filepath,crid);
        memberCall.enqueue(new Callback<UserFileUpload>() {

            @Override
            public void onResponse(Call<UserFileUpload> call, retrofit2.Response<UserFileUpload> response) {
                if(response.body() != null && response.body().toString().length() > 0){
                    if(response.body().getMessage().equalsIgnoreCase("OK")){
                        pr_imageLoder.setVisibility(View.GONE);

                        Glide.with(mContext).load(realPath)
                                .thumbnail(0.5f)
                                .transition(withCrossFade())
                                .apply( new RequestOptions().transform(new RoundedCorners(20)).diskCacheStrategy(DiskCacheStrategy.ALL))
                                .into(company_image);

                    }
                }
            }

            @Override
            public void onFailure(Call<UserFileUpload> call, Throwable t) {
                pr_imageLoder.setVisibility(View.VISIBLE);
                Log.e(">>>", "onFailure: " + t.getMessage());
            }
        });

    }


    public boolean IsLoading = false;
    public  CustomAuthRequest request;
    private List<Long> ExistingServices = new ArrayList<Long>();
    private void getCompanyDetail(final boolean isAppend, final int offset, final String CurrentSearctText) {
        Log.e("TEST","Call Again "+CR.CRId);
        ExistingServices.clear();
        IsLoading = true;
        request = new CustomAuthRequest(Request.Method.POST, METHOD_COMPANY_GETDETAIL, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // showMessage(getText(R.string.label_empty_list).toString());
                        Log.w(TAG, response.toString() );

                        if (App.getInstance().authorizeSimple(response)) {
                            try {

                                JSONObject obj = new JSONObject(response.toString());
                                JSONObject getObj = obj.getJSONObject("obj");
                                String sName = null;
                                JSONArray serArray = getObj.getJSONArray("Services");
                                for (int i = 0; i < serArray.length(); i++) {
                                    JSONObject serObj = serArray.getJSONObject(i);

                                    TextView rowTextView = new TextView(getActivity());
                                    rowTextView.setPadding(16, 5, 16, 10);
                                    rowTextView.setText(" "+serObj.getString("N"));
                                    lin_serviceInfo.addView(rowTextView);

                                }
                               // txt_serviceInfo.setText(sName);

                                String sc = getObj.getString("SC");
                                Log.e("TEST","Company Profile Service Count :"+sc);

                                CompanyRelationJ CRN =CompanyRelationJ.getJSON(response.getString("obj"));
                                List<CompanyRelationSubServiceJ> CRList = new ArrayList<>();
                                if(CRN != null)
                                {
                                    CR = CRN;
                                }
                                CRList = CR.Services;
                                company_project_count.setText(String.valueOf(CR.PP));
                                if (!isAppend)
                                    mSearchResultsAdapter.ClearData();
                                if (CRList != null) {
                                    for (CompanyRelationSubServiceJ message : CRList) {
                                        message.setColor(getRandomMaterialColor("400"));
                                        message.isExpanded = false;
                                        if (ExistingServices.indexOf(message.Id) == -1) {
                                            ExistingServices.add(message.Id);
                                            mSearchResultsAdapter.Add(message, false);
                                        }
                                    }
                                }
                                if (mSearchResultsAdapter.getItemCount() != 0) {
                                    hideMessage();
                                }
                                mSearchResultsAdapter.notifyDataSetChanged();
                            } catch (JSONException ex) {
                                DataGlobal.SaveLog(TAG, ex);
                                SnakebarCustom.danger(mContext, mView, "Unable to fetch Services", 5000);
                            }
                            IsLoading = false;
                        } else {
                            IsLoading = false;
                            try {
                                if (response.getString("Message") != null)
                                    SnakebarCustom.danger(mContext, mView, response.getString("Message"), 5000);
                            } catch (JSONException ex) {
                            }
                        }
                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                SnakebarCustom.danger(mContext, mView, "Unable to fetch Companies: " + error.getMessage(), 5000);
                IsLoading = false;
            }
        }) {

            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("CRId", CR.CRId);
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

    private void getCompanyContact(String crId) {
        Log.e("TEST","Get Company Profile Detalis :"+CR.CRId);
        pbHeaderProgress.setVisibility(View.VISIBLE);
        IsLoading = true;
        request = new CustomAuthRequest(Request.Method.POST, METHOD_COMPANY_CONTACT, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                       // showMessage(getText(R.string.label_empty_list).toString());
                        Log.w(TAG, response.toString());

                        if(response != null){
                            pbHeaderProgress.setVisibility(View.GONE);
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                JSONObject object = obj.getJSONObject("obj");
                                ContactDetails cd = new ContactDetails();
                                cd.cName = object.getString("Name");
                                cName = cd.cName;
                                JSONArray addsArray = object.getJSONArray("Adds");
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

                                    Log.e("TEST","L1 :"+L1);
                                    Log.e("TEST","T :"+T);
                                    Log.e("TEST","L2 :"+L2);
                                    Log.e("TEST","city :"+city);
                                    Log.e("TEST","landMark :"+landMark);
                                    Log.e("TEST","state :"+state);
                                    Log.e("TEST","country :"+country);
                                    Log.e("TEST","zip :"+zip);
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

                                    address = L1+","+" "+L2+","+" "+landMark+","+" "+city+","+" "+state+","+" "+country+","+" "+zip;
                                    if(!address.equals("")) {
  //                                      Log.e("TEST","get Comoany ProfileAddress :"+address);
                                        cv_info.setVisibility(View.VISIBLE);
                                        aT = aT.substring(0, aT.length() - 1);
                                        txt_addType.setText(aT);
                                        txt_companyAddress.setText(add);
                                    }
                                    // ((TextView) findViewById(R.id.txt_postal)).setText(zip);
                                }

                                JSONArray jArr = object.getJSONArray("CRUs");
                                String c = String.valueOf(jArr.length());
                                contact_count.setText(c);


                                /*JSONArray jArr = object.getJSONArray("CRUs");
                                for (int i = 0; i < jArr.length(); i++) {
                                    JSONObject cruObj = jArr.getJSONObject(i);
                                    cd.userId = cruObj.getString("CRUId");

                                    JSONArray psArr = cruObj.getJSONArray("Ps");
                                    for (int j = 0; j < psArr.length(); j++) {
                                        JSONObject psObj = psArr.getJSONObject(j);
                                        cd.position = psObj.getString("P");
                                        cd.jdate = psObj.getString("CD");
                                    }
                                    JSONObject userObj = cruObj.getJSONObject("User");
                                    cd.FN = userObj.getString("FN");
                                    cd.MN = userObj.getString("MN");
                                    cd.LN = userObj.getString("LN");
                                    Log.e("TEST","FN :"+cd.FN);


                                }*/


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else {
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                        }



                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                SnakebarCustom.danger(mContext, mView, "Unable to fetch Companies: " + error.getMessage(), 5000);
                IsLoading = false;
            }
        }) {

            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("Id", CR.CRId);
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
    public void onMessageRowClicked(int position, CompanyServiceListAdapter.ViewHolder holder) {
        // verify whether action mode is enabled or not
        // if enabled, change the row state to activated

            // read the message which removes bold from the row
            CompanyRelationSubServiceJ message = mSearchResultsAdapter.getItem(position);
            message.setRead(true);
            mSearchResultsAdapter.set(position, message);
            mSearchResultsAdapter.notifyDataSetChanged();


    }

    public void showMessage(String message) {

        mMessage.setText(message);
        mMessage.setVisibility(View.VISIBLE);
    }

    public void hideMessage() {

        mMessage.setVisibility(View.GONE);
    }

    private boolean supportsViewElevation() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        if (savedInstanceState != null) {

            loading = savedInstanceState.getBoolean("loading");

        } else {

            loading = false;
        }

        if (loading) {

            showpDialog();
        }
    }

    public void onDestroyView() {
        if(request != null) {
            request.destroy();
        }
        request = null;
        if (mlinearLayoutManager != null) {
            mlinearLayoutManager.removeAllViews();
            mlinearLayoutManager = null;
        }

        if (mSearchResultsList != null) {
            mSearchResultsList.setItemAnimator(null);
            mSearchResultsList.setAdapter(null);
            mSearchResultsList = null;
        }

        mlinearLayoutManager = null;
        super.onDestroyView();
        Log.w(TAG, "onDestroyView: " );
        hidepDialog();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);

        outState.putBoolean("loading", loading);
    }

    private int getRandomMaterialColor(String typeColor) {
        int returnColor = Color.GRAY;
        int arrayId = getResources().getIdentifier("mdcolor_" + typeColor, "array", CurrentActivity.getPackageName());

        if (arrayId != 0) {
            TypedArray colors = getResources().obtainTypedArray(arrayId);
            int index = (int) (Math.random() * colors.length());
            returnColor = colors.getColor(index, Color.GRAY);
            colors.recycle();
        }
        return returnColor;
    }

    protected void initpDialog() {

        pDialog = new ProgressDialog(getActivity());
        pDialog.setMessage(getString(R.string.msg_loading));
        pDialog.setCancelable(false);
    }

    protected void showpDialog() {

        if (!pDialog.isShowing())
            pDialog.show();
    }

    protected void hidepDialog() {

        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    @Override
    public boolean onActivityBackPress() {
        return true;
    }

    private boolean allPermissionsGranted() {
        for(String permission : REQUIRED_PERMISSIONS){
            if(ContextCompat.checkSelfPermission(getActivity(), permission) != PackageManager.PERMISSION_GRANTED){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onMyDialogClick(boolean isCameraClick) {
        if (isCameraClick)
            cameraIntent();
        else
            galleryIntent();

    }

    private class SpacingItemDecoration extends RecyclerView.ItemDecoration {
        private int spanCount;
        private int spacingPx;
        private boolean includeEdge;
        public SpacingItemDecoration(int spanCount, int spacingPx, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacingPx = spacingPx;
            this.includeEdge = includeEdge;
        }
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacingPx - column * spacingPx / spanCount;
                outRect.right = (column + 1) * spacingPx / spanCount;

                if (position < spanCount) { // top edge
                    outRect.top = spacingPx;
                }
                outRect.bottom = spacingPx; // item bottom
            } else {
                outRect.left = column * spacingPx / spanCount;
                outRect.right = spacingPx - (column + 1) * spacingPx / spanCount;
                if (position >= spanCount) {
                    outRect.top = spacingPx; // item top
                }
            }
        }
    }
}