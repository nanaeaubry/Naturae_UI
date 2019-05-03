package com.example.naturae_ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

		byte[] decodedString = Base64.decode(mPost.encodedImage, Base64.DEFAULT);
		Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
		mImageView.setImageBitmap(decodedByte);


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
				Toast.makeText(getActivity().getApplicationContext(),"Your rating has been recorded.",Toast.LENGTH_SHORT).show();
				mLikeButton.setClickable(false);
				mDislikeButton.setClickable(false);
			}
		});

		mDislikeButton = mView.findViewById(R.id.thumb_down);
		mDislikeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity().getApplicationContext(),"Your rating has been recorded.",Toast.LENGTH_SHORT).show();
				mDislikeButton.setClickable(false);
				mLikeButton.setClickable(false);
			}
		});

		return mView;
	}

}
