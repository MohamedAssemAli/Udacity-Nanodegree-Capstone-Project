package app.capstone.assem.com.capstone.Activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import app.capstone.assem.com.capstone.Fragments.StartFragments.SignInFragment;
import app.capstone.assem.com.capstone.R;

public class StartActivity extends AppCompatActivity {

    public static Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        manageFragments();
    }

    private void manageFragments() {
        // get fragment manager
        FragmentManager fm = getSupportFragmentManager();
        // add
        FragmentTransaction ft = fm.beginTransaction();
        if (fragment == null)
            fragment = new SignInFragment();
        ft.replace(R.id.start_content, fragment);
        ft.setCustomAnimations(R.anim.fade_out, R.anim.fade_in);
        ft.commit();
    }
}
