package com.example.naturae_ui.Fragments;

import android.content.Intent;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.example.naturae_ui.Containers.MainActivityContainer;
import com.example.naturae_ui.Containers.StartUpContainer;
import com.example.naturae_ui.R;
import com.example.naturae_ui.Util.UserUtilities;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;


public class ProfileFragment extends Fragment {
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

        firstName = mView.findViewById(R.id.first_name_edit_text);
        lastName = mView.findViewById(R.id.last_name_edit_text);

        //profileName.setText(UserUtilities.getFirstName() + UserUtilities.getLastName());

//        bChangePass.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });


        ibProfileImage = mView.findViewById(R.id.ibProfileImage);
        ibProfileImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent for picking a photo from the gallery
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
                // So as long as the result is not null, it's safe to use the intent.
                if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                    // Bring up gallery to select a photo
                    startActivityForResult(intent, PICK_PHOTO);
                }
            }
        });
        Button bLogout = (Button) mView.findViewById(R.id.bLogout);
        bLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), StartUpContainer.class);
                startActivity(in);
            }
        });
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


}
