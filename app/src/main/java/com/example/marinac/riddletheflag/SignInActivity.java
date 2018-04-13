package com.example.marinac.riddletheflag;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.UnicodeSetSpanner;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    //setting fields
    EditText nameTB, passTB;
    private FirebaseAuth mAuth;
    ProgressBar progressBar;
    private static int time_out = 3500;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();

        nameTB = findViewById(R.id.emailTb);
        passTB = findViewById(R.id.passTb);
        progressBar = findViewById(R.id.progressBar);

        findViewById(R.id.registerBtn).setOnClickListener(this);
        findViewById(R.id.signInBtn).setOnClickListener(this);
        findViewById(R.id.signCon).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
                return true;
            }
        });

    }



    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.registerBtn:
                startActivity(new Intent(this, RegisterAcitvity.class));
                break;
            case R.id.signInBtn:
                LogInUser();
                break;
        }
    }


    private void LogInUser() {
        String email = nameTB.getText().toString().trim();
        String pass = passTB.getText().toString().trim();

        if(email.isEmpty()){
            nameTB.setError("Email is required!");
            nameTB.requestFocus();
            return;
        }
        if(pass.isEmpty())
        {
            passTB.setError("Password is required!");
            passTB.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            nameTB.setError("Please enter a valid email!");
            nameTB.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                 if(task.isSuccessful())
                 {
                     ///checkLocationPermission();
                     new Handler().postDelayed(new Runnable() {
                         @Override
                         public void run() {
                             Intent intent = new Intent(SignInActivity.this, Drawer.class);
                             intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                             progressBar.setVisibility(View.GONE);
                             startActivity(intent);
                         }
                     },time_out);
                 }
                 else  {
                         Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                 }
            }
        });
    }
}
