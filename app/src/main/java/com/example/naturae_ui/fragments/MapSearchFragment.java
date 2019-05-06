package com.example.naturae_ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.naturae_ui.R;

/**
 * A simple map overlay class.
 */
public class MapSearchFragment extends Fragment {
	private OnDataPass dataPasser;
	private static final String TAG = "MapSearchFragment";
	private TextInputEditText searchFieldInput;
	Button searchSubmitButton;

	public MapSearchFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using provided parameters.
	 *
	 * @return A new instance of fragment MapSearchFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static MapSearchFragment newInstance(String param1, String param2) {
		MapSearchFragment fragment = new MapSearchFragment();
		Bundle args = new Bundle();
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = inflater.inflate(R.layout.fragment_search, container, false);

		searchFieldInput = view.findViewById(R.id.map_search_text);
		searchSubmitButton = view.findViewById(R.id.map_search_button);

		/**
		 * SEARCH FIELD EVENT HANDLER
		 */
		searchFieldInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

				if(actionId == EditorInfo.IME_ACTION_DONE){
					//Retrieve the query the user typed
					String searchQuery = searchFieldInput.getText().toString();
					passData(searchQuery);
					searchFieldInput.clearFocus();
				}
				return false;
			}
		});

		/* Doesn't work >:(
		searchFieldInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				InputMethodManager inputMethodManager =(InputMethodManager)getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
				inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
			}
		});
		*/

		/**
		 * SEARCH BUTTON EVENT HANDLER
		 */
		searchSubmitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String searchQuery = searchFieldInput.getText().toString();
				passData(searchQuery);
				searchFieldInput.clearFocus();
			}
		});

		return view;
	}

	/**
	 * Sends a string back to the main activity for processing
	 * @param data the text to send back
	 */
	public void passData(String data) {
		dataPasser.onDataPass(data);
	}

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		dataPasser = (OnDataPass) context;
	}

	@Override
	public void onDetach() {
		super.onDetach();
	}

	/**
	 * Interface for messaging between fragment and activity
	 */
	public interface OnDataPass {
		public void onDataPass(String data);
	}
}
