package com.svrinfoteh.trainingmanagement;


import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.svrinfoteh.trainingmanagement.adapter.SubjectAdapter;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.recyclerlistener.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class TotalAdmission extends Fragment {
    private RecyclerView subjectRecyclerView;

    public TotalAdmission() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_total_admission, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        subjectRecyclerView=rootView.findViewById(R.id.allSubjects);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        DatabaseReference subjectReference= FirebaseReference.getFirebaseDatabaseReference().child("admission");
        subjectReference.keepSynced(true);
        final ArrayList<String> subjectList=new ArrayList<>();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        subjectRecyclerView.setLayoutManager(layoutManager);
        subjectRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL));
        final SubjectAdapter subjectAdapter=new SubjectAdapter(subjectList);


        subjectReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot subjectDataSnapshot=iterator.next();
                    String subjectKey=subjectDataSnapshot.getKey();
                    subjectList.add(subjectKey);
                }
                subjectAdapter.notifyDataSetChanged();
                subjectRecyclerView.setAdapter(subjectAdapter);

                subjectRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(),
                        subjectRecyclerView,
                        new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                String subject=subjectList.get(position);
                                Bundle bundle=new Bundle();
                                bundle.putString("subject",subject);
                                Fragment fragment=new DisplayStudentData();
                                fragment.setArguments(bundle);
                                getFragmentManager().beginTransaction().replace(R.id.totalAdmissions,fragment).commit();
                                hideFrontView();
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

    }

    private void hideFrontView() {
        getActivity().findViewById(R.id.subjectTitle).setVisibility(View.GONE);
        subjectRecyclerView.setVisibility(View.GONE);
    }
}
