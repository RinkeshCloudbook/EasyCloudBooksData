package com.easycloudbooks.easycloudbooks.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.annotation.CallSuper;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.ActionBarDrawerToggle ;
import  	androidx.appcompat.app.AppCompatActivity ;
import androidx.appcompat.view.ActionMode ;
import  	androidx.recyclerview.widget.LinearLayoutManager ;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import  	androidx.recyclerview.widget.ItemTouchHelper ;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.MainActivity;
import com.easycloudbooks.easycloudbooks.adapter.Project.ProjectListAdapter;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.app.ECB;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.NoteCustomDialog;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.common.SweetAlertCustom;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.adapter.EndlessRecyclerViewScrollListener;
import com.easycloudbooks.easycloudbooks.model.*;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


import static com.easycloudbooks.easycloudbooks.constants.Constants.*;

public class ProjectFragment extends BaseExampleFragment
		implements SwipeRefreshLayout.OnRefreshListener,
		ProjectListAdapter.MessageAdapterListener{

	private static final String TAG = ProjectFragment.class.getSimpleName();

	TextView mMessage;
	private Context mContext;
	private SwipeRefreshLayout swipeRefreshLayout;
	public RecyclerView mSearchResultsList;
	private ProjectListAdapter mSearchResultsAdapter;
	private Activity CurrentActivity;
	ArrayList<ProjectJ> list;
	private Paint p = new Paint();
	public View mView;
	private AppCompatActivity activity;
	String contactId;

	public static ProjectFragment newInstance() {
		return new ProjectFragment();
	}

	public View GetView() {
		return mView;
	}


	public  String Filter_NFId ;
	public  long[] Filter_CRIds ;
	public  long[] Filter_ProjectIds ;
	public void SetFilterCRId(long[] CRIds) {
		Filter_CRIds = CRIds;
	}
	public void SetFilterProjectId(long[] ProjectIds) {

		Filter_ProjectIds = ProjectIds;
		Log.e("TEST","Filter_ProjectIds :"+Filter_ProjectIds);
		Log.w(TAG, "SetFilterProjectId: ProjectIds"+ProjectIds.length );
	}
	public void SetFilterNFId(String NFId) {

		Filter_NFId = NFId;
		Log.w(TAG, "SetFilterNFId: "+NFId );
	}
	public boolean IsFiltered()
	{
		if(IsProjectFiltered() || IsCompanyFiltered() || IsNFIdFiltered())
		{
			return  true;
		}
		return  false;
	}
	public boolean IsNFIdFiltered()
	{
		if(Filter_NFId != null && Filter_NFId.length() > 0)
		{
			return  true;
		}
		return  false;
	}
	public boolean IsProjectFiltered()
	{
		if(Filter_ProjectIds != null && Filter_ProjectIds.length > 0)
		{
			return  true;
		}
		return  false;
	}
	public boolean IsCompanyFiltered()
	{
		if(Filter_CRIds != null && Filter_CRIds.length > 0)
		{
			return  true;
		}
		return  false;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_project, container, false);
		mView = rootView;
		Log.e("TEST","Project Fragment");

		if(getArguments() != null){
			contactId = getArguments().getString("params");
			Log.e("TEST","Project UserId :"+contactId);

			//	filtergetcompanies(contactId);
		}
		//mListener.OnCompleteFragment(mView);
		CurrentActivity = this.getActivity();
		mContext = CurrentActivity.getApplicationContext();
		activity = ((AppCompatActivity) getActivity());
		mMessage = (TextView) rootView.findViewById(R.id.message);
		showMessage(getText(R.string.label_empty_list).toString());



		initpDialog();
		return rootView;
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
	private ProjectFragment _this;
	private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
	public LinearLayoutManager mlinearLayoutManager;
	public FButton fab;
	public  TextView search_card_title;
	LinearLayout search_edit_frame;

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		_this = this;
		super.onViewCreated(view, savedInstanceState);
		//mSearchResultsList = (RecyclerView) view.findViewById(R.id.recycler_view);
		search_edit_frame = (LinearLayout) view.findViewById(R.id.search_edit_frame);
		search_card_title = (TextView) view.findViewById(R.id.search_card_title);
		if(IsProjectFiltered())
		{
			search_card_title.setText("Projects Filter Applied");
			search_edit_frame.setVisibility(View.VISIBLE);
		}
		else if(IsNFIdFiltered())
		{
			search_card_title.setText("Notification Filter Applied");
			search_edit_frame.setVisibility(View.VISIBLE);
		}
		else if(IsCompanyFiltered())
		{
			search_card_title.setText("Company Filter Applied");
			search_edit_frame.setVisibility(View.VISIBLE);
		}
		else{
			search_edit_frame.setVisibility(View.GONE);
		}
		list = new ArrayList<ProjectJ>();
		mToolbar = (Toolbar) CurrentActivity.findViewById(R.id.projecttoolbar);
		if (mToolbar != null) {
			mToolbar.setTitle("Projects");
			mToolbar.setNavigationContentDescription("Projects");
			((MainActivity) CurrentActivity).setSupportActionBar(mToolbar);
		}


		final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;

		mlinearLayoutManager = new LinearLayoutManager(CurrentActivity);


		mSearchResultsList = (RecyclerView) CurrentActivity.findViewById(R.id.recycler_view);
		//mlinearLayoutManager = (LinearLayoutManager) mSearchResultsList.getLayoutManager();

		swipeRefreshLayout = (SwipeRefreshLayout) CurrentActivity.findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setOnRefreshListener(this);

		mSearchResultsAdapter = new ProjectListAdapter(this.getContext(), list, this,  _this, activity, roleJ);

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
				//Log.w(TAG, "onLoadMore: "+totalItemsCount );
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
						getProjects(true, 0, CurrentSearctText);
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
		//Log.w(TAG, "loadNextDataFromApi: "+offset + "-"+ IsLoading);
		if (!IsLoading && LastPage != offset) {
			IsLoading = true;
			LastPage = offset;
			//Log.w(TAG, "loadNextDataFromApi1: "+LastPage );
			getProjects(true, offset, CurrentSearctText);
		}
	}

	private int LastPage = 0;
	private int TotalCount = 0;

	private List<Long> ExistingProjects = new ArrayList<Long>();
	public static RoleJ roleJ;
	public  CustomAuthRequest request;

	private void getProjects(final boolean isAppend, final int offset, final String CurrentSearctText) {
		swipeRefreshLayout.setRefreshing(true);
		if (!isAppend){
			LastPage = 0;
			TotalCount = 0;
			ExistingProjects.clear();
		}

		request =  new CustomAuthRequest(Request.Method.POST, METHOD_PROJECT_GET, null,0,
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
								List<ProjectJ> projectList =ProjectJ.getJSONList(response.getString("obj"));
								TotalCount =(int)response.getDouble("TotalCount");

								JSONObject object = new JSONObject(response.toString());
								JSONArray jArr = object.getJSONArray("obj");
								for (int i = 0; i < jArr.length(); i++) {
									ProjectJ pj = new ProjectJ();
									JSONObject obj = jArr.getJSONObject(i);

									JSONObject objPMR = obj.getJSONObject("PMR");
									//String role = objPMR.getString("Role");
									//Log.e("TEST","Role :"+role);
									JSONObject objUser = objPMR.getJSONObject("user");
									String uId = objUser.getString("Id");
								}
								if (!isAppend)
									mSearchResultsAdapter.ClearData();
								if (projectList != null) {
									for (ProjectJ message : projectList) {
										message.setColor(getRandomMaterialColor("400"));
										message.isExpanded = false;
										if (ExistingProjects.indexOf(message.ProjectId) == -1) {
											ExistingProjects.add(message.ProjectId);
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
								SnakebarCustom.danger(mContext, mView, "Unable to fetch Projects", 5000);
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
				SnakebarCustom.danger(mContext, mView, "Unable to fetch Projects: " + error.getMessage(), 5000);
				swipeRefreshLayout.setRefreshing(false);
				IsLoading = false;
			}
		}) {

			@Override
			protected JSONObject getParams() {
				try {
					if(contactId == null){
						Log.e("TEST","Param C Id :"+contactId);
						JSONObject params = new JSONObject();
						params.put("PageIndex", offset);
						params.put("PageSize", 20);
						params.put("filter", CurrentSearctText);
						String Status = "All";
						JSONObject filterExpression = new JSONObject();
						try {
							filterExpression.put("Status","All");
							if(Filter_CRIds != null)
							{
								JSONObject CRIds = new JSONObject();
//							for (Long CRId: Filter_CRIds) {
//								CRIds.put(CRId);
//							}
								if (Filter_CRIds.length > 0) {
									Status = "filtered";
									filterExpression.put("CRId", Filter_CRIds[0]);
								}
//							}
							}
							if(Filter_ProjectIds != null)
							{
								JSONArray Ids = new JSONArray();
								for (Long CRId: Filter_ProjectIds) {
									Ids.put(CRId);
								}
								Log.w(TAG, "Filter_ProjectIds: "+Ids.length ());
								if(Ids.length() > 0)
								{
									Status = "filtered";
									filterExpression.put("ProjectIds",Ids);
								}
							}
							if(Filter_NFId != null && Filter_NFId.length() > 0)
							{
								Status = "filtered";
								filterExpression.put("NFId",Filter_NFId);
							}
							if(Status.equals("filtered"))
							{
								filterExpression.put("Status",Status);
							}
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						params.put("filterExpression", filterExpression);
						return params;
					}else {
						Log.e("TEST","Param ELSE Id :"+contactId);
						JSONObject params = new JSONObject();
						params.put("PageIndex", offset);
						params.put("PageSize", 20);
						params.put("filter", CurrentSearctText);
						String Status = "All";
						JSONObject filterExpression = new JSONObject();
						Status = "filtered";
						filterExpression.put("ContactUserId", contactId);
						try {
							filterExpression.put("Status","All");
							/*if(Filter_CRIds != null)
							{
								JSONObject CRIds = new JSONObject();
//							for (Long CRId: Filter_CRIds) {
//								CRIds.put(CRId);
//							}
								if (Filter_CRIds.length > 0) {
									Status = "filtered";
									filterExpression.put("ContactUserId", contactId);
								}
//							}
							}
							if(Filter_ProjectIds != null)
							{
								JSONArray Ids = new JSONArray();
								for (Long CRId: Filter_ProjectIds) {
									Ids.put(CRId);
								}
								Log.w(TAG, "Filter_ProjectIds: "+Ids.length ());
								if(Ids.length() > 0)
								{
									Status = "filtered";
									filterExpression.put("ProjectIds",Ids);
								}
							}
							if(Filter_NFId != null && Filter_NFId.length() > 0)
							{
								Status = "filtered";
								filterExpression.put("NFId",Filter_NFId);
							}
							if (Filter_CRIds.length > 0) {
								Status = "filtered";
								filterExpression.put("ContactUserId", contactId);
							}
							if(Status.equals("filtered"))
							{
								filterExpression.put("Status",Status);
							}*/
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						params.put("filterExpression", filterExpression);
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
		getProjects(false, 0, CurrentSearctText);
	}

	@Override
	public void onContactDeleted(int position) {
		mSearchResultsList.removeViewAt(position);
		//mWrappedAdapter.notifyItemRemoved(position);
	}


	@Override
	public void onMessageRowClicked(int position, ProjectListAdapter.ViewHolder holder) {
		// verify whether action mode is enabled or not
		// if enabled, change the row state to activated
		if (mSearchResultsAdapter.getSelectedItemCount() > 0) {
			//enableActionMode(position, holder);
		} else {
			// read the message which removes bold from the row
			ProjectJ message = mSearchResultsAdapter.getItem(position);
			message.setRead(true);
			mSearchResultsAdapter.set(position, message);
			mSearchResultsAdapter.notifyDataSetChanged();

			Toast.makeText(mContext, "Read: " + message.Title, Toast.LENGTH_SHORT).show();
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
		/*if (requestCode == CONTACT_NEW_USER && resultCode == getActivity().RESULT_OK && null != data) {
			String Message = data.getStringExtra("ReturnMessage");
			ProjectJ saveduser = data.getParcelableExtra("ProjectJ");
			ContactProfileActivity.navigate(activity, null, saveduser, Message);
		} else*/
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
