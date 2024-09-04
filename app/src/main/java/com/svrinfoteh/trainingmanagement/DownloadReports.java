package com.svrinfoteh.trainingmanagement;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.holder.ReportHolder;
import com.svrinfoteh.trainingmanagement.pojo.Report;



/**
 * A simple {@link Fragment} subclass.
 */
public class DownloadReports extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference reportReference;
    public DownloadReports() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_download_reports, container, false);
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        init(rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity().getBaseContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity().getBaseContext(), LinearLayoutManager.VERTICAL));
        FirebaseRecyclerOptions<Report> firebaseRecyclerOptions=new FirebaseRecyclerOptions
                .Builder<Report>()
                .setQuery(reportReference,Report.class)
                .build();

        FirebaseRecyclerAdapter<Report,ReportHolder> firebaseRecyclerAdapter=new FirebaseRecyclerAdapter<Report, ReportHolder>(
                firebaseRecyclerOptions
        ) {
            @Override
            protected void onBindViewHolder(@NonNull ReportHolder holder, int position, @NonNull final Report model) {
                holder.setFileName(model.getFileName());
                holder.setUploadDate(DateFormat.format("dd-MM-yy",model.getUploadDate()));
                holder.downloadFile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent();
                        intent.setType(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(model.getFilePath()));
                        getActivity().getBaseContext().startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public ReportHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.report_row,parent,false);
                return new ReportHolder(view);
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    private void init(View rootView) {
        recyclerView=rootView.findViewById(R.id.reportRecyclerView);
        reportReference= FirebaseReference.getFirebaseDatabaseReference().child("progressReport");
        reportReference.keepSynced(true);
    }
}