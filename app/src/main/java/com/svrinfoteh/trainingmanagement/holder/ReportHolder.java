package com.svrinfoteh.trainingmanagement.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.svrinfoteh.trainingmanagement.R;

public class ReportHolder extends RecyclerView.ViewHolder {
    private TextView filename,uploadDate;
    public TextView downloadFile;
    public ReportHolder(View itemView) {
        super(itemView);
        filename = itemView.findViewById(R.id.file);
        uploadDate = itemView.findViewById(R.id.uploadDate);
        downloadFile = itemView.findViewById(R.id.downloadFile);
    }

    public void setFileName(String name) {
        filename.setText(name);
    }

    public void setUploadDate(CharSequence date) {
        uploadDate.setText(date);
    }
}