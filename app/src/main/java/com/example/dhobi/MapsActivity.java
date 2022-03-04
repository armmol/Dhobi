package com.example.dhobi;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.dhobi.databinding.ActivityMapsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    private FirebaseAuth mAuth;
    private FirebaseDatabase db;
    private DatabaseReference dbref;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);

        binding = ActivityMapsBinding.inflate (getLayoutInflater ());
        setContentView (binding.getRoot ());
        mAuth = FirebaseAuth.getInstance ();
        db = FirebaseDatabase.getInstance ();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager ()
                .findFragmentById (R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync (this);
        }
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
    public void onMapReady (@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        checkperms (mMap);

        dbref = db.getReference ("Test");
        dbref.setValue (new LatLng (1, 1));
        dbref.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange (@NonNull DataSnapshot snapshot) {
                LatLng location = snapshot.child ("Test").getValue (LatLng.class);
                if (location != null) {
                    mMap.addMarker (new MarkerOptions ().position (location).title ("YOU ARE HERE"));
                    mMap.animateCamera (CameraUpdateFactory.newLatLngZoom (location, 16.0f));
                    Toast.makeText (MapsActivity.this, "Success" + location.latitude, Toast.LENGTH_SHORT).show ();
                }
            }

            @Override
            public void onCancelled (@NonNull DatabaseError error) {
                Toast.makeText (MapsActivity.this, "Failed", Toast.LENGTH_SHORT).show ();
            }
        });
    }

    private void checkperms(GoogleMap googleMap){
        if (ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (googleMap != null) {
                googleMap.setMyLocationEnabled (true);
                googleMap.getUiSettings ().setMyLocationButtonEnabled (true);
                googleMap.getUiSettings ().setAllGesturesEnabled (true);
                googleMap.getUiSettings ().setZoomControlsEnabled (true);

            }
        } else {
            if (ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 12);
            }
            if (ContextCompat.checkSelfPermission (this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions (this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 13);
            }
        }
    }
}