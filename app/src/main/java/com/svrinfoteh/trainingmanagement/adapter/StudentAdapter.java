package com.svrinfoteh.trainingmanagement.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.svrinfoteh.trainingmanagement.R;
import com.svrinfoteh.trainingmanagement.pojo.Admissions;

import java.util.ArrayList;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentHolder> {

    ArrayList<Admissions> admissionsList;

    public StudentAdapter(ArrayList<Admissions> admissionsList) {
        this.admissionsList = admissionsList;
    }

    public class StudentHolder extends RecyclerView.ViewHolder {
        TextView studName,studContactNo,studEmail,studCourse,studFee;
        public StudentHolder(View itemView) {
            super(itemView);
            studName=itemView.findViewById(R.id.studName);
            studContactNo=itemView.findViewById(R.id.studContactNo);
            studEmail=itemView.findViewById(R.id.studEmail);
            studCourse=itemView.findViewById(R.id.studCourse);
            studFee=itemView.findViewById(R.id.studFee);
        }
    }


    @NonNull
    @Override
    public StudentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.student_records,parent,false);
        return new StudentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentHolder holder, int position) {
        Admissions admissions=admissionsList.get(position);
        StringBuffer stringBuffer=new StringBuffer("Name : ").append(admissions.getName());
        holder.studName.setText(stringBuffer.toString());
        stringBuffer=new StringBuffer("Contact No : ").append(admissions.getContactno());
        holder.studContactNo.setText(stringBuffer.toString());
        stringBuffer=new StringBuffer("Email Id : ").append(admissions.getEmail());
        holder.studEmail.setText(stringBuffer.toString());
        stringBuffer=new StringBuffer("Joined For : ");
        stringBuffer.append(admissions.getCourse());
        holder.studCourse.setText(stringBuffer.toString());
        stringBuffer=new StringBuffer("Fee : ").append(admissions.getFee());
        holder.studFee.setText(stringBuffer.toString());
    }

    @Override
    public int getItemCount() {
        return admissionsList.size();
    }
}