package com.example.naturae_ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.naturae_ui.R;
import com.example.naturae_ui.containers.MainActivityContainer;
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.Helper;
import com.example.naturae_ui.util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static com.example.naturae_ui.fragments.PostFragment.REQUEST_IMAGE_CAPTURE;

public class ProfileFragment extends Fragment{
    public final static int PICK_PHOTO = 1046;
    View mView;
    EditText firstName;
    EditText lastName;
    TextView profileName;
    Button bLogout;
    Button bChangePass;
    ImageButton ibProfileImage;
    OnFragmentInteractionListener mListener;
    EditText currentPassword;
    EditText newPassword;
    EditText confirmPassword;
    Bitmap selectedImage = null;
    ImageView imagePreview;
    File photoFile;
    Uri photoFileUri;
    Bitmap mSelectedImage = null;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        super.onCreate(savedInstanceState);

        firstName = mView.findViewById(R.id.first_name_edit_text);
        lastName = mView.findViewById(R.id.last_name_edit_text);

        //profileName.setText(UserUtilities.getFirstName(getContext()) + UserUtilities.getLastName(getContext()));

        bChangePass = mView.findViewById(R.id.btChangePass);
        bChangePass.setOnClickListener(v -> {
            mListener.beginFragment(MainActivityContainer.AuthFragmentType.CHANGE_PASSWORD, true, true);
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

            //Make image a byte array to store in server
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            mSelectedImage.compress(Bitmap.CompressFormat.JPEG, 60, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

        });
        bLogout = mView.findViewById(R.id.btLogout);
        bLogout.setOnClickListener(v -> {
            UserUtilities.removeCachedUserInfo(getActivity());
            mListener.logout();
        });
        return mView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProfileFragment.OnFragmentInteractionListener) {
            mListener = (ProfileFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public interface OnFragmentInteractionListener {
        void beginFragment(MainActivityContainer.AuthFragmentType fragmentType, boolean setTransition,
                           boolean addToBackStack);

        void logout();
    }

    /**
     * Get image data and process accordingly based on whether photo is taken with camera
     * or uploaded from user gallery
     *
     * @param requestCode specifies how picture was gotten
     * @param resultCode  what will happen with image
     * @param data        data about the image
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }

        switch (requestCode) {
            case PICK_PHOTO:
                // Do something with the photo based on Uri
                try {

                    Uri photoUri = data.getData();
                    mSelectedImage = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);

                    // Load the selected image into a preview
                    imagePreview.setVisibility(View.VISIBLE);
                    imagePreview.setImageBitmap(mSelectedImage);

                    readExif(photoUri);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case REQUEST_IMAGE_CAPTURE:
                // by this point we have the camera photo on disk
                mSelectedImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

                // RESIZE BITMAP, see section below
                // Load the taken image into a preview
                ImageView imagePreview = mView.findViewById(R.id.image_preview);
                imagePreview.setVisibility(View.VISIBLE);
                imagePreview.setImageBitmap(mSelectedImage);

                readExif(photoFileUri);
                break;
        }
    }

    /**
     * Read data from image
     *
     * @param uri image uri
     */
    void readExif(Uri uri) {

        try {
            InputStream is = getContext().getContentResolver().openInputStream(uri);
            ExifInterface exifInterface = new ExifInterface(is);

            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    imagePreview.setRotation(90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    imagePreview.setRotation(180);
                case ExifInterface.ORIENTATION_ROTATE_270:
                    imagePreview.setRotation(270);
                default:
                    imagePreview.setRotation(0);
            }

        } catch (IOException e) {

            e.printStackTrace();
        }

    }

    private static class GrpcProfileImage extends AsyncTask<String, Void, Naturae.ProfileImageReply> {
        private final OnFragmentInteractionListener mListener;
        private final WeakReference<Activity> activity;
        private ManagedChannel channel;
        private String mEncodedImage;

        private GrpcProfileImage(OnFragmentInteractionListener mListener, Activity activity, String encodedImage) {
            this.mListener = mListener;
            this.activity = new WeakReference<>(activity);
            this.mEncodedImage = encodedImage;
        }

        @Override
        protected Naturae.ProfileImageReply doInBackground(String... params) {
            Naturae.ProfileImageReply reply;
            try {
                //Create a channel to connect to the server
                channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
                //Create a stub for with the channel
                ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
                //Create an gRPC create account request
                Naturae.ProfileImageRequest request = Naturae.ProfileImageRequest.newBuilder()
                        .setAppKey(Constants.NATURAE_APP_KEY)
                        .setAccessToken(UserUtilities.getAccessToken(activity.get()))
                        .setEncodedImage(mEncodedImage).build();
                //Send the request to the server and set reply to equal the response back from the server
                reply = stub.getProfileImage(request);

            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                pw.flush();
                return null;
            }
            return reply;

        }

        @Override
        protected void onPostExecute(Naturae.ProfileImageReply reply) {
            super.onPostExecute(reply);
            //Shut down the gRPC channel
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            //Check if reply is equal to null. If it's equal to null then there was an error with the server or phone
            //while communicating with the server.
            if (reply != null) {

            } else {
                displayError((String) activity.get().getText(R.string.internet_connection));

            }
        }
        /**
         * Create an dialog box that display the error
         *
         * @param message the error message to be display
         */
        private void displayError(String message) {
            //Create an instance of Alert Dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity.get());
            alertDialogBuilder.setTitle("Error").setMessage(message).setPositiveButton(R.string.ok, (dialog, which) -> {
                dialog.cancel();
            }).show();
        }
    }
}




