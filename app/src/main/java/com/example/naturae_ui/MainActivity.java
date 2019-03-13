package com.example.naturae_ui;


import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

	private TextView mTextMessage;


	private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			switch (item.getItemId()) {
				case R.id.navigation_map:
					return true;
				case R.id.navigation_post:
					mTextMessage.setText(R.string.title_post);
					return true;
				case R.id.navigation_profile:
					mTextMessage.setText(R.string.title_profile);
					return true;
			}
			return false;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		// Retrieve the content view that renders the map.
		setContentView(R.layout.activity_main);

		// Get the SupportMapFragment and request notification
		// when the map is ready to be used.
		SupportMapFragment mapFragment = (SupportMapFragment)
				getSupportFragmentManager()
						.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}
	@Override
	public void onMapReady(GoogleMap googleMap)
	{
		// Add a marker in Sydney, Australia,
		// and move the map's camera to the same location.
		LatLng myPos = new LatLng(Location.getLatitude(), Location.getLongitude());
		googleMap.moveCamera(CameraUpdateFactory.newLatLng(myPos));
	}
}
