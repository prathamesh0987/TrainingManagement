package com.svrinfoteh.trainingmanagement;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.CourseFee;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class TotalCollection extends Fragment {

    private int totalFee,feeCollected,totalDue;
    private TableLayout tableLayout;
    public TotalCollection() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_total_collection, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        tableLayout=rootView.findViewById(R.id.collectionTableLayout);
        final DatabaseReference feeReference=FirebaseReference.getFirebaseDatabaseReference().child("fee");
        feeReference.keepSynced(true);
        DatabaseReference admissionReference= FirebaseReference.getFirebaseDatabaseReference().child("admission");
        admissionReference.keepSynced(true);
        final ArrayList<String> subjectList=new ArrayList<>();
        admissionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot subjectDataSnapshot=iterator.next();
                    String subject=subjectDataSnapshot.getKey();
                    subjectList.add(subject);
                }
                for(String subject:subjectList) {
                    final TextView coursename,total,collected,due;
                    TableRow tableRow=new TableRow(getActivity().getBaseContext());
                    TableLayout.LayoutParams layoutRow = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.MATCH_PARENT);
                    tableRow.setLayoutParams(layoutRow);

                    coursename=new TextView(getActivity().getBaseContext());
                    TableRow.LayoutParams layoutHistory = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                    coursename.setLayoutParams(layoutHistory);
                    coursename.setText(subject);

                    total=new TextView(getActivity().getBaseContext());
                    layoutHistory = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                    total.setLayoutParams(layoutHistory);

                    collected=new TextView(getActivity().getBaseContext());
                    layoutHistory = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                    collected.setLayoutParams(layoutHistory);

                    due=new TextView(getActivity().getBaseContext());
                    layoutHistory = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
                    due.setLayoutParams(layoutHistory);

                    tableRow.setGravity(Gravity.CENTER);
                    coursename.setGravity(Gravity.CENTER);
                    total.setGravity(Gravity.CENTER);
                    collected.setGravity(Gravity.CENTER);
                    due.setGravity(Gravity.CENTER);

                    tableRow.addView(coursename);
                    tableRow.addView(total);
                    tableRow.addView(collected);
                    tableRow.addView(due);
                    tableLayout.addView(tableRow);
                    feeReference.child(subject).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                            while (iterator.hasNext()) {
                                DataSnapshot student=iterator.next();
                                CourseFee courseFee=student.getValue(CourseFee.class);
                                getCalculations(courseFee);
                            }
                            setValues(total,collected,due);
                            initializeCollection();
                         }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return rootView;
    }


    private void getCalculations(CourseFee courseFee) {
        totalFee+=Integer.parseInt(courseFee.getTotalFee());
        feeCollected+=Integer.parseInt(courseFee.getFeePaid());
        totalDue+=Integer.parseInt(courseFee.getFeeRemaining());
    }

    private void setValues(TextView total, TextView collected, TextView due) {
        total.setText(String.valueOf(totalFee));
        collected.setText(String.valueOf(feeCollected));
        due.setText(String.valueOf(totalDue));
    }

    private void initializeCollection() {
        totalFee=0;
        feeCollected=0;
        totalDue=0;
    }
}