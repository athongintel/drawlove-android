package com.immortplanet.drawlove;


import android.os.Bundle;
import android.app.Fragment;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatGroupFragment extends Fragment {

    View thisView;

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

        HttpRequest request = new HttpRequest("GET", "/group", null, new HttpCallback() {
            @Override
            public void finished(JSONObject jsonObject) {
                prLoading.setVisibility(View.GONE);
                txtLoadingInfo.setVisibility(View.GONE);
                //-- populate ListView with items
                gvGroups.setAdapter(new GroupAdapter(jsonObject));
                gvGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        System.out.println("catch");
                    }
                });
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

    class GroupAdapter extends BaseAdapter{

        JSONArray groups;
        LayoutInflater inflater;

        public GroupAdapter(JSONObject jsonObject){
            inflater = getActivity().getLayoutInflater();
            try {
                groups = (JSONArray)(jsonObject.getJSONArray("groups"));
                for (int i=0; i<groups.length(); i++){
                    ((JSONObject)groups.get(i)).put("id", i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public int getCount() {
            return groups.length()+1;
        }

        @Override
        public Object getItem(int position) {
            if (position<groups.length()){
                try {
                    return groups.get(position);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            else{
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            long id;
            if (position < groups.length()){
                try {
                    id = ((JSONObject)groups.get(position)).getLong("id");
                } catch (Exception e) {
                    e.printStackTrace();
                    id = -1;
                }
            }
            else{
                id = -1;
            }
            return id;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View groupView = null;
            if (position < groups.length()){
                //-- populate a view with data then return
                groupView = inflater.inflate(R.layout.chat_group, null);
                TextView txtGroupName = (TextView)groupView.findViewById(R.id.txtGroupName);
                TextView txtMemberCount = (TextView)groupView.findViewById(R.id.txtMemberCount);

                try {
                    JSONObject obj = (JSONObject)groups.get(position);
                    txtGroupName.setText(obj.getString("name"));
                    JSONArray arMember = (JSONArray)obj.getJSONArray("members");
                    txtMemberCount.setText(arMember.length() + " member(s)");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                //-- return add group view
                try {
                    groupView = inflater.inflate(R.layout.chat_group_new, null);
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            return groupView;
        }
    }

}
