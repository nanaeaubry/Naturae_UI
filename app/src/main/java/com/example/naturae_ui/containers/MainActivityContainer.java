package com.example.naturae_ui.containers;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;

import android.location.Location;
import android.os.AsyncTask;
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
import com.example.naturae_ui.fragments.MapFragment;
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
import com.google.android.gms.maps.model.VisibleRegion;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

interface GetPostsCompleted {
	void onGetPostsCompleted(Naturae.GetPostPreviewReply reply);
}

public class MainActivityContainer extends AppCompatActivity implements OnMapReadyCallback, PostFragment.OnPostListener,
		GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraIdleListener, GetPostsCompleted {

	public static final int REQUEST_LOCATION_PERMISSION = 99;

	GoogleMap mGoogleMap;
	MapView mMapView;
	FrameLayout mFragmentContainer;
	Fragment mMapFragment;
	Fragment mPostFragment;
	Fragment mPreviewFragment;
	Fragment mChatFragment;
	Fragment mProfileFragment;
	BottomNavigationView navigation;
	Marker mMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Load bottom navigation bar
		navigation = findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

		// Create fragments
		mMapFragment = new MapFragment();
		mPostFragment = new PostFragment();
		mProfileFragment = new ProfileFragment();
		mChatFragment = new FriendFragment();
		mPreviewFragment = new PreviewFragment();

		// Create fragment container
		mFragmentContainer = findViewById(R.id.fragment_container);
		mMapView = findViewById(R.id.map);
		if (mMapView != null) {
			mMapView.onCreate(null);
			mMapView.onResume();
			mMapView.getMapAsync(this);
		}

		mFragmentContainer.setVisibility(View.VISIBLE);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mMapFragment).commit();
	}

	// Show map when selected on bottom navigation
	private void showMap() {
		mMapView.setVisibility(View.VISIBLE);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mMapFragment).commit();
	}

	// Show post when selected on bottom navigation
	private void showPost() {
		mMapView.setVisibility(View.INVISIBLE);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPostFragment).commit();
	}

	// Show preview when selected on map
	private void showPreview(Post post) {
		Bundle bundle = new Bundle();
		bundle.putParcelable("post", post);
		mMapView.setVisibility(View.INVISIBLE);
		mPreviewFragment.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mPreviewFragment).commit();
	}

	// Show chat when selected on bottom navigation
	private void showChat() {
		mMapView.setVisibility(View.INVISIBLE);
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mChatFragment).commit();
	}

	// Show profile when selected on bottom navigation
	private void showProfile() {
		mMapView.setVisibility(View.INVISIBLE);
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
	 *
	 * @param googleMap map to be created
	 */
	@Override
	public void onMapReady(GoogleMap googleMap) {
		MapsInitializer.initialize(this);

		mGoogleMap = googleMap;
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		enableMyLocation();

		mGoogleMap.setOnMarkerClickListener(this);
		mGoogleMap.setOnCameraIdleListener(this);

		CameraPosition Home = CameraPosition.builder().target(new LatLng(33.7701, -118.1937)).zoom(14).bearing(0).tilt(45).build();
		googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Home));

	}

	@Override
	public void onCameraIdle() {
		//Get center latitude and longitude values
		LatLng center = mGoogleMap.getCameraPosition().target;
		float cLat = (float) center.latitude;
		float cLng = (float) center.longitude;

		//Get radius of visible map region
		VisibleRegion visibleRegion = mGoogleMap.getProjection().getVisibleRegion();

		float[] diagonalDistance = new float[1];

		LatLng farLeft = visibleRegion.farLeft;
		LatLng nearRight = visibleRegion.nearRight;

		Location.distanceBetween(
				farLeft.latitude,
				farLeft.longitude,
				nearRight.latitude,
				nearRight.longitude,
				diagonalDistance
		);

		int radius = (int) diagonalDistance[0] / 2;

		new GrpcGetPostPreview(this, cLat, cLng, radius).execute();
	}

	@Override
	public void onGetPostsCompleted(Naturae.GetPostPreviewReply reply) {
		if (reply != null) {
			if (reply.getStatus().getCode() == Constants.OK) {
				mGoogleMap.clear();
				int length = reply.getReplyCount();
				for (int i = 0; i < length; i++) {
					Naturae.PostStruct postStruct = reply.getReply(i);
					float lat = postStruct.getLatitude();
					float lng = postStruct.getLongitude();
					String title = postStruct.getTitle();
					String description = postStruct.getDescription();

					Marker marker = mGoogleMap.addMarker(new MarkerOptions()
							.position(new LatLng(lat, lng))
							.title(title)
							.snippet(description));
					marker.setTag(new Post(postStruct));

				}
			}
		}

	}

	private void enableMyLocation() {
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			mGoogleMap.setMyLocationEnabled(true);
		} else {
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
		}
	}

	/**
	 * Request permission for location
	 * @param requestCode code that indicates permission being requested
	 * @param permissions permission needed
	 * Request posts to load on map
	 *
	 * @param requestCode  code that indicates permission being requested
	 * @param permissions  permission needed
	 * @param grantResults give access to use location
	 */
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		// Check if location permissions are granted and if so enable the
		// location data layer.
		switch (requestCode) {
			case REQUEST_LOCATION_PERMISSION:
				if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
	 *
	 * @param post post that is created
	 */
	@Override
	public void onPostCreated(Post post) {
		mMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(post.lat, post.lng)).title(post.title).snippet(post.description));
		navigation.setSelectedItemId(R.id.navigation_map);

	}


	/**
	 * When a marker is clicked the preview fragment will be shown for the specific marker
	 *
	 * @param marker marker chosen
	 * @return true if marker is clickable.
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		showPreview((Post) marker.getTag());

		return true;
	}


	//If the current access token expired then this will request the server to generate a new access token
	private static class GrpcGetNewAccessToken extends AsyncTask<Void, Void, Naturae.GetAccessTokenReply>{

		private final WeakReference<Activity> activity;
		private ManagedChannel channel;

		public GrpcGetNewAccessToken(Activity activity) {
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
		protected void onPostExecute(Naturae.GetAccessTokenReply getAccessTokenReply) {
			super.onPostExecute(getAccessTokenReply);
			if (getAccessTokenReply == null) {
				Log.e("Access Token", "Unable to get new access token from the server");
			} else {
				//Cache new the access token to the phone
				UserUtilities.setAccessToken(activity.get(), getAccessTokenReply.getAccessToken());
			}
		}
	}

	//Get posts to put on map
	private static class GrpcGetPostPreview extends AsyncTask<Void, Void, Naturae.GetPostPreviewReply> {

		private GetPostsCompleted listener;
		private ManagedChannel channel;
		private float lat;
		private float lng;
		private int radius;

		public GrpcGetPostPreview(GetPostsCompleted listener, float lat, float lng, int radius) {
			this.listener = listener;
			this.lat = lat;
			this.lng = lng;
			this.radius = radius;
		}

		@Override
		protected Naturae.GetPostPreviewReply doInBackground(Void... voids) {
			Naturae.GetPostPreviewReply reply;
			try {
				channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
				//Create a stub for with the channel
				ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
				Naturae.GetPostPreviewRequest request = Naturae.GetPostPreviewRequest.newBuilder()
						.setAppKey(Constants.NATURAE_APP_KEY)
						.setLat(lat)
						.setLng(lng)
						.setRadius(radius)
						.build();
				reply = stub.getPostPreview(request);
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
		protected void onPostExecute(Naturae.GetPostPreviewReply getPostPreviewReply) {
			super.onPostExecute(getPostPreviewReply);
			//Shut down the gRPC channel
			try {
				channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			listener.onGetPostsCompleted(getPostPreviewReply);
		}
	}
}


