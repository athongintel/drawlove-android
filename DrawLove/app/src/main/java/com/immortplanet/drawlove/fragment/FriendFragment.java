package com.immortplanet.drawlove.fragment;


import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.os.Bundle;
import android.app.Fragment;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.immortplanet.drawlove.LoginActivity;
import com.immortplanet.drawlove.R;
import com.immortplanet.drawlove.fragment.friend.FriendFriendFragment;
import com.immortplanet.drawlove.fragment.friend.FriendNotificationFragment;
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
import java.util.HashMap;


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

    LinearLayout layoutInfo;
    TextView txtInfo;
    Button btRetry;

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

        layoutInfo = (LinearLayout)thisView.findViewById(R.id.layoutInfo);
        txtInfo = (TextView)thisView.findViewById(R.id.txtInfo);
        btRetry = (Button)thisView.findViewById(R.id.btRetry);

        btSearch.setOnClickListener(this);
        btFriend.setOnClickListener(this);
        btRequest.setOnClickListener(this);
        btNotification.setOnClickListener(this);

        frgSearch = new FriendSearchFragment();
        frgFriend = new FriendFriendFragment();
        frgRequest = new FriendRequestFragment();
        frgNotification = new FriendNotificationFragment();


        btRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reloadData();
            }
        });

        reloadData();
        return thisView;
    }

    private void reloadData(){
        prLoading.setVisibility(View.VISIBLE);
        btSearch.setEnabled(false);
        btFriend.setEnabled(false);
        btRequest.setEnabled(false);
        btNotification.setEnabled(false);
        layoutInfo.setVisibility(View.GONE);

        final User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
        HttpRequest allRequest = new HttpRequest("GET", "/user/request/all", null, new HttpCallback() {
            @Override
            public void finished(JSONObject jsonObject) {
                prLoading.setVisibility(View.GONE);
                try {
                    currentUser.friends = new ArrayList<>();
                    JSONArray arFriends = (JSONArray)jsonObject.getJSONArray("friends");
                    for (int i=0; i< arFriends.length(); i++){
                        User user = new User((JSONObject)arFriends.get(i));
                        currentUser.friends.add(user);
                    }

                    currentUser.sentRequests = new ArrayList<>();
                    JSONArray arRequest = (JSONArray)jsonObject.getJSONArray("sentRequests");
                    for (int i=0; i< arRequest.length(); i++){
                        Request request = new Request((JSONObject)arRequest.get(i));
                        currentUser.sentRequests.add(request);
                    }

                    currentUser.receivedRequests = new ArrayList<>();
                    JSONArray arReceivedRequests = (JSONArray)jsonObject.getJSONArray("receivedRequests");
                    for (int i=0; i< arReceivedRequests.length(); i++){
                        Request request = new Request((JSONObject)arReceivedRequests.get(i));
                        currentUser.receivedRequests.add(request);
                    }

                    btSearch.setEnabled(true);
                    btFriend.setEnabled(true);
                    btRequest.setEnabled(true);
                    btNotification.setEnabled(true);

                    selectFragment(R.id.btSearch);

                } catch (JSONException e) {
                    layoutInfo.setVisibility(View.VISIBLE);
                    e.printStackTrace();
                }
            }
        }, new HttpCallback() {
            @Override
            public void finished(JSONObject jsonObject) {
                prLoading.setVisibility(View.GONE);
                layoutInfo.setVisibility(View.VISIBLE);
            }
        });
        allRequest.execute();
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
