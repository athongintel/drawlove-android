package com.immortplanet.drawlove.fragment.friend;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.model.Request;
import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.util.SimpleDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by tom on 5/2/17.
 */

public class FriendSearchFragment extends Fragment {

    View thisView;
    Button btSearch;
    ListView liUser;
    EditText txtSearch;

    static User currentUser = (User) DataSingleton.getDataSingleton().data.get("currentUser");

    public FriendSearchFragment(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.friend_search_fragment, null);
        btSearch = (Button)thisView.findViewById(R.id.btSearch);
        liUser = (ListView)thisView.findViewById(R.id.liUser);
        txtSearch = (EditText)thisView.findViewById(R.id.txtSearch);
        btSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("search", txtSearch.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                HttpRequest request = new HttpRequest("POST", "/user/search", jsonObject, new HttpCallback() {
                    @Override
                    public void finished(JSONObject jsonObject) {
                        try {
                            JSONArray arUsers = (JSONArray)(jsonObject.getJSONArray("users"));
                            ArrayList<User> arList = new ArrayList<User>();
                            for (int i=0; i<arUsers.length(); i++){
                                User u = new User((JSONObject)arUsers.get(i));
                                arList.add(u);
                            }
                            liUser.setAdapter(new UserAdapter(getActivity(), R.layout.friend_search_user, arList));
                        } catch (JSONException e) {
                            SimpleDialog dialog = new SimpleDialog(getActivity(), "Error", "Data error.", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            dialog.show();
                            e.printStackTrace();
                        }
                    }
                }, new HttpCallback() {
                    @Override
                    public void finished(JSONObject jsonObject) {
                        String message = "Unknown";
                        try{
                            message = jsonObject.getString("reasonMessage");
                        }
                        catch(JSONException e) {
                            e.printStackTrace();
                        }
                        SimpleDialog dialog = new SimpleDialog(getActivity(), "Error", message, new DialogInterface.OnClickListener() {
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
            userView = inflater.inflate(R.layout.friend_search_user, null);
            TextView txtChatID = (TextView)userView.findViewById(R.id.txtChatID);
            TextView txtJoinedDate = (TextView)userView.findViewById(R.id.txtJoinedDate);
            final TextView txtStatus = (TextView)userView.findViewById(R.id.txtStatus);
            final ImageButton btAction = (ImageButton)userView.findViewById((R.id.btAction));
            btAction.setVisibility(View.GONE);
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
                        HttpRequest request = new HttpRequest("POST", "/user/request/friend", jsonObject, new HttpCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                //-- on success update view
                                txtStatus.setText("Pending");
                                btAction.setVisibility(View.GONE);
                                Request request = new Request(jsonObject);
                                currentUser.requests.add(request);
                            }
                        }, new HttpCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                String message = "Cannot send request";
                                try{
                                    message = jsonObject.getString("reasonMessage");
                                }
                                catch(JSONException e){
                                    e.printStackTrace();
                                }
                                SimpleDialog dialog = new SimpleDialog(getActivity(), "Error", message, new DialogInterface.OnClickListener() {
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
            txtJoinedDate.setText(u.joinedDate);
            txtChatID.setText(u.chatID);
            return userView;
        }
    }
}
