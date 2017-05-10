package com.immortplanet.drawlove;


import android.app.Activity;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;

import com.immortplanet.drawlove.model.DataSingleton;
import com.immortplanet.drawlove.model.Group;
import com.immortplanet.drawlove.model.Message;
import com.immortplanet.drawlove.model.User;
import com.immortplanet.drawlove.util.HttpCallback;
import com.immortplanet.drawlove.util.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ChatGroupActivity extends Activity {

    private static final int MESSAGE_COUNT = 20;

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    String groupID;
    String groupName;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_chatgroup);

        //-- set title, bundle is required to not be null
        Bundle bundle = getIntent().getExtras();
        groupID =  bundle.getString("groupID");
        groupName = bundle.getString("groupName");
        getActionBar().setTitle(groupName);

        //-- if this group is not loaded then load the info
        User currentUser = (User) DataSingleton.getDataSingleton().get("currentUser");
        final Group[] g = {currentUser.groups.get(groupID)};
        if (g[0] != null){
            //-- request the group and latest N message
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("groupID", groupID);
                jsonObject.put("messageCount", MESSAGE_COUNT);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            HttpRequest request = new HttpRequest("POST", "/group/chat", jsonObject, new HttpCallback() {
                @Override
                public void finished(JSONObject jsonObject) {
                    try {
                        JSONArray messages = jsonObject.getJSONArray("messages");
                        for (int i=0; i<messages.length(); i++) {
                            g[0].messages.add(new Message(messages.getJSONObject(i)));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new HttpCallback() {
                @Override
                public void finished(JSONObject jsonObject) {
                    try {
                        String u = jsonObject.getString("reasonMessage");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            request.execute();
        }

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:
                    return new ChatFragment();
                case 1:
                    return new MemberFragment();
                case 2:
                    return new SettingFragment();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "SECTION 1";
                case 1:
                    return "SECTION 2";
                case 2:
                    return "SECTION 3";
            }
            return null;
        }
    }

    public static class ChatFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat_group_chat, container, false);
            return rootView;
        }
    }

    public static class MemberFragment extends Fragment{

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat_group_game, container, false);
            return rootView;
        }

    }

    public static class SettingFragment extends Fragment{
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_chat_group_setting, container, false);
            return rootView;
        }
    }
}
