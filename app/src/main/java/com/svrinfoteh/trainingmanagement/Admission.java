package com.svrinfoteh.trainingmanagement;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.Admissions;
import com.svrinfoteh.trainingmanagement.pojo.CourseFee;
import com.svrinfoteh.trainingmanagement.pojo.User;

import java.util.ArrayList;
import java.util.Iterator;


/**
 * A simple {@link Fragment} subclass.
 */
public class Admission extends Fragment implements View.OnClickListener {
    DatabaseReference admissionReference;
    private EditText dateofbirth,contactno,email,qualification,coursefee,duration,joining;
    private AutoCompleteTextView name;
    private MultiAutoCompleteTextView courses;
    private Button save;
    private RadioGroup gender;
    private static String genderValue;
    Context baseContext;
    String[] coursesList={"English1","Mathematics1","Science1",
            "English2","Mathematics2","Science2",
            "English3","Mathematics3","Science3",
            "English4","Mathematics4","Science4",
            "English5","Mathematics5","Science5",
            "English6","Mathematics6","Science6",
            "English7","Mathematics7","Science7",
            "Physics8","Chemistry8","Mathematics8","Biology8","English8",
            "Physics9","Chemistry9","Mathematics9","Biology9","English9",
            "Physics10","Chemistry10","Mathematics10","Biology10","English10",
            "Physics11","Chemistry11","Mathematics11","Biology11","English11",
            "Physics12","Chemistry12","Mathematics12","Biology12","English12"};
    public Admission() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_admission, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init(rootView);
        save.setOnClickListener(this);
        baseContext=getActivity().getBaseContext();
        ArrayAdapter<String> coursesAdapter=new ArrayAdapter<>(
                baseContext,android.R.layout.simple_dropdown_item_1line,coursesList);

        courses.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        courses.setAdapter(coursesAdapter);

        DatabaseReference userReference= FirebaseReference.getFirebaseDatabaseReference().child("users");
        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<String> userList=new ArrayList<>();
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot userData=iterator.next();
                    User user=userData.getValue(User.class);
                    String stud_name=user.getName();
                    userList.add(stud_name);
                }

                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(
                        baseContext,android.R.layout.simple_dropdown_item_1line,userList);
                name.setAdapter(arrayAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(baseContext,
                        "Error : "+databaseError.getMessage(),
                        Toast.LENGTH_LONG).show();
            }
        });



        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.male:
                    genderValue="male";
                    break;
                case R.id.female:
                    genderValue="female";
                    break;
                }
            }
        });
    }

    private void init(View view) {
        admissionReference= FirebaseReference.getFirebaseDatabaseReference().child("admission");
        admissionReference.keepSynced(true);
        save=view.findViewById(R.id.save);
        name=view.findViewById(R.id.studentName);
        dateofbirth=view.findViewById(R.id.dateOfBirth);
        gender=view.findViewById(R.id.genderValue);
        contactno=view.findViewById(R.id.contactNo);
        email=view.findViewById(R.id.email_id);
        qualification=view.findViewById(R.id.qualificationDetails);
        courses=view.findViewById(R.id.courses);
        coursefee=view.findViewById(R.id.courseFee);
        duration=view.findViewById(R.id.duration);
        joining=view.findViewById(R.id.dateOfJoining);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:
                saveRecord();
                break;
        }
    }

    private void saveRecord() {
        final String studentName=name.getText().toString().trim();
        String dobDate=dateofbirth.getText().toString().trim();
        String contactNo=contactno.getText().toString().trim();
        String email_id=email.getText().toString().trim();
        String qualificationDetails=qualification.getText().toString().trim();
        final String courseName=courses.getText().toString();
        final String courseFee=coursefee.getText().toString().trim();
        String courseDuration=duration.getText().toString().trim();
        String dojDate=joining.getText().toString().trim();
        final Fragment fragment=new Progressbar();
        if(!TextUtils.isEmpty(studentName) || !TextUtils.isEmpty(dobDate) || !TextUtils.isEmpty(contactNo)
                || !TextUtils.isEmpty(email_id) || !TextUtils.isEmpty(qualificationDetails) || !TextUtils.isEmpty(courseName)
                || !TextUtils.isEmpty(courseFee) || !TextUtils.isEmpty(courseDuration) || !TextUtils.isEmpty(dojDate)) {
            Admissions admissions = new Admissions();
            admissions.setName(studentName);
            admissions.setContactno(contactNo);
            admissions.setDob(dobDate);
            admissions.setGender(genderValue);
            admissions.setEmail(email_id);
            admissions.setQualification(qualificationDetails);
            admissions.setFee(courseFee);
            admissions.setDuration(courseDuration);
            admissions.setDoj(dojDate);

            String[] courses=courseName.split(",");

            ArrayList<String> subjectList=new ArrayList<>();

            for(String subject:courses) {
                subject=subject.trim();
                subjectList.add(subject);
            }

            int removeIndexValue=subjectList.size()-1;
            subjectList.remove(removeIndexValue);

            getFragmentManager().beginTransaction().replace(R.id.admissionLayout, fragment).commit();
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            if(courses.length>1) {
                Double fee=Integer.parseInt(courseFee)/Double.valueOf(subjectList.size());
                Double roundFee=Math.round(fee*100.0)/100.0;
                for(String subject:subjectList) {
                    saveWRTSubject(subject,studentName,admissions,String.valueOf(roundFee));
                }
            } else {
                saveWRTSubject(courseName, studentName, admissions, courseFee);
            }


            getFragmentManager().beginTransaction().remove(fragment).commit();
        } else {
            Toast.makeText(getActivity().getApplicationContext(), "PLEASE FILL ALL DETAILS", Toast.LENGTH_LONG).show();
        }
    }

    private void setToInitial() {
        name.setText("");
        dateofbirth.setText("");
        gender.clearCheck();
        contactno.setText("");
        email.setText("");
        qualification.setText("");
        coursefee.setText("");
        duration.setText("");
        joining.setText("");
        courses.setText("");
    }

    private void saveWRTSubject(final String courseName,final String studentName, Admissions admissions,final String courseFee) {
        admissions.setCourse(courseName);
        admissionReference.child(courseName).child(studentName).setValue(admissions).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()) {
                    CourseFee courseFeeDetails=new CourseFee();
                    courseFeeDetails.setName(studentName);
                    courseFeeDetails.setTotalFee(courseFee);
                    courseFeeDetails.setFeePaid("0");
                    courseFeeDetails.setFeeRemaining(courseFee);
                    FirebaseReference.getFirebaseDatabaseReference().child("fee").child(courseName).child(studentName).setValue(courseFeeDetails);
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Toast.makeText(getActivity().getApplicationContext(),"Record Saved...",Toast.LENGTH_LONG).show();
                    setToInitial();

                } else {
                    Snackbar snackbar=Snackbar.make(getActivity().findViewById(android.R.id.content),
                            "SOMETHING WENT WRONG, RECORD NOT SAVED",
                            Snackbar.LENGTH_LONG);
                    snackbar.setActionTextColor(Color.RED);
                    snackbar.show();
                }
            }
        });
    }
}