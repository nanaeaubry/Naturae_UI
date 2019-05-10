package com.example.naturae_ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;

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

		searchFieldInput.setOnClickListener(v -> getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING));
		searchFieldInput.setOnFocusChangeListener((v, hasFocus) -> {
			if (hasFocus){
				getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
			}
		});
		/**
		 * SEARCH FIELD EVENT HANDLER
		 */
		searchFieldInput.setOnEditorActionListener((v, actionId, event) -> {

			if(actionId == EditorInfo.IME_ACTION_DONE){
				//Retrieve the query the user typed
				String searchQuery = searchFieldInput.getText().toString();
				passData(searchQuery);
				searchFieldInput.clearFocus();
			}
			return false;
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
	    void onDataPass(String data);
        void hideKeyboard();
	}
}
