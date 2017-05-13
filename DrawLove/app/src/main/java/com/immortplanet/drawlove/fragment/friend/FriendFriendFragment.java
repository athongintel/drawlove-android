package com.immortplanet.drawlove.fragment.friend;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.User;

import java.util.ArrayList;

/**
 * Created by tom on 5/2/17.
 */

public class FriendFriendFragment extends Fragment {

    View thisView;
    TextView txtInfo;

    public FriendFriendFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.friend_list_view_fragment, null);
        txtInfo = (TextView)thisView.findViewById(R.id.txtInfo);
        txtInfo.setVisibility(View.GONE);
        ListView liUser = (ListView)thisView.findViewById(R.id.liFriends);
        User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
        if (currentUser.friends.isEmpty()){
            txtInfo.setText("You don't have any friend for the moment.");
            txtInfo.setVisibility(View.VISIBLE);
        }
        liUser.setAdapter(new FriendFriendFragment.FriendAdapter(getActivity(), R.layout.user_fragment, currentUser.friends));
        return thisView;
    }

    class FriendAdapter extends ArrayAdapter<User> {

        int resourceId;
        Context context;
        ArrayList<User> arrayList;
        LayoutInflater inflater;

        public FriendAdapter(Context context, int resource, ArrayList<User> objects) {
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
            final User u = arrayList.get(position);
            userView = inflater.inflate(resourceId, null);
            TextView txtChatID = (TextView)userView.findViewById(R.id.txtChatID);
            TextView txtJoinedDate = (TextView)userView.findViewById(R.id.txtJoinedDate);
            final TextView txtStatus = (TextView)userView.findViewById(R.id.txtStatus);
            final ImageView btAction = (ImageView) userView.findViewById((R.id.btAction));
            btAction.setVisibility(View.GONE);
            txtStatus.setVisibility(View.GONE);
            txtJoinedDate.setText("joined on " + u.joinedDate);
            txtChatID.setText(u.chatID);
            return userView;
        }
    }

}
