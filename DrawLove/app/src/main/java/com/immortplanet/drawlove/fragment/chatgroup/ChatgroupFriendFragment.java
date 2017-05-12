package com.immortplanet.drawlove.fragment.chatgroup;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Group;
import com.immortplanet.drawlove.model.Request;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.util.SimpleDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by tom on 5/10/17.
 */

@SuppressLint("ValidFragment")
public class ChatgroupFriendFragment extends Fragment{

    ListView liUser;
    RadioGroup rgrJoin;
    TextView txtInfo;

    ArrayAdapter<User> arNotyet;
    Group chatGroup;

    public ChatgroupFriendFragment(Group group){
        super();
        this.chatGroup = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_chat_group_friend, container, false);
        rgrJoin = (RadioGroup) thisView.findViewById(R.id.rgrJoin);
        txtInfo = (TextView)thisView.findViewById(R.id.txtInfo);
        liUser = (ListView) thisView.findViewById(R.id.liUser);

        rgrJoin.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes final int checkedId) {
                final User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
                final HashMap<String, User> allUsers = (HashMap<String, User>) DataSingleton.getDataSingleton().get("allUsers");

                //-- check and load for missing Users
                ArrayList<String> missingUsers = new ArrayList<String>();
                for (String userID : chatGroup.members){
                    if (allUsers.get(userID) ==null){
                        missingUsers.add(userID);
                    }
                }

                final Runnable runner = new Runnable() {
                    @Override
                    public void run() {
                    switch (checkedId){
                        case R.id.rdJoined:
                            ArrayAdapter<User> arJoined;
                            ArrayList<User> listUsers = new ArrayList<User>();
                            for (String userID : chatGroup.members) {
                                listUsers.add(allUsers.get(userID));
                            }

                            arJoined = new JoinedUser(getActivity(), R.layout.friend_search_user, listUsers);
                            liUser.setAdapter(arJoined);
                            if (listUsers.size() > 0) {
                                txtInfo.setVisibility(View.GONE);
                            }
                            else{
                                txtInfo.setText("You're all alone here. Invite someone now!");
                                txtInfo.setVisibility(View.VISIBLE);
                            }
                            break;

                        case R.id.rdNotyet:
                            ArrayAdapter<User> arNotyet;
                            ArrayList<User> listUsersNotyet = new ArrayList<User>();
                            //-- check for friends that not joined this conversation
                            for (User u : currentUser.friends) {
                                if (!chatGroup.members.contains(u._id)) {
                                    listUsersNotyet.add(u);
                                }
                            }
                            arNotyet = new NotJoinedUser(getActivity(), R.layout.friend_search_user, listUsersNotyet);
                            liUser.setAdapter(arNotyet);
                            if (listUsersNotyet.size() > 0) {
                                txtInfo.setVisibility(View.GONE);
                            } else {
                                txtInfo.setText("All your friends has joined. Enjoy!");
                                txtInfo.setVisibility(View.VISIBLE);
                            }
                            break;
                        default:
                            break;
                    }
                    }
                };

                if (missingUsers.size()>0){
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put("userIDs", missingUsers.toArray());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HttpRequest request = new HttpRequest("POST", "/user/get_users", jsonObject, new HttpCallback() {
                        @Override
                        public void finished(JSONObject jsonObject) {
                        JSONArray arUsers = null;
                        try {
                            arUsers = jsonObject.getJSONArray("users");
                        } catch (JSONException e) {
                            arUsers = new JSONArray();
                            e.printStackTrace();
                        }
                        for (int i=0; i<arUsers.length(); i++){
                            try {
                                User u = new User(arUsers.getJSONObject(i));
                                allUsers.put(u._id, u);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        runner.run();
                        }
                    }, new HttpCallback() {
                        @Override
                        public void finished(JSONObject jsonObject) {
                            String reasonMessage = "Unknown";
                            if (jsonObject != null) {
                                try {
                                    reasonMessage = jsonObject.getString("reasonMessage");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            txtInfo.setText("Error occurred. Retry later: " + reasonMessage);
                            txtInfo.setVisibility(View.VISIBLE);
                        }
                    });
                    request.execute();
                }
                else {
                    runner.run();
                }
            }
        });

        txtInfo.setVisibility(View.GONE);
        rgrJoin.check(R.id.rdJoined);

        return thisView;
    }


    class JoinedUser extends ArrayAdapter<User>{

        List<User> users;
        int resource;

        public JoinedUser(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> objects) {
            super(context, resource, objects);
            users = objects;
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View thisView = (View) inflater.inflate(resource, null, false);

            User u = users.get(position);
            TextView txtChatID = (TextView) thisView.findViewById(R.id.txtChatID);
            TextView txtJoinedDate = (TextView) thisView.findViewById(R.id.txtJoinedDate);
            TextView txtStatus = (TextView) thisView.findViewById(R.id.txtStatus);
            ImageButton btAction = (ImageButton) thisView.findViewById(R.id.btAction);

            txtChatID.setText(u.chatID);
//          TODO:  txtStatus.setText("current_status");
            txtStatus.setVisibility(View.GONE);
            btAction.setVisibility(View.GONE);
            //-- find corresponding request to extract date
            String joinedDate = "creator";
            for (Request r : chatGroup.requests){
                if (r.receiver.equals(u._id) && r.status.equals("accepted")){
                    joinedDate = r.responseDate;
                    break;
                }
            }
            txtJoinedDate.setText(joinedDate);

            return thisView;
        }
    }

    class NotJoinedUser extends ArrayAdapter<User>{

        List<User> users;
        int resource;

        public NotJoinedUser(@NonNull Context context, @LayoutRes int resource, @NonNull List<User> objects) {
            super(context, resource, objects);
            users = objects;
            this.resource = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View thisView = (View) inflater.inflate(resource, null, false);

            final User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");

            final User u = users.get(position);
            TextView txtChatID = (TextView) thisView.findViewById(R.id.txtChatID);
            final TextView txtJoinedDate = (TextView) thisView.findViewById(R.id.txtJoinedDate);
            final TextView txtStatus = (TextView) thisView.findViewById(R.id.txtStatus);
            final ImageButton btAction = (ImageButton) thisView.findViewById(R.id.btAction);
            btAction.setVisibility(View.GONE);

            txtChatID.setText(u.chatID);

            boolean locked = false;
            //-- check if this user is being invited
            for (Request request : chatGroup.requests){
                if (u._id.equals(request.receiver)) {
                    if ("pending".equals(request.status) || "blocked".equals(request.status)) {
                        locked = true;
                        if ("pending".equals(request.status)){
                            txtStatus.setText("Pending");
                        }
                        else{
                            txtStatus.setText("Blocked");
                        }
                        if (currentUser._id.equals(request.sender)) {
                            txtJoinedDate.setText("You invited");
                        } else {
                            txtJoinedDate.setText("Invited by " + request.requestData[2]);
                        }
                        locked = true;
                        break;
                    }
                }
            }

            if (locked){
                btAction.setVisibility(View.GONE);
            }
            else{
                btAction.setVisibility(View.VISIBLE);
                txtJoinedDate.setVisibility(View.GONE);
                txtStatus.setVisibility(View.GONE);
                //-- set action listener
                btAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("userID", u._id);
                            jsonObject.put("groupID", chatGroup._id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        HttpRequest request = new HttpRequest("POST", "/user/request/add_to_group", jsonObject, new HttpCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                Request r = new Request(jsonObject);
                                btAction.setVisibility(View.GONE);
                                txtStatus.setText("Pending");
                                txtJoinedDate.setText("You invited");
                                txtStatus.setVisibility(View.VISIBLE);
                                txtJoinedDate.setVisibility(View.VISIBLE);
                                chatGroup.requests.add(r);
                                currentUser.sentRequests.add(r);
                                Toast.makeText(getActivity(), "Request sent", Toast.LENGTH_SHORT).show();
                            }
                        }, new HttpCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                String reasonMessage = "Unknown";
                                try {
                                    reasonMessage = jsonObject.getString("reasonMessage");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                SimpleDialog dialog = new SimpleDialog(getActivity(), "Error", reasonMessage, new DialogInterface.OnClickListener() {
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

            return thisView;
        }
    }
}
