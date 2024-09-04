package com.svrinfoteh.trainingmanagement;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.User;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangeUserDetails extends Fragment {

    private TextView username,contact,mail;
    private AutoCompleteTextView userStatus;
    private MultiAutoCompleteTextView courses;
    private Button update;
    private User user;
    private LinearLayout rootLayout,trainerLayout;
    public ChangeUserDetails() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_change_user_details, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        String[] status={"admin","owner","employee","student"};
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
                "Physics12","Chemistry12","Mathematics12","Biology12","English12","none"};
        init(rootView);
        ArrayAdapter<String> statusAdapter=new ArrayAdapter<>(
                getActivity().getBaseContext(),android.R.layout.simple_list_item_1,status);
        ArrayAdapter<String> coursesAdapter=new ArrayAdapter<>(
                getActivity().getBaseContext(),android.R.layout.simple_dropdown_item_1line,coursesList);

        courses.setTokenizer(new MultiAutoCompleteTextView.CommaTokenizer());
        userStatus.setAdapter(statusAdapter);
        courses.setAdapter(coursesAdapter);

        user=(User)getArguments().getSerializable("user");
        StringBuffer stringBuffer=new StringBuffer("Name : ").append(user.getName());
        username.setText(stringBuffer.toString());
        stringBuffer=new StringBuffer("Contact No : ").append(user.getMobile());
        contact.setText(stringBuffer.toString());
        stringBuffer=new StringBuffer("Mail id : ").append(user.getUsername());
        mail.setText(stringBuffer.toString());

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        String userName=user.getName();
        final DatabaseReference userReference= FirebaseReference.getFirebaseDatabaseReference().child("users").child(userName);
        userReference.keepSynced(true);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> subjectList=new ArrayList<>();
                String stat=userStatus.getText().toString();
                String subjects=courses.getText().toString();
                String[] subjectArray=subjects.split(",");
                for(String subject:subjectArray) {
                    subject=subject.trim();
                        subjectList.add(subject);
                }
                /*switch (stat){
                    case "student":
                        trainerLayout.setVisibility(View.VISIBLE);
                        ArrayList<String> employeeList=new ArrayList<>();
                        FirebaseReference.getFirebaseDatabaseReference()
                                .child("employee")
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                        break;
                    case "employee":

                        break;
                }*/

                if(!TextUtils.isEmpty(stat) && !TextUtils.isEmpty(subjects)) {
                    int removeObjectAt=subjectArray.length-1;
                    subjectList.remove(removeObjectAt);

                    user.setCourse(subjectList);
                    user.setStatus(stat);
                    userReference.setValue(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar.make(rootLayout,"User Updated...",Snackbar.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Snackbar.make(rootLayout,"Please Fill Details",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void init(View rootView) {
        username=rootView.findViewById(R.id.currentUser);
        contact=rootView.findViewById(R.id.currentUserContact);
        mail=rootView.findViewById(R.id.currentUserEmail);
        userStatus=rootView.findViewById(R.id.status);
        courses=rootView.findViewById(R.id.course);
        update=rootView.findViewById(R.id.updateUserDetails);
        rootLayout=rootView.findViewById(R.id.changeUserDetailsLayout);
       // trainerLayout=rootView.findViewById(R.id.trainerLayout);
    }
}
