package com.example.marinac.riddletheflag;

import android.content.Intent;
import android.icu.text.UnicodeSetSpanner;
import android.support.annotation.NonNull;
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
                     //Toast.makeText(getApplicationContext(),"User logged in successfully!", Toast.LENGTH_LONG).show();
                     Intent intent = new Intent(SignInActivity.this, Drawer.class);
                     intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                     startActivity(intent);
                 }
                 else  {
                         Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                 }
            }
        });
        progressBar.setVisibility(View.GONE);
    }
}
