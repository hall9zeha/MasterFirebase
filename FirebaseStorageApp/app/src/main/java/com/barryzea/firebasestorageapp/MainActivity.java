package com.barryzea.firebasestorageapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.barryzea.firebasestorageapp.ui.dashboard.DashboardFragment;
import com.barryzea.firebasestorageapp.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.barryzea.firebasestorageapp.databinding.ActivityMainBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;

import org.jetbrains.annotations.NotNull;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseStorage storageReference;
    private DatabaseReference dbReference;
    private BottomNavigationView btnNav;
    private FrameLayout navHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        btnNav=binding.navView;
        navHost=binding.navHostFragmentActivityMain;
        setContentView(binding.getRoot());

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.nav_host_fragment_activity_main, new HomeFragment())
                .commit();
        /*BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);*/

        btnNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
            switch (item.getItemId()) {
                    case R.id.navigation_home:
                        setFragment(new HomeFragment());
                        break;

                    case R.id.navigation_dashboard:

                        setFragment(new DashboardFragment());
                        break;

                }
                return false;


            }
        });

    }
    private void setFragment(Fragment fragment){
        getSupportFragmentManager()

                .beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, fragment)
                .commit();
    }



}