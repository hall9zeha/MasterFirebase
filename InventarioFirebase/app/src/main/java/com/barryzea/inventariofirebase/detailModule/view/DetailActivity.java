package com.barryzea.inventariofirebase.detailModule.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.barryzea.inventariofirebase.R;
import com.barryzea.inventariofirebase.common.pojo.Product;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Bundle bundle= getIntent().getExtras();

        Fragment fragment = new DetailFragment();
        fragment.setArguments(bundle);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.contentDetail, fragment)
                //.addToBackStack(null)
                .commit();
    }
}