package com.example.marinac.riddletheflag;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class AddMarkerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private LocationManager locationManager;

    private StorageReference mStorageRef;

    private static final int SELECTED_PICTURE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;

    private Drawable d;
    private Uri uri;

    private Button uploadBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

            if (location != null) {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(17)
                        .bearing(90)
                        .tilt(40)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                private DatabaseReference myRef;

                @Override
                public void onMapClick(final LatLng latLng) {
                    MarkerOptions markerOptions = new MarkerOptions();
                    markerOptions.position(latLng);
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                    mMap.addMarker(markerOptions);

                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                        @Override
                        public boolean onMarkerClick(Marker marker) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddMarkerActivity.this);
                            View mView = getLayoutInflater().inflate(R.layout.add_marker_dialog, null);
                            //mView.setBackgroundResource(android.R.color.transparent);
                            final EditText riddle = mView.findViewById(R.id.newMarkRiddle);
                            final EditText answer = mView.findViewById(R.id.newMarkAnswer);
                            final EditText descr = mView.findViewById(R.id.newMarkDescr);
                            final Button addMarkerButton = mView.findViewById(R.id.addRiddle);
                            final EditText diff = mView.findViewById(R.id.difTB);
                            //final ImageView imageView = mView.findViewById(R.id.newMarkImage);
                            final Button upldBtn = mView.findViewById(R.id.upldImg);


                            mBuilder.setView(mView);
                            AlertDialog dialog = mBuilder.create();
                            //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                            upldBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if (ContextCompat.checkSelfPermission(AddMarkerActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                        // Permission is not granted
                                        ActivityCompat.requestPermissions(AddMarkerActivity.this,
                                                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                    } else {
                                        getPictureFromStorage();
                                    }
                                }
                            });
                            //imageView.setBackground(d);
                            addMarkerButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    final Flag flag = new Flag(answer.getText().toString(), latLng);
                                    flag.description = descr.getText().toString();
                                    flag.difficulty = Integer.parseInt(diff.getText().toString());
                                    flag.hint = "test hint";
                                    flag.points = 1;
                                    flag.riddle = riddle.getText().toString();
                                    //flag.picture = "";
                                    StorageReference storageReference = mStorageRef.child("flags/" + flag.name + ".jpg");
                                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                            Uri downloadUri = taskSnapshot.getDownloadUrl();
                                            flag.picture = downloadUri.toString();
                                            if (!flag.description.equals("") && flag.difficulty != 0 && !flag.riddle.equals("")) {
                                                myRef = FirebaseDatabase.getInstance().getReference();
                                                myRef.child("flags").child(flag.name).setValue(flag);
                                                startActivity(new Intent(AddMarkerActivity.this, Drawer.class));
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Please input correct values in the fields!", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                }
                            });

                            return true;
                        }
                    });


                }
            });
        }

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            private DatabaseReference myRef;
            @Override
            public void onMapClick(final LatLng latLng) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(latLng);
                mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.addMarker(markerOptions);

                mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(AddMarkerActivity.this);
                        View mView = getLayoutInflater().inflate(R.layout.add_marker_dialog, null);
                        //mView.setBackgroundResource(android.R.color.transparent);
                        final EditText riddle = mView.findViewById(R.id.newMarkRiddle);
                        final EditText answer = mView.findViewById(R.id.newMarkAnswer);
                        final EditText descr = mView.findViewById(R.id.newMarkDescr);
                        final Button addMarkerButton = mView.findViewById(R.id.addRiddle);
                        final EditText diff = mView.findViewById(R.id.difTB);
                        //final ImageView imageView = mView.findViewById(R.id.newMarkImage);
                        final Button upldBtn = mView.findViewById(R.id.upldImg);


                        mBuilder.setView(mView);
                        AlertDialog dialog = mBuilder.create();
                        //dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        dialog.show();
                        upldBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (ContextCompat.checkSelfPermission(AddMarkerActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                        != PackageManager.PERMISSION_GRANTED) {
                                    // Permission is not granted
                                    ActivityCompat.requestPermissions(AddMarkerActivity.this,
                                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                                } else {
                                    getPictureFromStorage();
                                }
                            }
                        });
                        //imageView.setBackground(d);
                        addMarkerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                final Flag flag = new Flag(answer.getText().toString(), latLng);
                                flag.description = descr.getText().toString();
                                flag.difficulty = Integer.parseInt(diff.getText().toString());
                                flag.hint = "test hint";
                                flag.points = 1;
                                flag.riddle = riddle.getText().toString();
                                //flag.picture = "";
                                StorageReference storageReference = mStorageRef.child("flags/" + flag.name + ".jpg");
                                storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Uri downloadUri = taskSnapshot.getDownloadUrl();
                                        flag.picture = downloadUri.toString();
                                        if(!flag.description.equals("") && flag.difficulty != 0 && !flag.riddle.equals("")){
                                            myRef = FirebaseDatabase.getInstance().getReference();
                                            myRef.child("flags").child(flag.name).setValue(flag);
                                            startActivity(new Intent(AddMarkerActivity.this, Drawer.class));
                                        }
                                        else {
                                            Toast.makeText(getApplicationContext(), "Please input correct values in the fields!", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                            }
                        });

                        return true;
                    }
                });


            }
        });
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

        switch (requestCode) {
            case SELECTED_PICTURE:
                if (resultCode == RESULT_OK) {
                    uri = data.getData();

                    String[] projection = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(projection[0]);
                    String filepath = cursor.getString(columnIndex);

                    Bitmap yourSelectedImage = BitmapFactory.decodeFile(filepath);
                    d = new BitmapDrawable(yourSelectedImage);

                    //imageView.setBackground(d);
                } else {
                    Toast.makeText(getApplicationContext(), "Something went wrong!", Toast.LENGTH_SHORT);
                }
                break;
        }
    }
}
