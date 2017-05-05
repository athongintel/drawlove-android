package com.immortplanet.drawlove.fragment.friend;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Request;
import com.immortplanet.drawlove.model.User;

import java.util.ArrayList;
/**
 * Created by tom on 5/2/17.
 */

public class FriendRequestFragment extends Fragment {

    View thisView;

    public FriendRequestFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.friend_list_view_fragment, null);
        ListView liRequest = (ListView)thisView.findViewById(R.id.liFriends);
        User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
        liRequest.setAdapter(new FriendRequestFragment.RequestAdapter(getActivity(), R.layout.request_fragment, currentUser.sentRequests));
        return thisView;
    }

    class RequestAdapter extends ArrayAdapter<Request> {

        int resourceId;
        Context context;
        ArrayList<Request> arrayList;
        LayoutInflater inflater;

        public RequestAdapter(Context context, int resource, ArrayList<Request> objects) {
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
        public Request getItem(int position) {
            return arrayList.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View userView = null;
            //-- populate a view with data then return
            final Request request = arrayList.get(position);
            userView = inflater.inflate(resourceId, null);
            TextView txtSenderReceiver = (TextView)userView.findViewById(R.id.txtSenderReceiver);
            TextView txtDate = (TextView)userView.findViewById(R.id.txtDate);
            final TextView txtStatus = (TextView)userView.findViewById(R.id.txtStatus);
            final Button btAction = (Button) userView.findViewById((R.id.btAction));
            btAction.setVisibility(View.GONE);

            if (request.type.equals("friend")){
                txtSenderReceiver.setText("You sent a friend request to " + request.requestData[1]);
            }
            else if (request.type.equals("group")){
                txtSenderReceiver.setText("You wanted to add " + request.requestData[3] + " to group " + request.requestData[1]);
            }

            if (request.status.equals("pending")){
                txtStatus.setText("Waiting for response");
                btAction.setText("Cancel");
                btAction.setVisibility(View.VISIBLE);
                btAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
            }
            else if (request.status.equals("accepted")){
                txtStatus.setText("Accepted");
            }
            else if (request.status.equals("rejected")){
                txtStatus.setText("Rejected");

            }
            else if (request.status.equals("blocked")){
                txtStatus.setText("Blocked");
            }

            txtDate.setText(request.requestDate);

            return userView;
        }
    }
}
