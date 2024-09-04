package com.svrinfoteh.trainingmanagement;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.instacart.library.truetime.TrueTime;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.User;

import io.fabric.sdk.android.Fabric;
import java.io.IOException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

public class Login extends AppCompatActivity implements View.OnClickListener {
    private EditText username,password;
    private Button login,signup;
    private TextView forgotPassword,loginTitle;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FragmentManager fragmentManager;
    private Fragment fragment;
    private final String sharedPreferenceName="SHARED_PREFERENCE";
    private boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_login);
        init();
        login.setOnClickListener(this);
        signup.setOnClickListener(this);
        forgotPassword.setOnClickListener(this);

        authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null) {
                    final SharedPreferences.Editor sharedPreferences=getApplicationContext().getSharedPreferences(sharedPreferenceName,MODE_PRIVATE).edit();
                    final String email=firebaseAuth.getCurrentUser().getEmail();
                    DatabaseReference userReference= FirebaseReference.getFirebaseDatabaseReference().child("users");
                    userReference.keepSynced(true);
                    fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment).commitAllowingStateLoss();
                    getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    userReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Iterator<DataSnapshot> iterator=dataSnapshot.getChildren().iterator();
                            while (iterator.hasNext()) {
                                DataSnapshot userData=iterator.next();
                                User user=userData.getValue(User.class);
                                if(user.getUsername().equals(email)) {
                                    String name=user.getName();
                                    sharedPreferences.putString("name",name);
                                    String status=user.getStatus();
                                    sharedPreferences.putString("status",status);
                                    if(user.getCourse()!=null) {
                                        HashSet<String> courses=new HashSet<>(user.getCourse());
                                        sharedPreferences.putStringSet("courses",courses);
                                    }
                                    sharedPreferences.apply();
                                    fragmentManager.beginTransaction().remove(fragment).commitAllowingStateLoss();
                                    startActivity(new Intent(getBaseContext(),MainActivity.class));
                                    finish();
                                    break;
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        flag=false;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void init() {
        username=findViewById(R.id.username);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        signup=findViewById(R.id.signup);
        firebaseAuth=FirebaseAuth.getInstance();
        forgotPassword=findViewById(R.id.forgotPassword);
        loginTitle=findViewById(R.id.loginTitle);
        fragmentManager=getSupportFragmentManager();
        fragment=new Progressbar();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login:startLogin();
                break;
            case R.id.signup:
                hideFronView();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,new Signup()).commit();
                break;
            case R.id.forgotPassword:
                String email_id=username.getText().toString().trim();
                if(TextUtils.isEmpty(email_id)) {
                    username.setError("Please Enter Email-ID Then Click Forgot Password");
                } else {
                    firebaseAuth.sendPasswordResetEmail(email_id)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(),"Password Reset Link Has Been Sent To You Registred Email-ID",Toast.LENGTH_LONG)
                                                .show();
                                    } else {
                                        Toast.makeText(getApplicationContext(),"USER NOT FOUND",Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
                break;
        }
    }

    private void startLogin() {

        final String emailValue=username.getText().toString();
        String passwordValue=password.getText().toString();
        if(TextUtils.isEmpty(emailValue) || TextUtils.isEmpty(passwordValue)) {
            Toast.makeText(getApplicationContext(),"Please fill all details", Toast.LENGTH_LONG).show();
        } else {
            /*fragmentManager.beginTransaction().replace(R.id.frameLayout,fragment).commit();
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);*/
            firebaseAuth.signInWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(!task.isSuccessful()) {
                        fragmentManager.beginTransaction().remove(fragment).commit();
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        Toast.makeText(getApplicationContext(),"Login Issue",Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    private void hideFronView() {
        loginTitle.setVisibility(View.GONE);
        username.setVisibility(View.GONE);
        password.setVisibility(View.GONE);
        login.setVisibility(View.GONE);
        signup.setVisibility(View.GONE);
        forgotPassword.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if(flag) {
            finish();
        } else {
            Toast.makeText(getApplicationContext(),"PRESS AGAIN TO EXIT",Toast.LENGTH_LONG).show();
            flag=true;
        }
    }
}
