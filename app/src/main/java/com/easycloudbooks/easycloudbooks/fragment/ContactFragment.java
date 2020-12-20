package com.easycloudbooks.easycloudbooks.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.ContectProfile;
import com.easycloudbooks.easycloudbooks.activity.MainActivity;
import com.easycloudbooks.easycloudbooks.adapter.Company.CompanyListAdapter;
import com.easycloudbooks.easycloudbooks.adapter.ContactAdapter;
import com.easycloudbooks.easycloudbooks.adapter.EndlessRecyclerViewScrollListener;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.NoteCustomDialog;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.common.SweetAlertCustom;
import com.easycloudbooks.easycloudbooks.model.CompanyRelationJ;
import com.easycloudbooks.easycloudbooks.model.ContactDetails;
import com.easycloudbooks.easycloudbooks.model.RoleJ;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_COMPANY_CONTACT_PROFILE;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_COMPANY_GET;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_CONTACT_SEARCH;

public class ContactFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = CompanyFragment.class.getSimpleName();

    TextView mMessage;
    private Context mContext;
    private SwipeRefreshLayout swipeRefreshLayout;
    public RecyclerView recycler_view;
    private Activity CurrentActivity;
    List<ContactDetails> contactList = new ArrayList<>();
    List<ContactDetails> tempContactList = new ArrayList<>();
    public View mView;
    ContactAdapter adapter;
    private AppCompatActivity activity;
    public CustomAuthRequest request;
    private LinearLayout pbHeaderProgress,search_edit_frame;
    public LinearLayoutManager mlinearLayoutManager;
    boolean flage = false;
    EditText edt_search;
    String contactId;
    private boolean isFirstApiCall=true;
    private boolean isLastPage = false;

    public ContactFragment() {
        // Required empty public constructor
    }
    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    public View GetView() {
        return mView;
    }
    public  long[] Filter_CRIds ;
    public boolean IsFiltered()
    {
        if(Filter_CRIds != null && Filter_CRIds.length > 0)
        {
            return  true;
        }
        return  false;
    }
    public void SetFilter(long[] CRIds) {
        Filter_CRIds = CRIds;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_contact, container, false);

        mView = rootView;
        //mListener.OnCompleteFragment(mView);
        CurrentActivity = this.getActivity();
        mContext = CurrentActivity.getApplicationContext();
        activity = ((AppCompatActivity) getActivity());
        mMessage = (TextView) rootView.findViewById(R.id.message);
        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        recycler_view = rootView.findViewById(R.id.recycler_view);
        pbHeaderProgress = rootView.findViewById(R.id.pbHeaderProgress);
        search_edit_frame = rootView.findViewById(R.id.search_edit_frame);
        edt_search = rootView.findViewById(R.id.edt_search);

        if(getArguments() != null){
            contactId = getArguments().getString("contactId");
            getActivity().setTitle("Contact");
            Log.e("TEST","Filter Contact Id :"+contactId);
        }
        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();
        rootView.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    getActivity().finish();
                    return true;
                }
                return false;
            }
        } );


//        RecyclerView.LayoutManager recyce = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
//        recycler_view.setLayoutManager(recyce);

        initpDialog();



        ((FloatingActionButton) rootView.findViewById(R.id.add_dir)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewContectDialog();
            }
        });


        mlinearLayoutManager = new LinearLayoutManager(CurrentActivity);
        recycler_view.setLayoutManager(mlinearLayoutManager);

        RecyclerView.OnScrollListener onScrollListener = new EndlessRecyclerViewScrollListener(mlinearLayoutManager) {
            @Override
            public int getFooterViewType(int defaultNoFooterViewType) {
                return 1;
            }
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                //Log.e("TEST","Contact Number of page :"+page);
                //loadNextDataFromApi(page);
            }

            @Override
            public void onScrolled(RecyclerView view, int dx, int dy) {
                super.onScrolled(view, dx, dy);

                int visisbleItemCount = recycler_view.getChildCount();
                int totalItemCount = mlinearLayoutManager.getItemCount();
                int firstVisibleItemPosn = mlinearLayoutManager.findFirstVisibleItemPosition();

                Log.e("TEST","visisbleItemCount :"+visisbleItemCount+" totalItemCount :"+totalItemCount+" firstVisibleItemPosn :"+firstVisibleItemPosn);
                if (!IsLoading && !isLastPage){
                    if ((visisbleItemCount + firstVisibleItemPosn) >= totalItemCount && firstVisibleItemPosn > 0){
                        Log.e("TEST", "Calling API");
                        loadNextDataFromApi(0);
                    }
                }
            }
        };
        recycler_view.addOnScrollListener(onScrollListener);
        recycler_view.setAdapter(adapter);
        recycler_view.setHasFixedSize(false);

        adapter = new ContactAdapter(ContactFragment.this,getContext(),contactList);
        recycler_view.setAdapter(adapter);
        getContactDetails(true, 0, CurrentSearctText);

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
                    ((ImageButton) rootView.findViewById(R.id.bt_clear)).setVisibility(View.VISIBLE);
                }
                if(s.length() >= 4){
                    final String getText = s.toString();
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            if (System.currentTimeMillis() - lastChange >= 300) {
                                //send request
                                searchContact(getText);
                            }
                        }
                    }, 300);
                    lastChange = System.currentTimeMillis();

                }else if(s.length() == 0){
                    adapter.resetList();
//                    getContactDetails(false, 0, CurrentSearctText);
                    ((ImageButton) rootView.findViewById(R.id.bt_clear)).setVisibility(View.GONE);
                    //tempContactList.clear();
                   /*FragmentTransaction ft = getFragmentManager().beginTransaction();
                   ft.detach(ContactFragment.this).attach(ContactFragment.this).commit();*/
                }
            }
        });

        ((ImageView) rootView.findViewById(R.id.bt_clear)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((EditText) rootView.findViewById(R.id.edt_search)).setText("");
                adapter.resetList();
//                getContactDetails(true, 0, CurrentSearctText);
                /*tempContactList.clear();
                FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(ContactFragment.this).attach(ContactFragment.this).commit();*/
            }
        });

        if(getActivity() instanceof MainActivity)
        {
            if (((MainActivity)getActivity()).searchMenu != null)
                ((MainActivity)getActivity()).searchMenu.setVisible(true);
        }

        return rootView;
    }

    private void searchContact(final String getText) {
            pbHeaderProgress.setVisibility(View.VISIBLE);
        contactList.clear();
            request = new CustomAuthRequest(Request.Method.POST, METHOD_CONTACT_SEARCH, null,0,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            if (App.getInstance().authorizeSimple(response)) {

                                String strResponse = response.toString();
                                if(strResponse != null){
                                    try {
                                        //contactList.clear();
                                        ContactDetails cdt = new ContactDetails();
                                        pbHeaderProgress.setVisibility(View.GONE);
                                        JSONObject obj = new JSONObject(response.toString());

                                        cdt.totalCount = Integer.parseInt(obj.getString("TotalCount"));
                                        JSONArray jsonArray = obj.getJSONArray("obj");

                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            ContactDetails cd = new ContactDetails();
                                            JSONObject userDetail = jsonArray.getJSONObject(i);
                                            cd.FN = userDetail.getString("FN");
                                            cd.MN = userDetail.getString("MN");
                                            cd.LN = userDetail.getString("LN");
                                            cd.contectId = Long.parseLong(userDetail.getString("Id"));
                                            cd.imageUrl = userDetail.getString("I");;

                                            contactList.add(cd);
                                        }
                                       /* adapter = new ContactAdapter(ContactFragment.this,getContext(),tempContactList);
                                        recycler_view.setAdapter(adapter);*/
                                        adapter.notifyDataSetChanged();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }else {
//                                pbHeaderProgress.setVisibility(View.VISIBLE);
                                    SnakebarCustom.danger(mContext, mView, "Unable to fetch contact: " + "No data found", 5000);
                                }
                            }
                        }
                    }, new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    pbHeaderProgress.setVisibility(View.VISIBLE);
                    SnakebarCustom.danger(mContext, mView, "Unable to fetch Companies: " + error.getMessage(), 5000);
                }
            }) {

                @Override
                protected JSONObject getParams() {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("text", getText);
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

    public void searchClick(){
        if(flage == false){
            search_edit_frame.setVisibility(View.VISIBLE);
            edt_search.requestFocus();
            InputMethodManager inputMethodManager =
                    (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(
                    edt_search.getApplicationWindowToken(),
                    InputMethodManager.SHOW_FORCED, 0);
            flage = true;
        }else if(flage == true){
            search_edit_frame.setVisibility(View.GONE);
            InputMethodManager inputMethodManager =
                    (InputMethodManager) activity.getSystemService(
                            Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
            flage = false;
        }
    }

    private void filter(String searchText) {
        adapter.getFilter().filter(searchText);
    }

    private void addNewContectDialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.dialog_add_contect);
        dialog.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;

        ((ImageView) dialog.findViewById(R.id.bt_photo)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Post Photo Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        dialog.show();
        dialog.getWindow().setAttributes(lp);

    }

    public boolean IsLoading = false;
    private int LastPage = 0;
    private String CurrentSearctText = "";
    private int pageExecute=1;

    public void loadNextDataFromApi(int offset) {
        if (!IsLoading && !isLastPage) {
            IsLoading = true;
            getContactDetails(true,pageExecute, CurrentSearctText);

            //LastPage = pageExecute;
            pageExecute++;
        }
        /*if (!IsLoading && LastPage != pageExecute) {
            IsLoading = true;
            getContactDetails(true,pageExecute, CurrentSearctText);

            LastPage = pageExecute;
            pageExecute++;
        }*/


    }

    public void getContactDetails(final boolean isAppend, final int offset, final String CurrentSearctText) {
        Log.e("TEST", "Get Contact");
        pbHeaderProgress.setVisibility(View.VISIBLE);
        if (!isAppend)
            contactList.clear();
        request = new CustomAuthRequest(Request.Method.POST, METHOD_COMPANY_CONTACT_PROFILE, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (App.getInstance().authorizeSimple(response)) {
                            IsLoading=false;
                            String strResponse = response.toString();
                            if(strResponse != null){
                                try {
                                    //contactList.clear();
                                    pbHeaderProgress.setVisibility(View.GONE);
                                    JSONObject obj = new JSONObject(response.toString());
                                    int count = Integer.parseInt(obj.getString("TotalCount"));
                                    App.CCount = count;
                                   if (getActivity() instanceof MainActivity)
                                    ((MainActivity)activity).setContactCount();

                                    JSONArray jsonArray = obj.getJSONArray("obj");

                                    if (jsonArray.length() == 0)
                                        isLastPage = true;

                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        ContactDetails cd = new ContactDetails();
                                        JSONObject userDetail = jsonArray.getJSONObject(i);
                                        cd.FN = userDetail.getString("FN");
                                        cd.MN = userDetail.getString("MN");
                                        cd.LN = userDetail.getString("LN");
                                        cd.imageUrl = userDetail.getString("I");
                                        cd.contectId = Long.parseLong(userDetail.getString("Id"));

                                        JSONArray emailArr = userDetail.getJSONArray("Es");
                                        for (int j = 0; j < emailArr.length(); j++) {
                                            JSONObject eObj = emailArr.getJSONObject(j);
                                            cd.email = eObj.getString("E");
                                        }
                                        JSONArray phArray = userDetail.getJSONArray("PHs");
                                        for (int k = 0; k < phArray.length(); k++) {
                                            JSONObject phObj = phArray.getJSONObject(k);
                                            cd.Ph = phObj.getString("Ph");
                                        }

                                        contactList.add(cd);
                                    }
                                    if (isFirstApiCall){
                                        adapter.saveTempList(contactList);
                                        isFirstApiCall=false;
                                    }
                                    adapter.notifyDataSetChanged();


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }else {
//                                pbHeaderProgress.setVisibility(View.VISIBLE);
                                SnakebarCustom.danger(mContext, mView, "Unable to fetch contact: " + "No data found", 5000);
                            }
                        }
                    }
                }, new Response.ErrorListener()

        {
            @Override
            public void onErrorResponse(VolleyError error) {
                pbHeaderProgress.setVisibility(View.VISIBLE);
                SnakebarCustom.danger(mContext, mView, "Unable to fetch Companies: " + error.getMessage(), 5000);
            }
        }) {

            @Override
            protected JSONObject getParams() {
                try {
                    if(contactId == null) {
                        JSONObject params = new JSONObject();
                        params.put("PageIndex", offset);
                        params.put("PageSize", 20);
                        params.put("filter", CurrentSearctText);
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
                    }else {
                        JSONObject params=new  JSONObject();
                        JSONObject filterType= new JSONObject();

                        filterType.put("CRId",Integer.parseInt(contactId));
                        params.put("filterExpression",filterType);

                        return params;
                    }
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
    public void onResume() {
        super.onResume();
        //getContactDetails(true,pageExecute, CurrentSearctText);
    }

    private SweetAlertCustom swc;
    protected void initpDialog() {
        if (swc == null)
            swc = new SweetAlertCustom(mContext);
        swc.CreatingLoadingDialog("Loading");
    }

    public void onBackPressed() {
         super.getActivity().onBackPressed();
        getActivity().finish();
    }

    @Override
    public void onRefresh() {
     //   getContactDetails(true,pageExecute, CurrentSearctText);
    }
}
