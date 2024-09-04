package com.svrinfoteh.trainingmanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.instacart.library.truetime.TrueTime;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.CourseFee;
import com.svrinfoteh.trainingmanagement.pojo.ProfilePic;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment fragment;
    private FirebaseAuth firebaseAuth;
    private Toolbar toolbar;
    private NavigationView navigationView;
    private RoundedImageView dp;
    private String username;
    private final String sharedPreferenceName="SHARED_PREFERENCE";
    private SharedPreferences sharedPreferences;
    private final int GALLERY_REQUEST=1;
    private Uri downloadUri,imageUri=null,finalImageUri=null;
    private DatabaseReference profilePicReference,feeReference;
    private String status;
    Double feeRemaining=0.0D,totalFee=0.0D;
    private BroadcastReceiver broadcastReceiver;
    public Double getFeeRemaining() {
        return feeRemaining;
    }

    public void setFeeRemaining(Double feeRemaining) {
        this.feeRemaining = feeRemaining;
    }

    public Double getTotalFee() {
        return totalFee;
    }

    public void setTotalFee(Double totalFee) {
        this.totalFee = totalFee;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        broadcastReceiver=new TimeReceiver();

        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        this.registerReceiver(broadcastReceiver,intentFilter);


        setSupportActionBar(toolbar);
        DrawerLayout drawer =  findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        View headerLayout=navigationView.getHeaderView(0);

        String user=FirebaseAuth.getInstance().getCurrentUser().getEmail();

        TextView email=headerLayout.findViewById(R.id.email);
        email.setText(user);

        sharedPreferences=getApplicationContext().getSharedPreferences(sharedPreferenceName,MODE_PRIVATE);
        username=sharedPreferences.getString("name",null);
        if(username!=null) {
            TextView profile_name=headerLayout.findViewById(R.id.profile_name);
            profile_name.setText(username);
            profilePicReference= FirebaseReference.getFirebaseDatabaseReference().child("profileDP").child(username);
            profilePicReference.keepSynced(true);
        }

        dp=headerLayout.findViewById(R.id.profilePic);
        dp.setImageResource(R.mipmap.ic_account_circle_black_48dp);

        profilePicReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                while (iterator.hasNext()) {
                    DataSnapshot profileData=iterator.next();
                    final ProfilePic profile=profileData.getValue(ProfilePic.class);
                        Picasso.with(getBaseContext()).load(profile.getPic()).networkPolicy(NetworkPolicy.OFFLINE).into(dp, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError() {
                                Picasso.with(getBaseContext()).load(profile.getPic()).into(dp);
                            }
                        });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        dp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,GALLERY_REQUEST);
            }
        });

        status=sharedPreferences.getString("status",null);
        if(status!=null) {
            switch (status) {
                case "student":
                case "employee":
                    //hideMenuItem(R.id.newAdmission);
                    //hideMenuItem(R.id.totalAdmission);
                    hideMenuItem(R.id.totalCollection);
                    break;
                case "owner":
                    hideMenuItem(R.id.newAdmission);
                    hideMenuItem(R.id.groupChat);
                    break;
                case "admin":
                    hideMenuItem(R.id.groupChat);
                    break;
                case "none":
                    hideMenuItem(R.id.newAdmission);
                    hideMenuItem(R.id.totalAdmission);
                    hideMenuItem(R.id.totalCollection);
                    hideMenuItem(R.id.groupChat);
                    hideMenuItem(R.id.syllabus);
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(broadcastReceiver);
    }

    @Override
    public void invalidateOptionsMenu() {
        super.invalidateOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.admin_task,menu);
        switch (status) {
            case "none":
                hideMenuContent(R.id.setStatus,menu);
                hideMenuContent(R.id.setSyllabus,menu);
                hideMenuContent(R.id.uploadProgressReport,menu);
                hideMenuContent(R.id.downloadProgressReport,menu);
                hideMenuContent(R.id.feeStatus,menu);
                return true;
            case "student":
                hideMenuContent(R.id.setStatus,menu);
                hideMenuContent(R.id.setSyllabus,menu);
                hideMenuContent(R.id.uploadProgressReport,menu);
                return true;
            case "employee":
                hideMenuContent(R.id.setStatus,menu);
                hideMenuContent(R.id.feeStatus,menu);
                hideMenuContent(R.id.downloadProgressReport,menu);
                return true;
            case "owner":
            case "admin":
                hideMenuContent(R.id.setSyllabus,menu);
                hideMenuContent(R.id.uploadProgressReport,menu);
                hideMenuContent(R.id.feeStatus,menu);
                return true;
            default:return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setStatus:
                fragment=new AdminTask();
                setTitle("Admin");
                openFragment();
                return true;
            case R.id.setSyllabus:
                fragment=new SetSyllabus();
                Bundle bundle=new Bundle();
                ArrayList<String> subjectList=new ArrayList<>(sharedPreferences.getStringSet("courses",null));
                bundle.putStringArrayList("subjectList",subjectList);
                fragment.setArguments(bundle);
                setTitle("Set Syllabus");
                openFragment();
                return true;
            case R.id.uploadProgressReport:
                setTitle("Progress Report");
                fragment=new UploadProgressData();
                openFragment();
                return true;
            case R.id.downloadProgressReport:
                setTitle("Download Reports");
                fragment=new DownloadReports();
                openFragment();
                return true;
            case R.id.feeStatus:
                displayFeeStatus();
                return true;
            default:return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);

        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                finalImageUri = result.getUri();
                dp.setImageURI(finalImageUri);
                final StorageReference storageReference= FirebaseReference.getFirebaseStorageReference().child("profile").child(username);
                storageReference.putFile(finalImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        final ProfilePic profile=new ProfilePic();
                        profile.setUsername(username);
                        storageReference.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                downloadUri=task.getResult();
                                profile.setPic(downloadUri.toString());
                                profilePicReference.child("pic").setValue(profile).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(getApplicationContext(),
                                                "Profile Pic Changed",
                                                Toast.LENGTH_LONG)
                                                .show();
                                    }
                                });
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(
                                getApplicationContext(),
                                "Something went wrong",
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Log.e("Error", error.getMessage());
            }
        }
    }

    private void init() {
        firebaseAuth=FirebaseAuth.getInstance();
        toolbar = findViewById(R.id.toolbar);
        navigationView=findViewById(R.id.nav_view);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        switch (item.getItemId()) {
            case R.id.home:startActivity(new Intent(getApplicationContext(),MainActivity.class));
                finish();
                break;
            case R.id.newAdmission:fragment=new Admission();
                setTitle("NEW ADMISSION");
                openFragment();
                break;
            case R.id.totalAdmission:fragment=new TotalAdmission();
                setTitle("TOTAL ADMISSION");
                openFragment();
                break;
            case R.id.totalCollection:fragment=new TotalCollection();
                setTitle("TOTAL COLLECTION");
                openFragment();
                break;
            case R.id.syllabus:fragment=new Syllabus();
                setTitle("SYLLABUS");
                openFragment();
                break;
            case R.id.event:fragment=new Event();
                setTitle("EVENTS");
                openFragment();
                break;
            case R.id.groupChat:
                setTitle("GROUP CHAT");
                fragment=new GroupChat();
                openFragment();
                break;
            case R.id.logout:firebaseAuth.signOut();
                startActivity(new Intent(getBaseContext(),Login.class));
                finish();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openFragment() {
        if(fragment!=null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.mainLayout, fragment).commit();
        }
    }

    private void hideMenuItem(int id) {
        Menu menu=navigationView.getMenu();
        menu.findItem(id).setVisible(false);
    }

    private void hideMenuContent(int id,Menu menu) {
        MenuItem menuItem=menu.findItem(id);
        menuItem.setVisible(false);
    }

    private void displayFeeStatus() {
        ArrayList<String> subjectList = new ArrayList<>(sharedPreferences.getStringSet("courses", null));
        if (subjectList != null) {
            for (String subject : subjectList) {
                feeReference = FirebaseReference.getFirebaseDatabaseReference().child("fee").child(subject).child(username);
                feeReference.keepSynced(true);

                feeReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        CourseFee courseFee = dataSnapshot.getValue(CourseFee.class);
                        feeRemaining += Double.parseDouble(courseFee.getFeeRemaining());
                        totalFee += Double.parseDouble(courseFee.getTotalFee());
                        setFeeRemaining(feeRemaining);
                        setTotalFee(totalFee);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Toast.makeText(
                                getApplicationContext(),
                                "Error : " + databaseError.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                });
            }
            if (getTotalFee() != 0.0 && getFeeRemaining() != 0.0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
                StringBuffer fee = new StringBuffer("Total Fee : ").append(getTotalFee())
                        .append("\nFee Remaining : ").append(getFeeRemaining());
                builder.setMessage(fee.toString());
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            } else {
                Toast.makeText(
                        getApplicationContext(),
                        "Please Try again Later...",
                        Toast.LENGTH_LONG).show();
            }
            totalFee = feeRemaining = 0.0D;
        }
    }

    private class TimeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                initTrueTime(getApplicationContext());
                Date date=getTrueTimeDate();
                String realDate= DateFormat.format("dd-MM-yyyy",date.getTime()).toString();
                String dueDate="30-08-2018";

                String[] realDates=realDate.split("-");
                String[] dueDates=dueDate.split("-");
                if(realDate.equals(dueDate) ||
                        (Integer.parseInt(realDates[0])>=Integer.parseInt(dueDates[0]) &&
                                Integer.parseInt(realDates[1])>=Integer.parseInt(dueDates[1]) &&
                                realDates[2].equals(dueDates[2]))) {
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    Snackbar snackbar=Snackbar.make(findViewById(android.R.id.content),
                            "Subscription Expires.. Please Contact Administrator",
                            Snackbar.LENGTH_INDEFINITE);
                    snackbar.show();
                }
            }
        }
    }

    public static void initTrueTime(Context context) {
        if(isNetworkConnected(context)) {
            if(!TrueTime.isInitialized()) {
                new InitilizeTrueTime(context).execute();
            }
        }
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=connectivityManager.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnectedOrConnecting();
    }

    public Date getTrueTimeDate() {
        return TrueTime.isInitialized() ? TrueTime.now():new Date();
    }

    private static class InitilizeTrueTime extends AsyncTask<Void,Void,Void> {
        private Context context;
        public InitilizeTrueTime(Context context) {
            this.context=context;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                TrueTime.build()
                        .withSharedPreferences(context)
                        .withNtpHost("time.google.com")
                        .withLoggingEnabled(false)
                        .withConnectionTimeout(31_428)
                        .initialize();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("Exception : ",e.getMessage());
            }
            return null;
        }
    }
}