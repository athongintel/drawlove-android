package com.immortplanet.drawlove.fragment.friend;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Bundle;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.model.Request;
import com.immortplanet.drawlove.util.JsonCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.util.SimpleDialog;
import com.immortplanet.drawlove.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by tom on 5/2/17.
 */

public class FriendSearchFragment extends Fragment {

    View thisView;
    ListView liUser;
    EditText txtSearch;
    ProgressBar prLoading;
    TextView txtInfo;

    public FriendSearchFragment(){
    }

    private void search(){
        InputMethodManager in = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(txtSearch.getWindowToken(), 0);
        txtSearch.clearFocus();

        prLoading.setVisibility(View.VISIBLE);
        txtInfo.setVisibility(View.GONE);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("search", txtSearch.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        HttpRequest request = new HttpRequest("POST", "/user/search", jsonObject, new JsonCallback() {
            @Override
            public void finished(JSONObject jsonObject) {
                prLoading.setVisibility(View.GONE);
                try {
                    JSONArray arUsers = (JSONArray)(jsonObject.getJSONArray("users"));
                    if (arUsers.length()==0){
                        txtInfo.setText("No result found");
                        txtInfo.setVisibility(View.VISIBLE);
                    }
                    ArrayList<User> arList = new ArrayList<User>();
                    for (int i=0; i<arUsers.length(); i++){
                        User u = new User((JSONObject)arUsers.get(i));
                        arList.add(u);
                    }
                    liUser.setAdapter(new UserAdapter(getActivity(), R.layout.user_fragment, arList));
                } catch (JSONException e) {
                    new SimpleDialog(getActivity(), "Error", "Data error.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                    e.printStackTrace();
                }
            }
        }, new JsonCallback() {
            @Override
            public void finished(JSONObject jsonObject) {
                prLoading.setVisibility(View.GONE);
                txtInfo.setText("Some errors occurred. Please retry later.");
                txtInfo.setVisibility(View.VISIBLE);
            }
        });
        request.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.friend_search_fragment, null);
        liUser = (ListView)thisView.findViewById(R.id.listView);
        txtSearch = (EditText)thisView.findViewById(R.id.txtSearch);
        prLoading = (ProgressBar)thisView.findViewById(R.id.prLoading);
        txtInfo = (TextView)thisView.findViewById(R.id.txtInfo);
        prLoading.setVisibility(View.GONE);
        txtInfo.setVisibility(View.GONE);

        txtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    search();
                    return true;
                }
                return false;
            }
        });

        return thisView;
    }

    class UserAdapter extends ArrayAdapter<User>{

        int resourceId;
        Context context;
        ArrayList<User> arrayList;
        LayoutInflater inflater;

        public UserAdapter(Context context, int resource, ArrayList<User> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resourceId = resource;
            arrayList = objects;
            inflater = ((Activity)context).getLayoutInflater();
        }

        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public User getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View userView = null;
            //-- populate a view with data then return
            final User u = arrayList.get(position);
            userView = inflater.inflate(R.layout.user_fragment, null);
            TextView txtChatID = (TextView)userView.findViewById(R.id.txtChatID);
            TextView txtJoinedDate = (TextView)userView.findViewById(R.id.txtJoinedDate);
            ImageView imgPhoto = (ImageView) userView.findViewById(R.id.imgPhoto);
            final TextView txtStatus = (TextView)userView.findViewById(R.id.txtStatus);
            final ImageView btAction = (ImageView)userView.findViewById((R.id.btAction));
            btAction.setVisibility(View.GONE);
            final User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");

            HashMap<String, Bitmap> photoPool = (HashMap<String, Bitmap>) DataSingleton.getDataSingleton().get("photoPool");
            if (photoPool.get(u._id) == null){
                photoPool.put(u._id, Util.decodeBase64(u.profilePhoto));
            }
            imgPhoto.setImageBitmap(photoPool.get(u._id));

            if (currentUser._id.equals(u._id)){
                txtStatus.setText("Just you :)");
            }
            else if (currentUser.isFriend(u._id)){
                txtStatus.setText("Friend");
            }
            else if (currentUser.isBlocking(u._id, "friend")){
                txtStatus.setText("Blocked");
            } else if (currentUser.isBlocked(u._id, "friend")) {
                txtStatus.setText("Blocked you");
            }
            else if (currentUser.isPending(u._id, "friend")){
                txtStatus.setText("Pending");
            }
            else if (currentUser.isReceiving(u._id, "friend")){
                txtStatus.setText("Waiting you");
            }
            else{
                txtStatus.setText("Add friend");
                btAction.setVisibility(View.VISIBLE);
                btAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //-- send friend request
                        JSONObject jsonObject = new JSONObject();
                        try{
                            jsonObject.put("userID", u._id);
                        }
                        catch(JSONException e){
                            e.printStackTrace();
                        }
                        HttpRequest request = new HttpRequest("POST", "/user/request/friend", jsonObject, new JsonCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                //-- on success update view
                                txtStatus.setText("Pending");
                                btAction.setVisibility(View.GONE);
                                Request request = new Request(jsonObject);
                                currentUser.sentRequests.add(request);
                            }
                        }, new JsonCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                String message = "Cannot send request";
                                try{
                                    message = jsonObject.getString("reasonMessage");
                                }
                                catch(JSONException e){
                                    e.printStackTrace();
                                }
                                new SimpleDialog(getActivity(), "Error", message, new DialogInterface.OnClickListener() {
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
            txtJoinedDate.setText(u.joinedDate);
            txtChatID.setText(u.chatID);
            return userView;
        }
    }
}
