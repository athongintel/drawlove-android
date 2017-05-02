package com.immortplanet.drawlove.fragment.friend;

import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.immortplanet.drawlove.R;

/**
 * Created by tom on 5/2/17.
 */

public class FriendSearchFragment extends Fragment {

    View thisView;

    public FriendSearchFragment(){

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        thisView = inflater.inflate(R.layout.friend_search_fragment, null);
        return thisView;
    }
}
