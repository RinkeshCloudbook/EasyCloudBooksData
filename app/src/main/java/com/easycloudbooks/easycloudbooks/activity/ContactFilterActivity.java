package com.easycloudbooks.easycloudbooks.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.adapter.ContactAdapter;
import com.easycloudbooks.easycloudbooks.adapter.ContactListAdapter;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.fragment.ContactFragment;
import com.easycloudbooks.easycloudbooks.model.ContactDetails;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_COMPANY_CONTACT;

public class ContactFilterActivity extends AppCompatActivity {

    public  CustomAuthRequest request;
    private Context mContext;
    private static String TAG = ContactFilterActivity.class.getSimpleName();
    List<ContactDetails> contactLists = new ArrayList<>();
    RecyclerView recycler_view;
private  ContactListAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_filter);

        recycler_view = findViewById(R.id.recycler_view);

        RecyclerView.LayoutManager recyce = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false);
        recycler_view.setLayoutManager(recyce);

        Bundle intentBundle = getIntent().getExtras();
        String CRIds = intentBundle.getString("CRIds");
        String cName = intentBundle.getString("cName");
        String crId = String.valueOf(CRIds);

        ((TextView) findViewById(R.id.txt_selectDirName)).setText(cName);
        ((ImageView) findViewById(R.id.img_back)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getCompanyContact(crId);
    }

    private void getCompanyContact(final String crIds) {
            request = new CustomAuthRequest(Request.Method.POST, METHOD_COMPANY_CONTACT, null,0,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            Log.w(TAG, response.toString() );
                            if (App.getInstance().authorizeSimple(response)) {
                                try {
                                    JSONObject obj = new JSONObject(response.toString());
                                    JSONObject object = obj.getJSONObject("obj");

                                    JSONArray jArr = object.getJSONArray("CRUs");
                                    for (int i = 0; i < jArr.length(); i++) {
                                        ContactDetails cd = new ContactDetails();
                                        cd.cName = object.getString("Name");
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
                                        contactLists.add(cd);
                                    }
                                    adapter = new ContactListAdapter(ContactFilterActivity.this,contactLists);
                                    recycler_view.setAdapter(adapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            } else {
                                try {
                                    if (response.getString("Message") != null) {
                                        // SnakebarCustom.danger(mContext, ContactFilterActivity.this, response.getString("Message"), 5000);
                                    }
                                } catch (JSONException ex) {
                                }
                            }
                        }
                    }, new Response.ErrorListener()

            {
                @Override
                public void onErrorResponse(VolleyError error) {
                    //SnakebarCustom.danger(mContext, mView, "Unable to fetch Companies: " + error.getMessage(), 5000);

                }
            }) {

                @Override
                protected JSONObject getParams() {
                    try {
                        JSONObject params = new JSONObject();
                        params.put("Id", crIds);
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
