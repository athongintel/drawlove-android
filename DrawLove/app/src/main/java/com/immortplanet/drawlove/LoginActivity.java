package com.immortplanet.drawlove;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Request;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.util.DrawLovePreferences;
import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.util.SimpleDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends Activity {

    EditText txtChatID;
    EditText txtPassword;
    CheckBox chRemember;
    ProgressBar prLogin;
    Button btLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-- set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        txtChatID = (EditText)findViewById(R.id.txtChatID);
        txtPassword = (EditText)findViewById(R.id.txtPassword);
        chRemember = (CheckBox) findViewById(R.id.chRemember);
        prLogin = (ProgressBar)findViewById(R.id.prLogin);
        btLogin = (Button)findViewById(R.id.btLogin);

        String chatID="";
        String password="";
        boolean remember = false;

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chatID = (String) bundle.get("chatID");
            password = (String) bundle.get("password");
        }
        else{
            //-- try to get from preferences
            chatID = DrawLovePreferences.getInstance(LoginActivity.this).getString("USERNAME", "");
            password = DrawLovePreferences.getInstance(LoginActivity.this).getString("PASSWORD", "");
            remember = DrawLovePreferences.getInstance(LoginActivity.this).getBoolean("REMEMBER", false);
        }

        txtChatID.setText(chatID);
        txtPassword.setText(password);
        chRemember.setChecked(remember);
        prLogin.setVisibility(View.GONE);

        btLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //-- authentication
                txtChatID.setEnabled(false);
                txtPassword.setEnabled(false);
                chRemember.setEnabled(false);
                btLogin.setEnabled(false);
                prLogin.setVisibility(View.VISIBLE);

                JSONObject obj = new JSONObject();
                try {
                    obj.put("chatID", txtChatID.getText().toString());
                    obj.put("password", txtPassword.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpRequest request = new HttpRequest("POST", "/login", obj, new HttpCallback() {
                    @Override
                    public void finished(JSONObject jsonObject) {
                        //-- authenticated
                        //-- save login and password if check is selected
                        if (chRemember.isChecked()){
                            SharedPreferences.Editor editor = DrawLovePreferences.getInstance(getApplicationContext()).edit();
                            editor.putString("USERNAME", txtChatID.getText().toString());
                            editor.putString("PASSWORD", txtPassword.getText().toString());
                            editor.putBoolean("REMEMBER", chRemember.isChecked());
                            editor.commit();
                        }
                        prLogin.setVisibility(View.GONE);
                        User currentUser = null;
                        try {
                            HashMap<String, User> allUsers = new HashMap<String, User>();

                            currentUser = new User(jsonObject.getJSONObject("user"));
                            currentUser.friends = new ArrayList<User>();
                            JSONArray arFriends = jsonObject.getJSONArray("friends");
                            for (int i=0; i<arFriends.length(); i++){
                                User u = new User(arFriends.getJSONObject(i));
                                currentUser.friends.add(u);
                                allUsers.put(u._id, u);
                            }
                            allUsers.put(currentUser._id, currentUser);

                            currentUser.sentRequests = new ArrayList<>();
                            JSONArray arRequest = (JSONArray)jsonObject.getJSONArray("sentRequests");
                            for (int i=0; i< arRequest.length(); i++){
                                Request request = new Request((JSONObject)arRequest.get(i));
                                currentUser.sentRequests.add(request);
                            }

                            currentUser.receivedRequests = new ArrayList<>();
                            JSONArray arReceivedRequests = (JSONArray)jsonObject.getJSONArray("receivedRequests");
                            for (int i=0; i< arReceivedRequests.length(); i++){
                                Request request = new Request((JSONObject)arReceivedRequests.get(i));
                                currentUser.receivedRequests.add(request);
                            }

                            DataSingleton.getDataSingleton().put("currentUser", currentUser);
                            DataSingleton.getDataSingleton().put("allUsers", allUsers);

                            Intent iChat = new Intent(getApplicationContext(), ChatActivity.class);
                            startActivity(iChat);
                            finish();
                        } catch (JSONException e) {
                            new SimpleDialog(LoginActivity.this, "Error", "Cannot load data from server.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            }).show();
                            e.printStackTrace();
                        }
                    }
                }, new HttpCallback() {
                    @Override
                    public void finished(JSONObject jsonObject) {
                        prLogin.setVisibility(View.GONE);
                        txtChatID.setEnabled(true);
                        txtPassword.setEnabled(true);
                        chRemember.setEnabled(true);
                        btLogin.setEnabled(true);

                        String reasonMessage = "Unknown error";
                        if (jsonObject != null){
                            try {
                                reasonMessage = jsonObject.get("reasonMessage").toString();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        new SimpleDialog(LoginActivity.this, "Error", reasonMessage, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                });
                request.execute();
            }
        });
    }
}
