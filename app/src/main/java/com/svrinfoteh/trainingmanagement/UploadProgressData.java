package com.svrinfoteh.trainingmanagement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;

import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.Report;

import java.util.Date;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class UploadProgressData extends Fragment implements View.OnClickListener{

    private EditText fileName,uploadFileAs;
    private Button browse,upload;
    private final int GALLERY_REQUEST=1;
    String filePath,file;
    Uri fileUri,downloadUri;
    DatabaseReference progressReports;
    Context context;
    Progressbar progressbar;
    public UploadProgressData() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_upload_progress_data, container, false);
        init(rootView);
        context=getActivity().getBaseContext();
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        browse.setOnClickListener(this);
        upload.setOnClickListener(this);
    }

    private void init(View rootView) {
        fileName=rootView.findViewById(R.id.fileName);
        browse=rootView.findViewById(R.id.browse);
        upload=rootView.findViewById(R.id.upload);
        uploadFileAs=rootView.findViewById(R.id.uploadFileAs);
        progressReports= FirebaseReference.getFirebaseDatabaseReference().child("progressReport");
        progressReports.keepSynced(true);
        progressbar=new Progressbar();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.browse:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                startActivityForResult(intent, GALLERY_REQUEST);
                break;
            case R.id.upload:
                uploadFile();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK) {
            filePath = data.getData().getPath();
            fileUri=data.getData();
            fileName.setText(filePath);
        }
    }

    private void uploadFile() {
        file=uploadFileAs.getText().toString().trim();
        if(!TextUtils.isEmpty(file)) {
            final StorageReference fileStorage= FirebaseStorage.getInstance().getReference().child("uploadProgress").child(file);
            fileStorage.putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    getFragmentManager().beginTransaction().replace(R.id.uploadProgressLayout,progressbar).commitAllowingStateLoss();
                    final Report report=new Report();
                    report.setFileName(file);
                    report.setUploadDate(new Date().getTime());
                    fileStorage.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            downloadUri=uri;
                            report.setFilePath(downloadUri.toString());
                            getFragmentManager().beginTransaction().remove(progressbar).commitAllowingStateLoss();
                            progressReports.child(file).setValue(report).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(context,
                                            "File Uploaded...",
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            });
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(
                            context,
                            ""+e.getMessage(),
                            Toast.LENGTH_LONG
                    ).show();
                }
            });
        }
    }
}