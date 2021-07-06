package com.barryzea.inventariofirebase.MainModule.view;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barryzea.inventariofirebase.MainModule.MainPresenter;
import com.barryzea.inventariofirebase.MainModule.MainPresenterClass;
import com.barryzea.inventariofirebase.MainModule.view.adapter.ListenerEventsProducts;
import com.barryzea.inventariofirebase.MainModule.view.adapter.ProductAdapter;
import com.barryzea.inventariofirebase.R;
import com.barryzea.inventariofirebase.addModule.view.AddProductFragment;
import com.barryzea.inventariofirebase.common.pojo.Product;
import com.barryzea.inventariofirebase.databinding.ActivityMainBinding;
import com.barryzea.inventariofirebase.detailModule.view.DetailActivity;
import com.barryzea.inventariofirebase.detailModule.view.DetailFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  implements ListenerEventsProducts, MainView {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private RecyclerView rvProduct;
    private ProgressBar pbLoadingProduct;
    private Toolbar toolbar;
    private FloatingActionButton fabProduct;

    private MainPresenter mPresenter;
    private ProductAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(R.string.productTitleMain);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        rvProduct=binding.contentMain.recyclerViewProduct;
        pbLoadingProduct= binding.contentMain.progressBarLoadingProduct;



        setUpAdapter();
        setUpRecyclerView();

        mPresenter= new MainPresenterClass(this);
        mPresenter.onCreate();

       /* binding.fabProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/
        binding.fabProduct.setOnClickListener(fabClick->{
            new AddProductFragment().show(getSupportFragmentManager(),"DialogAdd");
        });

    }

    private void setUpAdapter(){
        mAdapter=new ProductAdapter(new ArrayList<Product>(), this );

    }
    private void setUpRecyclerView(){
        LinearLayoutManager layoutManager = new GridLayoutManager(this, getNumOfColumns());

        rvProduct.setLayoutManager(layoutManager);
        rvProduct.setAdapter(mAdapter);
    }

    private int getNumOfColumns() {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth=displayMetrics.widthPixels /displayMetrics.density;

        return (int) dpWidth / 180;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    /**
     * Interface MainView
     *
     * **/

    @Override
    public void showProgress() {
        pbLoadingProduct.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbLoadingProduct.setVisibility(View.GONE);
    }

    @Override
    public void add(Product product) {
        mAdapter.addItem(product);

    }

    @Override
    public void update(Product product) {

        mAdapter.updateItem(product);

    }

    @Override
    public void remove(Product product) {

        mAdapter.removeItem(product);
    }

    @Override
    public void removeFail() {
        Snackbar.make(findViewById(android.R.id.content), R.string.remove_message_error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void errorMsg(int msg) {
        Snackbar.make(findViewById(android.R.id.content), msg, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Interface ProductEventListener
     *
     * **/
    @Override
    public void onProductClick(Product product) {
        Bundle args= new Bundle();
        args.putString(Product.ID, product.getId());
        args.putString(Product.NAME, product.getName());
        args.putInt(Product.QUANTITY, product.getQuantity());
        args.putString(Product.PHOTO_URL, product.getPhotoUrl());
        args.putDouble(Product.SCORE, product.getScore());
        args.putLong(Product.TOTAL_VOTES, product.getTotalVotes());

        Intent intent= new Intent(this, DetailActivity.class);
        intent.putExtras(args);
        startActivity(intent);
       /* Fragment fragment = new DetailFragment();
        fragment.setArguments(args);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.contentMain, fragment)
                .addToBackStack(null)
                .commit();*/
    }

    @Override
    public void onProductLongClick(Product product) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.titleRemoveDialog)
                .setMessage(R.string.msgRemoveItem)

                .setPositiveButton(R.string.positiveMsg, (dialogInterface, i) -> {
                    mPresenter.remove(product);
                    dialogInterface.dismiss();
                })
                .setNegativeButton(R.string.negativeMsg, (dialog, i)->{
                    dialog.dismiss();
                })
                .show();
    }
}