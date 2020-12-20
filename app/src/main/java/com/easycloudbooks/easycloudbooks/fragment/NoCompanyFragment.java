package com.easycloudbooks.easycloudbooks.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.easycloudbooks.easycloudbooks.R;
import com.easycloudbooks.easycloudbooks.common.SweetAlertCustom;

public class NoCompanyFragment extends Fragment {

	public static NoCompanyFragment newInstance() {
		return new NoCompanyFragment();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_nocompany, container, false);

		return rootView;
	}
}
