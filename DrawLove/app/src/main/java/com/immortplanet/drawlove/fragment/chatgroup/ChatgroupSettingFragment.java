package com.immortplanet.drawlove.fragment.chatgroup;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Group;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.util.ConfirmDialog;
import com.immortplanet.drawlove.util.JsonCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.util.SimpleDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created by tom on 5/10/17.
 */

@SuppressLint("ValidFragment")
public class ChatgroupSettingFragment extends Fragment{

    Group chatGroup;

    public ChatgroupSettingFragment(Group group){
        super();
        this.chatGroup = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View thisView = inflater.inflate(R.layout.fragment_chat_group_setting, container, false);

        ImageView btLeave = (ImageView) thisView.findViewById(R.id.btLeave);
        btLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ConfirmDialog(getActivity(), "Do you want to leave this group?", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        JSONObject jsonObject = new JSONObject();
                        try {
                            jsonObject.put("groupID", chatGroup._id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        HttpRequest request = new HttpRequest("POST", "/user/leave_group", jsonObject, new JsonCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                //-- remove this group in current user list
                                User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
                                //-- remove by iterator
                                Iterator<Group> it = currentUser.groups.iterator();
                                while (it.hasNext()){
                                    Group g = it.next();
                                    if (g._id.equals(chatGroup._id)){
                                        it.remove();
                                        break;
                                    }
                                }
                                getActivity().finish();
                            }
                        }, new JsonCallback() {
                            @Override
                            public void finished(JSONObject jsonObject) {
                                System.err.print(jsonObject);
                                new SimpleDialog(getActivity(), "Error", "Error occurred. Please retry later.", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            }
                        });
                        request.execute();
                    }
                }, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        return thisView;
    }

}