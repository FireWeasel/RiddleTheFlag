package com.example.marinac.riddletheflag;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Drawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback,View.OnClickListener {

    FirebaseAuth mAuth;
    FirebaseDatabase database;
    DatabaseReference myRef;
    DatabaseReference myRef2;
    private String userId;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 2;
    private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 3;
    TextView nameView, emailView;
    SupportMapFragment map;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private List<Flag> userFlags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        userFlags = new ArrayList<>();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        map = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.setBackCurrentLocation);
        fab.setOnClickListener(this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        checkLocationPermission();

        View header = navigationView.getHeaderView(0);
        nameView = (TextView)header.findViewById(R.id.nameTextView);
        emailView = (TextView) header.findViewById(R.id.profileEmailVIew);
        map.onCreate(savedInstanceState);
        map.getMapAsync(this);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LoadUser();
        GetFlags();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

            if(location!= null)
            {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),13));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(location.getLatitude(), location.getLongitude()))
                        .zoom(17)
                        .bearing(90)
                        .tilt(40)
                        .build();
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    String riddleToSolve = marker.getSnippet();
                    Toast.makeText(getApplicationContext(), "Test", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(Drawer.this);
                    View mView = getLayoutInflater().inflate(R.layout.dialog_marker, null);
                    final EditText inputAnswer = (EditText)mView.findViewById(R.id.answerTb);
                    TextView riddle = (TextView)mView.findViewById(R.id.riddleLabel);
                    riddle.setText(riddleToSolve);
                    Button solve = (Button)mView.findViewById(R.id.solveRiddle);
                    final Flag flag = (Flag)marker.getTag();
                    final String name = flag.name;
                    myRef = FirebaseDatabase.getInstance().getReference().child("users");


                    solve.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String answer = inputAnswer.getText().toString().trim();
                            if(name.equals(answer))
                            {
                                userId = mAuth.getUid();
                                myRef.child(userId).child("flags").child(name).setValue(flag);
                                myRef2 = FirebaseDatabase.getInstance().getReference().child("flags");
                                User user = new User();
                                user.name = userId;
                                myRef2.child(name).child("users").child(name).setValue(user);
                                Log.d("status", "wohoo");
                                mMap.clear();
                                GetFlags();
                            }
                            else {
                                Toast.makeText(getApplicationContext(), "Wrong answer!", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    mBuilder.setView(mView);
                    AlertDialog dialog = mBuilder.create();
                    dialog.show();
                    return true;
                }
            });
        }

    }

    public boolean checkLocationPermission(){
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
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
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){

            }else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            }
            return false;
        }else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(Drawer.this, SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.friendListBtn) {
            startActivity(new Intent(Drawer.this, FriendActivity.class));
        } else if (id == R.id.foundFlagsBtn) {
            startActivity(new Intent(Drawer.this, FlagActivity.class));
        } else if (id == R.id.addFlagsBtn) {

        } else if (id == R.id.nav_signout) {
            mAuth.signOut();
            startActivity(new Intent(this, LauncherActivity.class));
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void LoadUser(){
        myRef = FirebaseDatabase.getInstance().getReference().child("users");
        userId = mAuth.getUid();
        myRef.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                nameView.setText(user.name);
                emailView.setText(user.rank);
                Log.d("user", user.toString());
//                for (DataSnapshot snapshot: dataSnapshot.child("flags").getChildren()){
//                    Flag flag = (Flag)snapshot.getValue(Flag.class);
//                    userFlags.add(flag);
//                    Log.d("result", flag.toString());
//                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void GetFlags(){

        myRef = FirebaseDatabase.getInstance().getReference().child("flags");
        myRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longtitude = dataSnapshot.child("longtitude").getValue(Double.class);
                String hint = dataSnapshot.child("hint").getValue(String.class);
                String riddle = dataSnapshot.child("riddle").getValue(String.class);
                Flag flag = dataSnapshot.getValue(Flag.class);
                String currentName = flag.name;
                List<Flag> userFlags = new ArrayList<>();
                userId = mAuth.getUid();

                for(DataSnapshot snapshot:dataSnapshot.child("users").getChildren()){
                    User usr = (User)snapshot.getValue(User.class);
                    String userName = usr.name;
                    Log.d("extracted name", usr.name);
                    Log.d("current user name", userId);
                    if(userId.equals(userName)){
                        userFlags.add(flag);
                    }
                }


                    LatLng location = new LatLng(
                            latitude,
                            longtitude
                    );
                    Log.d("location", location.toString());
                    String title = "Flag";
                    String snippet = "Riddle: "
                                     + riddle;
                    MarkerOptions options = new MarkerOptions()
                            .position(location)
                            .title(title)
                            .snippet(snippet);

                if(!userFlags.isEmpty()){
                    for(Flag f: userFlags){
                        if(!f.name.equals(currentName)){
                            Marker maker = mMap.addMarker(options);
                            maker.setTag(flag);
                        }
                    }
                }
                else {
                    Marker maker = mMap.addMarker(options);
                    maker.setTag(flag);
                    Log.d("bla  ", "bla");
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longtitude = dataSnapshot.child("longtitude").getValue(Double.class);
                String hint = dataSnapshot.child("hint").getValue(String.class);
                String riddle = dataSnapshot.child("riddle").getValue(String.class);
                Flag flag = dataSnapshot.getValue(Flag.class);
                String currentName = flag.name;
                LatLng location = new LatLng(
                        latitude,
                        longtitude
                );
                Log.d("location", location.toString());
                String title = "Flag";
                String snippet = "Riddle: "
                        + riddle;
                MarkerOptions options = new MarkerOptions()
                        .position(location)
                        .title(title)
                        .snippet(snippet);
                Marker maker = mMap.addMarker(options);
                maker.setTag(flag);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Double latitude = dataSnapshot.child("latitude").getValue(Double.class);
                Double longtitude = dataSnapshot.child("longtitude").getValue(Double.class);
                String hint = dataSnapshot.child("hint").getValue(String.class);
                String riddle = dataSnapshot.child("riddle").getValue(String.class);
                Flag flag = dataSnapshot.getValue(Flag.class);
                String currentName = flag.name;
                LatLng location = new LatLng(
                        latitude,
                        longtitude
                );
                Log.d("location", location.toString());
                String title = "Flag";
                String snippet = "Riddle: "
                        + riddle;
                MarkerOptions options = new MarkerOptions()
                        .position(location)
                        .title(title)
                        .snippet(snippet);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.setBackCurrentLocation:
                if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                    locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                    Criteria criteria = new Criteria();

                    Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));

                    if(location!= null)
                    {
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),13));
                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(new LatLng(location.getLatitude(), location.getLongitude()))
                                .zoom(17)
                                .bearing(90)
                                .tilt(40)
                                .build();
                        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    }
                }
                break;
        }
    }
}
