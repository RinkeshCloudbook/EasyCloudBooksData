package com.easycloudbooks.easycloudbooks.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.adapter.DocumentAdapterr;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.CommonDataSet;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.model.Document_upload_model;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

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
import java.util.List;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_DOCUMENT_GETDETAIL;


public class SearchOpration extends AppCompatActivity {

    public CustomAuthRequest request;
    public long[] Filter_CRIds;
    List<Document_upload_model> docList = new ArrayList<>();
    RecyclerView recycler_view;
    DocumentAdapterr adapter;
    List<String> mResponseStack = new ArrayList<>();
    LinearLayout lyt_delete;

    ProgressBar pbHeaderProgress;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private View bottom_sheet;
    private ProgressDialog mProgressDialog;
    SwipeRefreshLayout swipeRefreshLayout;
    View view;

    public static final int DIALOG_DOWNLOAD_PROGRESS = 0;
    String crId,searchText,getURL,getURLName,hash,cId,LastHash;
    File rootDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS); // in Download create dir
    private enum StackState {
        POP_IN,
        POP_OUT
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_opration);

        recycler_view = findViewById(R.id.recycler_view);
        pbHeaderProgress = findViewById(R.id.pbHeaderProgress);
        bottom_sheet = findViewById(R.id.bottom_sheet);
        lyt_delete = findViewById(R.id.lyt_delete);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        pbHeaderProgress.setVisibility(View.GONE);


        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
             hash = bundle.getString("H");
            cId = bundle.getString("CId");
        }
        hash = CommonDataSet.comHash;
        cId = CommonDataSet.comCiD;
        LastHash = hash;
        /*RecyclerView.LayoutManager recy = new GridLayoutManager(getApplicationContext(), 2);
        recycler_view.setLayoutManager(recy);*/
        RecyclerView.LayoutManager recyce = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false);
        recycler_view.setLayoutManager(recyce);

        ((ImageView) findViewById(R.id.img_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        RefreshData(cId,LastHash);
                        checkConnection();
                    }
                }, 2000);
            }
        });
        checkConnection();


        ((Button) findViewById(R.id.btn_search)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = ((EditText) findViewById(R.id.edt_search)).getText().toString();
                if(searchText.length() > 0) {
                    getSearchData(cId, hash, searchText);
                    Handler handler = new Handler();
                    pbHeaderProgress.setVisibility(View.VISIBLE);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pbHeaderProgress.setVisibility(View.GONE);

                        }
                    },1000);
                }else {
                    ((EditText) findViewById(R.id.edt_search)).setError("Enter value.");
                }
            }
        });
        ((ImageView) findViewById(R.id.bt_clear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) findViewById(R.id.edt_search)).setText("");
            }
        });
    }
    private void getSearchData(final String cId, final String hash, final String searchText) {
        crId = cId;

        pbHeaderProgress.setVisibility(View.VISIBLE);
            request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String jsonDataString = response.toString();
                            if(jsonDataString != null){
                                pbHeaderProgress.setVisibility(View.GONE);
                                try {
                                    JSONObject object = new JSONObject(jsonDataString);
                                    JSONArray array = object.getJSONArray("files");
                                   // CommonDataSet.comHash = cwdHash;
                                    docList.clear();
                                    if (object.has("files")) {
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject obj = array.getJSONObject(i);

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
                                                dm.volumeid = obj.getString("volumeid");

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

                            //adapter = new SearchAdapter(SearchOpration.this,docList,cId);
                            String searchId = "s1";
                            adapter = new DocumentAdapterr(SearchOpration.this,docList,"0","0",searchId,cId);
                            recycler_view.setAdapter(adapter);
                            //RefreshData(cId,hash,searchText);
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
                    //pbHeaderProgress.setVisibility(View.VISIBLE);
                }
            }) {
                @Override
                protected JSONObject getParams() {
                    try {
                        JSONObject params = new JSONObject();

                        params.put("CRId", cId);
                        params.put("cmd", "search");
                        params.put("q", searchText);
                        params.put("target", hash);

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
                        //DataGlobal.SaveLog(TAG, ex);
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
    public void RefreshData(final String cId, final String hash){
            crId = cId;
            pbHeaderProgress.setVisibility(View.VISIBLE);
            request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            String jsonDataString = response.toString();
                            if(jsonDataString != null){
                                pbHeaderProgress.setVisibility(View.GONE);
                                try {
                                    JSONObject object = new JSONObject(jsonDataString);
                                    JSONArray array = object.getJSONArray("files");
                                    // CommonDataSet.comHash = cwdHash;
                                    docList.clear();
                                    if (object.has("files")) {
                                        for (int i = 0; i < array.length(); i++) {
                                            JSONObject obj = array.getJSONObject(i);
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
                                                dm.volumeid = obj.getString("volumeid");

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

                            String searchId = "s1";
                            adapter = new DocumentAdapterr(SearchOpration.this,docList,"0","0",searchId,cId);
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
                    //pbHeaderProgress.setVisibility(View.VISIBLE);
                }
            }) {
                @Override
                protected JSONObject getParams() {
                    try {
                        JSONObject params = new JSONObject();

                        params.put("CRId", cId);
                        params.put("cmd", "search");
                        params.put("q", searchText);
                        params.put("target", hash);

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
                        //DataGlobal.SaveLog(TAG, ex);
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
    public void showBottomSheetDialog(final String hash, String getcId, final String lastName, String type){
        final String getHash = hash;
        final String cID = getcId;
        final String lName = lastName;
        String filetype = lastName;
        final JSONArray jsonArray = new JSONArray();
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
                String rm = "rename";
                reNameDialogBox(cID,getHash,rm,lName);
            }
        });
        if(filetype.equalsIgnoreCase("directory")){
            view.findViewById(R.id.lyt_download).setVisibility(View.GONE);
        }else {
            view.findViewById(R.id.lyt_download).setVisibility(View.VISIBLE);
            view.findViewById(R.id.lyt_download).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFileDownload(cID,getHash);
                    pbHeaderProgress.setVisibility(View.VISIBLE);
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pbHeaderProgress.setVisibility(View.GONE);
                            RefreshData(cId,LastHash);
                            mBottomSheetDialog.dismiss();
                        }
                    },1000);

                }
            });
        }

        mBottomSheetDialog = new BottomSheetDialog(SearchOpration.this);
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
                        Log.e("TEST","Response Download :"+response.toString());
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
                   // DataGlobal.SaveLog(TAG, ex);
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
            adapter.notifyDataSetChanged();
        }
    }
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
    private void checkAndCreateDirectory(String dirName) {
        File new_dir = new File( rootDir + dirName );
        if( !new_dir.exists() ){
            new_dir.mkdirs();
        }
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
                                    adapter.notifyDataSetChanged();
                                    pbHeaderProgress.setVisibility(View.GONE);
                                    //getDirInside(cId, trgt,name);
                                    RefreshData(cId,LastHash);
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
                    //DataGlobal.SaveLog(TAG, ex);
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
    private void reNameDialogBox(String cID, String getHash, String cmd, final String lName) {
        Log.e("TEST","Selected Name :"+lName);
        final String rCId = cID,rHash = getHash,rcmd = cmd,name = lName;
        final Dialog dialog = new Dialog(SearchOpration.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
        dialog.setContentView(R.layout.customdialogbox);
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
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pbHeaderProgress.setVisibility(View.GONE);
                            RefreshData(cId,LastHash);
                        }
                    },1000);
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
    private void getRenameDirectory(final String rCId, final String rHash, String rcmd, final String getName) {
        pbHeaderProgress.setVisibility(View.VISIBLE);
        final String dCid = rCId,cmd = rcmd;
        request = new CustomAuthRequest(Request.Method.POST, METHOD_DOCUMENT_GETDETAIL, null, 0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        pbHeaderProgress.setVisibility(View.VISIBLE);

                        String jsonDataString = response.toString();

                        if(jsonDataString != null){
                            pbHeaderProgress.setVisibility(View.GONE);
                            try {
                                JSONObject object = new JSONObject(jsonDataString);
                                JSONArray array = object.getJSONArray("files");

                                docList.clear();
                                if (object.has("files")) {
                                    for (int i = 0; i < array.length(); i++) {
                                        JSONObject obj = array.getJSONObject(i);
                                        Document_upload_model dm = new Document_upload_model();
                                        //dm.cwdHash = cwdHash;
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
                        String searchId = "s1";
                        adapter = new DocumentAdapterr(SearchOpration.this,docList,"0","0",searchId,cId);
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

                    params.put("CRId", rCId);
                    params.put("cmd", "rename");
                    params.put("name", getName);
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
                    //DataGlobal.SaveLog(TAG, ex);
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
    public void prepareList(String directoryJSONDataString, StackState mStackState ) {

        //  Manage Response Stack

        if (mStackState == StackState.POP_IN)
            mResponseStack.add(directoryJSONDataString);
        else
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
        Log.e("TEST", "Stack Size = "+mResponseStack.size());
        for (int i=0; i<mResponseStack.size();i++) {
            Log.e("TEST", "Stack at " + i + " = " + mResponseStack.get(i));
        }
        if (directoryJSONDataString.equals(""))
            return;
        docList.clear();

        try {
            JSONObject object = new JSONObject(directoryJSONDataString);
            String cwHash = null, cwPhash = null ,getCWD;
            JSONArray companyArray = object.getJSONArray("files");
            JSONObject cwdObj = object.getJSONObject("cwd");
            String cwd = cwdObj.toString();
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

            recycler_view.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            //txt_selectDirName.setText(docList.get(0).name);
            for (int i=0; i<docList.size();i++) {
                //Log.e("TEST", "Dir Name = "+docList.get(1).name);
                //txt_selectDirName.setText(docList.get(docList.size()-1).name);
            }
            // Log.e("TEST", "========================");
        } catch (JSONException e) {
            Log.e("TEST","Exception :"+e);
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
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

}
