package com.immortplanet.drawlove.fragment;


import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.util.JsonCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.LoginActivity;
import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.util.SimpleDialog;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link android.app.Fragment} subclass.
 */
public class SettingFragment extends Fragment {

    View thisView;
    TextView txtLogout;
    TextView txtChangePhoto;
    ImageView imgPhoto;
    TextView txtEmail;
    TextView txtJoinedDate;
    TextView txtChatID;

    public SettingFragment() {
        // Required empty public constructor
    }

    private void updateUI(User u){
        txtChatID.setText(u.chatID);
        txtEmail.setText(u.email);
        txtJoinedDate.setText(u.joinedDate);
        HashMap<String, Bitmap> photoPool = (HashMap<String, Bitmap>) DataSingleton.getDataSingleton().get("photoPool");
        if (photoPool.get(u._id) == null){
            photoPool.put(u._id, Util.decodeBase64(u.profilePhoto));
        }
        imgPhoto.setImageBitmap(photoPool.get(u._id));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_setting, container, false);
        txtChatID = (TextView)thisView.findViewById(R.id.txtChatID);
        txtLogout = (TextView)thisView.findViewById(R.id.txtLogout);
        txtChangePhoto = (TextView)thisView.findViewById(R.id.txtChangePhoto);
        imgPhoto = (ImageView)thisView.findViewById(R.id.imgPhoto);
        txtEmail = (TextView)thisView.findViewById(R.id.txtEmail);
        txtJoinedDate = (TextView)thisView.findViewById(R.id.txtJoinedDate);


        txtLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            HttpRequest request = new HttpRequest("GET", "/logout", null, new JsonCallback() {
                @Override
                public void finished(JSONObject jsonObject) {
                    Intent iLogin = new Intent(getActivity(), LoginActivity.class);
                    startActivity(iLogin);
                    getActivity().finish();
                }
            }, new JsonCallback() {
                @Override
                public void finished(JSONObject jsonObject) {
                    Intent iLogin = new Intent(getActivity(), LoginActivity.class);
                    startActivity(iLogin);
                    getActivity().finish();
                }
            });
            request.execute();
            }
        });

        txtChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-- image from gallery
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select a profile photo"), 1);
//                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); -- from camera
//                startActivityForResult(takePicture, 0);
            }
        });

        User user = (User) DataSingleton.getDataSingleton().get("currentUser");
        if (user == null){
            new SimpleDialog(getActivity(), "Error", "Session expired. Please login again.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent iLogin = new Intent(getActivity(), LoginActivity.class);
                    startActivity(iLogin);
                    getActivity().finish();
                }
            }).show();
        }
        else{
            updateUI(user);
        }

        return thisView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case 0:
//                if(resultCode == RESULT_OK){
//                    Uri selectedImage = imageReturnedIntent.getData();
//                    imageview.setImageURI(selectedImage);
//                }
                break;
            case 1:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    imgPhoto.setImageURI(selectedImage);
                }
                break;
        }
        //-- here get image content and call update

        final Bitmap bitmap = ((BitmapDrawable)imgPhoto.getDrawable()).getBitmap();
        JSONObject jsonObject = new JSONObject();
        final String photo = Util.encodeToBase64(bitmap);
        try {
            jsonObject.put("profilePhoto", photo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest r = new HttpRequest("POST", "/user/update_photo", jsonObject, new JsonCallback() {
            @Override
            public void finished(JSONObject jsonObject) {
                //-- update success
                User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
                currentUser.profilePhoto = photo;
                HashMap<String, Bitmap> photoPool = (HashMap<String, Bitmap>) DataSingleton.getDataSingleton().get("photoPool");
                photoPool.put(currentUser._id, bitmap);
            }
        }, new JsonCallback() {
            @Override
            public void finished(JSONObject jsonObject) {
                new SimpleDialog(getActivity(), "Error", "Cannot update profile photo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //-- revert to old photo
                        User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
                        HashMap<String, Bitmap> photoPool = (HashMap<String, Bitmap>) DataSingleton.getDataSingleton().get("photoPool");
                        imgPhoto.setImageBitmap(photoPool.get(currentUser._id));
                        dialog.dismiss();
                    }
                }).show();
            }
        });
        r.execute();
    }
}
