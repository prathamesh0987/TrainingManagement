package com.svrinfoteh.trainingmanagement;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class SetSyllabus extends Fragment {

    private Spinner subjectSpinner;
    private EditText topic;
    private Button addTopic;
    DatabaseReference syllabusReference;
    public SetSyllabus() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_set_syllabus, container, false);
        init(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        final ArrayList<String> subjectList=getArguments().getStringArrayList("subjectList");
        ArrayAdapter<String> subjectAdapter=new ArrayAdapter<>(
                getActivity().getBaseContext(),android.R.layout.simple_list_item_1,subjectList);
        subjectSpinner.setAdapter(subjectAdapter);

        subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String subject=subjectList.get(position);
                syllabusReference= FirebaseReference.getFirebaseDatabaseReference().child("syllabus").child(subject);
                syllabusReference.keepSynced(true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addTopic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topicName=topic.getText().toString().trim();
                syllabusReference.child(topicName).setValue(topicName).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Topic Added",
                                Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    private void init(View rootView) {
        subjectSpinner=rootView.findViewById(R.id.subjectList);
        topic=rootView.findViewById(R.id.topic);
        addTopic=rootView.findViewById(R.id.addTopic);
    }

}
