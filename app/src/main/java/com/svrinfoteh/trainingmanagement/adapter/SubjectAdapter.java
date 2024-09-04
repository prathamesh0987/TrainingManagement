package com.svrinfoteh.trainingmanagement.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.svrinfoteh.trainingmanagement.R;

import java.util.ArrayList;

public class SubjectAdapter extends RecyclerView.Adapter<SubjectAdapter.SubjectHolder> {
    ArrayList<String> subjectList;

    public SubjectAdapter(ArrayList<String> subjectList) {
        this.subjectList = subjectList;
    }

    public class SubjectHolder extends RecyclerView.ViewHolder{
        TextView subjectName;

        public SubjectHolder(View itemView) {
            super(itemView);
            subjectName=itemView.findViewById(R.id.subjectName);
        }
    }

    @NonNull
    @Override
    public SubjectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View rootView= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.subject_title_row,parent,false);
        return new SubjectHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull SubjectHolder holder, int position) {
        String subject=subjectList.get(position);
        holder.subjectName.setText(subject);
    }

    @Override
    public int getItemCount() {
        return subjectList.size();
    }

}
