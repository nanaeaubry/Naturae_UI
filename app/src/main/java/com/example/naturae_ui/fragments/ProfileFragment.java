package com.example.naturae_ui.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.naturae_ui.R;
import com.example.naturae_ui.containers.MainActivityContainer;
import com.example.naturae_ui.util.Constants;
import com.example.naturae_ui.util.UserUtilities;
import com.examples.naturaeproto.Naturae;
import com.examples.naturaeproto.ServerRequestsGrpc;
import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.concurrent.TimeUnit;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import static android.support.constraint.Constraints.TAG;

public class ProfileFragment extends Fragment {
    public final static int PICK_PHOTO = 1046;
    Fragment mChangePasswordFragment;
    View mView;
    EditText firstName;
    EditText lastName;
    TextView profileName;
    TextView profileEmail;
    Button bLogout;
    Button bChangePass;
    ImageView ibProfileImage;
    OnFragmentInteractionListener mListener;
    EditText currentPassword;
    EditText newPassword;
    EditText confirmPassword;
    File photoFile;
    Uri photoFileUri;
    Bitmap mSelectedImage;
    String profileNameString;
    String profileEmailString;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        profileNameString = (UserUtilities.getFirstName(getContext()) + " " + UserUtilities.getLastName(getContext()));
        profileEmailString = (UserUtilities.getEmail(getContext()));

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_profile, container, false);
        super.onCreate(savedInstanceState);

        mChangePasswordFragment = new ChangePasswordFragment();

        firstName = mView.findViewById(R.id.first_name_edit_text);
        lastName = mView.findViewById(R.id.last_name_edit_text);
        profileName = mView.findViewById(R.id.tvProfileName);
        profileEmail = mView.findViewById(R.id.tvProfileEmail);

        profileName.setText(profileNameString);
        profileEmail.setText(profileEmailString);

        bChangePass = mView.findViewById(R.id.btChangePass);
        bChangePass.setOnClickListener(v -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            //Acquire container id and switch fragment
            fragmentTransaction.replace(((ViewGroup) getView().getParent()).getId(), mChangePasswordFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });




        ibProfileImage = mView.findViewById(R.id.ibProfileImage);
        //Do something with the encoded image
        GetProfileImageTask imageTaskGet = new GetProfileImageTask(getActivity());
        imageTaskGet.setListener(new GetProfileImageTask.AsyncTaskListener(){
            @Override
            public void onGetProfileImageCompleted(String encodedImageLink) {
                //Display
                Picasso.get().load(encodedImageLink).placeholder(R.drawable.ic_person_black_24dp).error(R.drawable.ic_person_black_24dp).fit().transform(new RoundedTransformationBuilder().borderColor(Color.BLACK).borderWidthDp(1).cornerRadiusDp(30).oval(false).build()).centerCrop().into(ibProfileImage);
                ibProfileImage.setRotation(90f);
            }
        });

        imageTaskGet.execute();
        ibProfileImage.setOnClickListener(v -> {
            //mSelectedImage = null;
            // Create intent for picking a photo from the gallery
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                // Bring up gallery to select a photo
                startActivityForResult(intent, PICK_PHOTO);
            }

            //Make image a byte array to store in server
         //   ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
          //  mSelectedImage.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
//            byte[] byteArray = byteArrayOutputStream.toByteArray();
            //String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

           // new GrpcProfileImage(mListener,getActivity(), encodedImage).execute();

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
                  //  ibProfileImage.setVisibility(View.VISIBLE);
                    //ibProfileImage.setImageBitmap(mSelectedImage);

                    //Make image a byte array to store in server
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    mSelectedImage.compress(Bitmap.CompressFormat.JPEG, 40, byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                    SetProfileImageTask imageTask = new SetProfileImageTask(getActivity());
                    imageTask.setListener(new SetProfileImageTask.AsyncTaskListener(){
                        @Override
                        public void onSetProfileImageCompleted() {
                            //Do something with the encoded image
                            GetProfileImageTask imageTaskGet = new GetProfileImageTask(getActivity());
                            imageTaskGet.setListener(new GetProfileImageTask.AsyncTaskListener(){
                                @Override
                                public void onGetProfileImageCompleted(String encodedImageLink) {
                                    //Display
                                    ibProfileImage.setRotation(90);
                                    Picasso.get().load(encodedImageLink).placeholder(R.drawable.ic_person_black_24dp).fit().transform(new RoundedTransformationBuilder().borderColor(Color.BLACK).borderWidthDp(1).cornerRadiusDp(30).oval(false).build()).centerCrop().into(ibProfileImage);
                                }
                            });

                            imageTaskGet.execute();
                        }
                    });

                    imageTask.execute(encodedImage);


                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    /**
     * GET PROFILE IMAGE STUFF
     */
    private static class GetProfileImageTask extends AsyncTask<String, Void, Naturae.GetProfileImageReply> {
        private AsyncTaskListener listener;
        private final WeakReference<Activity> activity;
        private ManagedChannel channel;
        private String mEncodedImage;

        private GetProfileImageTask(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        protected Naturae.GetProfileImageReply doInBackground(String... params) {
            Naturae.GetProfileImageReply reply;
            try {
                //Create a channel to connect to the server
                channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
                //Create a stub for with the channel
                ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
                //Create an gRPC create account request
                Log.d(TAG, "doInBackground: GET PROFILE IMAGE REPLY");
                Naturae.GetProfileImageRequest request = Naturae.GetProfileImageRequest.newBuilder()
                        .setAppKey(Constants.NATURAE_APP_KEY)
                        .setAccessToken(UserUtilities.getAccessToken(activity.get())).build();
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
        protected void onPostExecute(Naturae.GetProfileImageReply reply) {
            super.onPostExecute(reply);
            //Shut down the gRPC channel
            try {
                channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            Log.d("Tag", "onPostExecute: 'this occurred");
            //Check if reply is equal to null. If it's equal to null then there was an error with the server or phone
            //while communicating with the server.
            if (!reply.getEncodedImage().equals("")) {
                listener.onGetProfileImageCompleted(reply.getEncodedImage());
                Log.d(TAG, "onPostExecute: ENCODED IMAGE GOT " + reply.getEncodedImage());
            }
        }

        public void setListener(AsyncTaskListener listener) {
            this.listener = listener;
        }

        public interface AsyncTaskListener {
            void onGetProfileImageCompleted(String encodedImage);
        }


        /**
         * Create an dialog box that display the error
         */
        private void displayError(String message) {
            //Create an instance of Alert Dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity.get());
            alertDialogBuilder.setTitle("Error").setMessage(message).setPositiveButton(R.string.ok, (dialog, which) -> {
                dialog.cancel();
            }).show();
        }
    }

    /**
     * SET PROFILE IMAGE TASK
     */
        private static class SetProfileImageTask extends AsyncTask<String, Void, Naturae.SetProfileImageReply> {
            private SetProfileImageTask.AsyncTaskListener listener;
            private final WeakReference<Activity> activity;
            private ManagedChannel channel;
            private String mEncodedImage;
            private String encodedImageString;

            private SetProfileImageTask(Activity activity) {
                this.activity = new WeakReference<>(activity);
            }

            @Override
            protected Naturae.SetProfileImageReply doInBackground(String... params) {
                Naturae.SetProfileImageReply reply;
                try {
                    encodedImageString = params[0];
                    //Create a channel to connect to the server
                    channel = ManagedChannelBuilder.forAddress(Constants.HOST, Constants.PORT).useTransportSecurity().build();
                    //Create a stub for with the channel
                    ServerRequestsGrpc.ServerRequestsBlockingStub stub = ServerRequestsGrpc.newBlockingStub(channel);
                    //Create an gRPC create account request
                    Log.d(TAG, "doInBackground: GET PROFILE IMAGE REPLY");
                    Naturae.SetProfileImageRequest request = Naturae.SetProfileImageRequest.newBuilder()
                            .setAppKey(Constants.NATURAE_APP_KEY)
                            .setAccessToken(UserUtilities.getAccessToken(activity.get()))
                            .setEncodedImage(encodedImageString)
                            .build();

                    //Send the request to the server and set reply to equal the response back from the server
                    reply = stub.setProfileImage(request);

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
            protected void onPostExecute(Naturae.SetProfileImageReply reply) {
                super.onPostExecute(reply);
                Log.d("Tag", "onPostExecute: 'this occurred SetProfileImageReply");
                if (reply != null) {
                    listener.onSetProfileImageCompleted();
                } else {
                    displayError((String) activity.get().getText(R.string.internet_connection));
                }

                //Shut down the gRPC channel
                try {
                    channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                //Check if reply is equal to null. If it's equal to null then there was an error with the server or phone
                //while communicating with the server.

            }

            public void setListener(SetProfileImageTask.AsyncTaskListener listener) {
                this.listener = listener;
            }

            public interface AsyncTaskListener {
                void onSetProfileImageCompleted();
            }

        /**
         * Create an dialog box that display the error
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


