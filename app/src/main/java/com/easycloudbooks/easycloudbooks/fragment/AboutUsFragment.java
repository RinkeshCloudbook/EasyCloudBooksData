package com.easycloudbooks.easycloudbooks.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easycloudbooks.easycloudbooks.R;

public class AboutUsFragment extends Fragment {

	public View mView;
	public static AboutUsFragment newInstance() {
		return new AboutUsFragment();
	}

	public View GetmView()
	{
		return  mView;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_aboutus, container, false);
		mView = rootView;
                Log.i("onCreateView", mView == null ? "true" : "false");
		mListener.OnCompleteFragment(mView);
		return rootView;
	}

	public static interface OnCompleteFragment {
		public abstract void OnCompleteFragment(View mView);
	}

	private OnCompleteFragment mListener;

	public void onAttach(Context context) {
		super.onAttach(context);
		Activity activity;

		if (context instanceof Activity){

			activity=(Activity) context;

			try {
				this.mListener = (OnCompleteFragment)activity;
			} catch (final ClassCastException e) {
				throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
			}
		}
	}
}
