package com.immortplanet.drawlove.fragment.friend;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Group;
import com.immortplanet.drawlove.model.Request;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.util.ConfirmDialog;
import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.util.AppDateTime;
import com.immortplanet.drawlove.util.SimpleDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by tom on 5/2/17.
 */

public class FriendNotificationFragment extends Fragment {
    View thisView;
    TextView txtInfo;

    public FriendNotificationFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.friend_list_view_fragment, null);
        ListView liRequest = (ListView)thisView.findViewById(R.id.liFriends);
        txtInfo = (TextView)thisView.findViewById(R.id.txtInfo);
        txtInfo.setVisibility(View.GONE);
        User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
        if (currentUser.receivedRequests.isEmpty()){
            txtInfo.setText("No notification to show.");
            txtInfo.setVisibility(View.VISIBLE);
        }
        liRequest.setAdapter(new FriendNotificationFragment.RequestAdapter(getActivity(), R.layout.notification_fragment, currentUser.receivedRequests));
        return thisView;
    }

    class RequestAdapter extends ArrayAdapter<Request> {

        int resourceId;
        Context context;
        ArrayList<Request> arrayList;
        LayoutInflater inflater;

        String selectedResponse="";

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
            final Spinner spAction = (Spinner) userView.findViewById((R.id.spAction));
            spAction.setVisibility(View.GONE);

            if ("friend".equals(request.type)){
                txtSenderReceiver.setText(request.requestData[0] + " would like to be your friend");
            }
            else if ("group".equals(request.type)){
                txtSenderReceiver.setText(request.requestData[2] + " tried to add you to group " + request.requestData[1]);
            }

            if (request.status.equals("pending")){
                txtDate.setText(AppDateTime.getDateFromID(request._id));
                txtStatus.setVisibility(View.GONE);
                List<String> arActions = new ArrayList<String>();
                arActions.add("Select an action");
                arActions.add("Accept");
                arActions.add("Reject");
                arActions.add("Block");
                spAction.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, arActions));
                spAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (position){
                            case 0:
                                //-- do nothing
                                break;
                            case 1:
                                selectedResponse = "accepted";
                                break;
                            case 2:
                                selectedResponse = "rejected";
                                break;
                            case 3:
                                selectedResponse = "blocked";
                        }
                        if (!"".equals(selectedResponse)) {
                            ConfirmDialog dialog = new ConfirmDialog(getActivity(), "Do you want to set this request to be " + selectedResponse + "?", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("requestID", request._id);
                                        jsonObject.put("status", selectedResponse);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    HttpRequest httpRequest = new HttpRequest("POST", "/user/request/action", jsonObject, new HttpCallback() {
                                        @Override
                                        public void finished(JSONObject jsonObject) {
                                            //-- update request status
                                            request.status = selectedResponse;
                                            spAction.setVisibility(View.GONE);
                                            if ("accepted".equals(selectedResponse)){
                                                txtStatus.setText("Accepted");
                                                User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
                                                if ("friend".equals(request.type)){
                                                    //-- a new user is returned to be friend
                                                    User u = new User(jsonObject);
                                                    HashMap<String, User> allUsers = (HashMap<String, User>)DataSingleton.getDataSingleton().get("allUsers");
                                                    currentUser.friends.add(u);
                                                    allUsers.put(u._id, u);
                                                }
                                                else if ("group".equals(request.type)){
                                                    //-- add this new group
                                                    Group g = new Group(jsonObject);
                                                    currentUser.groups.put(g._id, g);
                                                }
                                            }
                                            else if ("blocked".equals(selectedResponse)){
                                                txtStatus.setText("Blocked");
                                            }
                                            else if ("rejected".equals(selectedResponse)){
                                                txtStatus.setText("Rejected");
                                            }
                                            txtStatus.setVisibility(View.VISIBLE);
                                        }
                                    }, new HttpCallback() {
                                        @Override
                                        public void finished(JSONObject jsonObject) {
                                            selectedResponse = "";
                                            spAction.setSelection(0);
                                            new SimpleDialog(getActivity(), "Error", "Cannot update request status", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            }).show();
                                        }
                                    });
                                    httpRequest.execute();
                                }
                            }, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    selectedResponse = "";
                                    spAction.setSelection(0);
                                }
                            });
                            dialog.setCanceledOnTouchOutside(true);
                            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    dialog.dismiss();
                                    selectedResponse = "";
                                    spAction.setSelection(0);
                                }
                            });
                            dialog.show();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        selectedResponse = "";
                        spAction.setSelection(0);
                    }
                });
                spAction.setVisibility(View.VISIBLE);
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
