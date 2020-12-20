package com.easycloudbooks.easycloudbooks.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easycloudbooks.easycloudbooks.R;

public class OfflineFragment extends Fragment {

	public static OfflineFragment newInstance() {
		return new OfflineFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_offline, container, false);
		return rootView;
	}
}
