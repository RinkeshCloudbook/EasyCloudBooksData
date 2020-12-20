package com.easycloudbooks.easycloudbooks.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.CustomEvent;
import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.activity.CompanyFilterActivity;
import com.easycloudbooks.easycloudbooks.activity.MainActivity;
import com.easycloudbooks.easycloudbooks.activity.ProjectFilterActivity;
import com.easycloudbooks.easycloudbooks.adapter.EndlessRecyclerViewScrollListener;
import com.easycloudbooks.easycloudbooks.adapter.Notification.NotificationListAdapter;
import com.easycloudbooks.easycloudbooks.app.App;
import com.easycloudbooks.easycloudbooks.app.Config;
import com.easycloudbooks.easycloudbooks.common.DataGlobal;
import com.easycloudbooks.easycloudbooks.common.SnakebarCustom;
import com.easycloudbooks.easycloudbooks.common.SweetAlertCustom;
import com.easycloudbooks.easycloudbooks.drawable.SwipeHelper;
import com.easycloudbooks.easycloudbooks.font.FButton;
import com.easycloudbooks.easycloudbooks.model.*;
import com.easycloudbooks.easycloudbooks.model.RoleJ;
import com.easycloudbooks.easycloudbooks.util.CustomAuthRequest;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_COMPANY_CONTACT_PROFILE;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_NOTIFICATIONS_GET;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_NOTIFICATIONS_Remove;
import static com.easycloudbooks.easycloudbooks.constants.Constants.METHOD_NOTIFICATIONS_acknowledge;

public class NotificationFragment extends BaseExampleFragment
		implements SwipeRefreshLayout.OnRefreshListener,
		NotificationListAdapter.MessageAdapterListener {

	private static final String TAG = NotificationFragment.class.getSimpleName();

	TextView mMessage;
	private Context mContext;
	private SwipeRefreshLayout swipeRefreshLayout;
	public RecyclerView mSearchResultsList;
	private NotificationListAdapter mSearchResultsAdapter;
	private Activity CurrentActivity;
	ArrayList<UserNotificationJ> list;
	private Paint p = new Paint();
	public View mView;
	private AppCompatActivity activity;
	private Bundle intentBundle ;
	private  String NFId ;

	public void SetFilterBundle(Bundle bundle) {

		intentBundle = bundle;

		String nfid = intentBundle.getString("nfid");
		Log.w(TAG, "onCreate nfid:-"+nfid );
		if(intentBundle != null) {
			NFId = intentBundle.getString("nfid");
		}
		Log.w(TAG, "SetFilterId: Filter_nfids " );
	}
	public static ProjectFragment newInstance() {
		return new ProjectFragment();
	}

	public View GetView() {
		return mView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
		mView = rootView;
		intentBundle = getActivity().getIntent().getExtras();
		if (intentBundle != null) {

		}
		//mListener.OnCompleteFragment(mView);
		CurrentActivity = this.getActivity();
		mContext = CurrentActivity.getApplicationContext();
		activity = ((AppCompatActivity) getActivity());
		mMessage = (TextView) rootView.findViewById(R.id.message);
		showMessage(getText(R.string.label_empty_list).toString());
		initpDialog();
		if (((MainActivity)getActivity()).searchMenu!=null)
        ((MainActivity)getActivity()).searchMenu.setVisible(false);

				getContactCount();



		return rootView;
	}

	public static interface OnCompleteFragment {
		public abstract void OnCompleteFragment(View mView);
	}

	private NotificationFragment.OnCompleteFragment mListener;

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
	private NotificationFragment _this;
	private static final String SAVED_STATE_EXPANDABLE_ITEM_MANAGER = "RecyclerViewExpandableItemManager";
	public LinearLayoutManager mlinearLayoutManager;
	public FButton fab;


	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		_this = this;
		super.onViewCreated(view, savedInstanceState);
		//mSearchResultsList = (RecyclerView) view.findViewById(R.id.recycler_view);

		list = new ArrayList<UserNotificationJ>();
		mToolbar = (Toolbar) CurrentActivity.findViewById(R.id.projecttoolbar);
		if (mToolbar != null) {
			mToolbar.setTitle("Projects");
			mToolbar.setNavigationContentDescription("Projects");
			((MainActivity) CurrentActivity).setSupportActionBar(mToolbar);
		}
		Answers.getInstance().logCustom(new CustomEvent("Notification"));
		final Parcelable eimSavedState = (savedInstanceState != null) ? savedInstanceState.getParcelable(SAVED_STATE_EXPANDABLE_ITEM_MANAGER) : null;

		mlinearLayoutManager = new LinearLayoutManager(CurrentActivity);


		mSearchResultsList = (RecyclerView) CurrentActivity.findViewById(R.id.recycler_view);
		//mlinearLayoutManager = (LinearLayoutManager) mSearchResultsList.getLayoutManager();

		swipeRefreshLayout = (SwipeRefreshLayout) CurrentActivity.findViewById(R.id.swipe_refresh_layout);
		swipeRefreshLayout.setOnRefreshListener(this);

		mSearchResultsAdapter = new NotificationListAdapter(this.getContext(), list, this, _this, activity, roleJ);

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
						getClientNotifications(true, 0, CurrentSearctText);
					}
				}
		);
		enableSwipe();



		LinearLayout search_edit_frame = (LinearLayout) CurrentActivity.findViewById(R.id.search_edit_frame);
		search_edit_frame.setVisibility(View.GONE);


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
	public void onDestroy() {
		//Log.w(TAG, "onDestroy: Notification" );
		super.onDestroy();
	}
	@Override
	public void onDestroyView() {
		//Log.w(TAG, "onDestroyView: " + (request == null ? "null": "not-null") );
		if(request != null) {
			request.destroy();
		}
		request = null;

		if(RemoveNotification != null)
		{
			RemoveNotification.destroy();
		}
		RemoveNotification = null;
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
			getClientNotifications(true, offset, CurrentSearctText);


		}
	}

	private void enableSwipe(){

		SwipeHelper swipeHelper = new SwipeHelper(this.mContext, mSearchResultsList) {

			@Override
			public void instantiateUnderlayButton(final RecyclerView.ViewHolder viewHolder, List<UnderlayButton> underlayButtons,int Direction,final int position) {

				final SwipeHelper _this = this;
				 Context context = getContext();
				if(Direction == ItemTouchHelper.RIGHT) {
					underlayButtons.add(new SwipeHelper.UnderlayButton(
							context,
							"Mark as Read",
							ItemTouchHelper.RIGHT,
							0,
							ResourcesCompat.getColor(mContext.getResources(), R.color.theme_danger, null),
							new SwipeHelper.UnderlayButtonClickListener() {
								@Override
								public void onClick(MotionEvent e,int pos) {
									mSearchResultsAdapter.removeData(pos);
									// showing snack bar with Undo option
									Snackbar snackbar = Snackbar.make(getActivity().getWindow().getDecorView().getRootView(), " Notification Removed!", Snackbar.LENGTH_LONG);

									snackbar.show();
								}
							}
					));

				}
				else{

					underlayButtons.add(new SwipeHelper.UnderlayButton(
							context,
							"Chat",
							ItemTouchHelper.LEFT,
							0,
							ResourcesCompat.getColor(mContext.getResources(), R.color.theme_info, null),
							new SwipeHelper.UnderlayButtonClickListener() {
								@Override
								public void onClick(MotionEvent e,int pos) {
									Context context = getContext();
									mSearchResultsAdapter.itemClicked(pos);
									swc.NormalMessage(context,"Coming Soon");
								}
							}
					));
					final UserNotificationJ CN = mSearchResultsAdapter.getItem(position);
					Log.w(TAG, CN.toString() );
					if(CN != null)
					{
						if(CN.DT.equals("Project")) {
							underlayButtons.add(new SwipeHelper.UnderlayButton(
									context,
									"Project",
									ItemTouchHelper.LEFT,
									0,
									ResourcesCompat.getColor(mContext.getResources(), R.color.theme_success, null),
									new SwipeHelper.UnderlayButtonClickListener() {
										@Override
										public void onClick(MotionEvent e, int pos) {
											mSearchResultsAdapter.itemClicked(pos);
											Intent i = new Intent(CurrentActivity, ProjectFilterActivity.class);
											i.putExtra("nfid", CN.Id);
											i.putExtra("acid", CN.ACId);
											CurrentActivity.startActivity(i);
										}
									}
							));
						}



						final long CRId = CN.GetCompanyRelationId();
						if(CRId > 0)
						{
							underlayButtons.add(new SwipeHelper.UnderlayButton(
									context,
									"Company",
									ItemTouchHelper.LEFT,
									0,
									ResourcesCompat.getColor(mContext.getResources(), R.color.theme_warning, null),
									new SwipeHelper.UnderlayButtonClickListener() {
										@Override
										public void onClick(MotionEvent e,int pos) {
											mSearchResultsAdapter.itemClicked(pos);
											Intent i = new Intent(CurrentActivity, CompanyFilterActivity.class);
											i.putExtra("CRIds",new long[]{CRId});
											i.putExtra("acid",CN.ACId);
											CurrentActivity.startActivity(i);
										}
									}
							));
						}
					}


				}
			}
		};


	}

	private int LastPage = 0;
	private  int TotalCount = 0;

	private List<String> ExistingClientNotifications = new ArrayList<String>();
	public static RoleJ roleJ;
	private CustomAuthRequest request;
	private void getClientNotifications(final boolean isAppend, final int offset, final String CurrentSearctText) {
		Log.e("TEST","Notification");
		swipeRefreshLayout.setRefreshing(true);
		if (!isAppend) {
			LastPage = 0;
			TotalCount = 0;
			ExistingClientNotifications.clear();
		}
		request = new CustomAuthRequest(Request.Method.POST, METHOD_NOTIFICATIONS_GET, null, 0,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.w(TAG, response.toString());
						showMessage(getText(R.string.label_empty_list).toString());
						if (App.getInstance().authorizeSimple(response)) {
							try {
								//JSONObject jsonDataRole = response.getJSONObject("obj1");
								roleJ = new RoleJ();
								setRole();
								List<UserNotificationJ> ClientNotificationList = UserNotificationJ.getJSONList(response.getString("obj"));

								TotalCount =(int)response.getDouble("TotalCount");
								if (!isAppend) {
									mSearchResultsAdapter.ClearData();
									mSearchResultsAdapter.notifyDataSetChanged();
								}
								if (ClientNotificationList != null) {
									for (UserNotificationJ message : ClientNotificationList) {
										message.setColor(getRandomMaterialColor("400"));
										message.isExpanded = false;
										if (!ExistingClientNotifications.contains(message.Id)) {
											ExistingClientNotifications.add(message.Id);
											mSearchResultsAdapter.Add(message, false);
										}
										else
										{
											//Log.w(TAG, "onResponse: "+message.Id );
										}
									}
									if(NFId	!= null && NFId	 != "" && sentfilter == false)
									{
										if(ClientNotificationList.size() == 1)
										{
											UserNotificationJ message = ClientNotificationList.get(0);
											if(message != null && message.Id.equals(NFId))
											{
												//search_edit_frame.setVisibility(View.GONE);
												sentfilter = true;
												mSearchResultsAdapter.itemClicked(0);
											}
										}
									}
								}
								if (mSearchResultsAdapter.getItemCount() != 0) {
									hideMessage();
								}
								mSearchResultsAdapter.notifyDataSetChanged();
							} catch (JSONException ex) {
								DataGlobal.SaveLog(TAG, ex);
								SnakebarCustom.danger(mContext, mView, "Unable to fetch ClientNotifications", 5000);
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
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				SnakebarCustom.danger(mContext, mView, "Unable to fetch ClientNotifications: " + error.getMessage(), 5000);
				swipeRefreshLayout.setRefreshing(false);
				IsLoading = false;
			}
		}) {

			@Override
			protected JSONObject getParams() {
				try {
					JSONObject params = new JSONObject();
					params.put("PageIndex", offset);
					params.put("PageSize", 20);
					params.put("filter", CurrentSearctText);
					JSONObject filterExpression = new JSONObject();
					Log.w(TAG, NFId == null ? "null" : NFId);
					try {
						if(NFId != null && NFId != "" && sentfilter == false)
						{
							JSONArray Ids = new JSONArray();
							Ids.put(NFId);
							if(Ids.length() > 0)
							{
								filterExpression.put("Ids",Ids);
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
	boolean sentfilter = false;


	public void getContactCount() {
		request = new CustomAuthRequest(Request.Method.POST, METHOD_COMPANY_CONTACT_PROFILE, null,0,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						if (App.getInstance().authorizeSimple(response)) {
							IsLoading=false;
							String strResponse = response.toString();
							if(strResponse != null){
								try {
									JSONObject obj = new JSONObject(response.toString());
									int count = Integer.parseInt(obj.getString("TotalCount"));
									App.CCount = count;
									((MainActivity)activity).setContactCount();
									} catch (JSONException e) {
									e.printStackTrace();
								}
							}
						}
					}
				}, new Response.ErrorListener()

		{
			@Override
			public void onErrorResponse(VolleyError error) {
				//pbHeaderProgress.setVisibility(View.VISIBLE);
				//SnakebarCustom.danger(mContext, mView, "Unable to fetch Companies: " + error.getMessage(), 5000);
			}
		}) {

			@Override
			protected JSONObject getParams() {
				try {
					JSONObject params = new JSONObject();
					params.put("PageIndex", 0);
					params.put("PageSize", 20);
					params.put("filter", CurrentSearctText);
					JSONObject filterExpression = new JSONObject();
					try {
						filterExpression.put("Status", "All");
						/*if (Filter_CRIds != null) {
							JSONArray CRIds = new JSONArray();

							for (Long CRId : Filter_CRIds) {
								CRIds.put(CRId);
							}
							if (CRIds.length() > 0) {
								filterExpression.put("CRIds", CRIds);
							}
						}*/

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
		getClientNotifications(false, 0, CurrentSearctText);
	}
	@Override
	public void onNotificationRefresh() {

		getClientNotifications(false, 0, CurrentSearctText);
	}

	private  CustomAuthRequest RemoveNotification;
	@Override
	public void  onNotificationDeleted(int position,final UserNotificationJ message) {
		//mSearchResultsList.removeViewAt(position);
		//mWrappedAdapter.notifyItemRemoved(position);
		RemoveNotification = new CustomAuthRequest(Request.Method.POST, METHOD_NOTIFICATIONS_Remove, null, message.ACId > 0 ? message.ACId : 0,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						Log.w(TAG, response.toString());
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				SnakebarCustom.danger(mContext, mView, "Unable Remove Notifications: " + error.getMessage(), 5000);
				swipeRefreshLayout.setRefreshing(false);
				IsLoading = false;
			}
		}) {

			@Override
			protected JSONObject getParams() {
				try {
					JSONObject params = new JSONObject();
					params.put("Id", message.Id);
					params.put("ACId", message.ACId);
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
	public void  onNotificationClicked(int position,final UserNotificationJ message)
	{
		Log.w(TAG, "onNotificationClicked: " + position );
		String Status = message.GetNobjStatus();
		//Log.w(TAG, "onNotificationClicked: "+Status+ "-"+ message.DT );
		if(!Status.equals("removed") && !Status.equals("click") && message.DT != null && message.DT.equals("NotificationObj"))
		{
			final String NObjId =  message.DSI;
			if(NObjId != null && NObjId != "")
			{
				new CustomAuthRequest(Request.Method.POST, METHOD_NOTIFICATIONS_acknowledge, null, message.ACId > 0 ? message.ACId : 0,
						null,null) {

					@Override
					protected JSONObject getParams() {
						try {
							JSONObject params = new JSONObject();
							params.put("Id", message.Id);
							params.put("ACId", message.ACId);
							params.put("DId", App.getInstance().getDeviceId());
							String[]  NObjIds = new String[]{ NObjId };
							params.put("nobjids", new JSONArray(NObjIds));
							params.put("status", "click");

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
		else if(!Status.equals("removed") && !Status.equals("click") && message.DT != null && message.DT.equals("Project"))
		{
			final String NFId =  message.Id;
			if(NFId != null && NFId != "")
			{
				Intent i = new Intent(CurrentActivity, ProjectFilterActivity.class);
				i.putExtra("nfid",NFId);
				i.putExtra("acid", message.ACId);
				CurrentActivity.startActivity(i);
			}
		}
	}


	// deleting the messages from recycler view
	private void deleteMessages() {
		//Log.w(TAG, "deleteMessages: " );
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
			ClientNotificationJ saveduser = data.getParcelableExtra("ClientNotificationJ");
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


	@Override
	public void onResume() {
		super.onResume();
		if(sentfilter ==  true )
		{
			NFId = null;
			sentfilter = false;
			getClientNotifications(true, 0, CurrentSearctText);
		}
	}
}
