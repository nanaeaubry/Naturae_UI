package com.example.naturae_ui;



import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.naturae_ui.Fragments.FriendFragment;


public class MainActivity extends AppCompatActivity{



	/**
	 * Enable navigation on bottom bar.
	 */
	private BottomNavigationView.OnNavigationItemSelectedListener onNavigationItemSelectedListener
			= new BottomNavigationView.OnNavigationItemSelectedListener() {

		@Override
		public boolean onNavigationItemSelected(@NonNull MenuItem item) {
			Fragment selectedFragment = null;
			switch (item.getItemId()) {
				case R.id.navigation_map:
					selectedFragment = new MapFragment();
					break;
				case R.id.navigation_post:
					selectedFragment = new PostFragment();
					break;
				case R.id.navigation_chat:
					selectedFragment = new FriendFragment();
					break;
				case R.id.navigation_profile:
					selectedFragment = new ProfileFragment();
					break;
			}
			getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
			return true;
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Load bottom navigation bar
		BottomNavigationView navigation = findViewById(R.id.navigation);
		navigation.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener);

		// Map Fragment will open first
		MapFragment mapFragment = new MapFragment();
		getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, mapFragment).commit();



	}

}
