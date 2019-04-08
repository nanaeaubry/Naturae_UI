package com.example.naturae_ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.TextView;

import java.util.Objects;


public class ProfileFragment extends Fragment {
    View mView;
    private EditText firstNameEditText;
    private EditText lastNameEditText;
    private TextView profileName;
    private Button bLogout;
    private Button bChangePass;
    private ImageButton ibProfileImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        super.onCreate(savedInstanceState);


        return mView;
    }
    ImageView image;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addListenerOnButton();

        bLogout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getActivity()).finish();
            }
        });

        ibProfileImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        bChangePass.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        profileName = mView.findViewById(R.id.first_name_edit_text + R.id.last_name_edit_text);
    }

    public void addListenerOnButton() {

        image =  image.findViewById(R.id.ibProfileImage);
        image.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                image.setImageResource(R.drawable.img);
            }

        });


    }
}
