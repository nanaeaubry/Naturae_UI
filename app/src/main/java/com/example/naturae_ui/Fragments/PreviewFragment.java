package com.example.naturae_ui.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.naturae_ui.R;

public class PreviewFragment extends Fragment {
	View mView;
	ImageButton goBack;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_preview, container, false);
		super.onCreate(savedInstanceState);

		//Button to go back to map
		goBack = mView.findViewById(R.id.go_back);
		goBack.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) {
				getActivity().onBackPressed();
			}
		});





		return mView;
	}

}
