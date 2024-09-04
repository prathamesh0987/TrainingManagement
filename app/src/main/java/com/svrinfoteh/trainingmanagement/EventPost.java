package com.svrinfoteh.trainingmanagement;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventPost extends Fragment implements View.OnClickListener {

    private Button post;
    private ImageButton media;
    private EditText title,description;
    private final int GALLERY_REQUEST=1;
    private Uri imageUri=null,finalImageUri=null;
    private StorageReference storageReference;
    private DatabaseReference databaseReference;
    private Uri downloadURI;
    Context context;
    Progressbar progressbar;

    public EventPost() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_event_post, container, false);
        init(rootView);
        media.setOnClickListener(this);
        post.setOnClickListener(this);
        context=getActivity().getBaseContext();
        return rootView;
    }

    private void init(View view) {
        media=view.findViewById(R.id.uploadMedia);
        title=view.findViewById(R.id.eventTitle);
        description=view.findViewById(R.id.desc);
        post=view.findViewById(R.id.post);
        storageReference= FirebaseReference.getFirebaseStorageReference();
        databaseReference= FirebaseReference.getFirebaseDatabaseReference().child("event");
        progressbar=new Progressbar();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.uploadMedia:
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
                break;

            case R.id.post:
                postEvent();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK) {
            imageUri=data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(3,2)
                    .start(getActivity().getBaseContext(),this);

        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK) {
                finalImageUri=result.getUri();
                media.setImageURI(finalImageUri);
            } else if(resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error=result.getError();
                Log.e("Error",error.getMessage());
            }
        }
    }

    private void postEvent() {
        final String titleField=title.getText().toString().trim();
        final String descField=description.getText().toString().trim();
        title.setFocusable(false);
        description.setFocusable(false);
        if(!TextUtils.isEmpty(titleField) && !TextUtils.isEmpty(descField) && imageUri!=null) {
            final StorageReference filePath=storageReference.child("Event").child(titleField);
            getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            filePath.putFile(finalImageUri).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    getFragmentManager().beginTransaction().replace(R.id.uploadLayout,progressbar).commitNowAllowingStateLoss();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    getFragmentManager().beginTransaction().remove(progressbar).commitNowAllowingStateLoss();
                    final DatabaseReference newPost=databaseReference.child(titleField);
                    newPost.child("title").setValue(titleField);
                    newPost.child("description").setValue(descField);
                    filePath.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            downloadURI=task.getResult();
                            newPost.child("image").setValue(downloadURI.toString());
                        }
                    });
                    Toast.makeText(getActivity().getApplicationContext(),"Successfully Uploaded",Toast.LENGTH_LONG).show();
                    hideFrontPage();
                    getFragmentManager().beginTransaction().replace(R.id.uploadLayout,new Event()).commit();
                }
            });
        }
    }


    public void hideFrontPage() {
        post.setVisibility(View.GONE);
        title.setVisibility(View.GONE);
        description.setVisibility(View.GONE);
        media.setVisibility(View.GONE);
    }
}