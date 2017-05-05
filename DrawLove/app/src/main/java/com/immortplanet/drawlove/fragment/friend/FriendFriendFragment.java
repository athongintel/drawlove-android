package com.immortplanet.drawlove.fragment.friend;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
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

    public FriendFriendFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.friend_list_view_fragment, null);
        ListView liUser = (ListView)thisView.findViewById(R.id.liFriends);
        User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
        liUser.setAdapter(new FriendFriendFragment.FriendAdapter(getActivity(), R.layout.friend_search_user, currentUser.friends));
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
            final ImageButton btAction = (ImageButton)userView.findViewById((R.id.btAction));
            btAction.setVisibility(View.GONE);
            txtStatus.setVisibility(View.GONE);
            txtJoinedDate.setText(u.joinedDate);
            txtChatID.setText(u.chatID);
            return userView;
        }
    }

}
