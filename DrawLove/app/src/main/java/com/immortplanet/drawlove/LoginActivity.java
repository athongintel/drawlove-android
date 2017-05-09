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

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chatID = (String) bundle.get("chatID");
            password = (String) bundle.get("password");
        }
        else{
            //-- try to get from preferences
            chatID = DrawLovePreferences.getInstance(this).getString("USERNAME", "");
            password = DrawLovePreferences.getInstance(this).getString("PASSWORD", "");
        }

        txtChatID.setText(chatID);
        txtPassword.setText(password);
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
                        //-- save login and password if check is seleced
                        if (chRemember.isChecked()){
                            SharedPreferences.Editor editor = DrawLovePreferences.getInstance(getApplicationContext()).edit();
                            editor.putString("USERNAME", txtChatID.getText().toString());
                            editor.putString("PASSWORD", txtPassword.getText().toString());
                            editor.commit();
                        }
                        prLogin.setVisibility(View.GONE);
                        User currentUser = new User(jsonObject);
                        DataSingleton.getDataSingleton().put("currentUser", currentUser);
                        DataSingleton.getDataSingleton().put("allUsers", new HashMap<String, User>());

                        Intent iChat = new Intent(getApplicationContext(), ChatActivity.class);
                        startActivity(iChat);
                        finish();
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
