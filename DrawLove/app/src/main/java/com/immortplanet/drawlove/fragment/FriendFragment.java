package com.immortplanet.drawlove.fragment;


import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.fragment.friend.FriendFriendFragment;
import com.immortplanet.drawlove.fragment.friend.FriendNotificationFragement;
import com.immortplanet.drawlove.fragment.friend.FriendRequestFragment;
import com.immortplanet.drawlove.fragment.friend.FriendSearchFragment;
import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Request;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;
import com.immortplanet.drawlove.util.SimpleDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendFragment extends Fragment implements View.OnClickListener{

    View thisView;

    FragmentTransaction fragmentTransaction;

    View btLastSelected;
    ImageButton btSearch;
    ImageButton btFriend;
    ImageButton btRequest;
    ImageButton btNotification;
    ProgressBar prLoading;

    Fragment frgSearch;
    Fragment frgFriend;
    Fragment frgRequest;
    Fragment frgNotification;

    public FriendFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        thisView = inflater.inflate(R.layout.fragment_friend, container, false);

        btSearch = (ImageButton)thisView.findViewById(R.id.btSearch);
        btFriend = (ImageButton)thisView.findViewById(R.id.btFriend);
        btRequest = (ImageButton)thisView.findViewById(R.id.btRequest);
        btNotification = (ImageButton)thisView.findViewById(R.id.btNotification);
        prLoading = (ProgressBar)thisView.findViewById(R.id.prLoading);
        btSearch.setOnClickListener(this);
        btFriend.setOnClickListener(this);
        btRequest.setOnClickListener(this);
        btNotification.setOnClickListener(this);

        frgSearch = new FriendSearchFragment();
        frgFriend = new FriendFriendFragment();
        frgRequest = new FriendRequestFragment();
        frgNotification = new FriendNotificationFragement();

        prLoading.setVisibility(View.VISIBLE);

        final User currentUser = (User) DataSingleton.getDataSingleton().data.get("currentUser");
        if (currentUser.requests == null || currentUser.friendRequests == null){
            HttpRequest request = new HttpRequest("GET", "/user/request/all", null, new HttpCallback() {
                @Override
                public void finished(JSONObject jsonObject) {
                    prLoading.setVisibility(View.GONE);
                    try {
                        currentUser.requests = new ArrayList<>();
                        JSONArray arRequest = (JSONArray)jsonObject.getJSONArray("requests");
                        for (int i=0; i< arRequest.length(); i++){
                            Request request = new Request((JSONObject)arRequest.get(i));
                            currentUser.requests.add(request);
                        }

                        currentUser.friendRequests = new ArrayList<>();
                        JSONArray arFriendRequests = (JSONArray)jsonObject.getJSONArray("friendRequests");
                        for (int i=0; i< arFriendRequests.length(); i++){
                            Request request = new Request((JSONObject)arFriendRequests.get(i));
                            currentUser.friendRequests.add(request);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new HttpCallback() {
                @Override
                public void finished(JSONObject jsonObject) {
                    prLoading.setVisibility(View.GONE);
                    SimpleDialog dialog = new SimpleDialog(getActivity(), "Error", "Cannot load user info. Retry later.", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
            request.execute();
        }

        selectFragment(R.id.btSearch);
        return thisView;
    }

    private void selectFragment(int id){
        View btSelected = thisView.findViewById(id);
        btSelected.setBackgroundResource(R.color.colorTabSelected);
        if (btLastSelected != null){
            btLastSelected.setBackgroundResource(R.color.colorTabNotSelected);
        }

        fragmentTransaction = getFragmentManager().beginTransaction();
        switch (id){
            case R.id.btSearch:
                btSearch.setBackgroundResource(R.color.colorTabSelected);
                fragmentTransaction.replace(R.id.friend_frame, frgSearch).commit();
                break;
            case R.id.btFriend:
                btFriend.setBackgroundResource(R.color.colorTabSelected);
                fragmentTransaction.replace(R.id.friend_frame, frgFriend).commit();
                break;
            case R.id.btRequest:
                btRequest.setBackgroundResource(R.color.colorTabSelected);
                fragmentTransaction.replace(R.id.friend_frame, frgRequest).commit();
                break;
            case R.id.btNotification:
                btNotification.setBackgroundResource(R.color.colorTabSelected);
                fragmentTransaction.replace(R.id.friend_frame, frgNotification).commit();
                break;
        }

        btLastSelected = btSelected;
    }


    @Override
    public void onClick(View v) {
        selectFragment(v.getId());
    }
}
