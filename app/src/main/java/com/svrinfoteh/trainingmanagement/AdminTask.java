package com.svrinfoteh.trainingmanagement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.svrinfoteh.trainingmanagement.adapter.UserAdapter;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.User;
import com.svrinfoteh.trainingmanagement.recyclerlistener.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class AdminTask extends Fragment {



    public AdminTask() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_admin_task, container, false);
        final RecyclerView userRecyclerView=rootView.findViewById(R.id.usersRecyclerView);
        final ArrayList<User> userList=new ArrayList<>();
        final UserAdapter userAdapter=new UserAdapter(userList);

        DatabaseReference userReference= FirebaseReference.getFirebaseDatabaseReference().child("users");
        userReference.keepSynced(true);


        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot users=iterator.next();
                    User user=users.getValue(User.class);
                    if(user.getStatus().equals("none")) {
                        userList.add(user);
                        userAdapter.notifyDataSetChanged();
                    }
                }

                userRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getBaseContext(),
                        userRecyclerView,
                        new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                User user=userList.get(position);
                                Fragment fragment=new ChangeUserDetails();
                                Bundle bundle=new Bundle();
                                bundle.putSerializable("user",user);
                                fragment.setArguments(bundle);
                                userRecyclerView.setVisibility(View.GONE);
                                getFragmentManager().beginTransaction().replace(R.id.adminTaskLayout,fragment).commit();
                            }

                            @Override
                            public void onHold(View view, int position) {

                            }
                        }));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        //userRecyclerView.setHasFixedSize(true);
        userRecyclerView.setLayoutManager(layoutManager);
        userRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL));
        userRecyclerView.setAdapter(userAdapter);

        return rootView;
    }

}
