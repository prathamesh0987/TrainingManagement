package com.svrinfoteh.trainingmanagement.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.svrinfoteh.trainingmanagement.R;

import java.util.ArrayList;

public class StudentDataHolder extends RecyclerView.ViewHolder {

    private TextView name,mail,contact,course,fee;

    public StudentDataHolder(View itemView) {
        super(itemView);
        name=itemView.findViewById(R.id.studName);
        mail=itemView.findViewById(R.id.studEmail);
        contact=itemView.findViewById(R.id.studContactNo);
        course=itemView.findViewById(R.id.studCourse);
        fee=itemView.findViewById(R.id.studFee);
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    public void setContact(String contact) {
        StringBuffer stringBuffer=new StringBuffer("Contact No : ").append(contact);
        this.contact.setText(stringBuffer);
    }

    public void setMail(String mail) {
        StringBuffer stringBuffer=new StringBuffer("Mail Id : ").append(mail);
        this.mail.setText(stringBuffer);
    }

    public void setCourse(String courseName) {
        StringBuffer stringBuffer=new StringBuffer("Joined For : ");
            stringBuffer.append(courseName);
        course.setText(stringBuffer);
    }

    public void setFee(String fee) {
        StringBuffer stringBuffer=new StringBuffer("Fee : ").append(fee);
        this.fee.setText(stringBuffer);
    }
}