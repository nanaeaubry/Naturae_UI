package com.example.naturae_ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.naturae_ui.Models.Post;
import com.example.naturae_ui.R;
import com.google.android.gms.maps.model.Marker;

public class PreviewFragment extends Fragment {
	View mView;
	ImageView imageView;
	TextView mTitle;
	TextView mSpecies;
	TextView mDescription;
	Post post = new Post();
	Marker marker;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_preview, container, false);
		super.onCreate(savedInstanceState);

		imageView = mView.findViewById(R.id.image_view);



		mTitle = mView.findViewById(R.id.preview_title);

		mSpecies = mView.findViewById(R.id.preview_species);

		mDescription = mView.findViewById(R.id.preview_description);






		return mView;
	}

}
