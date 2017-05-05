package com.immortplanet.drawlove;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Request;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.util.SimpleDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //-- set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_login);
        final EditText txtChatID = (EditText)findViewById(R.id.txtChatID);
        final EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
        final CheckBox chRemember = (CheckBox) findViewById(R.id.chRemember);
        final ProgressBar prLogin = (ProgressBar)findViewById(R.id.prLogin);

        String chatID="";
        String password="";

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chatID = (String) bundle.get("chatID");
            password = (String) bundle.get("password");
        }

        txtChatID.setText(chatID);
        txtPassword.setText(password);
        prLogin.setVisibility(View.GONE);

        final Button btLogin = (Button)findViewById(R.id.btLogin);
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
                        prLogin.setVisibility(View.GONE);
                        final User currentUser = new User(jsonObject);
                        HttpRequest allRequest = new HttpRequest("GET", "/user/request/all", null, new HttpCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                try {
                                    currentUser.friends = new ArrayList<>();
                                    JSONArray arFriends = (JSONArray)jsonObject.getJSONArray("friends");
                                    for (int i=0; i< arFriends.length(); i++){
                                        User user = new User((JSONObject)arFriends.get(i));
                                        currentUser.friends.add(user);
                                    }

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
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                DataSingleton.getDataSingleton().put("currentUser", currentUser);
                                Intent iChat = new Intent(getApplicationContext(), ChatActivity.class);
                                startActivity(iChat);
                                finish();
                            }
                        }, new HttpCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                SimpleDialog dialog = new SimpleDialog(LoginActivity.this, "Error", "Cannot load user info. Retry later.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                dialog.show();
                            }
                        });
                        allRequest.execute();

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

                        SimpleDialog dialog = new SimpleDialog(LoginActivity.this, "Error", reasonMessage, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });
                request.execute();
            }
        });
    }
}
