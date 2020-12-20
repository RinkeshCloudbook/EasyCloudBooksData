package com.easycloudbooks.easycloudbooks.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.font.RobotoTextView;

public class ComingSoonFragment extends Fragment {

	public static ComingSoonFragment newInstance(String PageName) {
		ComingSoonFragment f =new ComingSoonFragment();
		Bundle args = new Bundle();
		args.putString("pagename", PageName);
		f.setArguments(args);
		return f;
	}

	RobotoTextView pagename ;
	String mPageName ;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_comingsoon, container, false);

		pagename = (RobotoTextView) rootView.findViewById(R.id.commingsoonpagename);
		Bundle args = getArguments();
		mPageName = args.getString("pagename", "");
		pagename.setText(mPageName);
		return rootView;
	}
}
