package com.example.naturae_ui.Containers;



import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.naturae_ui.Fragments.FriendFragment;
import com.example.naturae_ui.Fragments.PostFragment;
import com.example.naturae_ui.Fragments.PreviewFragment;
import com.example.naturae_ui.Fragments.ProfileFragment;
import com.example.naturae_ui.Models.Post;
import com.example.naturae_ui.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class MainActivityContainer extends AppCompatActivity implements OnMapReadyCallback, PostFragment.OnPostListener, GoogleMap.OnMarkerClickListener {

	public static final int REQUEST_LOCATION_PERMISSION = 99;

	GoogleMap mGoogleMap;
	MapView mMapView;
	FrameLayout mFragmentContainer;
	Fragment mPostFragment;
	Fragment mPreviewFragment;
	Fragment mChatFragment;
	Fragment mProfileFragment;
	BottomNavigationView navigation;
	Marker mMarker;

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

	private void showPreview(){
		mMapView.setVisibility(View.INVISIBLE);
		mFragmentContainer.setVisibility(View.VISIBLE);
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
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
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
		}
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

		CameraPosition Home = CameraPosition.builder().target(new LatLng(34.055569, -117.182541)).zoom(14).bearing(0).tilt(45).build();
		googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Home));

		//TODO Nanae
		//Start GRPC task to fetch all posts
		//GRPCtask(map)-> fetch posts and push on map
	}

	private void enableMyLocation() {
		if (ContextCompat.checkSelfPermission(this,	Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
			mGoogleMap.setMyLocationEnabled(true);
		} else {
			ActivityCompat.requestPermissions(this, new String[]	{Manifest.permission.ACCESS_FINE_LOCATION},	REQUEST_LOCATION_PERMISSION);
		}
	}

	/**
	 * Request permission for location
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

	/**
	 * Create marker when post is created
	 * @param post post that is created
	 */
	@Override
	public void onPostCreated(Post post) {
		mMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(post.lat, post.lng)).title(post.title).snippet(post.description));
		navigation.setSelectedItemId(R.id.navigation_map);

	}

	/**
	 * When a marker is clicked the preview fragment will be shown for the specific marker
	 * @param marker marker chosen
	 * @return true if marker is clickable.
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		if(marker == mMarker) {
			showPreview();
		}
		return true;
	}
}
