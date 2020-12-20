package com.easycloudbooks.easycloudbooks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.model.ContactDetails;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_CONTACT_PROJECT_FILTER;

public class ContactProjectFilter extends AppCompatActivity {

    private static final String TAG = ContactProjectFilter.class.getSimpleName();
    public CustomAuthRequest request;
    RecyclerView recycler_view;
    LinearLayout pbHeaderProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_project_filter);

        recycler_view = findViewById(R.id.recycler_view);
        pbHeaderProgress = findViewById(R.id.pbHeaderProgress);


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String userId = bundle.getString("Id");
            Log.e("TEST","Contact User Id :"+userId);
            getProjectList(userId);
        }
    }
    public void getProjectList(final String uId){
        Log.e("TEST","Contact Project Filter Id :"+uId);
        pbHeaderProgress.setVisibility(View.VISIBLE);
        request = new CustomAuthRequest(Request.Method.POST, METHOD_CONTACT_PROJECT_FILTER, null,0,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //showMessage(getText(R.string.label_empty_list).toString());
                        Log.w(TAG, response.toString() );
                       // pbHeaderProgress.setVisibility(View.VISIBLE);

                        String strResponse = response.toString();
                        if(strResponse != null){
                            try {
                                pbHeaderProgress.setVisibility(View.GONE);
                                JSONObject obj = new JSONObject(response.toString());

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
                pbHeaderProgress.setVisibility(View.VISIBLE);
                /*SnakebarCustom.danger(mContext, mView, "Unable to fetch Companies: " + error.getMessage(), 5000);
                swipeRefreshLayout.setRefreshing(false);*/
            }
        }) {

            @Override
            protected JSONObject getParams() {
                try {
                    JSONObject params = new JSONObject();
                    params.put("Id", uId);
                    JSONObject filterExpression = new JSONObject();

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
}
