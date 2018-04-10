package com.example.marinac.riddletheflag;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.UnicodeSetSpanner;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
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
    private static final int MY_PERMISSIONS_REQUEST_ACESS_FINE_LOCATION = 2;
    private static final int MY_PERMISSIONS_REQUEST_ACESS_COARSE_LOCATION = 3;

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

    public boolean checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION)) {

            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        }
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){

            }else {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
            return false;
        }else {
            return true;
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
