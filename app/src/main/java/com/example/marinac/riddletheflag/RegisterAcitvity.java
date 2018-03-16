package com.example.marinac.riddletheflag;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class RegisterAcitvity extends AppCompatActivity implements View.OnClickListener{

    EditText nameTB, passTB;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitvity);
        mAuth = FirebaseAuth.getInstance();

        nameTB = findViewById(R.id.nameTB);
        passTB = findViewById(R.id.passTB);

        findViewById(R.id.registerBtn).setOnClickListener(this);
    }

    private void RegisterUser(){
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
        if(pass.length()<6){
            passTB.setError("Minimum lenght of password should be 6");
            passTB.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(getApplicationContext(),"User registered successfully!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
         switch (view.getId())
         {
             case R.id.registerBtn:
                 RegisterUser();
             break;
         }
    }
}
