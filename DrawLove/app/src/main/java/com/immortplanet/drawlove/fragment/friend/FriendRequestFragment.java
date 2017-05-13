package com.immortplanet.drawlove.fragment.friend;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Request;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.util.AppDateTime;
import com.immortplanet.drawlove.util.ConfirmDialog;
import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.util.SimpleDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by tom on 5/2/17.
 */

public class FriendRequestFragment extends Fragment {

    View thisView;
    TextView txtInfo;

    public FriendRequestFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.friend_list_view_fragment, null);
        ListView liRequest = (ListView)thisView.findViewById(R.id.liFriends);
        txtInfo = (TextView)thisView.findViewById(R.id.txtInfo);
        txtInfo.setVisibility(View.GONE);
        User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
        if (currentUser.sentRequests.isEmpty()){
            txtInfo.setText("Nothing to show here.");
            txtInfo.setVisibility(View.VISIBLE);
        }
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
            final ImageView btAction = (ImageView) userView.findViewById((R.id.btAction));
            btAction.setVisibility(View.GONE);

            if (request.type.equals("friend")){
                txtSenderReceiver.setText("You sent a friend request to " + request.requestData[1]);
            }
            else if (request.type.equals("group")){
                txtSenderReceiver.setText("You wanted to add " + request.requestData[3] + " to group " + request.requestData[1]);
            }

            if (request.status.equals("pending")){
                txtStatus.setText("Waiting for response");
                txtDate.setText(AppDateTime.getDateFromID(request._id));
                btAction.setVisibility(View.VISIBLE);
                btAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final Runnable removeRequest = new Runnable() {
                            @Override
                            public void run() {
                                User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
                                Iterator<Request> it = currentUser.sentRequests.iterator();
                                while (it.hasNext()){
                                    Request re = it.next();
                                    if (re._id.equals(request._id)){
                                        it.remove();
                                        break;
                                    }
                                }
                            }
                        };

                        new ConfirmDialog(getActivity(), "Do you want to cancel this request?", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("requestID", request._id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                HttpRequest httpRequest = new HttpRequest("POST", "/user/request/cancel", jsonObject, new HttpCallback() {
                                    @Override
                                    public void finished(JSONObject jsonObject) {
                                        removeRequest.run();
                                    }
                                }, new HttpCallback() {
                                    @Override
                                    public void finished(JSONObject jsonObject) {
                                        String reason = "unknown";
                                        if (jsonObject != null) {
                                            try {
                                                reason = jsonObject.getString("reason");
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        if (reason.equals("request_processed")) {
                                            new SimpleDialog(getActivity(), "Error", "This request is no more available.", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    removeRequest.run();
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                        }
                                        else{
                                            new SimpleDialog(getActivity(), "Error", "Error occurred. Please retry later.", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                        }
                                    }
                                });
                                httpRequest.execute();
                            }
                        }, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
                    }
                });
            }
            else {
                txtDate.setText(request.responseDate);
                if (request.status.equals("accepted")) {
                    txtStatus.setText("Accepted");
                } else if (request.status.equals("rejected")) {
                    txtStatus.setText("Rejected");

                } else if (request.status.equals("blocked")) {
                    txtStatus.setText("Blocked");
                }
            }

            return userView;
        }
    }
}
