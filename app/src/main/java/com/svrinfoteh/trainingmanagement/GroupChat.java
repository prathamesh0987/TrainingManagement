package com.svrinfoteh.trainingmanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.svrinfoteh.trainingmanagement.adapter.ChatPagerAdapter;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class GroupChat extends Fragment {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private String name;
    private ArrayList<String> course;
    private final String sharedPreferenceName="SHARED_PREFERENCE";
    public GroupChat() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_group_chat, container, false);
        init(rootView);
        SharedPreferences sharedPreferences=getActivity().getApplicationContext().getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        name=sharedPreferences.getString("name",null);
        course=new ArrayList<>(sharedPreferences.getStringSet("courses",null));
        if(course!=null) {
            setupViewPager();
            tabLayout.setupWithViewPager(viewPager);
            tabLayout.setSelectedTabIndicatorHeight((int) (5 * getResources().getDisplayMetrics().density));
            tabLayout.setTabTextColors(Color.parseColor("#ffffff"),Color.parseColor("#000000"));
        }

        return rootView;
    }

    private void init(View rootView) {
        tabLayout=rootView.findViewById(R.id.tabView);
        viewPager=rootView.findViewById(R.id.chatViewPager);
    }

    private void setupViewPager() {
        ChatPagerAdapter chatPagerAdapter=new ChatPagerAdapter(getFragmentManager());
        for(String subject:course) {
            Fragment fragment=new Chat();
            Bundle bundle=new Bundle();
            bundle.putString("subject",subject);
            fragment.setArguments(bundle);
            chatPagerAdapter.addFragment(fragment,subject);
        }
        viewPager.setAdapter(chatPagerAdapter);
    }

}