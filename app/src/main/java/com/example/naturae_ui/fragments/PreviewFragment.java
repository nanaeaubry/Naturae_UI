package com.example.naturae_ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.naturae_ui.models.Post;
import com.example.naturae_ui.R;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

public class PreviewFragment extends Fragment {
	View mView;
	ImageView mImageView;
	TextView mTitle;
	TextView mSpecies;
	TextView mDescription;
	Post mPost;
	ImageButton mLikeButton;
	ImageButton mDislikeButton;


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_preview, container, false);
		super.onCreate(savedInstanceState);

		mPost = getArguments().getParcelable("post");

		mImageView = mView.findViewById(R.id.image_view);

		Picasso.get().load(mPost.encodedImage).into(mImageView);

		mTitle = mView.findViewById(R.id.preview_title);
		mTitle.setText(mPost.title);

		mSpecies = mView.findViewById(R.id.preview_species);
		mSpecies.setText("Species: " + mPost.species);

		mDescription = mView.findViewById(R.id.preview_description);
		mDescription.setText("Description: " + mPost.description);

		mLikeButton = mView.findViewById(R.id.thumb_up);
		mLikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity().getApplicationContext(), "Your rating has been recorded.", Toast.LENGTH_SHORT).show();
				mLikeButton.setClickable(false);
				mDislikeButton.setClickable(false);
			}
		});

		mDislikeButton = mView.findViewById(R.id.thumb_down);
		mDislikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity().getApplicationContext(), "Your rating has been recorded.", Toast.LENGTH_SHORT).show();
				mDislikeButton.setClickable(false);
				mLikeButton.setClickable(false);
			}
		});

		return mView;
	}


}


