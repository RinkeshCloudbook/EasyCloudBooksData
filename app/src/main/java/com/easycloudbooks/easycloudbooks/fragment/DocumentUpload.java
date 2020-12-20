package com.easycloudbooks.easycloudbooks.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
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
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.Retrofit.ClientUploadExample;
import com.easycloudbooks.easycloudbooks.Retrofit.FileUploadService;
import com.easycloudbooks.easycloudbooks.activity.DocumentInside;
import com.easycloudbooks.easycloudbooks.activity.MainActivity;
import com.easycloudbooks.easycloudbooks.adapter.Company.CompanyListAdapter;
import com.easycloudbooks.easycloudbooks.adapter.Company.CompanySearchAdapter;
import com.easycloudbooks.easycloudbooks.adapter.DocumentAdapter;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.AlertDialogBox;
import com.easycloudbooks.easycloudbooks.common.CommonDataSet;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.MyDialogClickListener;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.model.CompanyRelationJ;
import com.easycloudbooks.easycloudbooks.model.Document_upload_model;
import com.easycloudbooks.easycloudbooks.model.RoleJ;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.easycloudbooks.easycloudbooks.util.GenericFileProvider;
import com.easycloudbooks.easycloudbooks.util.MyDialogClickListenerString;
import com.easycloudbooks.easycloudbooks.util.RealPathUtil;
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
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_COMPANY_SEARCH;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_DOCUMENT_GETDETAIL;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_DOCUMENT_SELECT_COMPANY;

/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentUpload extends BaseExampleFragment
        implements SwipeRefreshLayout.OnRefreshListener, MyDialogClickListenerString ,CompanyListAdapter.AdapterListener{

    private static final String TAG = DocumentUpload.class.getSimpleName();
    private static boolean isNetworkAvailable;

    TextView mMessage;
    public static RoleJ roleJ;
    public CustomAuthRequest request;
    public long[] Filter_CRIds;
    List<Document_upload_model> docList = new ArrayList<>();
    List<Document_upload_model> loadSpinnerList = new ArrayList<>();
    ArrayList<CompanyRelationJ> list = new ArrayList<>();
    private CompanyListAdapter mSearchResultsAdapter;
    List<String> companyList = new ArrayList<>();
    DocumentAdapter adapter;
    FloatingActionButton add_dir;
    public View mView;
    private SwipeRefreshLayout swipeRefreshLayout;
    Spinner company_spinner;
    String cwdHash,trgt,SpinnercId,createHash,getURLName,getURL;
    //ProgressBar pbHeaderProgress;
    LinearLayout pbHeaderProgress;
    RecyclerView recycler_view,search_recycler_view;
    private View parent_view;
    ImageView img_search,img_show;
    public String photoFileName = "photo.jpg";
    File photoFile;
    boolean flage = false;
    Retrofit retrofit;
    LinearLayout lin_nodata_found,search_edit_frame,lyt_delete;
    EditText edt_search;
    public final String APP_TAG = "MyCustomApp";
    public final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1034;
    private ProgressDialog mProgressDialog;
    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    File rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private int CAMREA_CODE = 1;

    public static DocumentUpload newInstance() {
        return new DocumentUpload();
    }
    public View GetView() {
        return mView;
    }
    public DocumentUpload() {
        // Required empty public constructor
    }
    public void SetFilter(long[] CRIds) {
        Filter_CRIds = CRIds;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_document_upload, container, false);
        mView = view;
        //list_doc = view.findViewById(R.id.list_doc);
        recycler_view = view.findViewById(R.id.recycler_view);
        search_recycler_view = view.findViewById(R.id.search_recycler_view);

        RecyclerView.LayoutManager recyce = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        recycler_view.setLayoutManager(recyce);

        RecyclerView.LayoutManager searchRecyce = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        search_recycler_view.setLayoutManager(searchRecyce);


       /* RecyclerView.LayoutManager recyce = new GridLayoutManager(getActivity(),2);
        recycler_view.setLayoutManager(recyce);*/

        add_dir = view.findViewById(R.id.add_dir);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        mMessage = view.findViewById(R.id.message);
        company_spinner = view.findViewById(R.id.company_spinner);
        parent_view = view.findViewById(R.id.content);
        img_search = view.findViewById(R.id.img_search);
        lyt_delete = view.findViewById(R.id.lyt_delete);
        pbHeaderProgress  = view.findViewById(R.id.pbHeaderProgress );
        bottom_sheet = view.findViewById(R.id.bottom_sheet);
        lin_nodata_found = view.findViewById(R.id.lin_nodata_found);
        edt_search = view.findViewById(R.id.edt_search);
        search_edit_frame = view.findViewById(R.id.search_edit_frame);
        img_show = view.findViewById(R.id.img_show);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        checkConnection();
                        getDocumentData(SpinnercId);

                    }
                }, 2000);
            }
        });
        checkConnection();
        //LoadSpinner();

        add_dir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //DialogBox(cwdHash,SpinnercId);
                selectImage();
            }
        });
        Log.e("TEST","Show Keyboad");


        ((Button) view.findViewById(R.id.btn_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((RelativeLayout) mView.findViewById(R.id.lyt_img_show)).setVisibility(View.GONE);
                getDocumentData(SpinnercId);
                adapter.notifyDataSetChanged();
            }
        });



        edt_search.addTextChangedListener(new TextWatcher() {
            long lastChange=0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() >= 1){
                    ((ImageButton) view.findViewById(R.id.bt_clear)).setVisibility(View.VISIBLE);
                }
                if(s.length() >= 4){
                    final String getText = s.toString();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (System.currentTimeMillis() - lastChange >= 300) {
                                //send request
                                searchCompany(getText);
                            }
                        }
                    }, 300);
                    lastChange = System.currentTimeMillis();

                }else if(s.length() == 0){
                   // getcompanies(false, 0, CurrentSearctText);
                    ((ImageButton) view.findViewById(R.id.bt_clear)).setVisibility(View.GONE);

                    //tempContactList.clear();
                   /*FragmentTransaction ft = getFragmentManager().beginTransaction();
                   ft.detach(ContactFragment.this).attach(ContactFragment.this).commit();*/
                }
            }
        });

        ((ImageButton) view.findViewById(R.id.bt_clear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.setText("");
                search_recycler_view.setVisibility(View.GONE);
                docList.clear();
                list.clear();
                adapter.notifyDataSetChanged();
            }
        });
        showKeyboard();
        return view;
    }

    /*public void searchClick(){
        if(flage == false){
            search_edit_frame.setVisibility(View.VISIBLE);
            //edt_search.requestFocus();


            flage = true;
        }else if(flage == true){
            search_edit_frame.setVisibility(View.GONE);

            flage = false;
        }
    }*/


    private void selectImage() {
        AlertDialogBox dailogBox = new AlertDialogBox(getActivity(),"Add Photo",this);
        dailogBox.show();

        /*TextView titleView = new TextView(getActivity());
        titleView.setText("Add Photo!");
        //titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(20, 20, 20, 20);
        titleView.setTextSize(20F);
        titleView.setTypeface(Typeface.DEFAULT_BOLD);
        titleView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.main_color_500));
        titleView.setTextColor(ContextCompat.getColor(getActivity(), R.color.colorAccent));

        final CharSequence[] items = { "Camera", "Browse","Create Folder",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCustomTitle(titleView);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utility.checkPermission(getActivity());
                if (items[item].equals("Camera")) {
                    //userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Browse")) {
                    //userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                }else if (items[item].equals("Create Folder")) {
                    //userChoosenTask="Choose from Library";
                    if(result)
                        DialogBox(cwdHash,SpinnercId);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();*/
    }

    public void cameraIntent() {
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

    private boolean permissionAlreadyGranted() {

        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
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

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.CAMERA)) {
        }
        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMREA_CODE);
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

    public void galleryIntent() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),111);
    }

    private File getPhotoFileUri(String photoFileName) {
        File mediaStorageDir = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

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
                String realPath= RealPathUtil.getRealPath(getActivity(),data.getData());
                String filename = realPath.substring(realPath.lastIndexOf("/")+1);
                imagesEncodedList.add(realPath);

                uploadImage(parsedurl,imagesEncodedList,Token,SpinnercId,cwdHash,filename);
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
                        String realPath = RealPathUtil.getRealPath(getActivity(), uri);
                        Log.e("TEST", "File Path = " + realPath);

                        sbString.append(realPath.substring(realPath.lastIndexOf("/") + 1)).append(",");
                        // Get the cursor
                        Cursor cursor = getContext().getContentResolver().query(uri, filePathColumn, null, null, null);
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

                        uploadImage(parsedurl,imagesEncodedList,Token,SpinnercId,cwdHash,filename);

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
                uploadImage(parsedurl,imagesEncodedList,Token,SpinnercId,cwdHash,filename);
            }

            }

        }

    private void uploadImage(final String parsedurl, final ArrayList<String> realPath, final String token, final String crID, String gethash, String filename) {
        pbHeaderProgress.setVisibility(View.VISIBLE);

        StringBuilder sb3 = new StringBuilder();
        sb3.append(".provider");
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient client = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS).addInterceptor(interceptor).build();
        retrofit = new Retrofit.Builder()
                .baseUrl(parsedurl+"/").client(client).addConverterFactory(GsonConverterFactory.create(new GsonBuilder().serializeNulls().create())).build();
        MultipartBody.Part[] surveyImagesParts = new MultipartBody.Part[realPath.size()];
        for (int index = 0; index <  realPath.size(); index++) {
            RequestBody surveyBody =  RequestBody.create(MediaType.parse("image/*"),
                    new File(realPath.get(index)));
            surveyImagesParts[index] = MultipartBody.Part.createFormData("file",
                    new File(realPath.get(index)).getName(),
                    surveyBody);
        }
        RequestBody clientimage =  RequestBody.create(MediaType.parse("text/plain"), filename);
        RequestBody clientHash =  RequestBody.create(MediaType.parse("text/plain"), gethash);
        RequestBody crid =  RequestBody.create(MediaType.parse("text/plain"), String.valueOf(crID));
        RequestBody comand =  RequestBody.create(MediaType.parse("text/plain"), "upload");

        FileUploadService apiService = retrofit.create(FileUploadService.class);
        Call<ClientUploadExample> memberCall = apiService.uploadImage(parsedurl,"Bearer "+token,surveyImagesParts, clientHash, crid,clientimage,comand);
        memberCall.enqueue(new Callback<ClientUploadExample>() {

            @Override
            public void onResponse(Call<ClientUploadExample> call, retrofit2.Response<ClientUploadExample> response) {
                if(response.body() != null && response.body().toString().length() > 0){
                    pbHeaderProgress.setVisibility(View.GONE);
                       Log.e("TEST","Message :"+response.body().getName());
                       adapter.notifyDataSetChanged();
                        getDocumentData(crID);

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

    private void DialogBox(final String dHash, final String dCId) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.customdialogbox);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        ((Button) dialog.findViewById(R.id.bt_create)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView edt_folderName = dialog.findViewById(R.id.edt_folderName);
                String getName = edt_folderName.getText().toString();
                if(getName.length() == 0){
                    edt_folderName.setError("Name is required!");
                }else {
                    getCreateDirectory(dCId, getName, dHash);
                    dialog.dismiss();
                }

            }
        });

        ((Button) dialog.findViewById(R.id.bt_decline)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        dialog.show();
    }

    public void deleteVisible() {
        lyt_delete.setVisibility(View.VISIBLE);
        Log.e("TEST","getSelected() Size :"+adapter.getSelected().size());
        if(adapter.getSelected().size() == 0){
            lyt_delete.setVisibility(View.GONE);
            getDocumentData(SpinnercId);
        }
        img_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.getSelected().size() > 0){
                    JSONArray jsonArray=new JSONArray();
                    StringBuilder stringBuilder = new StringBuilder();

                    for (int i = 0; i < adapter.getSelected().size(); i++) {
                        stringBuilder.append(adapter.getSelected().get(i).getHash());
                        jsonArray.put(adapter.getSelected().get(i).getHash());
                        pbHeaderProgress.setVisibility(View.VISIBLE);
                        adapter.notifyDataSetChanged();
                    }
                    multiDeleteDir(SpinnercId,jsonArray);
                }else {
                    showToast("No Selection");
                    lyt_delete.setVisibility(View.GONE);
                }

            }

        });
        //img_search.setVisibility(View.VISIBLE);
    }

    private void multiDeleteDir(final String cId, final JSONArray deleteHash) {
        Log.e("TEST","Delete CID :"+cId);
        Log.e("TEST","Delete HASH :"+deleteHash);
        pbHeaderProgress.setVisibility(View.VISIBLE);
        request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pbHeaderProgress.setVisibility(View.VISIBLE);
                        String directoryJSONDataString = response.toString();
                        if(directoryJSONDataString != null){

                            pbHeaderProgress.setVisibility(View.GONE);
                            lyt_delete.setVisibility(View.GONE);
                            //prepareList(directoryJSONDataString, StackState.POP_IN);
                        }else {
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                        }
                        //getDirInside(cId, trgt,name);
                        pbHeaderProgress.setVisibility(View.VISIBLE);
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                                pbHeaderProgress.setVisibility(View.GONE);
                                getDocumentData(cId);
                            }
                        },1000);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbHeaderProgress.setVisibility(View.VISIBLE);
            }
            // adapterr.notifyItemRangeInserted(docList.size()-1, docList.size());
            //adapterr = new DocumentAdapterr(DocumentInside.this, docList, cwPhash, cwHash, cwd,cId);
            //recycler_view.setAdapter(adapterr);
            // getDirInside(cId,target,fName);

        }) {
            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("CRId", cId);
                    params.put("cmd", "rm");
                    params.put("targets", deleteHash);
                    /*for (int i = 0; i < deleteHash.length(); i++)
                    {
                        params.put("targets[+i+]", deleteHash.get(i));

                    }*/

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

    private void showToast(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
    }

    private void getCreateDirectory(final String cId, final String fName, final String target) {
            pbHeaderProgress.setVisibility(View.VISIBLE);
        request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String cwHash = null, cwPhash = null;

                        String jsonDataString = response.toString();
                        if(jsonDataString.length() != 0){
                            pbHeaderProgress.setVisibility(View.GONE);
                            try {
                                JSONObject object = new JSONObject(jsonDataString);

                                //docList.clear();
                                if(object.has("added")){
                                    JSONArray array = object.getJSONArray("added");
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);
                                        trgt = obj.getString("hash");
                                        cwHash = obj.getString("hash");
                                        createHash = obj.getString("hash");
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
                            adapter = new DocumentAdapter(DocumentUpload.this,getContext(), docList, cwHash,cId);
                            recycler_view.setAdapter(adapter);
                            getDocumentData(cId);
                            adapter.notifyDataSetChanged();

                        }else {
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                        }
                        if (App.getInstance().authorizeSimple(response)) {
                            try {
                            } catch (Exception ex) {
                                ex.printStackTrace();
                                pbHeaderProgress.setVisibility(View.VISIBLE);
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

    private void LoadSpinner() {
        request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_SELECT_COMPANY, null, 0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.w(TAG, response.toString());
                        String companyjsonDataString = response.toString();
                        try {
                            JSONObject object = new JSONObject(companyjsonDataString);
                            JSONArray companyArray = object.getJSONArray("obj");

                            if (object.has("obj")) {
                                for (int j = 0; j < companyArray.length(); j++) {
                                    JSONObject objdata = companyArray.getJSONObject(j);

                                    Document_upload_model dm = new Document_upload_model();
                                    dm.name = objdata.getString("FN");
                                    dm.CRId = objdata.getString("Id");

                                    companyList.add(dm.name);
                                    loadSpinnerList.add(dm);

                                }
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        ArrayAdapter<String> company_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, companyList);
                                        company_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                        company_spinner.setAdapter(company_adapter);
                                        company_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                                SpinnercId = loadSpinnerList.get(i).CRId;
                                                CommonDataSet.comCiD =  SpinnercId;
                                                getDocumentData(SpinnercId);
                                                Log.e("TEST","Get Spinner Id Cid :"+SpinnercId);
                                                docList.clear();
                                                //Log.e("TEST","Cwd Hash in Spinner :"+cwdHash);

                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> adapterView) {

                                            }
                                        });
                                    }
                                },1000);



                            } else if (object.has("Message"))
                                Log.e("TEST", "Message = " + object.getString("Message"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("text", "");
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

    private void searchCompany(final String getText) {
        //pbHeaderProgress.setVisibility(View.VISIBLE);
        Log.e("TEST","Call Search");
        list.clear();
        request = new CustomAuthRequest(Request.Method.POST, METHOD_COMPANY_SEARCH, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (App.getInstance().authorizeSimple(response)) {

                            String strResponse = response.toString();
                            if(strResponse != null){
                                try {
                                    //contactList.clear();

                                    //pbHeaderProgress.setVisibility(View.GONE);
                                    JSONObject obj = new JSONObject(response.toString());
                                    JSONArray jsonArray = obj.getJSONArray("obj");

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        CompanyRelationJ cd = new CompanyRelationJ();
                                        JSONObject userDetail = jsonArray.getJSONObject(i);
                                        Log.e("TEST","GEt Company Id :"+userDetail.getString("Id"));
                                        cd.Name = userDetail.getString("FN");
                                        cd.UId = userDetail.getString("Id");
                                        Log.e("TEST","Company Name :"+cd.Name);
                                        list.add(cd);
                                    }

                                    CompanySearchAdapter adaper = new CompanySearchAdapter(DocumentUpload.this,getContext(),list);
                                    recycler_view.setAdapter(adaper);

                                    //mSearchResultsAdapter.notifyDataSetChanged();
                                  } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
//                                pbHeaderProgress.setVisibility(View.VISIBLE);
                                SnakebarCustom.danger(getActivity(), mView, "Unable to fetch contact: " + "No data found", 5000);
                            }
                        }
                    }
                }, new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error) {
                //pbHeaderProgress.setVisibility(View.VISIBLE);
                SnakebarCustom.danger(getActivity(), mView, "Unable to fetch Companies: " + error.getMessage(), 5000);
            }
        }) {

            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    Log.e("TEST","Param :"+getText);
                    params.put("text", getText.trim());
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
                    //params.put("filterExpression", filterExpression);

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

    public void getSearchName(String cName){
        Log.e("TEST","Get Comapny Name :"+cName);

    }

    public void getDocumentData(final String cId) {
        SpinnercId = cId;
        Log.e("TEST","Call from Adapter"+SpinnercId);
        pbHeaderProgress.setVisibility(View.VISIBLE);
        request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pbHeaderProgress.setVisibility(View.VISIBLE);
                        Log.w(TAG, response.toString());
                        String jsonDataString = response.toString();

                        if(jsonDataString != null){
                            pbHeaderProgress.setVisibility(View.GONE);

                            try {
                                JSONObject object = new JSONObject(jsonDataString);
                                JSONArray array = object.getJSONArray("files");
                                Log.e("TEST","File size :"+array.length());
                                if(array.length() <= 1){
                                    lin_nodata_found.setVisibility(View.VISIBLE);
                                }else {
                                    lin_nodata_found.setVisibility(View.GONE);
                                }
                                JSONObject cwdObj = object.getJSONObject("cwd");
                                cwdHash = cwdObj.getString("hash");
                                CommonDataSet.comHash = cwdHash;

                                docList.clear();
                                if (object.has("files")) {
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);
                                        trgt = obj.getString("hash");
                                        Document_upload_model dm = new Document_upload_model();
                                        dm.cwdHash = cwdHash;
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

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }else {
                            pbHeaderProgress.setVisibility(View.VISIBLE);
                        }

                        adapter = new DocumentAdapter(DocumentUpload.this, getContext(), docList, cwdHash,cId);
                        recycler_view.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                        if (App.getInstance().authorizeSimple(response)) {
                            try {
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        } else {
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

                    params.put("CRId", cId);
                    params.put("cmd", "open");
                    params.put("target", "");
                    params.put("init", "1");
                    params.put("tree", "1");
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

    public void showBottomSheetDialog(String hash,String getcId, String lastName,String type){

        final String getHash = hash;
        final String cID = getcId;
        final String lName = lastName;
        final String fileType = type;
        Log.e("TEST","Bottum :"+cID);
        final JSONArray jsonArray=new JSONArray();
        jsonArray.put(getHash);
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
                    //getDocumentData(cID);
                    Log.e("TEST","Donload From Menu :"+getHash);
                    Log.e("TEST","Donload From CiD :"+cID);
                    getFileDownload(cID,getHash);
                }
            });
        }

        mBottomSheetDialog = new BottomSheetDialog(getActivity());
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
                                Log.e("TEST","Show Image Document Url :"+url);




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

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if (view == null) {
            view = new View(getActivity());
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void showKeyboard(){

        InputMethodManager inputMethodManager =
                (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        edt_search.requestFocus();
        inputMethodManager.showSoftInput(
                edt_search,
                0);
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

                            try {
                                JSONObject object = new JSONObject(jsonResponse);
                                String url = object.getString("url");

                                //showImageUrl = url;
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
        TextView tv = new TextView(getActivity());
        tv.setText("Android Download File With Progress Bar");
        checkAndCreateDirectory("/ECB");
        new DownloadFileAsync().execute(getURL);

    }

    private void reNameDialogBox(String cID, String getHash, String cmd, final String lName) {
        final String rCId = cID,rHash = getHash,rcmd = cmd,name = lName;
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.renamecustomdialogbox);
        final TextView edt_folderName = dialog.findViewById(R.id.edt_folderName);
        final Button btn_create = dialog.findViewById(R.id.bt_create);
        btn_create.setText("Rename");
        edt_folderName.setText(lName);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);

        ((Button) dialog.findViewById(R.id.bt_create)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getName = edt_folderName.getText().toString();

                if(getName.length() == 0){
                    edt_folderName.setError("Name is required!");
                }else {
                    getRenameDirectory(rCId,rHash,rcmd,getName);
                    adapter.notifyDataSetChanged();
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
                            //prepareList(directoryJSONDataString, StackState.POP_IN);
                            getDocumentData(rCId);

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

    public void showCustomDialog(final String crId, final JSONArray hash){
        Log.e("TEST","showCustomDialog :"+crId);
        Log.e("TEST","showCustomDialog HASH :"+hash);
        final String delCid = crId;
        final Dialog dialog = new Dialog(getActivity());
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
                //deleteDir(delCid,delHash);
                multiDeleteDir(delCid,hash);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pbHeaderProgress.setVisibility(View.VISIBLE);
                    }
                },500);
                //getDirInside(cId, trgt,name);
                getDocumentData(crId);
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
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }

    }
    public void checkConnection(){
        if(isOnline()){
           // Toast.makeText(getActivity(), "You are connected to Internet", Toast.LENGTH_SHORT).show();
        }else{
            SnakebarCustom.danger(getActivity(), mView, "You are not connected to Internet", 5000);
        }

    }

    @Override
    public boolean onActivityBackPress() {
        return true;
    }

    @Override
    public void onRefresh() {
    }

    @Override
    public void onMyDialogClick(String cam,String gal,String folder) {
        if(cam.equalsIgnoreCase("cam")){
            cameraIntent();
        }else if(gal.equalsIgnoreCase("gal")){
            galleryIntent();
        }else if(folder.equalsIgnoreCase("folder")){
            DialogBox(cwdHash,SpinnercId);
        }
    }

    @Override
    public void onMessageRowClicked(int position, CompanyListAdapter.ViewHolder holder) {

    }

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DIALOG_DOWNLOAD_PROGRESS: //we set this to 0
                mProgressDialog = new ProgressDialog(getActivity());
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

    private class DownloadFileAsync extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            getActivity().showDialog(DIALOG_DOWNLOAD_PROGRESS);
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
            getActivity().dismissDialog(DIALOG_DOWNLOAD_PROGRESS);
            adapter.notifyDataSetChanged();
        }
    }
    private void checkAndCreateDirectory(String dirName) {
        File new_dir = new File( rootDir + dirName );
        if( !new_dir.exists() ){
            new_dir.mkdirs();
        }
    }
/*

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            adapter.notifyDataSetChanged();
        }
    }//onActivityResult

*/

}
