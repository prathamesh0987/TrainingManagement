package com.svrinfoteh.trainingmanagement;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.Admissions;
import com.svrinfoteh.trainingmanagement.pojo.CourseFee;


/**
 * A simple {@link Fragment} subclass.
 */
public class ManageFee extends Fragment {

    TextView name,totalFee,feePaid,feeRemaining;
    EditText payingAmount;
    Button update;
    DatabaseReference feeReference;
    String subject,studentName;
    Admissions student;
    Context context;
    public ManageFee() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_manage_fee, container, false);
        subject=getArguments().getString("course");
        student=(Admissions) getArguments().getSerializable("student");
        studentName=student.getName();
        init(rootView);
        context=getActivity().getBaseContext();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        feeReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final CourseFee courseFee=dataSnapshot.getValue(CourseFee.class);
                StringBuffer stringBuffer=new StringBuffer("Name : ").append(student.getName());
                name.setText(stringBuffer.toString());
                stringBuffer=new StringBuffer("Total Fee : ").append(student.getFee());
                totalFee.setText(stringBuffer.toString());
                stringBuffer=new StringBuffer("Fee Paid : ").append(courseFee.getFeePaid());
                feePaid.setText(stringBuffer.toString());
                stringBuffer=new StringBuffer("Fee Remaining : ").append(courseFee.getFeeRemaining());
                feeRemaining.setText(stringBuffer.toString());


                update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String amount=payingAmount.getText().toString().trim();
                        if(!TextUtils.isEmpty(amount)) {
                            int feePaid=Integer.parseInt(courseFee.getFeePaid())+Integer.parseInt(amount);
                            int feeRemaining=Integer.parseInt(courseFee.getTotalFee())-feePaid;
                            if(feeRemaining<0) {
                                Toast.makeText(context,
                                        "Please Enter Valid Amount",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                courseFee.setFeePaid(String.valueOf(feePaid));
                                courseFee.setFeeRemaining(String.valueOf(feeRemaining));
                                feeReference.setValue(courseFee);
                                payingAmount.setText("");
                            }
                        } else {
                            payingAmount.setError("Please Enter Amount First");
                        }
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void init(View rootView) {
        name=rootView.findViewById(R.id.name);
        totalFee=rootView.findViewById(R.id.totalFee);
        feePaid=rootView.findViewById(R.id.feePaid);
        feeRemaining=rootView.findViewById(R.id.feeRemaining);
        payingAmount=rootView.findViewById(R.id.payingAmount);
        update=rootView.findViewById(R.id.updateFee);
        feeReference= FirebaseReference.getFirebaseDatabaseReference().child("fee").child(subject).child(studentName);
        feeReference.keepSynced(true);
    }

}
