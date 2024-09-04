package com.svrinfoteh.trainingmanagement;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.Admissions;
import com.svrinfoteh.trainingmanagement.pojo.RevisionPoints;
import com.svrinfoteh.trainingmanagement.pojo.User;

import java.util.ArrayList;
import java.util.Iterator;

import io.netopen.hotbitmapgg.library.view.RingProgressBar;


/**
 * A simple {@link Fragment} subclass.
 */
public class DisplaySyllabusProgress extends Fragment {

    private Admissions admissions;
    private String studentName,joinFor;
    private TextView syllabusCovered,name,revision;
    private StringBuffer checkedSyllabus,revisionPoints;
    private RadioButton employee,student;
    private RingProgressBar ringProgressBar;
    private DatabaseReference revisionReference;
    private int percentage;
    Long total,count;


    public DisplaySyllabusProgress() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_display_syllabus_progress, container, false);
        admissions=(Admissions) getArguments().getSerializable("admission");
        studentName=admissions.getName();
        joinFor=admissions.getCourse();
        init(rootView);
        syllabusCovered.setMovementMethod(new ScrollingMovementMethod());
        revision.setMovementMethod(new ScrollingMovementMethod());
        revisionReference.keepSynced(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        name.setText(studentName);
        name.setPaintFlags(name.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkedSyllabus = new StringBuffer("Syllabus Covered : ");
                    FirebaseReference.getFirebaseDatabaseReference()
                            .child("covered")
                            .child(joinFor)
                            .child(studentName)
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                                    count=dataSnapshot.getChildrenCount();
                                    while (iterator.hasNext()) {
                                        DataSnapshot student=iterator.next();
                                        checkedSyllabus=checkedSyllabus.append(student.getValue(String.class)).append(", ");
                                    }
                                    syllabusCovered.setText(checkedSyllabus.toString());
                                    setPercentage(count);
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                    getRevisionPoints();
                }
            }
        });

        employee.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    checkedSyllabus = new StringBuffer("Syllabus Covered : ");

                    final DatabaseReference userReference=FirebaseReference.getFirebaseDatabaseReference()
                            .child("users");
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                            while (iterator.hasNext()) {
                                DataSnapshot userData=iterator.next();
                                User user=userData.getValue(User.class);
                                if(user.getStatus().equals("employee")) {
                                    ArrayList<String> courseList=user.getCourse();
                                    for(String course:courseList) {
                                        if(course.equals(joinFor)) {
                                            DatabaseReference coveredReference=FirebaseReference.getFirebaseDatabaseReference()
                                                    .child("covered")
                                                    .child(joinFor)
                                                    .child(user.getName())
                                                    .child(studentName);

                                            coveredReference.keepSynced(true);

                                            coveredReference.addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    Iterator<DataSnapshot> userIterator=dataSnapshot.getChildren().iterator();
                                                    count=dataSnapshot.getChildrenCount();
                                                    while (userIterator.hasNext()) {
                                                        DataSnapshot student=userIterator.next();
                                                        checkedSyllabus=checkedSyllabus.append(student.getValue(String.class)).append(", ");
                                                    }
                                                    syllabusCovered.setText(checkedSyllabus.toString());
                                                    setPercentage(count);
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });
                                        break;
                                        }
                                    }
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        });
    }


    private Float getPercentage(Float count, Integer total) {
        return (count/total)*100;
    }

    private void setPercentage(final Long count) {

        DatabaseReference syllabusReference=FirebaseReference.getFirebaseDatabaseReference().child("syllabus").child(joinFor);
        syllabusReference.keepSynced(true);
        syllabusReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                total=dataSnapshot.getChildrenCount();
                percentage=getPercentage(count.floatValue(),total.intValue()).intValue();
                ringProgressBar.setProgress(percentage);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    private void getRevisionPoints() {
        revisionPoints=new StringBuffer();
        revisionPoints.append("Points To Be Revised :\n");
        revisionReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot revisionPoint=iterator.next();
                    RevisionPoints points=revisionPoint.getValue(RevisionPoints.class);
                    revisionPoints.append("Point : ").append(points.getPoint()).append("\nReason : ").append(points.getReason()).append("\n");
                }
                revision.setText(revisionPoints.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void init(View rootView) {
        syllabusCovered = rootView.findViewById(R.id.syllabusCovered);
        name=rootView.findViewById(R.id.name);
        employee=rootView.findViewById(R.id.employee);
        student=rootView.findViewById(R.id.student);
        ringProgressBar=rootView.findViewById(R.id.percentCovered);
        revision=rootView.findViewById(R.id.revision);
        revisionReference= FirebaseReference.getFirebaseDatabaseReference().child("revision").child(joinFor).child(studentName);
    }
}
