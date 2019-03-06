package com.example.naturae_ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

public class MapFragment extends AppCompatActivity implements OnMapReadyCallback {

	private static final int REQUEST_LOCATION_PERMISSION = 99;
	private GoogleMap mMap;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_map);
		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		mMap = googleMap;

		// Add a marker in Sydney, Australia, and move the camera.
		LatLng sydney = new LatLng(-34, 151);
		mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
		mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

		//Long Click to add markers
		setMapLongClick(mMap);

		//Enable location tracking
		enableMyLocation();
	}

	private void setMapLongClick(final GoogleMap map) {
		map.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
			@Override
			public void onMapLongClick(LatLng latLng) {
				String snippet = String.format(Locale.getDefault(),
						"Lat: %1$.5f, Long: %2$.5f",
						latLng.latitude,
						latLng.longitude);
				map.addMarker(new MarkerOptions().position(latLng).title(getString(R.string.dropped_pin)).snippet(snippet));
			}
		});
	}

	private void enableMyLocation() {
		if (ContextCompat.checkSelfPermission(this,
				android.Manifest.permission.ACCESS_FINE_LOCATION)
				== PackageManager.PERMISSION_GRANTED) {
			mMap.setMyLocationEnabled(true);
		} else {
			ActivityCompat.requestPermissions(this, new String[]
							{Manifest.permission.ACCESS_FINE_LOCATION},
					REQUEST_LOCATION_PERMISSION);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode,
	                                       @NonNull String[] permissions,
	                                       @NonNull int[] grantResults) {
		// Check if location permissions are granted and if so enable the
		// location data layer.
		switch (requestCode) {
			case REQUEST_LOCATION_PERMISSION:
				if (grantResults.length > 0
						&& grantResults[0]
						== PackageManager.PERMISSION_GRANTED) {
					enableMyLocation();
					break;
				}
		}
	}
}
