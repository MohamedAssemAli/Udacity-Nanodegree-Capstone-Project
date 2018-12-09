package app.capstone.assem.com.capstone.Fragments.MainFragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

import app.capstone.assem.com.capstone.App.AppConfig;
import app.capstone.assem.com.capstone.Models.PlaceBookmarkModel;
import app.capstone.assem.com.capstone.R;
import app.capstone.assem.com.capstone.Utils.MapUtil;
import butterknife.ButterKnife;

public class MapFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = MapFragment.class.getSimpleName();
    // Vars
    private boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private MapUtil mapUtil;
    private String uid;
    // Firebase
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.frament_map, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        getLocationPermission();
        // Initialize GoogleApiClient
        mGoogleApiClient = new GoogleApiClient
                .Builder(Objects.requireNonNull(getActivity()))
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(), this)
                .build();
        // Initialize Firebase
        mRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserCredentials();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        if (mLocationPermissionGranted) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mapUtil = new MapUtil();
            getDeviceLocation();
            mMap.setMyLocationEnabled(true); //to get blue marker with GPS icon
            mMap.getUiSettings().setMyLocationButtonEnabled(true); //to hide GPS icon
            mMap.getUiSettings().setZoomControlsEnabled(true); // to enable zoom controls
            mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
                @Override
                public void onMapLongClick(LatLng latLng) {
                    setupAddBookmarkDialog(latLng);
                }
            });
            getPlaceToBookmarks(mMap);
        } else {
            Toast.makeText(getContext(), R.string.need_to_add_permissions, Toast.LENGTH_LONG).show();
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: initializing map");
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
        }
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.stopAutoManage(Objects.requireNonNull(getActivity()));
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(Objects.requireNonNull(getActivity()));
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
    }

    private void setupAddBookmarkDialog(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(Objects.requireNonNull(getContext()));
        final View mView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_bookmark, null);

        Button addBtn = mView.findViewById(R.id.dialog_add_bookmark_add_btn);
        Button cancelBtn = mView.findViewById(R.id.dialog_add_bookmark_cancel_btn);

        builder.setView(mView);
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPlaceToBookmark(latLng);
                alertDialog.dismiss();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }

    private void getUserCredentials() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
        }
    }

    private void addPlaceToBookmark(LatLng latLng) {
        String key = mRef.child(AppConfig.BOOKMARKS).child(uid).push().getKey();
        if (key != null) {
            final PlaceBookmarkModel placeBookmarkModel = new PlaceBookmarkModel(key, latLng.latitude, latLng.longitude, getString(R.string.bookmark));
            mRef.child(AppConfig.BOOKMARKS).child(uid).child(key).setValue(placeBookmarkModel)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(), R.string.added_successfully, Toast.LENGTH_LONG).show();
                            mapUtil.customMarker(getContext(), mMap, R.drawable.pin, getString(R.string.bookmark), new LatLng(placeBookmarkModel.getLat(), placeBookmarkModel.getLon()));
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(), R.string.error_adding, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void getPlaceToBookmarks(final GoogleMap mMap) {
        mRef.child(AppConfig.BOOKMARKS).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PlaceBookmarkModel placeBookmarkModel = snapshot.getValue(PlaceBookmarkModel.class);
                        mapUtil.customMarker(getContext(), mMap, R.drawable.pin, getString(R.string.bookmark), new LatLng(placeBookmarkModel.getLat(), placeBookmarkModel.getLon()));
                    }
                } else {
                    Toast.makeText(getContext(), R.string.how_to_add, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
                Log.d(TAG, databaseError.getDetails());
                Toast.makeText(getContext(), R.string.error_getting_data, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getDeviceLocation() {
        FusedLocationProviderClient mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext());
        try {
            if (mLocationPermissionGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "task.isSuccessful: task.isSuccessful and location found");
                            Location currentLocation = (Location) task.getResult();
                            mapUtil.animateCamera(mMap, currentLocation, DEFAULT_ZOOM);
                            mapUtil.customMarker(getContext(), mMap, R.drawable.user_4, "", currentLocation);
                        } else {
                            Log.d(TAG, "task.isSuccessful: task.is not Successful and can't find location");
                            Toast.makeText(getActivity(), "Can't find location", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Log.e(TAG, "getDeviceLocation: else");
            }
        } catch (SecurityException e) {
            Log.e(TAG, "getDeviceLocation: " + e.getMessage());
        }
    }

    private void getLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ActivityCompat.checkSelfPermission
                (requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (requireContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            mLocationPermissionGranted = true;
            initMap();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionGranted = true;
                    //initialize our map
                    initMap();
                }
            }
        }
    }
}
