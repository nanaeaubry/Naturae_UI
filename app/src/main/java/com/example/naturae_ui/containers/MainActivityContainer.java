package com.example.naturae_ui.containers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
<<<<<<< HEAD:app/src/main/java/com/example/naturae_ui/Containers/MainActivityContainer.java
import android.graphics.Bitmap;
=======

import android.os.AsyncTask;
>>>>>>> ac4fe51c944528fb9cf3d530bc23f2eed704635a:app/src/main/java/com/example/naturae_ui/containers/MainActivityContainer.java
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import android.view.View;
import android.widget.FrameLayout;

import com.example.naturae_ui.R;
import com.example.naturae_ui.fragments.FriendFragment;
import com.example.naturae_ui.fragments.PostFragment;
import com.example.naturae_ui.fragments.PreviewFragment;
import com.example.naturae_ui.fragments.ProfileFragment;
import com.example.naturae_ui.models.Post;
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

<<<<<<< HEAD:app/src/main/java/com/example/naturae_ui/Containers/MainActivityContainer.java
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
=======
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
>>>>>>> ac4fe51c944528fb9cf3d530bc23f2eed704635a:app/src/main/java/com/example/naturae_ui/containers/MainActivityContainer.java


public class MainActivityContainer extends AppCompatActivity implements OnMapReadyCallback, PostFragment.OnPostListener, GoogleMap.OnMarkerClickListener {

	public static final int REQUEST_LOCATION_PERMISSION = 99;

	GoogleMap mGoogleMap;
	MapView mMapView;
	FrameLayout mFragmentContainer;
	Fragment mPostFragment;
	PreviewFragment mPreviewFragment;
	Fragment mChatFragment;
	Fragment mProfileFragment;
	BottomNavigationView navigation;
	Marker mMarker;
	List<Post> posts;
	HashMap<Marker, Post> postIdentifier = new HashMap<Marker, Post>();

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);


		// Load bottom navigation bar
		navigation = findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);


		mPostFragment = new PostFragment();
		mProfileFragment = new ProfileFragment();
		mChatFragment = new FriendFragment();
		mPreviewFragment = new PreviewFragment();

		mFragmentContainer = findViewById(R.id.fragment_container);

		mMapView = findViewById(R.id.map);
		if (mMapView != null) {
			mMapView.onCreate(null);
			mMapView.onResume();
			mMapView.getMapAsync(this);
		}
	}

	private void showMap(){
		mMapView.setVisibility(View.VISIBLE);
		mFragmentContainer.setVisibility(View.GONE);
	}

	private void showPost(){
		mMapView.setVisibility(View.INVISIBLE);
		mFragmentContainer.setVisibility(View.VISIBLE);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPostFragment).commit();
	}
	//TODO: why the fuck it null (it seems that PreviewFragment.onCreateView() is not being called)
	private void showPreview(){
		mMapView.setVisibility(View.INVISIBLE);
<<<<<<< HEAD:app/src/main/java/com/example/naturae_ui/Containers/MainActivityContainer.java
		//mFragmentContainer.setVisibility(View.VISIBLE);
=======
		mFragmentContainer.setVisibility(View.VISIBLE);
>>>>>>> ac4fe51c944528fb9cf3d530bc23f2eed704635a:app/src/main/java/com/example/naturae_ui/containers/MainActivityContainer.java
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPreviewFragment).commit();
	}

	private void showChat(){
		mMapView.setVisibility(View.INVISIBLE);
		mFragmentContainer.setVisibility(View.VISIBLE);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mChatFragment).commit();
	}

	private void showProfile(){
		mMapView.setVisibility(View.INVISIBLE);
		mFragmentContainer.setVisibility(View.VISIBLE);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mProfileFragment).commit();

	}

	/**
	 * Enable navigation on bottom bar.
	 */
	private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
			= item -> {
				switch (item.getItemId()) {
					case R.id.navigation_map:
						showMap();
						break;
					case R.id.navigation_post:
						showPost();
						break;
					case R.id.navigation_chat:
						showChat();
						break;
					case R.id.navigation_profile:
						showProfile();
						break;
				}
				return true;
			};


	/**
	 * Create map
	 * @param googleMap map to be created
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		MapsInitializer.initialize(this);

		mGoogleMap = googleMap;
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		enableMyLocation();
		mGoogleMap.setOnMarkerClickListener(this);

		//CameraPosition Home = CameraPosition.builder().target(new LatLng(34.055569, -117.182541)).zoom(14).bearing(0).tilt(45).build();
		CameraPosition Home = CameraPosition.builder().target(new LatLng(0, 0)).zoom(14).bearing(0).tilt(45).build();
		googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Home));

<<<<<<< HEAD:app/src/main/java/com/example/naturae_ui/Containers/MainActivityContainer.java
		posts = getPosts();
		for(Post curr : posts){
			createMarkerFromPost(curr);
		}
	}

	/**
	 * Gets all posts that will be displayed (can potentially modify this to get all posts in database)
	 * @return ArrayList<Post> containing all the posts
	 */
	private ArrayList<Post> getPosts() {
		ArrayList<Post> posts = new ArrayList<Post>();
		Post post1 = new Post();
		post1.title = "test1";
		post1.description = "test_description1";
		post1.lat = 0;
		post1.lng = 0;
		post1.species = "species1";
		int[] ints = new int[300];
		for(int i = 0; i < ints.length; i++){
			ints[i] = 0;
		}
		post1.image = Bitmap.createBitmap(ints, 10, 10, Bitmap.Config.RGB_565);
		posts.add(post1);
		return posts;
	}

	/**
	 * This sets up the infrastructure to create a mrker from a post, so this should allow onMarkerClick() to create a preview from the post
	 * @param post Post that is being added to map as marker, and also create preview when clicked
	 */
	private void createMarkerFromPost(Post post){
		mMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(post.lat, post.lng)).title(post.title).snippet(post.description));
		postIdentifier.put(mMarker, post);
=======
		//TODO Nanae
		//Start GRPC task to fetch all posts
		//GRPCtask(map)-> fetch posts and push on map
>>>>>>> ac4fe51c944528fb9cf3d530bc23f2eed704635a:app/src/main/java/com/example/naturae_ui/containers/MainActivityContainer.java
	}

	private void enableMyLocation() {
		if (ContextCompat.checkSelfPermission(this,	Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			mGoogleMap.setMyLocationEnabled(true);
		} else {
			ActivityCompat.requestPermissions(this, new String[]	{Manifest.permission.ACCESS_FINE_LOCATION},	REQUEST_LOCATION_PERMISSION);
		}
	}

	/**
	 * Request posts to load on map
	 * @param requestCode code that indicates permission being requested
	 * @param permissions permission needed
	 * @param grantResults give access to use location
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		// Check if location permissions are granted and if so enable the
		// location data layer.
		switch (requestCode) {
			case REQUEST_LOCATION_PERMISSION:
				if (grantResults.length > 0	&& grantResults[0] == PackageManager.PERMISSION_GRANTED) {
					enableMyLocation();
					break;
				}
		}
	}

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /**
	 * Create marker when post is created
	 * @param post post that is created
	 */
	@Override
	public void onPostCreated(Post post) {
		//mMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(post.lat, post.lng)).title(post.title).snippet(post.description));
		createMarkerFromPost(post);
		navigation.setSelectedItemId(R.id.navigation_map);

	}

	/**
	 * When a marker is clicked the preview fragment will be shown for the specific marker
	 * @param marker marker chosen
	 * @return true if marker is clickable.
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
<<<<<<< HEAD:app/src/main/java/com/example/naturae_ui/Containers/MainActivityContainer.java
		Post currentPost = postIdentifier.get(marker);
		showPreview();
		mPreviewFragment.determinePost(currentPost);
=======
		if(marker == mMarker) {
			showPreview();
		}
>>>>>>> ac4fe51c944528fb9cf3d530bc23f2eed704635a:app/src/main/java/com/example/naturae_ui/containers/MainActivityContainer.java
		return true;
	}

	//If the current access token expired then this will request the server to generate a new access token
	private static class GrpcGetNewAccessToken extends AsyncTask<Void, Void, Naturae.GetAccessTokenReply> {

		private final WeakReference<Activity> activity;
		private ManagedChannel channel;

		public GrpcGetNewAccessToken(Activity activity){
			this.activity = new WeakReference<>(activity);
		}
		@Override
		protected Naturae.GetAccessTokenReply doInBackground(Void... voids) {
			Naturae.GetAccessTokenReply reply;
			try {
				channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
				//Create a stub for with the channel
				ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
				Naturae.GetAccessTokenRequest request = Naturae.GetAccessTokenRequest.newBuilder().setAppKey(Constants.NATURAE_APP_KEY)
						.setRefreshToken(UserUtilities.getRefreshToken(activity.get())).build();
				reply = stub.getNewAccessToken(request);
			}catch (Exception e){
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				pw.flush();
				return null;
			}

			return reply;
		}

		@Override
		protected void onPostExecute(Naturae.GetAccessTokenReply getAccessTokenReply) {
			super.onPostExecute(getAccessTokenReply);
			if (getAccessTokenReply == null){
				Log.e("Access Token", "Unable to get new access token from the server");
			}
			else{
				//Cache new the access token to the phone
				UserUtilities.setAccessToken(activity.get(), getAccessTokenReply.getAccessToken());
			}
		}
	}
}
