package com.svrinfoteh.trainingmanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.holder.SyllabusHolder;
import com.svrinfoteh.trainingmanagement.pojo.Admissions;

import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplaySyllabus extends Fragment {

    private RecyclerView syllabusRecyclerView;
    private DatabaseReference syllabusReference,coveredReference;
    private String subject,username,name;
    private Admissions admissions;
    private TextView subjectTitle;
    public DisplaySyllabus() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_display_syllabus, container, false);
        final String sharedPreferenceName="SHARED_PREFERENCE";
        SharedPreferences sharedPreferences=getActivity().getApplicationContext().getSharedPreferences(sharedPreferenceName, Context.MODE_PRIVATE);
        name=sharedPreferences.getString("name",null);
        admissions=(Admissions) getArguments().getSerializable("admission");
        subject=admissions.getCourse();
        username=admissions.getName();
        init(rootView);
        subjectTitle.setText(subject);
        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();

        FirebaseRecyclerOptions<String> firebaseRecyclerOptions=new FirebaseRecyclerOptions
                .Builder<String>()
                .setQuery(syllabusReference,String.class)
                .build();
        FirebaseRecyclerAdapter<String,SyllabusHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<String, SyllabusHolder>(
                firebaseRecyclerOptions
        ) {
            @Override
            protected void onBindViewHolder(@NonNull final SyllabusHolder holder, int position, @NonNull final String model) {
                holder.setSyllabusPoint(model);

                coveredReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                        while (iterator.hasNext()) {
                            DataSnapshot data=iterator.next();
                            String value=data.getValue(String.class);
                            if(value.equals(holder.syllabusPoint.getText())) {
                                holder.syllabusPoint.setChecked(true);
                                break;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                holder.syllabusPoint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked) {
                            coveredReference.child(model).setValue(model);
                        }
                        else {
                            coveredReference.child(model).removeValue();
                        }
                    }
                });
            }

            @NonNull
            @Override
            public SyllabusHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.syllabus_point,parent,false);
                return new SyllabusHolder(view);
            }
        };

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        syllabusRecyclerView.setHasFixedSize(true);
        syllabusRecyclerView.setLayoutManager(layoutManager);
        syllabusRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    private void init(View rootView) {
        syllabusReference= FirebaseReference.getFirebaseDatabaseReference().child("syllabus").child(subject);
        syllabusReference.keepSynced(true);
        coveredReference=FirebaseReference.getFirebaseDatabaseReference().child("covered").child(subject).child(name).child(username);
        coveredReference.keepSynced(true);
        syllabusRecyclerView=rootView.findViewById(R.id.syllabusRecyclerView);
        subjectTitle=rootView.findViewById(R.id.subject);
    }
}