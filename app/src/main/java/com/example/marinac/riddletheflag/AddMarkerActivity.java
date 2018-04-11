package com.example.marinac.riddletheflag;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Layout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddMarkerActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_marker);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
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
                        final EditText riddle = mView.findViewById(R.id.newMarkRiddle);
                        final EditText answer = mView.findViewById(R.id.newMarkAnswer);
                        final EditText descr = mView.findViewById(R.id.newMarkDescr);
                        final Button addMarkerButton = mView.findViewById(R.id.addRiddle);

                        mBuilder.setView(mView);
                        AlertDialog dialog = mBuilder.create();
                        dialog.show();
                        addMarkerButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Flag flag = new Flag(answer.getText().toString(), latLng);
                                flag.description = descr.getText().toString();
                                flag.difficulty = 1;
                                flag.hint = "test hint";
                                flag.points = 1;
                                flag.riddle = riddle.getText().toString();
                                flag.picture = "";

                                myRef = FirebaseDatabase.getInstance().getReference();
                                myRef.child("flags").child(flag.name).setValue(flag);
                                startActivity(new Intent(AddMarkerActivity.this, Drawer.class));

                            }
                        });

                        return true;
                    }
                });


            }
        });
    }
}
