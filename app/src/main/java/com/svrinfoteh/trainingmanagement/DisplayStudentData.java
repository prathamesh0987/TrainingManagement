package com.svrinfoteh.trainingmanagement;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.holder.StudentDataHolder;
import com.svrinfoteh.trainingmanagement.pojo.Admissions;
import com.svrinfoteh.trainingmanagement.recyclerlistener.RecyclerTouchListener;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplayStudentData extends Fragment {
    private RecyclerView studRecyclerView;
    String subject;
    public DisplayStudentData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_display_student_data, container, false);
        subject=getArguments().getString("subject");
        DatabaseReference subjectReference= FirebaseReference.getFirebaseDatabaseReference().child("admission").child(subject);
        subjectReference.keepSynced(true);

        final ArrayList<Admissions> admissions=new ArrayList<>();

        studRecyclerView=rootView.findViewById(R.id.studentDetails);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        studRecyclerView.setHasFixedSize(true);
        studRecyclerView.setLayoutManager(layoutManager);
        studRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL));

        FirebaseRecyclerOptions<Admissions> firebaseRecyclerOptions=new FirebaseRecyclerOptions.
                Builder<Admissions>()
                .setQuery(subjectReference,Admissions.class)
                .build();

        FirebaseRecyclerAdapter<Admissions,StudentDataHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Admissions, StudentDataHolder>(firebaseRecyclerOptions) {
            @Override
            protected void onBindViewHolder(@NonNull StudentDataHolder holder, int position, @NonNull Admissions model) {
                holder.setName(model.getName());
                holder.setMail(model.getEmail());
                holder.setContact(model.getContactno());
                holder.setCourse(model.getCourse());
                holder.setFee(model.getFee());

                admissions.add(model);
            }

            @NonNull
            @Override
            public StudentDataHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.student_records,parent,false);
                return new StudentDataHolder(view);
            }
        };

        studRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

        studRecyclerView.addOnItemTouchListener(
                new RecyclerTouchListener(getActivity().getApplicationContext(),
                        studRecyclerView,
                        new RecyclerTouchListener.ClickListener() {
                            @Override
                            public void onClick(View view, int position) {
                                Admissions student=admissions.get(position);
                                Fragment fragment=new ManageFee();
                                Bundle bundle=new Bundle();
                                bundle.putSerializable("student",student);
                                bundle.putString("course",subject);
                                fragment.setArguments(bundle);
                                hideFrontView();
                                getFragmentManager().beginTransaction().replace(R.id.studentDataLayout,fragment).commit();
                            }

                            @Override
                            public void onHold(View view, int position) {

                            }
                        }
                ));


        /*subjectReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot studData=iterator.next();
                    Admissions admissions=studData.getValue(Admissions.class);
                    ArrayList<String> courseList=admissions.getCourse();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/

        return rootView;
    }

    private void hideFrontView() {
        studRecyclerView.setVisibility(View.GONE);
    }
}