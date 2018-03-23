package com.example.marinac.riddletheflag;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;
import com.example.marinac.riddletheflag.User;


public class RegisterAcitvity extends AppCompatActivity implements View.OnClickListener{

    //fields for GUI elements
    private ProgressBar progressBar;
    private EditText emailTb, passTB, nameTb;
    private ImageView iv;

    //
    private Drawable d;
    private Uri uri;


    //Firebase fields
    private FirebaseAuth mAuth;
    private StorageReference mStorageRef;
    private FirebaseDatabase database;
    private DatabaseReference myRef;



    //static fields for permissions and picture size
    private static final int SELECTED_PICTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    private static final int pHeight = 512;
    private static final int pLength = 512;

    //
    private String imageName;
    private String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_acitvity);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        emailTb = findViewById(R.id.emailTb);
        passTB = findViewById(R.id.passTB);
        nameTb = findViewById(R.id.NameTb);
        progressBar = findViewById(R.id.progressBar);
        iv = findViewById(R.id.profileImageView);

        findViewById(R.id.registerBtn).setOnClickListener(this);
        findViewById(R.id.uploadPictureBtn).setOnClickListener(this);

        emailTb.setOnClickListener(this);
        passTB.setOnClickListener(this);
        nameTb.setOnClickListener(this);

    }

    private void RegisterUser(){
        String email = emailTb.getText().toString().trim();
        String pass = passTB.getText().toString().trim();
        final String name = nameTb.getText().toString().trim();

        if(email.isEmpty()){
            emailTb.setError("Email is required!");
            emailTb.requestFocus();
            return;
        }
        if(pass.isEmpty())
        {
            passTB.setError("Password is required!");
            passTB.requestFocus();
            return;
        }
        if(name.isEmpty())
        {
            nameTb.setError("Name is required!");
            nameTb.requestFocus();
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTb.setError("Please enter a valid email!");
            emailTb.requestFocus();
            return;
        }
        if(pass.length()<6){
            passTB.setError("Minimum lenght of password should be 6");
            passTB.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressBar.setVisibility(View.GONE);
                if(task.isSuccessful())
                {
                    userId = mAuth.getUid();
                    imageName = name + ".jpg";

                    StorageReference storageReference = mStorageRef.child("users/" + userId + "/" + imageName);
                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                            User user = new User(name, 0, downloadUri.toString(), "Novice");

                            myRef.child("users").child(userId).setValue(user);
                            Toast.makeText(getApplicationContext(),"User registered successfully!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterAcitvity.this, Drawer.class));
                        }
                    });
                }
                else {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(),"You are already registered", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        progressBar.setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
         switch (view.getId())
         {
             case R.id.registerBtn:
                 RegisterUser();
                break;

             case R.id.emailTb:
                 emailTb.getText().clear();
                 break;

             case R.id.passTB:
                 passTB.getText().clear();
                 break;

             case R.id.NameTb:
                 nameTb.getText().clear();
                 break;

             case R.id.uploadPictureBtn:
                 if (ContextCompat.checkSelfPermission(RegisterAcitvity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                         != PackageManager.PERMISSION_GRANTED) {
                     // Permission is not granted
                     ActivityCompat.requestPermissions(RegisterAcitvity.this,
                             new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                             MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);



                 } else {
                     getPictureFromStorage();
                 }
                 break;
         }
    }

    public void getPictureFromStorage(){
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, SELECTED_PICTURE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getPictureFromStorage();
                } else {

                    Toast.makeText(this, "Permission was denied!",Toast.LENGTH_SHORT).show();
                }
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode)
        {
            case SELECTED_PICTURE:
                if(resultCode==RESULT_OK){
                    uri = data.getData();

                    String[]projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filepath = cursor.getString(columnIndex);

                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filepath);
                    d = new BitmapDrawable(yourSelectedImage);

                    iv.setBackground(d);
                }
                else {
                    Toast.makeText(getApplicationContext(),"Something went wrong!",Toast.LENGTH_SHORT);
                }
                break;
        }
    }
}

