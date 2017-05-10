package com.immortplanet.drawlove.fragment.chatgroup;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.model.Group;

/**
 * Created by tom on 5/10/17.
 */

@SuppressLint("ValidFragment")
public class ChatgroupGameFragment extends Fragment {

    Group chatGroup;

    public ChatgroupGameFragment(Group group){
        super();
        this.chatGroup = group;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat_group_game, container, false);
        return rootView;
    }
}
