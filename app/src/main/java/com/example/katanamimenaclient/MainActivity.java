package com.example.katanamimenaclient;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Load the FragmentSearch when the activity starts
        if (savedInstanceState == null) {
            loadFragment(new CriteriaFragment());
        }
    }

    private void loadFragment(Fragment fragment) {
        // Create a FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        // Create a FragmentTransaction to begin the transaction and replace the Fragment
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        // Replace the FrameLayout with the new Fragment
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit(); // Save the changes
    }
}
