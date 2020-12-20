package com.easycloudbooks.easycloudbooks.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.easycloudbooks.easycloudbooks.activity.MainActivity;
import com.easycloudbooks.easycloudbooks.adapter.ContactAdapter;
import com.easycloudbooks.easycloudbooks.adapter.EndlessRecyclerViewScrollListener;
import com.easycloudbooks.easycloudbooks.adapter.Company.CompanyListAdapter;
import com.easycloudbooks.easycloudbooks.adapter.GetCompanyAdapter;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.NoteCustomDialog;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.common.SweetAlertCustom;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.model.CompanyRelationJ;
import com.easycloudbooks.easycloudbooks.model.ContactDetails;
import com.easycloudbooks.easycloudbooks.model.RoleJ;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_COMPANY_GET;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_COMPANY_SEARCH;

public class CompanyFragment extends BaseExampleFragment
		implements SwipeRefreshLayout.OnRefreshListener,
		CompanyListAdapter.AdapterListener{

	private static final String TAG = CompanyFragment.class.getSimpleName();

	LinearLayout search_edit_frame;
	TextView mMessage;
	private Context mContext;
	private SwipeRefreshLayout swipeRefreshLayout;
	public RecyclerView mSearchResultsList,company_recycler_view;
	private CompanyListAdapter mSearchResultsAdapter;
	private Activity CurrentActivity;
	ArrayList<CompanyRelationJ> list;
	ArrayList<CompanyRelationJ> tempContactList = new ArrayList<>();

	private Paint p = new Paint();
	public View mView;
	private AppCompatActivity activity;
	String contactId;
	EditText edt_search;
	boolean flage = false;

	public static CompanyFragment newInstance() {
		return new CompanyFragment();
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
		final View rootView = inflater.inflate(R.layout.fragment_company, container, false);
		mView = rootView;
		if(getArguments() != null){
			contactId = getArguments().getString("params");
		//	filtergetcompanies(contactId);
		}

		//mListener.OnCompleteFragment(mView);
		CurrentActivity = this.getActivity();
		mContext = CurrentActivity.getApplicationContext();
		activity = ((AppCompatActivity) getActivity());
		mMessage = (TextView) rootView.findViewById(R.id.message);
        edt_search = rootView.findViewById(R.id.edt_search);

		/*showMessage(getText(R.string.label_empty_list).toString());*/


        edt_search.addTextChangedListener(new TextWatcher() {
        	long lastChange=0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(final Editable s) {
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
					getcompanies(false, 0, CurrentSearctText);
					((ImageButton) rootView.findViewById(R.id.bt_clear)).setVisibility(View.GONE);

					//tempContactList.clear();
                   /*FragmentTransaction ft = getFragmentManager().beginTransaction();
                   ft.detach(ContactFragment.this).attach(ContactFragment.this).commit();*/
				}
                //filter(s.toString());
            }
        });

		if (getActivity() instanceof MainActivity)
		((MainActivity)getActivity()).searchMenu.setVisible(true);

		initpDialog();

		return rootView;
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
      //  mSearchResultsAdapter.getFilter().filter(searchText);
    }

    public static interface OnCompleteFragment {
		public abstract void OnCompleteFragment(View mView);
	}

	private OnCompleteFragment mListener;

	public static interface SetDrawer {
		public abstract void SetDrawer(Toolbar toolbar, RelativeLayout mView);
	}

	private SetDrawer mDrawer;

	public void onAttach(Context context) {
		super.onAttach(context);
		Activity activity;

		if (context instanceof Activity) {

			activity = (Activity) context;

			/*try {
				this.mListener = (OnCompleteFragment) activity;
				this.mDrawer = (SetDrawer) activity;
			} catch (final ClassCastException e) {
				throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
			}*/
		}
	}

	protected ActionBarDrawerToggle mActionBarDrawerToggle = null;
	private Toolbar mToolbar;
	private CompanyFragment _this;
	private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
	public LinearLayoutManager mlinearLayoutManager;
	public FButton fab;
	public  TextView search_card_title;



	@Override
	public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
		_this = this;
		super.onViewCreated(view, savedInstanceState);
		//mSearchResultsList = (RecyclerView) view.findViewById(R.id.recycler_view);

		search_edit_frame = (LinearLayout) view.findViewById(R.id.search_edit_frame);
		search_card_title = (TextView) view.findViewById(R.id.search_card_title);
		/*if(IsFiltered())
		{
			search_card_title.setText("Filters Applied");
			search_edit_frame.setVisibility(View.VISIBLE);
		}
		else{
			search_edit_frame.setVisibility(View.VISIBLE);
		}
*/
		((ImageView) view.findViewById(R.id.bt_clear)).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((EditText) view.findViewById(R.id.edt_search)).setText("");
				getcompanies(true, 0, CurrentSearctText);
			}
		});

		list = new ArrayList<CompanyRelationJ>();
		mToolbar = (Toolbar) CurrentActivity.findViewById(R.id.companytoolbar);
		if (mToolbar != null) {
			mToolbar.setTitle("Companies");
			mToolbar.setNavigationContentDescription("Companies");
			((MainActivity) CurrentActivity).setSupportActionBar(mToolbar);
		}


		final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;

		mlinearLayoutManager = new LinearLayoutManager(CurrentActivity);


		mSearchResultsList = (RecyclerView) CurrentActivity.findViewById(R.id.recycler_view);
		company_recycler_view = (RecyclerView) CurrentActivity.findViewById(R.id.company_recycler_view);
		//mlinearLayoutManager = (LinearLayoutManager) mSearchResultsList.getLayoutManager();

		swipeRefreshLayout = (SwipeRefreshLayout) CurrentActivity.findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setOnRefreshListener(this);

		mSearchResultsAdapter = new CompanyListAdapter(this.getContext(), list, this,  _this, activity, roleJ);

		//mWrappedAdapter = linearLayoutManager.createWrappedAdapter(mSearchResultsAdapter);
		RecyclerView.OnScrollListener onScrollListener = new EndlessRecyclerViewScrollListener(mlinearLayoutManager) {
			@Override
			public int getFooterViewType(int defaultNoFooterViewType) {
				return 1;
			}

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				// Triggered only when new data needs to be appended to the list
				// Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(page);

				// or loadNextDataFromApi(totalItemsCount);
				// return true;
			}
		};
		mSearchResultsList.addOnScrollListener(onScrollListener);
		//mSearchResultsList.setAdapter(mWrappedAdapter);
		mSearchResultsList.setAdapter(mSearchResultsAdapter);
		mSearchResultsList.setLayoutManager(mlinearLayoutManager);
		mSearchResultsList.setHasFixedSize(false);

		//mlinearLayoutManager.attachView(mSearchResultsList);
		Bundle extras = CurrentActivity.getIntent().getExtras();
		if (extras != null) {
			CurrentSearctText = extras.getString(EXTRA_KEY_TEXT);
		}
		// show loader and fetch messages
		swipeRefreshLayout.post(
				new Runnable() {
					@Override
					public void run() {
							getcompanies(true, 0, CurrentSearctText);
					}
				}
		);
	}

	private String CurrentSearctText = "";

	public void setRole() {
		mSearchResultsAdapter.SetRole(roleJ);

	}

	public LinearLayoutManager getlinearLayoutManager() {
		return mlinearLayoutManager;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
										   String permissions[], int[] grantResults) {
		switch (requestCode) {
			case Config.MY_PERMISSIONS_REQUEST_CALL_PHONE: {
				// If request is cancelled, the result arrays are empty.
				if (grantResults.length > 0
						&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					mSearchResultsAdapter.CallPhoneNumber();
					SnakebarCustom.danger(mContext, _this.getView(), "Call Permission Required", 1000);
				} else {
					SnakebarCustom.danger(mContext, _this.getView(), "Call Permission Required", 1000);
				}
				return;
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	private RecyclerView.Adapter mWrappedAdapter;

	@Override
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

		if (mWrappedAdapter != null) {
			mWrappedAdapter = null;
		}
		mlinearLayoutManager = null;

		super.onDestroyView();
	}


	private void adjustScrollPositionOnGroupExpanded(int groupPosition) {
		int childItemHeight = getActivity().getResources().getDimensionPixelSize(R.dimen.list_item_height);
		int topMargin = (int) (getActivity().getResources().getDisplayMetrics().density * 16); // top-spacing: 16dp
		int bottomMargin = topMargin; // bottom-spacing: 16dp

		mlinearLayoutManager.scrollToPosition(groupPosition);
	}

	private boolean supportsViewElevation() {
		return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
	}

	protected static final String EXTRA_KEY_TEXT = "Searchtext";
	private static final String EXTRA_KEY_VERSION = "version";
	private static final String EXTRA_KEY_THEME = "theme";
	private static final String EXTRA_KEY_RELOAD = "reload";
	private static final String EXTRA_KEY_VERSION_MARGINS = "version_margins";



	@Override
	public boolean onActivityBackPress() {
		return true;
	}



	public boolean IsLoading = false;

	// Append the next page of data into the adapter
	// This method probably sends out a network request and appends new data items to your adapter.
	public void loadNextDataFromApi(int offset) {
		// Send an API request to retrieve appropriate paginated data
		//  --> Send the request including an offset value (i.e `page`) as a query parameter.
		//  --> Deserialize and construct new model objects from the API response
		//  --> Append the new data objects to the existing set of items inside the array of items
		//  --> Notify the adapter of the new items made with `notifyDataSetChanged()`
		if (!IsLoading && LastPage != offset) {
			IsLoading = true;
			LastPage = offset;
                getcompanies(true, offset, CurrentSearctText);
		}
	}

	private int LastPage = 0;

	private List<Long> ExistingCompanies = new ArrayList<Long>();
	public static RoleJ roleJ;

	public  CustomAuthRequest request;

	private void getcompanies(final boolean isAppend, final int offset, final String CurrentSearctText) {


		swipeRefreshLayout.setRefreshing(true);
		if (!isAppend)
			ExistingCompanies.clear();

		request = new CustomAuthRequest(Request.Method.POST, METHOD_COMPANY_GET, null,0,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						showMessage(getText(R.string.label_empty_list).toString());
						Log.w(TAG, response.toString() );
						if (App.getInstance().authorizeSimple(response)) {
							try {
								//JSONObject jsonDataRole = response.getJSONObject("obj1");
								roleJ = new RoleJ();
								setRole();
								List<CompanyRelationJ> CRList =CompanyRelationJ.getJSONList(response.getString("obj"));

								if (!isAppend)
									mSearchResultsAdapter.ClearData();

                                    if (CRList != null) {
                                        for (CompanyRelationJ message : CRList) {
                                            message.setColor(getRandomMaterialColor("400"));
                                            message.isExpanded = false;
                                            if (ExistingCompanies.indexOf(message.CRId) == -1) {
                                                ExistingCompanies.add(message.CRId);
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
								SnakebarCustom.danger(mContext, mView, "Unable to fetch Companies", 5000);
							}
							swipeRefreshLayout.setRefreshing(false);
							IsLoading = false;
						} else {
							swipeRefreshLayout.setRefreshing(false);
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
				swipeRefreshLayout.setRefreshing(false);
				IsLoading = false;
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

                        filterType.put("UserId",Integer.parseInt(contactId));
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

	private void searchContact(final String getText) {
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

									//list.clear();
									mSearchResultsAdapter.notifyDataSetChanged();
									//CompanyListAdapter
									/*GetCompanyAdapter adapter = new GetCompanyAdapter(CompanyFragment.this,getContext(),tempContactList);
									mSearchResultsList.setAdapter(adapter);*/

									//adapter.notifyDataSetChanged();

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
				//pbHeaderProgress.setVisibility(View.VISIBLE);
				SnakebarCustom.danger(mContext, mView, "Unable to fetch Companies: " + error.getMessage(), 5000);
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

	@Override
	public void onResume() {
		super.onResume();
		getcompanies(true, 00, CurrentSearctText);
	}
	/**
	 * chooses a random color from array.xml
	 */

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

	@Override
	public void onRefresh() {
		// swipe refresh is performed, fetch the messages again
		getcompanies(false, 0, CurrentSearctText);
	}

	@Override
	public void onMessageRowClicked(int position, CompanyListAdapter.ViewHolder holder) {
		// verify whether action mode is enabled or not
		// if enabled, change the row state to activated
		if (mSearchResultsAdapter.getSelectedItemCount() > 0) {
			//enableActionMode(position, holder);
		} else {
			// read the message which removes bold from the row
			CompanyRelationJ message = mSearchResultsAdapter.getItem(position);
			message.setRead(true);
			mSearchResultsAdapter.set(position, message);
			mSearchResultsAdapter.notifyDataSetChanged();

			Toast.makeText(mContext, "Read: " + message.Name, Toast.LENGTH_SHORT).show();
		}
	}




	// deleting the messages from recycler view
	private void deleteMessages() {
		mSearchResultsAdapter.resetAnimationIndex();
		List<Integer> selectedItemPositions = mSearchResultsAdapter.getSelectedItems();
		for (int i = selectedItemPositions.size() - 1; i >= 0; i--) {
			mSearchResultsAdapter.removeData(selectedItemPositions.get(i));
		}
		mSearchResultsAdapter.notifyDataSetChanged();
	}

	private SweetAlertCustom swc;

	protected void initpDialog() {
		if (swc == null)
			swc = new SweetAlertCustom(mContext);
		swc.CreatingLoadingDialog("Loading");
	}

	protected void showpDialog() {
		loading = true;
		swc.ShowLoading();
	}

	boolean loading = false;

	protected void hidepDialog() {
		loading = false;
		swc.HideLoading();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
			if (requestCode == Config.DIALOG_PHONE_NUMBER_CHOOSE) {
			String Phone = data.getStringExtra("Phone");
			if (!Phone.equals("CANCEL"))
				mSearchResultsAdapter.DialNumber(Phone);
		}
	}

	public void showMessage(String message) {

		mMessage.setText(message);
		mMessage.setVisibility(View.VISIBLE);
	}

	public void hideMessage() {

		mMessage.setVisibility(View.GONE);
	}
}
