package com.example.naturae_ui.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.naturae_ui.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapFragment extends Fragment implements OnMapReadyCallback {

	GoogleMap mGoogleMap;
	MapView mMapView;
	View    mView;

	@Nullable
	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_map, container, false);
		super.onCreate(savedInstanceState);

		return mView;
	}

	@Override
	public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mMapView = mView.findViewById(R.id.map);
		if(mMapView != null){
			mMapView.onCreate(null);
			mMapView.onResume();
			mMapView.getMapAsync(this);
		}
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		MapsInitializer.initialize(getContext());

		mGoogleMap = googleMap;
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		googleMap.addMarker(new MarkerOptions().position(new LatLng(33.7874, -118.1144)).title("Walter Pyramid").snippet("Hope its ok"));
		CameraPosition Pyramid = CameraPosition.builder().target(new LatLng(33.7874, -118.1144)).zoom(14).bearing(0).tilt(45).build();

		googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Pyramid));
	}
}
