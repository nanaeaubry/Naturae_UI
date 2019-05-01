package com.example.naturae_ui.fragments;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import com.example.naturae_ui.R;
import com.example.naturae_ui.containers.StartUpActivityContainer;
import com.example.naturae_ui.util.UserUtilities;

import java.util.Objects;

public class ProfileFragment extends Fragment implements LoginFragment.OnFragmentInteractionListener {
    public final static int PICK_PHOTO = 1046;
    View mView;
    EditText firstName;
    EditText lastName;
    TextView profileName;
    Button bLogout;
    Button bChangePass;
    ImageButton ibProfileImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        super.onCreate(savedInstanceState);
        System.out.print("hi");
        firstName = mView.findViewById(R.id.first_name_edit_text);
        lastName = mView.findViewById(R.id.last_name_edit_text);

        //profileName.setText(UserUtilities.getFirstName(getContext()) + UserUtilities.getLastName(getContext()));

        bChangePass = mView.findViewById(R.id.btChangePass);
        bChangePass.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            ChangePasswordFragment changePasswordFragment = new ChangePasswordFragment();
            fragmentTransaction.replace(R.id.fragment_container, changePasswordFragment);
            fragmentTransaction.commit();
        });


        ibProfileImage = mView.findViewById(R.id.ibProfileImage);
        ibProfileImage.setOnClickListener(v -> {
            // Create intent for picking a photo from the gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Bring up gallery to select a photo
                startActivityForResult(intent, PICK_PHOTO);
            }
        });
        /*bLogout =  mView.findViewById(R.id.btLogout);
        bLogout.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            LoginFragment loginFragment = new LoginFragment();
            fragmentTransaction.replace(R.id.fragment_container, loginFragment);
            fragmentTransaction.commit();
        });*/
        return mView;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        Uri photoUri = data.getData();
        ibProfileImage.setImageURI(photoUri);

    }


    @Override
    public void hideKeyboard() {

    }

    @Override
    public void beginFragment(StartUpActivityContainer.AuthFragmentType fragmentType, boolean setTransition, boolean addToBackStack) {

    }

    @Override
    public void startMainActivity() {

    }
}




