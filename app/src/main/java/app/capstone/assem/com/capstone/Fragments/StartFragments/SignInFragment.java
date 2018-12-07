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
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import app.capstone.assem.com.capstone.Activities.MainActivity;
import app.capstone.assem.com.capstone.Activities.StartActivity;
import app.capstone.assem.com.capstone.App.AppConfig;
import app.capstone.assem.com.capstone.R;
import app.capstone.assem.com.capstone.Utils.Validation;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SignInFragment extends Fragment {

    // Vars
    String email, password;
    boolean isValid = false;
    AppConfig appConfig;
    // Firebase
    private FirebaseAuth mAuth;
    // Views
    @BindView(R.id.fragment_sign_in_email_input_layout)
    TextInputLayout emailLayout;
    @BindView(R.id.fragment_sign_in_email_edit_text)
    TextInputEditText emailTxt;
    @BindView(R.id.fragment_sign_in_password_input_layout)
    TextInputLayout passwordLayout;
    @BindView(R.id.fragment_sign_in_password_edit_text)
    TextInputEditText passwordTxt;
    @BindView(R.id.fragment_sign_in_button)
    Button signInBtn;
    @BindView(R.id.progress_bar)
    ContentLoadingProgressBar progressBar;
    @BindView(R.id.progress_layout)
    RelativeLayout progressLayout;

    @OnClick(R.id.fragment_sign_in_button)
    void goToMain() {
        CheckValidation();
        if (isValid)
            signIn();
    }

    @OnClick(R.id.fragment_sign_in_go_sign_up)
    void goToSignUp() {
        manageFragments();
    }


    public SignInFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        ButterKnife.bind(this, view);
        init();
        return view;
    }

    private void init() {
        passwordTxt.setTypeface(emailTxt.getTypeface());
        appConfig = new AppConfig();
        // Firebase
        mAuth = FirebaseAuth.getInstance();
    }

    private void signIn() {
        toggleLayout(false);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    Objects.requireNonNull(getActivity()).finish();
                    toggleLayout(true);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                toggleLayout(false);
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_LONG).show();
                Log.e("login", e.toString());
                toggleLayout(true);
            }
        });
    }

    private void CheckValidation() {
        boolean emailFlag, passwordFlag;
        emailFlag = validateInput(emailTxt, emailLayout, Validation.VALIDATE_EMAIL, getString(R.string.empty_email), getString(R.string.invalid_email));
        passwordFlag = validateInput(passwordTxt, passwordLayout, Validation.VALIDATE_PASSWORD, getString(R.string.empty_password), getString(R.string.invalid_password));

        if (emailFlag && passwordFlag) {
            email = getInput(emailTxt);
            password = getInput(passwordTxt);
            isValid = true;
        } else {
            isValid = false;
        }
    }

    private boolean validateInput(TextInputEditText textInputEditText, TextInputLayout textInputLayout, int funNum, String emptyFieldMsg, String errorMsg) {
        String text = getInput(textInputEditText);
        if (text.isEmpty()) {
            textInputLayout.setErrorEnabled(true);
            textInputLayout.setError(emptyFieldMsg);
            return false;
        } else {
            textInputLayout.setErrorEnabled(false);
            return true;
        }
    }

    private String getInput(TextInputEditText textInputEditText) {
        return Objects.requireNonNull(textInputEditText.getText()).toString();
    }

    private void manageFragments() {
        FragmentTransaction ft = Objects.requireNonNull(getActivity()).getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        StartActivity.fragment = new SignUpFragment();
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
