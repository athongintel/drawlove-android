package com.immortplanet.drawlove.fragment.chatgroup;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Group;
import com.immortplanet.drawlove.model.User;

import java.util.ArrayList;
import java.util.List;

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
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
                switch (checkedId){
                    case R.id.rdJoined:
                        ArrayAdapter<User> arJoined;
                        ArrayList<User> listUsers = new ArrayList<User>();
                        //-- check for friends that joined this conversation
                        for (User u : currentUser.friends) {
                            if (chatGroup.members.contains(u._id)){
                                listUsers.add(u);
                            }
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
                        //-- check for friends that joined this conversation
                        for (User u : currentUser.friends) {
                            if (!chatGroup.members.contains(u._id)) {
                                listUsersNotyet.add(u);
                            }
                        }
                        arNotyet = new JoinedUser(getActivity(), R.layout.friend_search_user, listUsersNotyet);
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

            txtChatID.setText(u.chatID);

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

            User u = users.get(position);
            TextView txtChatID = (TextView) thisView.findViewById(R.id.txtChatID);

            txtChatID.setText(u.chatID);

            return thisView;
        }
    }
}
