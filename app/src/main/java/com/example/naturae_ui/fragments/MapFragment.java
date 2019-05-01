package com.example.naturae_ui.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.naturae_ui.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MapFragment extends Fragment {
	private static final String TAG = "MapFragment";
	EditText mSearchText;
	Button mSearchButton;
	View mView;

	public MapFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using provided parameters.
	 *
	 * @return A new instance of fragment MapFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static MapFragment newInstance(String param1, String param2) {
		MapFragment fragment = new MapFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		mSearchText = mView.findViewById(R.id.search_text);
//
//		mSearchButton = mView.findViewById(R.id.search_button);

		return inflater.inflate(R.layout.fragment_search, container, false);
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}
}
