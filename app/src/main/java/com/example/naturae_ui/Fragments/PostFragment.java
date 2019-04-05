package com.example.naturae_ui.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.naturae_ui.Models.Post;
import com.example.naturae_ui.R;

public class PostFragment extends Fragment {
	View mView;
	ImageButton openCamera;
	ImageButton openPhotos;
	AutoCompleteTextView titlePost;
	AutoCompleteTextView speciesPost;
	AutoCompleteTextView descriptionPost;
	Button submitPost;
	OnPostListener listener;


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
				if (TextUtils.isEmpty(titlePost.getText()) || TextUtils.isEmpty(speciesPost.getText()) || TextUtils.isEmpty(descriptionPost.getText())) {
					new AlertDialog.Builder(getContext())
							.setTitle("One or more fields are empty").setMessage("Please make sure all fields are correct ")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

								}
							}).show();
					return;
				}
				Post post = new Post();


				post.title = titlePost.getText().toString();
				post.species = speciesPost.getText().toString();
				post.description = descriptionPost.getText().toString();
				listener.onPostCreated(post);
			}

			;


		});
		return mView;
	}


	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		try {
			listener = (OnPostListener) context;

		} catch (ClassCastException c) {
			throw new ClassCastException(context.toString() + " must implement OnPostListener");
		}
	}

	public interface OnPostListener {
		void onPostCreated(Post post);
	}
}
