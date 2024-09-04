package com.svrinfoteh.trainingmanagement.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;

import com.svrinfoteh.trainingmanagement.R;

public class SyllabusHolder extends RecyclerView.ViewHolder {
    public CheckBox syllabusPoint;
    public SyllabusHolder(View itemView) {
        super(itemView);
        syllabusPoint=itemView.findViewById(R.id.pointCheck);
    }

    public  void setSyllabusPoint(String data) {
        syllabusPoint.setText(data);
    }

}
