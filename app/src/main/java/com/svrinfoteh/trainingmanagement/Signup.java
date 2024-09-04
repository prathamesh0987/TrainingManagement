package com.svrinfoteh.trainingmanagement;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.svrinfoteh.trainingmanagement.firebasedata.FirebaseReference;
import com.svrinfoteh.trainingmanagement.pojo.User;

public class Signup extends Fragment {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;
    private Button submit;
    private EditText name,mobile_no,username,password,confirmPassword;
    private Context context;
    private Window window;
    public Signup() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView=inflater.inflate(R.layout.fragment_signup, container, false);
        init(rootView);
        context=getActivity().getApplicationContext();
        window=getActivity().getWindow();
        userReference.keepSynced(true);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    int passLength=password.getText().toString().length();
                    if(passLength<6) {
                        password.setError("Must Be Greater Than 6");
                    }
                }
            }
        });

        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String pass=password.getText().toString();
                    String confPass=confirmPassword.getText().toString();

                    if(!(pass.equals(confPass))) {
                        confirmPassword.setText("");
                        confirmPassword.setError("Password Not Match");
                    }
                }
            }
        });

        mobile_no.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus) {
                    String mobile = mobile_no.getText().toString();
                    if(mobile.length()<10) {
                        mobile_no.setError("Mobile number should contain 10 digits");
                    }
                }
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailValue = username.getText().toString();
                final String passwordValue = password.getText().toString();
                final String userName = name.getText().toString();
                final String mobile = mobile_no.getText().toString();
                final Fragment fragment=new Progressbar();
                final FragmentManager fragmentManager = getFragmentManager();

                if (TextUtils.isEmpty(userName) ||
                        TextUtils.isEmpty(emailValue) ||
                        TextUtils.isEmpty(passwordValue) ||
                        TextUtils.isEmpty(mobile)) {
                    Toast.makeText(context,
                            "Please fill all details",
                            Toast.LENGTH_LONG).show();
                } else {
                    if(mobile.length()<10) {
                        Toast.makeText(
                                context,
                                "Please enter valid mobile number",
                                Toast.LENGTH_LONG
                        ).show();
                    } else {
                        fragmentManager.beginTransaction().replace(R.id.signupLayout,fragment).commit();
                        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                        firebaseAuth.createUserWithEmailAndPassword(emailValue,passwordValue).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    User user=new User(userName,mobile,emailValue,"none",null);
                                    userReference.child(userName).setValue(user);
                                    fragmentManager.beginTransaction().remove(fragment).commit();
                                    window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                    Toast.makeText(context,
                                            "Account created",
                                            Toast.LENGTH_LONG).show();
                                    context.startActivity(new Intent(context,Login.class));
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                fragmentManager.beginTransaction().remove(fragment).commit();
                                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                                Toast.makeText(context,
                                        "Failure Reason : "+e.getMessage(),
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void init(View view) {
        firebaseAuth=FirebaseAuth.getInstance();
        userReference= FirebaseReference.getFirebaseDatabaseReference().child("users");
        name=view.findViewById(R.id.username);
        mobile_no=view.findViewById(R.id.mobile);
        username=view.findViewById(R.id.emailField);
        password=view.findViewById(R.id.passwordField);
        confirmPassword=view.findViewById(R.id.confirmPassword);
        submit=view.findViewById(R.id.signup);
    }
}
