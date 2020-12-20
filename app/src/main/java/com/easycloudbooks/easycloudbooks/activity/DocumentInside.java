package com.easycloudbooks.easycloudbooks.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.Retrofit.ClientUploadExample;
import com.easycloudbooks.easycloudbooks.Retrofit.FileUploadService;
import com.easycloudbooks.easycloudbooks.adapter.DocumentAdapterr;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.fragment.DocumentUpload;
import com.easycloudbooks.easycloudbooks.model.Document_upload_model;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.util.GenericFileProvider;
import com.easycloudbooks.easycloudbooks.util.RealPathUtil;
import com.easycloudbooks.easycloudbooks.util.Utility;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_DOCUMENT_GETDETAIL;

public class DocumentInside extends AppCompatActivity {

    ImageView img_back,img_search,img_find;
    CustomAuthRequest request;
    public long[] Filter_CRIds;
    //ProgressBar pbHeaderProgress;
    LinearLayout pbHeaderProgress;
    String trgt,cwd,cId,createHash,getURLName,getURL;
    DocumentAdapterr adapterr;
    FloatingActionButton add_dir;
    List<Document_upload_model> docList = new ArrayList<>();
    List<Document_upload_model> tempDocList = new ArrayList<>();
    List<String> mResponseStack = new ArrayList<>();
    SwipeRefreshLayout swipeRefreshLayout;
    TextView txt_selectDirName;
    RecyclerView recycler_view;
    String name = null;
    LinearLayout lyt_delete,lin_nodata_found;
    private int CAMREA_CODE = 1;

    public String photoFileName = "photo.jpg";
    File photoFile;
    Retrofit retrofit;
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;

    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    View view;

    private ProgressDialog mProgressDialog;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;

    File rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private enum StackState {
        POP_IN,
        POP_OUT
    }

    private static final String TAG = DocumentUpload.class.getSimpleName();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_inside);

        txt_selectDirName = findViewById(R.id.txt_selectDirName);
        recycler_view = findViewById(R.id.recycler_view);
        add_dir = findViewById(R.id.add_dir);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        pbHeaderProgress  = findViewById(R.id.pbHeaderProgress );
        img_back = findViewById(R.id.img_back);
        view = findViewById(android.R.id.content);
        img_search = findViewById(R.id.img_search);
        lyt_delete = findViewById(R.id.lyt_delete);
        img_find = findViewById(R.id.img_find);
        lin_nodata_found = findViewById(R.id.lin_nodata_found);


        RecyclerView.LayoutManager recyce = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        recycler_view.setLayoutManager(recyce);
        /*RecyclerView.LayoutManager recy = new GridLayoutManager(getApplicationContext(),2);
        recycler_view.setLayoutManager(recy);*/

        Bundle intent = getIntent().getExtras();
        if(intent != null){
            cId = intent.getString("cId");
            trgt = intent.getString("target");
            name = intent.getString("name");
            txt_selectDirName.setText(name);

            getDirInside(cId, trgt,name);
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        checkConnection();
                        refreshData(trgt,cId,"");
                    }
                }, 2000);
            }
        });
        checkConnection();

        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack();
            }
        });
        add_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DialogBox(trgt);
                selectImage(trgt);
            }
        });

        pbHeaderProgress.setVisibility(View.VISIBLE);

        createList();
        img_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(getApplicationContext(),SearchOpration.class);
                searchIntent.putExtra("H",trgt);
                searchIntent.putExtra("CId",cId);
                startActivity(searchIntent);
            }
        });
    }

    private void selectImage(final String trgt) {
        final CharSequence[] items = { "Take Photo", "Choose from Gallary","Create Folder",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(DocumentInside.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(DocumentInside.this);
                if (items[item].equals("Take Photo")) {
                    //userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Gallary")) {
                    //userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                }else if (items[item].equals("Create Folder")) {
                    //userChoosenTask="Choose from Library";
                    if(result)
                        DialogBox(cId,trgt);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        if (permissionAlreadyGranted()) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            photoFileName= Calendar.getInstance().getTimeInMillis()+".jpg";
            photoFile = getPhotoFileUri(photoFileName);
            Uri fileProvider = GenericFileProvider.getUriForFile(DocumentInside.this, "com.easycloudbooks.easycloudbooks.util.GenericFileProvider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            if (intent.resolveActivity(DocumentInside.this.getPackageManager()) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

            }
            return;
        }
        requestPermission();
    }

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA);
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
    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMREA_CODE);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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

    private void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),111);
    }

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(DocumentInside.this.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

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

        final String parsedurl = App.getInstance().GetCustonDomain(ParseACId,METHOD_DOCUMENT_GETDETAIL);
        ArrayList<String> imagesEncodedList = new ArrayList<String>();
        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == 111){
                if (data.getData() != null){
                    String realPath= RealPathUtil.getRealPath(DocumentInside.this,data.getData());
                    String filename = realPath.substring(realPath.lastIndexOf("/")+1);
                    imagesEncodedList.add(realPath);
                    uploadImage(parsedurl,imagesEncodedList,Token,cId,trgt,filename);
                }else {

                    if (data.getClipData() != null) {
                        ClipData mClipData = data.getClipData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};
                        ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                        StringBuilder sbString = new StringBuilder("");
                        for (int i = 0; i < mClipData.getItemCount(); i++) {
                            ClipData.Item item = mClipData.getItemAt(i);
                            Uri uri = item.getUri();
                            mArrayUri.add(uri);
                            String realPath = RealPathUtil.getRealPath(DocumentInside.this, uri);

                            sbString.append(realPath.substring(realPath.lastIndexOf("/") + 1)).append(",");
                            // Get the cursor
                            Cursor cursor = getContentResolver().query(uri, filePathColumn, null, null, null);
                            // Move to first row
                            cursor.moveToFirst();

                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                            String imageEncoded = cursor.getString(columnIndex);
                            imagesEncodedList.add(realPath);
                            cursor.close();

                        }
                        String listOfFile = sbString.toString();
                        if (listOfFile.length() > 0) {
                            String filename = listOfFile.substring(0, listOfFile.length() - 1);
                            uploadImage(parsedurl,imagesEncodedList,Token,cId,trgt,filename);

                        }
                        Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                        //uploadImage(parsedurl,realPath,Token,SpinnercId,cwdHash,filename);
                    }
                }
            }
            else if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE){

                String realPath=photoFile.getAbsolutePath();
                String filename = realPath.substring(realPath.lastIndexOf("/")+1);
                imagesEncodedList.add(realPath);
                uploadImage(parsedurl,imagesEncodedList,Token,cId,trgt,filename);
            }

        }

    }

    private void uploadImage(final String parsedurl, final ArrayList<String> realPath, final String token, final String crID, String gethash, String filename) {
        pbHeaderProgress.setVisibility(View.VISIBLE);

        //pr_imageLoder.setVisibility(View.VISIBLE);
//        File N_file = new File(realPath);
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
//        okhttp3.RequestBody requestBodyx = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/*"), N_file);
        MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[realPath.size()];
        for (int index = 0; index <  realPath.size(); index++) {
            okhttp3.RequestBody surveyBody =  okhttp3.RequestBody.create(MediaType.parse("image/*"),
                    new File(realPath.get(index)));
            surveyImagesParts[index] = MultipartBody.Part.createFormData("file",
                    new File(realPath.get(index)).getName(),
                    surveyBody);
        }
//        String fileName=","+filename+"" 1.jpg,2.jpg,3.p
//        MultipartBody.Part fileToUpload = MultipartBody.Part.createFormData("file", new File(realPath).getName(), requestBodyx);
        okhttp3.RequestBody clientimage =  okhttp3.RequestBody.create(MediaType.parse("text/plain"), filename);
        okhttp3.RequestBody clientHash =  okhttp3.RequestBody.create(MediaType.parse("text/plain"), gethash);
        okhttp3.RequestBody crid =  okhttp3.RequestBody.create(MediaType.parse("text/plain"), String.valueOf(crID));
        okhttp3.RequestBody comand =  okhttp3.RequestBody.create(MediaType.parse("text/plain"), "upload");

        FileUploadService apiService = retrofit.create(FileUploadService.class);
        Call<ClientUploadExample> memberCall = apiService.uploadImage(parsedurl,"Bearer "+token,surveyImagesParts, clientHash, crid,clientimage,comand);
        memberCall.enqueue(new Callback<ClientUploadExample>() {

            @Override
            public void onResponse(Call<ClientUploadExample> call, retrofit2.Response<ClientUploadExample> response) {
                if(response.body() != null && response.body().toString().length() > 0){
                    pbHeaderProgress.setVisibility(View.GONE);
                    refreshData(cId,trgt,"");

                }else {

                    pbHeaderProgress.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<ClientUploadExample> call, Throwable t) {
                //pr_imageLoder.setVisibility(View.VISIBLE);
                pbHeaderProgress.setVisibility(View.VISIBLE);
                Log.e(">>>", "onFailure: " + t.getMessage());
            }
        });

    }

    public void deleteVisible() {
        lyt_delete.setVisibility(View.VISIBLE);
        if(adapterr.getSelected().size() == 0){
            lyt_delete.setVisibility(View.GONE);
            getDirInside(cId, trgt,name);
        }
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapterr.getSelected().size() > 0){
                    JSONArray jsonArray=new JSONArray();
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int i = 0; i < adapterr.getSelected().size(); i++) {
                        stringBuilder.append(adapterr.getSelected().get(i).getHash());
                        jsonArray.put(adapterr.getSelected().get(i).getHash());
                        pbHeaderProgress.setVisibility(View.VISIBLE);
                        // getDirInside(cId, trgt,name);
                        adapterr.notifyDataSetChanged();
                    }
                    multiDeleteDir(cId,jsonArray);
                    // showToast(stringBuilder.toString().trim());
                }else {
                    showToast("No Selection");
                }
            }

        });
        //img_search.setVisibility(View.VISIBLE);
    }

    private void multiDeleteDir(final String cId, final JSONArray deleteHash) {
        pbHeaderProgress.setVisibility(View.VISIBLE);
        request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pbHeaderProgress.setVisibility(View.VISIBLE);
                        String directoryJSONDataString = response.toString();
                        if(directoryJSONDataString != null){
                            //pbHeaderProgress.setVisibility(View.GONE);
                            lyt_delete.setVisibility(View.GONE);
                          //  prepareList(directoryJSONDataString, StackState.POP_IN);
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    adapterr.notifyDataSetChanged();
                                    pbHeaderProgress.setVisibility(View.GONE);
                                    //getDirInside(cId, trgt,name);
                                    refreshData(cId, trgt,name);
                                }
                            },1000);
                        }else {
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                        }
                        //adapterr.notifyItemRangeInserted(docList.size()-1, docList.size());
                        //getDirInside(cId, trgt,name);
                        //adapterr.notifyItemRangeInserted(docList.size()-1, docList.size());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbHeaderProgress.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("CRId", cId);
                    params.put("cmd", "rm");
                    params.put("targets", deleteHash);

                    JSONObject filterExpression = new JSONObject();
                    try {
                        filterExpression.put("Status", "All");
                        if (Filter_CRIds != null) {
                            JSONArray CRIds = new JSONArray();
                            for (Long CRId : Filter_CRIds) {
                                CRIds.put(CRId);
                            }
                            if (CRIds.length() > 0) {
                                filterExpression.put("CRIds", CRIds);
                            }
                        }
                    } catch (JSONException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        Log.e("TEST","Delete Exception :"+e);
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

    private void refreshData(final String cId, final String trgt, String name) {
            pbHeaderProgress.setVisibility(View.VISIBLE);
            //txt_selectDirName.setText(name);
            request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pbHeaderProgress.setVisibility(View.VISIBLE);

                            String directoryJSONDataString = response.toString();
                            if(directoryJSONDataString != null){
                                pbHeaderProgress.setVisibility(View.GONE);
                                docList.clear();

                                try {
                                    JSONObject object = new JSONObject(directoryJSONDataString);
                                    String cwHash = null, cwPhash = null ,getCWD;
                                    JSONArray companyArray = object.getJSONArray("files");
                                    JSONObject cwdObj = object.getJSONObject("cwd");
                                    cwd = cwdObj.toString();
                                    getCWD = cwdObj.toString();

                                    cwPhash = cwdObj.getString("phash");
                                    cwHash = cwdObj.getString("hash");

                                    docList.clear();
                                    if (object.has("files")) {

                                        for (int i = 0; i < companyArray.length(); i++) {
                                            JSONObject obj = companyArray.getJSONObject(i);
                                            Document_upload_model dm = new Document_upload_model();

                                            if (obj.has("phash"))
                                                dm.phash = obj.getString("phash");
                                            if (obj.has("dirs"))
                                                dm.dirs = obj.getString("dirs");
                                            if (obj.has("isspecial"))
                                                dm.isspecial = obj.getString("isspecial");
                                            if (obj.has("name"))
                                                dm.name = obj.getString("name");
                                            if (obj.has("isroot"))
                                                dm.isroot = obj.getInt("isroot");
                                            if (obj.has("error"))
                                                dm.error = obj.getString("error");
                                            if (obj.has("path"))
                                                dm.path = obj.getString("path");
                                            if (obj.has("hash"))
                                                dm.hash = obj.getString("hash");
                                            if (obj.has("lhash"))
                                                dm.lhash = obj.getString("lhash");
                                            if (obj.has("mime"))
                                                dm.mime = obj.getString("mime");
                                            if (obj.has("icon"))
                                                dm.icon = obj.getString("icon");
                                            if (obj.has("csscls"))
                                                dm.csscls = obj.getString("csscls");
                                            if (obj.has("ts"))
                                                dm.ts = obj.getInt("ts");
                                            if (obj.has("size"))
                                                dm.size = obj.getInt("size");
                                            if (obj.has("read"))
                                                dm.read = obj.getInt("read");
                                            if (obj.has("write"))
                                                dm.write = obj.getInt("write");
                                            if (obj.has("locked"))
                                                dm.locked = obj.getInt("locked");
                                            if (obj.has("guid"))
                                                dm.guid = obj.getString("guid");
                                            if (obj.has("volumeid"))
                                                dm.guid = obj.getString("volumeid");

                                            docList.add(dm);

                                        }
                                    } else if (object.has("Message"))
                                        Log.e("TEST", "Message = " + object.getString("Message"));

                                    //adapterr = new DocumentAdapterr(this, docList, cwPhash, cwHash,getCWD,cId);
                                    recycler_view.setAdapter(adapterr);
                                    adapterr.notifyDataSetChanged();

                                    txt_selectDirName.setText(docList.get(0).name);
                                    for (int i=0; i<docList.size();i++) {
                                        //Log.e("TEST", "Dir Name = "+docList.get(1).name);
                                        //txt_selectDirName.setText(docList.get(docList.size()-1).name);
                                    }
                                    // Log.e("TEST", "========================");
                                } catch (JSONException e) {
                                    Log.e("TEST","Exception :"+e);
                                    e.printStackTrace();
                                }
                                adapterr.notifyDataSetChanged();

                            }else {
                                pbHeaderProgress.setVisibility(View.VISIBLE);
                            }
                            // btn_delete.setVisibility(View.GONE);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pbHeaderProgress.setVisibility(View.VISIBLE);
                }
            }) {
                @Override
                protected JSONObject getParams() {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("CRId", cId);
                        params.put("cmd", "open");
                        params.put("target", trgt);

                        JSONObject filterExpression = new JSONObject();
                        try {
                            filterExpression.put("Status", "All");
                            if (Filter_CRIds != null) {
                                JSONArray CRIds = new JSONArray();
                                for (Long CRId : Filter_CRIds) {
                                    CRIds.put(CRId);
                                }
                                if (CRIds.length() > 0) {
                                    filterExpression.put("CRIds", CRIds);
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

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void createList() {
        for (int i = 0; i < 20; i++) {
            Document_upload_model dm = new Document_upload_model();
            dm.setHash("Folder Name : " + (i + 1));
            if (i == 0) {
                dm.setChecked(true);
            }
            docList.add(dm);
        }
        //adapterr.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        goBack();
    }

    private void goBack() {
        prepareList("", StackState.POP_OUT);
    }

    public void showImagePath(final String cID, final String getHash){
        pbHeaderProgress.setVisibility(View.VISIBLE);
        request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pbHeaderProgress.setVisibility(View.VISIBLE);

                        String jsonResponse = response.toString();
                        if (jsonResponse != null){
                            pbHeaderProgress.setVisibility(View.GONE);

                            try {
                                JSONObject object = new JSONObject(jsonResponse);
                                String url = object.getString("url");
                                String FileName = object.getString("Name");
                                if(url != null){
                                    //((RelativeLayout) mView.findViewById(R.id.lyt_img_show)).setVisibility(View.VISIBLE);
                                    Intent intent = new Intent(Intent.ACTION_VIEW);
                                    intent.setData(Uri.parse(url));
                                    startActivity(intent);
                                    // File file = new File(url);
                                    //openFile(file);
                                    hideKeyboard();
                                    // Glide.with(DocumentUpload.this).load(url).into(img_show);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TEST","Error :"+error);
                pbHeaderProgress.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();

                    params.put("cmd", "file");
                    params.put("CRId", cID);
                    params.put("target", getHash);
                    params.put("acid", "undefined");
                    params.put("jsonrequired","1" );

                    JSONObject filterExpression = new JSONObject();
                    try {
                        filterExpression.put("Status", "All");
                        if (Filter_CRIds != null) {
                            JSONArray CRIds = new JSONArray();
                            for (Long CRId : Filter_CRIds) {
                                CRIds.put(CRId);
                            }
                            if (CRIds.length() > 0) {
                                filterExpression.put("CRIds", CRIds);
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
                    Log.e("TEST","JSONException");
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

    public void getDirInside(final String cId, final String target,String name) {

        pbHeaderProgress.setVisibility(View.VISIBLE);
    trgt = target;
        txt_selectDirName.setText(name);
        request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pbHeaderProgress.setVisibility(View.VISIBLE);

                        String directoryJSONDataString = response.toString();
                        if(directoryJSONDataString != null){
                            pbHeaderProgress.setVisibility(View.GONE);
                            prepareList(directoryJSONDataString, StackState.POP_IN);

                        }else {
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                        }
                       // btn_delete.setVisibility(View.GONE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbHeaderProgress.setVisibility(View.VISIBLE);
            }
        }) {
            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("CRId", cId);
                    params.put("cmd", "open");
                    params.put("target", target);

                    JSONObject filterExpression = new JSONObject();
                    try {
                        filterExpression.put("Status", "All");
                        if (Filter_CRIds != null) {
                            JSONArray CRIds = new JSONArray();
                            for (Long CRId : Filter_CRIds) {
                                CRIds.put(CRId);
                            }
                            if (CRIds.length() > 0) {
                                filterExpression.put("CRIds", CRIds);
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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getCurrentFocus();
        if (view == null) {
            view = new View(DocumentInside.this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void prepareList(String directoryJSONDataString, StackState mStackState ) {

        //  Manage Response Stack

        if (mStackState == StackState.POP_IN) {
            mResponseStack.add(directoryJSONDataString);
        }else
        {
            if (mResponseStack.size() > 0) {
                mResponseStack.remove(mResponseStack.size() - 1);
                if (mResponseStack.size() > 0)
                    directoryJSONDataString = mResponseStack.get(mResponseStack.size() - 1);
                else
                    super.onBackPressed();
            }
            else
                super.onBackPressed();
        }
        for (int i=0; i<mResponseStack.size();i++) {

        }
        if (directoryJSONDataString.equals(""))
            return;
        docList.clear();

        try {
            JSONObject object = new JSONObject(directoryJSONDataString);
            String cwHash = null, cwPhash = null ,getCWD;
            JSONArray companyArray = object.getJSONArray("files");
            Log.e("TEST","File size :"+companyArray.length());
            if(companyArray.length() <= 2){
                lin_nodata_found.setVisibility(View.VISIBLE);
            }else {
                lin_nodata_found.setVisibility(View.GONE);
            }
            JSONObject cwdObj = object.getJSONObject("cwd");
            cwd = cwdObj.toString();
            getCWD = cwdObj.toString();

            cwPhash = cwdObj.getString("phash");
            cwHash = cwdObj.getString("hash");

            docList.clear();
            if (object.has("files")) {

                for (int i = 0; i < companyArray.length(); i++) {
                    JSONObject obj = companyArray.getJSONObject(i);
                    Document_upload_model dm = new Document_upload_model();

                    if (obj.has("phash"))
                        dm.phash = obj.getString("phash");
                    if (obj.has("dirs"))
                        dm.dirs = obj.getString("dirs");
                    if (obj.has("isspecial"))
                        dm.isspecial = obj.getString("isspecial");
                    if (obj.has("name"))
                        dm.name = obj.getString("name");
                    if (obj.has("isroot"))
                        dm.isroot = obj.getInt("isroot");
                    if (obj.has("error"))
                        dm.error = obj.getString("error");
                    if (obj.has("path"))
                        dm.path = obj.getString("path");
                    if (obj.has("hash"))
                        dm.hash = obj.getString("hash");
                    if (obj.has("lhash"))
                        dm.lhash = obj.getString("lhash");
                    if (obj.has("mime"))
                        dm.mime = obj.getString("mime");
                    if (obj.has("icon"))
                        dm.icon = obj.getString("icon");
                    if (obj.has("csscls"))
                        dm.csscls = obj.getString("csscls");
                    if (obj.has("ts"))
                        dm.ts = obj.getInt("ts");
                    if (obj.has("size"))
                        dm.size = obj.getInt("size");
                    if (obj.has("read"))
                        dm.read = obj.getInt("read");
                    if (obj.has("write"))
                        dm.write = obj.getInt("write");
                    if (obj.has("locked"))
                        dm.locked = obj.getInt("locked");
                    if (obj.has("guid"))
                        dm.guid = obj.getString("guid");
                    if (obj.has("volumeid"))
                        dm.guid = obj.getString("volumeid");

                    docList.add(dm);

                }
            } else if (object.has("Message"))
                Log.e("TEST", "Message = " + object.getString("Message"));

            adapterr = new DocumentAdapterr(this, docList, cwPhash, cwHash,getCWD,cId);
            recycler_view.setAdapter(adapterr);
            adapterr.notifyDataSetChanged();

            txt_selectDirName.setText(docList.get(0).name);
            for (int i=0; i<docList.size();i++) {
                //Log.e("TEST", "Dir Name = "+docList.get(1).name);
                //txt_selectDirName.setText(docList.get(docList.size()-1).name);
            }
           // Log.e("TEST", "========================");
        } catch (JSONException e) {
            Log.e("TEST","Exception :"+e);
            e.printStackTrace();
        }
        adapterr.notifyDataSetChanged();
    }

    private void DialogBox(String s, final String trgt) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.customdialogbox);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(true);

        ((Button) dialog.findViewById(R.id.bt_create)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView edt_folderName = dialog.findViewById(R.id.edt_folderName);
                String getName = edt_folderName.getText().toString();
                if(getName.length() == 0){
                    edt_folderName.setError("Name is required!");
                }else {
                    getCreateDirectory(cId,getName, trgt);
                    dialog.dismiss();
                }

            }
        });

        ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Button Decline Clicked", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    private void getCreateDirectory(final String cId, final String fName, final String target) {
        pbHeaderProgress.setVisibility(View.VISIBLE);
        request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String cwHash = null, cwPhash = null;
                        pbHeaderProgress.setVisibility(View.VISIBLE);

                        String jsonDataString = response.toString();
                        if(jsonDataString != null){
                            try {
                                JSONObject object = new JSONObject(jsonDataString);
                                pbHeaderProgress.setVisibility(View.GONE);

                                if(object.has("added")){
                                    JSONArray array = object.getJSONArray("added");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);

                                        createHash = obj.getString("hash");

                                        cwPhash = obj.getString("phash");
                                        cwHash = obj.getString("hash");
                                        Document_upload_model dm = new Document_upload_model();

                                        if (obj.has("phash"))
                                            dm.phash = obj.getString("phash");
                                        if (obj.has("dirs"))
                                            dm.dirs = obj.getString("dirs");
                                        if (obj.has("isspecial"))
                                            dm.isspecial = obj.getString("isspecial");
                                        if (obj.has("name"))
                                            dm.name = obj.getString("name");
                                        if (obj.has("isroot"))
                                            dm.isroot = obj.getInt("isroot");
                                        if (obj.has("error"))
                                            dm.error = obj.getString("error");
                                        if (obj.has("path"))
                                            dm.path = obj.getString("path");
                                        if (obj.has("hash"))
                                            dm.hash = obj.getString("hash");
                                        if (obj.has("lhash"))
                                            dm.lhash = obj.getString("lhash");
                                        if (obj.has("mime"))
                                            dm.mime = obj.getString("mime");
                                        if (obj.has("icon"))
                                            dm.icon = obj.getString("icon");
                                        if (obj.has("csscls"))
                                            dm.csscls = obj.getString("csscls");
                                        if (obj.has("ts"))
                                            dm.ts = obj.getInt("ts");
                                        if (obj.has("size"))
                                            dm.size = obj.getInt("size");
                                        if (obj.has("read"))
                                            dm.read = obj.getInt("read");
                                        if (obj.has("write"))
                                            dm.write = obj.getInt("write");
                                        if (obj.has("locked"))
                                            dm.locked = obj.getInt("locked");
                                        if (obj.has("guid"))
                                            dm.guid = obj.getString("guid");
                                        if (obj.has("volumeid"))
                                            dm.guid = obj.getString("volumeid");
                                        docList.add(dm);

                                    }
                                }
                                else if (object.has("Message"))
                                    Log.e("TEST", "Message = "+object.getString("Message"));

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            adapterr.notifyItemRangeInserted(docList.size()-1, docList.size());
                            //adapterr = new DocumentAdapterr(DocumentInside.this, docList, cwPhash, cwHash, cwd,cId);
                            //recycler_view.setAdapter(adapterr);
                            //getDirInside(cId,target,fName);
                            //adapterr.notifyDataSetChanged();


                        }else {
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                        }
                        if (App.getInstance().authorizeSimple(response)) {
                            try {
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {  }
                    }

                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbHeaderProgress.setVisibility(View.VISIBLE);
            }
        })
        {
            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();

                    params.put("CRId", cId);
                    params.put("cmd", "mkdir");
                    params.put("name", fName);
                    params.put("target", target);

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

    public void showBottomSheetDialog(String hash,String getcId, String lastName,String type){
        final String getHash = hash;
        final String cID = getcId;
        final String lName = lastName;
        final String fileType = type;
       final JSONArray jsonArray = new JSONArray();
       jsonArray.put(hash);

        bottom_sheet = findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);

        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }
        final View view = getLayoutInflater().inflate(R.layout.sheet_list, null);

        view.findViewById(R.id.lyt_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog(cID,jsonArray);
            }
        });
        view.findViewById(R.id.lyt_rename).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //showCustomDialog(cID,getHash);
                String rm = "rename";
                reNameDialogBox(cID,getHash,rm,lName);
            }
        });
        if(fileType.equalsIgnoreCase("directory")){
            view.findViewById(R.id.lyt_download).setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.lyt_download).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lyt_download).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFileDownload(cID,getHash);
                }
            });
        }

        mBottomSheetDialog = new BottomSheetDialog(this);
        mBottomSheetDialog.setContentView(view);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }

    private void getFileDownload(final String cID, final String getHash) {

        pbHeaderProgress.setVisibility(View.VISIBLE);
            request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pbHeaderProgress.setVisibility(View.VISIBLE);

                            String jsonResponse = response.toString();
                            if (jsonResponse != null){
                                pbHeaderProgress.setVisibility(View.GONE);
                                prepareList(jsonResponse, StackState.POP_IN);
                                try {
                                    JSONObject object = new JSONObject(jsonResponse);
                                    String url = object.getString("url");
                                    String FileName = object.getString("Name");
                                    DownloadTask(url,FileName);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
                                pbHeaderProgress.setVisibility(View.VISIBLE);
                            }


                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TEST","Error :"+error);
                    pbHeaderProgress.setVisibility(View.VISIBLE);
                }
            }) {
                @Override
                protected JSONObject getParams() {
                    try {
                        JSONObject params = new JSONObject();

                        params.put("cmd", "file");
                        params.put("CRId", cID);
                        params.put("target", getHash);
                        params.put("acid", "undefined");
                        params.put("jsonrequired","1" );

                        JSONObject filterExpression = new JSONObject();
                        try {
                            filterExpression.put("Status", "All");
                            if (Filter_CRIds != null) {
                                JSONArray CRIds = new JSONArray();
                                for (Long CRId : Filter_CRIds) {
                                    CRIds.put(CRId);
                                }
                                if (CRIds.length() > 0) {
                                    filterExpression.put("CRIds", CRIds);
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
                        Log.e("TEST","JSONException");
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

    private void DownloadTask(String url, String fileName) {
        getURL = url;
        getURLName = fileName;
        TextView tv = new TextView(this);
        tv.setText("Android Download File With Progress Bar");
        checkAndCreateDirectory("/ECB");
        new DownloadFileAsync().execute(getURL);

    }

    private void reNameDialogBox(String cID, String getHash, String cmd, final String lName) {
            final String rCId = cID,rHash = getHash,rcmd = cmd,name = lName;
            final Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
            dialog.setContentView(R.layout.renamecustomdialogbox);
            final TextView edt_folderName = dialog.findViewById(R.id.edt_folderName);
            final Button btn_create = dialog.findViewById(R.id.bt_create);
            btn_create.setText("Rename");
             edt_folderName.setText(lName);

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.setCancelable(true);

            ((Button) dialog.findViewById(R.id.bt_create)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String getName = edt_folderName.getText().toString();

                    if(getName.length() == 0){
                        edt_folderName.setError("Name is required!");
                    }else {
                        getRenameDirectory(rCId,rHash,rcmd,getName);

                        dialog.dismiss();
                        mBottomSheetDialog.dismiss();
                    }
                }
            });

            ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    mBottomSheetDialog.dismiss();
                }
            });


            dialog.show();

    }

    private void getRenameDirectory(final String rCId, final String rHash, String rcmd, final String name) {
        pbHeaderProgress.setVisibility(View.VISIBLE);
            request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            pbHeaderProgress.setVisibility(View.VISIBLE);

                            String directoryJSONDataString = response.toString();
                            if(directoryJSONDataString != null){
                                pbHeaderProgress.setVisibility(View.GONE);
                                prepareList(directoryJSONDataString, StackState.POP_IN);
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        pbHeaderProgress.setVisibility(View.VISIBLE);
                                    }
                                },1000);
                                getDirInside(cId, trgt,name);

                            }else {
                                pbHeaderProgress.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pbHeaderProgress.setVisibility(View.VISIBLE);
                }
            }) {
                @Override
                protected JSONObject getParams() {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("CRId", rCId);
                        params.put("cmd", "rename");
                        params.put("name", name);
                        params.put("target", rHash);

                        JSONObject filterExpression = new JSONObject();
                        try {
                            filterExpression.put("Status", "All");
                            if (Filter_CRIds != null) {
                                JSONArray CRIds = new JSONArray();
                                for (Long CRId : Filter_CRIds) {
                                    CRIds.put(CRId);
                                }
                                if (CRIds.length() > 0) {
                                    filterExpression.put("CRIds", CRIds);
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

    public void showCustomDialog(String crId, final JSONArray hash){
        final String delCid = crId;
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.dialog_delete_basic);
        dialog.setCancelable(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        ((TextView) dialog.findViewById(R.id.tv_content)).setMovementMethod(LinkMovementMethod.getInstance());

        ((Button) dialog.findViewById(R.id.bt_accept)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                multiDeleteDir(delCid,hash);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pbHeaderProgress.setVisibility(View.VISIBLE);
                    }
                },500);
                getDirInside(cId, trgt,name);
                dialog.dismiss();
                mBottomSheetDialog.dismiss();

            }
        });
        ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }

    }
    public void checkConnection(){
        if(isOnline()){
           // Toast.makeText(getApplication(), "You are connected to Internet", Toast.LENGTH_SHORT).show();
        }else{
            SnakebarCustom.danger(getApplicationContext(),view,"You are not connected to Internet", 5000 );
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage("Downloading file…");
                mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMax(100);
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                mProgressDialog.setCancelable(true);
                mProgressDialog.show();
                return mProgressDialog;
            default:
                return null;
        }
    }

    private class DownloadFileAsync extends AsyncTask<String, String, String>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showDialog(DIALOG_DOWNLOAD_PROGRESS);
        }

        @Override
        protected String doInBackground(String... aurl) {
            int count;
            try {
                URL url = new URL(getURL);
                URLConnection conection = url.openConnection();
                conection.connect();

                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/ECB/"+getURLName);
                byte data[] = new byte[1024];
                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress(""+(int)((total*100)/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Error: ", e.getMessage());
                e.printStackTrace();
            }

            return null;
        }
        protected void onProgressUpdate(String... progress) {
            mProgressDialog.setProgress(Integer.parseInt(progress[0]));
        }
        @Override
        protected void onPostExecute(String unused) {
            dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            adapterr.notifyDataSetChanged();
        }
    }
    private void checkAndCreateDirectory(String dirName) {
        File new_dir = new File( rootDir + dirName );
        if( !new_dir.exists() ){
            new_dir.mkdirs();
        }
    }
}
