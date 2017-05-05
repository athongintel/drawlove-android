package com.immortplanet.drawlove.fragment;


import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.LoginActivity;
import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.util.SimpleDialog;
import com.immortplanet.drawlove.model.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
            HttpRequest request = new HttpRequest("GET", "/logout", null, new HttpCallback() {
                @Override
                public void finished(JSONObject jsonObject) {
                    Intent iLogin = new Intent(getActivity(), LoginActivity.class);
                    startActivity(iLogin);
                    getActivity().finish();
                }
            }, new HttpCallback() {
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

        User user = (User) DataSingleton.getDataSingleton().get("currentUser");
        if (user == null){
            SimpleDialog dialog = new SimpleDialog(getActivity(), "Error", "Session expired. Please login again.", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Intent iLogin = new Intent(getActivity(), LoginActivity.class);
                    startActivity(iLogin);
                    getActivity().finish();
                }
            });
            dialog.show();
        }
        else{
            updateUI(user);
        }

        return thisView;
    }

}
