package com.example.naturae_ui.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import com.example.naturae_ui.R;
import com.example.naturae_ui.Util.Friend;
import com.example.naturae_ui.Util.FriendAdapter;

import java.util.ArrayList;
import java.util.List;

public class FriendFragment extends Fragment {
    private static final String TAG = "FriendFragment";
    private List<Friend> friendsList;
    /**
     * Called to do initial creation of a fragment. This is called after onAttach(Activity) and before onCreateView
     * Note that this can be called while the fragment's activity is still in the process of being created.
     * @param savedInstanceState : If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * @param inflater : Used to instantiate an xml file into a corresponding view object
     * @param container : Holds view objects, provides efficient ways to layout the child views
     * @param savedInstanceState : If the fragment is being re-created from a previous saved state, this is the state.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        Log.d(TAG, "initRecyclerView: init recyclerview.");
        friendsList = new ArrayList<Friend>();
        friendsList.add(new Friend("Jimmy"));
        friendsList.add(new Friend("Trung"));
        friendsList.add(new Friend("Jacob"));
        friendsList.add(new Friend("Simon"));
        friendsList.add(new Friend("Henry"));
        friendsList.add(new Friend("Catherine"));
        friendsList.add(new Friend("Josh"));
        friendsList.add(new Friend("Colin"));

        RecyclerView recyclerView = view.findViewById(R.id.friend_recycler);
        FriendAdapter adapter = new FriendAdapter(getContext(), friendsList);

        adapter.setClickListener(new FriendAdapter.ClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.d(TAG, "onItemClick position: " + position);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL));
        return view;
    }

    /**
     * Commits any changes that should be persisted beydond the current user session
     * The system calls this method as the first indication that the user is leaving the fragment
     */
    @Override
    public void onPause(){
        super.onPause();
    }

}
