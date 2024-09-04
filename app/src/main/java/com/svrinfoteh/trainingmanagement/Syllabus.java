package com.svrinfoteh.trainingmanagement;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.svrinfoteh.trainingmanagement.adapter.StudentAdapter;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.Admissions;
import com.svrinfoteh.trainingmanagement.recyclerlistener.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.Iterator;

import static android.content.Context.MODE_PRIVATE;


/**
 * A simple {@link Fragment} subclass.
 */
public class Syllabus extends Fragment {

    private RecyclerView studentRecyclerView;
    private StudentAdapter studentAdapter;
    private ArrayList<Admissions> admissionsArrayList;
    ArrayList<String> courses;
    public Syllabus() {
        // Required empty public constructor
    }

    public ArrayList<Admissions> getAdmissionsArrayList() {
        return admissionsArrayList;
    }

    public void setAdmissionsArrayList(ArrayList<Admissions> admissionsArrayList) {
        this.admissionsArrayList = admissionsArrayList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_syllabus, container, false);
        init(rootView);
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(getActivity().getApplicationContext());
        studentRecyclerView.setHasFixedSize(true);
        studentRecyclerView.setLayoutManager(layoutManager);
        studentRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL));
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String sharedPreferenceName="SHARED_PREFERENCE";
        SharedPreferences sharedPreferences=getActivity().getApplicationContext().getSharedPreferences(sharedPreferenceName,MODE_PRIVATE);
        String status=sharedPreferences.getString("status",null);
        Log.e("Status",status);

        if(status!=null) {
            switch (status) {
                case "employee":
                    courses=new ArrayList<>(sharedPreferences.getStringSet("courses",null));
                    if(courses!=null) {
                        for(String subject:courses) {
                            DatabaseReference subjectReference= FirebaseReference.getFirebaseDatabaseReference().child("admission").child(subject);
                            subjectReference.keepSynced(true);

                            subjectReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                                    while (iterator.hasNext()) {
                                        DataSnapshot studentData=iterator.next();
                                        Admissions admissions=studentData.getValue(Admissions.class);
                                        admissionsArrayList.add(admissions);
                                    }
                                    setAdmissionsArrayList(admissionsArrayList);
                                    studentAdapter.notifyDataSetChanged();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                        studentRecyclerView.setAdapter(studentAdapter);


                        studentRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(),
                                studentRecyclerView,
                                new RecyclerTouchListener.ClickListener() {
                                    @Override
                                    public void onClick(View view, int position) {
                                        Admissions admissions=getAdmissionsArrayList().get(position);
                                        Bundle bundle=new Bundle();
                                        bundle.putSerializable("admission",admissions);
                                        Fragment fragment=new DisplaySyllabus();
                                        fragment.setArguments(bundle);
                                        hideFrontView();
                                        getFragmentManager().beginTransaction().replace(R.id.syllabusLayout,fragment).commit();
                                    }

                                    @Override
                                    public void onHold(View view, int position) {

                                    }
                                }));
                    }
                    break;
                case "student":
                    courses=new ArrayList<>(sharedPreferences.getStringSet("courses",null));
                    if(courses!=null) {
                        Fragment fragment=new StudentSyllabus();
                        hideFrontView();
                        getFragmentManager().beginTransaction().replace(R.id.syllabusLayout,fragment).commit();
                    }
                    break;
                case "owner":
                case "admin":
                    DatabaseReference userReference=FirebaseReference.getFirebaseDatabaseReference().child("admission");
                    userReference.keepSynced(true);
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                            while (iterator.hasNext()) {
                                DataSnapshot subjectData=iterator.next();
                                Iterator<DataSnapshot> admissionIterator=subjectData.getChildren().iterator();
                                while (admissionIterator.hasNext()) {
                                    DataSnapshot admissionData=admissionIterator.next();
                                    Admissions admissions=admissionData.getValue(Admissions.class);
                                    admissionsArrayList.add(admissions);
                                }
                            }
                             setAdmissionsArrayList(admissionsArrayList);
                            studentAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    studentRecyclerView.setAdapter(studentAdapter);

                    studentRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity().getApplicationContext(),
                            studentRecyclerView,
                            new RecyclerTouchListener.ClickListener() {
                                @Override
                                public void onClick(View view, int position) {
                                    Admissions admissions=getAdmissionsArrayList().get(position);
                                    Bundle bundle=new Bundle();
                                    bundle.putSerializable("admission",admissions);
                                    Fragment fragment=new DisplaySyllabusProgress();
                                    fragment.setArguments(bundle);
                                    hideFrontView();
                                    getFragmentManager().beginTransaction().replace(R.id.syllabusLayout,fragment).commit();
                                }

                                @Override
                                public void onHold(View view, int position) {

                                }
                            }));
                    break;
            }
        }
    }

    private void init(View rootView) {
        studentRecyclerView=rootView.findViewById(R.id.studentData);
        admissionsArrayList=new ArrayList<>();
        studentAdapter=new StudentAdapter(admissionsArrayList);
    }

    private void hideFrontView() {
        studentRecyclerView.setVisibility(View.GONE);
    }
}
