package com.immortplanet.drawlove.fragment;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.immortplanet.drawlove.model.Group;
import com.immortplanet.drawlove.util.CallbackWithData;
import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.util.InputDialog;
import com.immortplanet.drawlove.util.SimpleDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends Fragment {

    View thisView;
    GroupAdapter adapter;
    ArrayList<Group> arList;

    public ChatGroupFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.fragment_chat_group, container, false);
        final GridView gvGroups = (GridView)(thisView.findViewById(R.id.gvGroups));
        final ProgressBar prLoading = (ProgressBar)(thisView.findViewById(R.id.prLoading));
        final TextView txtLoadingInfo = (TextView)(thisView.findViewById(R.id.txtLoadingInfo));

        prLoading.setVisibility(View.VISIBLE);
        txtLoadingInfo.setText("Loading groups...");
        arList = new ArrayList<>();

        HttpRequest request = new HttpRequest("GET", "/group", null, new HttpCallback() {
            @Override
            public void finished(JSONObject jsonObject) {
                prLoading.setVisibility(View.GONE);
                txtLoadingInfo.setVisibility(View.GONE);
                //-- populate ListView with items
                JSONArray groups = null;
                try {
                    groups = (JSONArray)(jsonObject.getJSONArray("groups"));
                    for (int i=0; i<groups.length(); i++){
                        Group g = new Group((JSONObject) groups.get(i));
                        g.id = i;
                        arList.add(g);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter = new GroupAdapter(getActivity(), 0, arList);
                gvGroups.setAdapter(adapter);
            }
        }, new HttpCallback() {
            @Override
            public void finished(JSONObject jsonObject) {
                prLoading.setVisibility(View.GONE);
                txtLoadingInfo.setText("Error occurred.");
            }
        });
        request.execute();
        return thisView;
    }

    class GroupAdapter extends ArrayAdapter<Group>{

        ArrayList<Group> arrayList;
        Context context;
        LayoutInflater inflater;
        int resource;

        public GroupAdapter(Context context, int resource, ArrayList<Group> objects) {
            super(context, resource, objects);
            this.arrayList = objects;
            this.context = context;
            this.inflater = ((Activity)context).getLayoutInflater();
        }

        @Override
        public int getCount() {
            return arrayList.size()+1;
        }

        @Override
        public Group getItem(int position) {
            if (position<arrayList.size()){
                return arrayList.get(position);
            }
            else{
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            if (position < arrayList.size()){
                return arrayList.get(position).id;
            }
            else{
                return -1;
            }
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View groupView = null;
            View.OnClickListener onClickListener = null;
            if (position < arrayList.size()){
                //-- populate a view with data then return
                groupView = inflater.inflate(R.layout.chat_group, null);
                TextView txtGroupName = (TextView)groupView.findViewById(R.id.txtGroupName);
                TextView txtMemberCount = (TextView)groupView.findViewById(R.id.txtMemberCount);

                txtGroupName.setText(arrayList.get(position).name);
                txtMemberCount.setText(arrayList.get(position).members.length + " member(s)");
                onClickListener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //-- load ChatGroupActivity with this group
                    }
                };
            }
            else{
                //-- return add group view
                try {
                    final Context _context = getActivity();
                    groupView = inflater.inflate(R.layout.chat_group_new, null);
                    onClickListener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            InputDialog inputDialog = new InputDialog(_context, "Create new group", "Group name", new CallbackWithData() {
                                @Override
                                public void callback(Object data) {
                                    //-- http request to create group
                                    String groupName = (String)data;
                                    JSONObject jsonObject = new JSONObject();
                                    try {
                                        jsonObject.put("name", groupName);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    HttpRequest request = new HttpRequest("POST", "/group", jsonObject, new HttpCallback() {
                                        @Override
                                        public void finished(JSONObject jsonObject) {
                                            //-- reload
                                            Group g = new Group(jsonObject);
                                            g.id = adapter.getCount()-1;
                                            adapter.add(g);
                                        }
                                    }, new HttpCallback() {
                                        @Override
                                        public void finished(JSONObject jsonObject) {
                                            String reasonMessage = "Unknown";
                                            try{
                                                reasonMessage = jsonObject.getString("reasonMessage");
                                            }
                                            catch (JSONException e){
                                                e.printStackTrace();
                                            }
                                            SimpleDialog simpleDialog = new SimpleDialog(_context, "Error", reasonMessage, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                }
                                            });
                                            simpleDialog.show();
                                        }
                                    });
                                    request.execute();
                                }
                            }, new CallbackWithData() {
                                @Override
                                public void callback(Object data) {
                                }
                            });
                            inputDialog.show();
                        }
                    };
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            groupView.setOnClickListener(onClickListener);
            return groupView;
        }
    }

}
