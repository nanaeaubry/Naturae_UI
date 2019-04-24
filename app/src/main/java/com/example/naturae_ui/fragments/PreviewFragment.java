package com.example.naturae_ui.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

<<<<<<< HEAD:app/src/main/java/com/example/naturae_ui/Fragments/PreviewFragment.java
import com.example.naturae_ui.Models.Post;
=======
import com.example.naturae_ui.models.Post;
>>>>>>> ac4fe51c944528fb9cf3d530bc23f2eed704635a:app/src/main/java/com/example/naturae_ui/fragments/PreviewFragment.java
import com.example.naturae_ui.R;
import com.google.android.gms.maps.model.Marker;

public class PreviewFragment extends Fragment {
	View mView;
	ImageView imageView;
	TextView mTitle;
	TextView mSpecies;
	TextView mDescription;
<<<<<<< HEAD:app/src/main/java/com/example/naturae_ui/Fragments/PreviewFragment.java
	Post mPost;
	//Marker marker;
=======
	Post post = new Post();
	Marker marker;
>>>>>>> ac4fe51c944528fb9cf3d530bc23f2eed704635a:app/src/main/java/com/example/naturae_ui/fragments/PreviewFragment.java


	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_preview, container, false);
		super.onCreate(savedInstanceState);

		imageView = mView.findViewById(R.id.image_view);
<<<<<<< HEAD:app/src/main/java/com/example/naturae_ui/Fragments/PreviewFragment.java
		Log.d("text", new Boolean(imageView == null).toString());
=======
>>>>>>> ac4fe51c944528fb9cf3d530bc23f2eed704635a:app/src/main/java/com/example/naturae_ui/fragments/PreviewFragment.java



		mTitle = mView.findViewById(R.id.preview_title);

		mSpecies = mView.findViewById(R.id.preview_species);

		mDescription = mView.findViewById(R.id.preview_description);






		return mView;
	}

	public void determinePost(Post post){
		mPost = post;
		imageView.setImageBitmap(mPost.image);
		mTitle.setText(mPost.title);
		mSpecies.setText(mPost.species);
		mDescription.setText(mPost.description);
	}

}