package app.capstone.assem.com.capstone.Fragments.MainFragments;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import app.capstone.assem.com.capstone.Activities.StartActivity;
import app.capstone.assem.com.capstone.Adapters.PlacesBookmarkAdapter;
import app.capstone.assem.com.capstone.App.AppConfig;
import app.capstone.assem.com.capstone.Models.PlaceBookmarkModel;
import app.capstone.assem.com.capstone.R;
import app.capstone.assem.com.capstone.Utils.Imageutility;
import app.capstone.assem.com.capstone.Utils.ViewsUtils;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.app.Activity.RESULT_OK;

public class BookmarksFragment extends Fragment {
    private static final String TAG = BookmarksFragment.class.getSimpleName();
    // Vars
    private boolean mCamPermissionGranted = false;
    private static final int CAM_PERMISSION_REQUEST_CODE = 1234;
    private ArrayList<PlaceBookmarkModel> placeBookmarkModelArrayList;
    private PlacesBookmarkAdapter placesBookmarkAdapter;
    private String uid;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private StorageReference mStorageReference;
    // Views
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.fragment_bookmarks_profile_pic)
    ImageView profilePicImg;
    @BindView(R.id.fragment_bookmarks_add_profile_pic)
    ImageView addProfilePicImage;
    @BindView(R.id.fragment_bookmarks_profile_pic_progress_bar)
    ContentLoadingProgressBar profilePicProgressBar;
    @BindView(R.id.fragment_bookmarks_username)
    TextView usernameTxt;
    @BindView(R.id.fragment_bookmarks_email)
    TextView emailTxt;
    @BindView(R.id.fragment_bookmarks_recycler_view)
    RecyclerView bookmarksRecyclerView;
    @BindView(R.id.fragment_bookmarks_empty_recycler)
    ImageView emptyRecyclerPlaceHolder;

    // OnClicks
    @OnClick(R.id.fragment_bookmarks_add_profile_pic)
    public void addPp() {
        getCamPermission();
    }

    @OnClick(R.id.fragment_bookmarks_logout)
    void goToStart() {
        logoutUser();
    }

    public BookmarksFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_bookmarks, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        toggleLayout(false);
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
        mStorageReference = FirebaseStorage.getInstance().getReference().child(AppConfig.USERS_PROFILE_IMAGES);
        placeBookmarkModelArrayList = new ArrayList<>();
        bookmarksRecyclerView.setNestedScrollingEnabled(false);
        new ViewsUtils().setupLinearVerticalRecView(getContext(), bookmarksRecyclerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        getUserCredentials();
    }

    private void getUserCredentials() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            uid = currentUser.getUid();
            placesBookmarkAdapter = new PlacesBookmarkAdapter(getContext(), placeBookmarkModelArrayList, uid);
            getUserdata(uid);
            getBookmarks(uid);
        }
    }

    private void getUserdata(String uid) {
        mRef.child(AppConfig.USERS).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    usernameTxt.setText(Objects.requireNonNull(dataSnapshot.child(AppConfig.USERS_USERNAME).getValue()).toString());
                    emailTxt.setText(Objects.requireNonNull(dataSnapshot.child(AppConfig.USERS_EMAIL).getValue()).toString());
                    Imageutility.circularImage(requireContext(),
                            profilePicImg,
                            Objects.requireNonNull(dataSnapshot.child(AppConfig.USERS_IMG).getValue()).toString(),
                            R.drawable.profile_placeholder,
                            R.drawable.profile_placeholder);
                } else {
                    Toast.makeText(requireContext(), R.string.error_getting_data, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e(TAG, "load user data:onCancelled", databaseError.toException());
            }
        });
    }

    private void getBookmarks(String uid) {
        mRef.child(AppConfig.BOOKMARKS).child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    placeBookmarkModelArrayList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        PlaceBookmarkModel placeBookmarkModel = snapshot.getValue(PlaceBookmarkModel.class);
                        placeBookmarkModelArrayList.add(placeBookmarkModel);
                        placesBookmarkAdapter.notifyDataSetChanged();
                    }
                    Collections.reverse(placeBookmarkModelArrayList);
                }
                toggleLayout(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, databaseError.getMessage());
                Log.d(TAG, databaseError.getDetails());
                Toast.makeText(getContext(), R.string.error_getting_data, Toast.LENGTH_LONG).show();
            }
        });
        bookmarksRecyclerView.setAdapter(placesBookmarkAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                final Uri resultUri = result.getUri();
                if (resultUri != null) {
                    toggleProfilePicProgress(true);
                    final StorageReference imgStorageReference = mStorageReference.child(uid + ".jpg");
                    imgStorageReference.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imgStorageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //Do what you want with the url
                                    mRef.child(AppConfig.USERS).child(uid).child(AppConfig.USERS_IMG).setValue(uri.toString());
                                    Imageutility.circularImage(requireContext(), profilePicImg, uri, R.drawable.profile_placeholder, R.drawable.profile_placeholder);
                                    Toast.makeText(requireContext(), getString(R.string.upload_img_success), Toast.LENGTH_LONG).show();
                                    toggleProfilePicProgress(false);
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(requireContext(), getString(R.string.error_upload_img), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(requireContext(), error.toString(), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getCamPermission() {
        String[] permissions = {Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if (ActivityCompat.checkSelfPermission
                (requireContext(), android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission
                (requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(permissions, CAM_PERMISSION_REQUEST_CODE);
        } else {
            mCamPermissionGranted = true;
            startCrop();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mCamPermissionGranted = false;
        switch (requestCode) {
            case CAM_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int grantResult : grantResults) {
                        if (grantResult != PackageManager.PERMISSION_GRANTED) {
                            mCamPermissionGranted = false;
                            return;
                        }
                    }
                    mCamPermissionGranted = true;
                    startCrop();
                }
            }
        }
    }

    private void startCrop() {
        CropImage.activity()
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(Objects.requireNonNull(getActivity()));
    }

    private void toggleLayout(boolean flag) {
        if (flag) {
            bookmarksRecyclerView.setVisibility(View.VISIBLE);
            progressLayout.setVisibility(View.GONE);
            progressBar.hide();
            if (placeBookmarkModelArrayList.isEmpty()) {
                emptyRecyclerPlaceHolder.setVisibility(View.VISIBLE);
                bookmarksRecyclerView.setVisibility(View.GONE);
            } else {
                emptyRecyclerPlaceHolder.setVisibility(View.GONE);
                bookmarksRecyclerView.setVisibility(View.VISIBLE);
            }
        } else {
            progressLayout.setVisibility(View.VISIBLE);
            progressBar.show();
            bookmarksRecyclerView.setVisibility(View.GONE);
        }
    }

    private void toggleProfilePicProgress(boolean flag) {
        if (flag) {
            profilePicProgressBar.setVisibility(View.VISIBLE);
            profilePicProgressBar.show();
        } else {
            profilePicProgressBar.setVisibility(View.GONE);
            profilePicProgressBar.hide();
        }
    }

    private void sendToStart() {
        Intent intent = new Intent(requireContext(), StartActivity.class);
        StartActivity.fragment = null;
        startActivity(intent);
        getActivity().finish();
    }

    private void logoutUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut();
            sendToStart();
        }
    }
}
