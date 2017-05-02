package com.immortplanet.drawlove;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.util.SimpleDialog;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends Activity {


    private boolean[] validation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //-- set fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register);

        //-- get views
        final EditText txtChatID = (EditText)findViewById(R.id.txtChatID);
        final EditText txtPassword = (EditText)findViewById(R.id.txtPassword);
        final EditText txtRePassword = (EditText)findViewById(R.id.txtRePassword);
        final EditText txtEmail = (EditText)findViewById(R.id.txtEmail);
        final ProgressBar prChatID = (ProgressBar)findViewById(R.id.prChatID);
        final ProgressBar prRegister = (ProgressBar)findViewById(R.id.prRegister);
        final Button btRegister = (Button)findViewById(R.id.btRegister);

        //-- reset states
        validation = new boolean[3];
        prChatID.setVisibility(View.GONE);
        prRegister.setVisibility(View.GONE);

        //-- set listeners
        btRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation[0] & validation[1] & validation[2]){
                    //-- call register POST
                    btRegister.setEnabled(false);
                    txtChatID.setEnabled(false);
                    txtPassword.setEnabled(false);
                    txtRePassword.setEnabled(false);
                    txtEmail.setEnabled(false);

                    prRegister.setVisibility(View.VISIBLE);
                    JSONObject obj = new JSONObject();
                    try {
                        obj.put("chatID", txtChatID.getText().toString());
                        obj.put("password", txtPassword.getText().toString());
                        obj.put("email", txtEmail.getText().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpRequest request = new HttpRequest("POST", "/register", obj, new HttpCallback() {
                        @Override
                        public void finished(JSONObject jsonObject) {
                            prRegister.setVisibility(View.GONE);

                            final Intent iLogin = new Intent(RegisterActivity.this, LoginActivity.class);
                            final Bundle bundle = new Bundle();
                            bundle.putString("chatID", txtChatID.getText().toString());
                            bundle.putString("password", txtPassword.getText().toString());
                            iLogin.putExtras(bundle);

                            SimpleDialog dialog = new SimpleDialog(RegisterActivity.this, "Success", "Registration success. Please activate your account by mail.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    startActivity(iLogin, bundle);
                                }
                            });
                            dialog.show();

                        }
                    }, new HttpCallback() {
                        @Override
                        public void finished(JSONObject jsonObject) {
                            prRegister.setVisibility(View.GONE);
                            btRegister.setEnabled(true);
                            btRegister.setEnabled(true);
                            txtChatID.setEnabled(true);
                            txtPassword.setEnabled(true);
                            txtRePassword.setEnabled(true);
                            txtEmail.setEnabled(true);

                            String reasonMessage = "Unknown error";

                            if (jsonObject != null) {
                                try {
                                    reasonMessage = jsonObject.get("reasonMessage").toString();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            SimpleDialog dialog = new SimpleDialog(RegisterActivity.this, "Error", reasonMessage, new DialogInterface.OnClickListener() {
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
                else{
                    SimpleDialog dialog = new SimpleDialog(RegisterActivity.this, "Error", "Please check all inputs", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            }
        });

        txtRePassword.addTextChangedListener(new TextWatcher() {
             @Override
             public void beforeTextChanged(CharSequence s, int start, int count, int after) {

             }

             @Override
             public void onTextChanged(CharSequence s, int start, int before, int count) {

             }

             @Override
             public void afterTextChanged(Editable s) {
                 String pass = txtPassword.getText().toString();
                 String rePass = txtRePassword.getText().toString();

                 if (("").equals(pass) || !rePass.equals(pass)) {
                     txtRePassword.setError("Passwords not matched");
                     validation[1] = false;
                 } else {
                     txtRePassword.setError(null);
                     validation[1] = true;
                 }
             }
         });


        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s.toString()) && Patterns.EMAIL_ADDRESS.matcher(s).matches()){
                    txtEmail.setError(null);
                    validation[2] = true;
                }
                else{
                    txtEmail.setError("Not a valid email");
                    validation[2] = false;
                }
            }
        });

        txtChatID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!txtChatID.getText().toString().equals("")) {
                        //-- check for chatID validity
                        prChatID.setVisibility(View.VISIBLE);
                        //-- ajax call
                        JSONObject obj = new JSONObject();
                        try {
                            obj.put("chatID", txtChatID.getText().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        HttpRequest request = new HttpRequest("POST", "/checkChatID", obj, new HttpCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                prChatID.setVisibility(View.GONE);
                                txtChatID.setError(null);
                                validation[0] = true;
                            }
                        }, new HttpCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                prChatID.setVisibility(View.GONE);
                                txtChatID.setError("This ID had been taken");
                                validation[0] = false;
                            }
                        });
                        request.execute();
                    }
                    else{
                        txtChatID.setError("Cannot be empty");
                        validation[0] = false;
                    }
                }
            }
        });
    }
}
