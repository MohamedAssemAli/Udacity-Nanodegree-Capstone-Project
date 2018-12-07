package app.capstone.assem.com.capstone.Fragments.StartFragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

import app.capstone.assem.com.capstone.Activities.MainActivity;
import app.capstone.assem.com.capstone.Activities.StartActivity;
import app.capstone.assem.com.capstone.App.AppConfig;
import app.capstone.assem.com.capstone.Models.UserModel;
import app.capstone.assem.com.capstone.R;
import app.capstone.assem.com.capstone.Utils.Validation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignUpFragment extends Fragment {

    private final String TAG = SignUpFragment.class.getSimpleName();
    // Vars
    private String email, username, password;
    boolean isValid = false;
    // Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    // Views
    @BindView(R.id.fragment_sign_up_email_input_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.fragment_sign_up_email_edit_text)
    TextInputEditText emailTxt;
    @BindView(R.id.fragment_sign_up_username_input_layout)
    TextInputLayout usernameLayout;
    @BindView(R.id.fragment_sign_up_username_edit_text)
    TextInputEditText usernameTxt;
    @BindView(R.id.fragment_sign_up_password_input_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.fragment_sign_up_password_edit_text)
    TextInputEditText passwordTxt;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;

    @OnClick(R.id.fragment_sign_up_button)
    void goToMain() {
        CheckValidation();
        if (isValid)
            signUp();
    }

    @OnClick(R.id.fragment_sign_up_go_sign_in)
    void goToSignIn() {
        manageFragments();
    }


    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        passwordTxt.setTypeface(emailTxt.getTypeface());
        // Firebase
        mAuth = FirebaseAuth.getInstance();
        mRef = FirebaseDatabase.getInstance().getReference();
    }

    private void signUp() {
        final UserModel userModel = new UserModel(email, username, password, "default");
        toggleLayout(false);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        userModel.setUid(uid);
                        mRef.child(AppConfig.USERS).child(uid).setValue(userModel)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Intent intent = new Intent(getActivity(), MainActivity.class);
                                        startActivity(intent);
                                        Objects.requireNonNull(getActivity()).finish();
                                        toggleLayout(true);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.d(TAG, "onFailure: " + e.getMessage());
                                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                                        toggleLayout(true);
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: " + e.getMessage());
                        Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void CheckValidation() {
        boolean emailFlag, usernameFlag, passwordFlag;
        emailFlag = validateInput(emailTxt, emailLayout, Validation.VALIDATE_EMAIL, getString(R.string.empty_email), getString(R.string.invalid_email));
        usernameFlag = validateInput(usernameTxt, usernameLayout, Validation.VALIDATE_NAME, getString(R.string.empty_username), getString(R.string.invalid_username));
        passwordFlag = validateInput(passwordTxt, passwordLayout, Validation.VALIDATE_PASSWORD, getString(R.string.empty_password), getString(R.string.invalid_password));

        if (emailFlag && usernameFlag && passwordFlag) {
            email = getInput(emailTxt);
            username = getInput(usernameTxt);
            password = getInput(passwordTxt);
            isValid = true;
        } else {
            isValid = false;
        }
    }

    private boolean validateInput(TextInputEditText textInputEditText, TextInputLayout textInputLayout, int funNum, String emptyFieldMsg, String errorMsg) {
        String text = getInput(textInputEditText);
        Validation validation = new Validation();
        if (text.isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(emptyFieldMsg);
            return false;
        } else {
            if (validation.validate(funNum, text)) {
                textInputLayout.setErrorEnabled(false);
                return true;
            } else {
                textInputLayout.setErrorEnabled(true);
                textInputEditText.setError(errorMsg);
                return false;
            }
        }
    }

    private String getInput(TextInputEditText textInputEditText) {
        return Objects.requireNonNull(textInputEditText.getText()).toString();
    }

    private void manageFragments() {
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_left, R.anim.exit_to_right, R.anim.enter_from_right, R.anim.exit_to_left);
        StartActivity.fragment = new SignInFragment();
        ft.replace(R.id.start_content, StartActivity.fragment);
        ft.commit();
    }

    private void toggleLayout(boolean flag) {
        if (flag) {
            progressLayout.setVisibility(View.GONE);
            progressBar.hide();
        } else {
            progressLayout.setVisibility(View.VISIBLE);
            progressBar.show();
        }
    }
}
