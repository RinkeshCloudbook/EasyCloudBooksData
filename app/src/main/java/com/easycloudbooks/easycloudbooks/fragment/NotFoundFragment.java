package com.easycloudbooks.easycloudbooks.fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.easycloudbooks.easycloudbooks.R;

public class NotFoundFragment extends Fragment {

	public static NotFoundFragment newInstance() {
		return new NotFoundFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_notfound, container, false);
		return rootView;
	}
}
