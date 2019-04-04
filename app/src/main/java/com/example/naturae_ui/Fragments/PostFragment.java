package com.example.naturae_ui.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.naturae_ui.R;

public class PostFragment extends Fragment {
	View mView;
	ImageButton openCamera;
	ImageButton openPhotos;
	AutoCompleteTextView titlePost;
	AutoCompleteTextView speciesPost;
	AutoCompleteTextView descriptionPost;
	Button submitPost;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_post, container, false);
		super.onCreate(savedInstanceState);

		//Button to open camera on user phone
		openCamera = mView.findViewById(R.id.open_camera);
		openCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});

		//Button to open photos on user phone
		openPhotos = mView.findViewById(R.id.open_photos);
		openPhotos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		//User enters title of post
		titlePost = mView.findViewById(R.id.post_title);

		//User enters species of item in photo
		speciesPost = mView.findViewById(R.id.post_species);

		//User enters a description for the post
		descriptionPost = mView.findViewById(R.id.post_description);

		//Button to submit post
		submitPost = mView.findViewById(R.id.post_submit);
		submitPost.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		return mView;
	}


}
