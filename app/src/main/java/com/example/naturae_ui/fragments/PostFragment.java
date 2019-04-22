package com.example.naturae_ui.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;



import com.example.naturae_ui.models.Post;
import com.example.naturae_ui.R;
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;

//Test Comment by Nanae
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static android.view.View.GONE;

public class PostFragment extends Fragment {
	// PICK_PHOTO_CODE is a constant integer
	public final static int PICK_PHOTO = 1046;
	public final static int REQUEST_IMAGE_CAPTURE = 1047;
	public final String APP_TAG = "NaturaePhotos";
	public String photoFileName = "photo.jpg";
	File photoFile;
	Uri photoFileUri;
	private static Context context;

	View mView;
	ImageButton openCamera;
	ImageButton openPhotos;
	AutoCompleteTextView titlePost;
	AutoCompleteTextView speciesPost;
	AutoCompleteTextView descriptionPost;
	Button submitPost;
	OnPostListener listener;
	Bitmap selectedImage = null;
	ImageView imagePreview;
	float[] latLong = new float[2];

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_post, container, false);
		super.onCreate(savedInstanceState);
		imagePreview = mView.findViewById(R.id.image_preview);

		// Create a File reference for photo capture
		photoFile = getPhotoFile(photoFileName);

		// wrap File object into a content provider
		photoFileUri = FileProvider.getUriForFile(getContext(), "com.example.naturae_ui", photoFile);

		//Button to open camera on user phone
		openCamera = mView.findViewById(R.id.open_camera);
		openCamera.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri);

				if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
					startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
				}
			}
		});

		//Button to open media gallery on user phone
		openPhotos = mView.findViewById(R.id.open_photos);
		openPhotos.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Create intent for picking a photo from the gallery
				Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

				// If you call startActivityForResult() using an intent that no app can handle, your app will crash.
				// So as long as the result is not null, it's safe to use the intent.
				if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
					// Bring up gallery to select a photo
					startActivityForResult(intent, PICK_PHOTO);
				}
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
				boolean missingData = TextUtils.isEmpty(titlePost.getText()) ||
						TextUtils.isEmpty(speciesPost.getText()) ||
						TextUtils.isEmpty(descriptionPost.getText()) ||
						selectedImage == null;
				if (missingData) {
					new AlertDialog.Builder(getContext())
							.setTitle("One or more fields are empty").setMessage("Please make sure all fields are correct ")
							.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {

								}
							}).show();
					return;
				}

				//New post to hold information input by user
				Post post = new Post();

				//User input will be put in post
				post.title = titlePost.getText().toString();
				post.species = speciesPost.getText().toString();
				post.description = descriptionPost.getText().toString();
				post.lat = latLong[0];
				post.lng = latLong[1];
				post.image = selectedImage;

				new GrpcCreatePost(listener, post, getActivity()).execute();

			}

		});
		return mView;
	}

	/**
	 * Get image data and process accordingly based on whether photo is taken with camera
	 * or uploaded from user gallery
	 *
	 * @param requestCode specifies how picture was gotten
	 * @param resultCode  what will happen with image
	 * @param data        data about the image
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (data == null) {
			return;
		}

		switch (requestCode) {
			case PICK_PHOTO:
				// Do something with the photo based on Uri
				try {

					Uri photoUri = data.getData();
					selectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);

					// Load the selected image into a preview
					imagePreview.setVisibility(View.VISIBLE);
					imagePreview.setImageBitmap(selectedImage);

					readExif(photoUri);

				} catch (IOException e) {
					e.printStackTrace();
				}
				break;

			case REQUEST_IMAGE_CAPTURE:
				// by this point we have the camera photo on disk
				selectedImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

				// RESIZE BITMAP, see section below
				// Load the taken image into a preview
				ImageView imagePreview = mView.findViewById(R.id.image_preview);
				imagePreview.setVisibility(View.VISIBLE);
				imagePreview.setImageBitmap(selectedImage);

				readExif(photoFileUri);
				break;
		}
	}

	// Returns the File for a photo stored on disk given the fileName
	public File getPhotoFile(String fileName) {
		// Get safe storage directory for photos
		// Use `getExternalFilesDir` on Context to access package-specific directories.
		// This way, we don't need to request external read/write runtime permissions.
		File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
			Log.d(APP_TAG, "failed to create directory");
		}

		// Return the file target for the photo based on filename
		File file = new File(mediaStorageDir.getPath() + File.separator + fileName);

		return file;
	}

	/**
	 * Read data from image
	 *
	 * @param uri image uri
	 */
	void readExif(Uri uri) {

		try {
			InputStream is = getContext().getContentResolver().openInputStream(uri);
			ExifInterface exifInterface = new ExifInterface(is);

			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					imagePreview.setRotation(90);
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					imagePreview.setRotation(180);
				case ExifInterface.ORIENTATION_ROTATE_270:
					imagePreview.setRotation(270);
				default:
					imagePreview.setRotation(0);
			}

			exifInterface.getLatLong(latLong);


		} catch (IOException e) {

			e.printStackTrace();
		}

	}


	/**
	 * Attach post to listener
	 *
	 * @param context context of the app
	 */
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

	private static class GrpcCreatePost extends AsyncTask<Void, Void, Naturae.CreatePostReply> {

		private final PostFragment.OnPostListener mListener;
		private final Post mPost;
		private final WeakReference<Context> cReference ;

		private ManagedChannel channel;


		private GrpcCreatePost(OnPostListener mListener, Post post, Activity activity) {
			this.mListener = mListener;
			this.mPost = post;
			this.cReference = new WeakReference<>(activity.getApplicationContext());


		}

		@Override
		protected Naturae.CreatePostReply doInBackground(Void... voids) {

			//Make image a byte array to store in server
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			mPost.image.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
			byte[] byteArray = byteArrayOutputStream.toByteArray();
			String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
			System.out.println(UserUtilities.getEmail(cReference.get()));
			Naturae.CreatePostReply reply;
			try {
				channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
				//Create a stub for with the channel
				ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
				//Create an gRPC login request
				Naturae.CreatePostRequest request = Naturae.CreatePostRequest.newBuilder()
						.setAppKey(Constants.NATURAE_APP_KEY)
						.setAccessToken(UserUtilities.getAccessToken(cReference.get()))
						.setTitle(mPost.title).setSpecies(mPost.species)
						.setDescription(mPost.description)
						.setLat(mPost.lat)
						.setLng(mPost.lng)
						.setEncodedImage(encodedImage)
						.build();
				reply = stub.createPost(request);

			} catch (Exception e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				pw.flush();
				return null;
			}
			return reply;
		}

		@Override
		protected void onPostExecute(Naturae.CreatePostReply createPostReply) {
			//Check if reply is equal to null. If it's equal to null then there is an error
			//when communicating with the server
			if (createPostReply != null) {
				if(createPostReply.getStatus().getCode() == Constants.OK ){
					mListener.onPostCreated(mPost);
				}
			}

		}

	}

}

