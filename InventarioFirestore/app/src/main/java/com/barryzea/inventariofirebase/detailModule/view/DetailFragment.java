package com.barryzea.inventariofirebase.detailModule.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.barryzea.inventariofirebase.R;
import com.barryzea.inventariofirebase.common.Utils.CommonUtils;
import com.barryzea.inventariofirebase.common.pojo.Product;
import com.barryzea.inventariofirebase.databinding.FragmentDetailBinding;
import com.barryzea.inventariofirebase.detailModule.DetailProductPresenter;
import com.barryzea.inventariofirebase.detailModule.DetailProductPresenterClass;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;


public class DetailFragment extends Fragment  implements  DetailProductView{


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FragmentDetailBinding binding;

    private String mParam1;
    private String mParam2;
    private TextInputEditText ietName, ietQuantity, ietUrl;
    private ImageView imgProduct;
    private ProgressBar pbLoadingDetail;
    private Button btnSave;
    private Product mProduct;
    private DetailProductPresenter mPresenter;

    public DetailFragment() {
        mPresenter= new DetailProductPresenterClass(this);
    }


    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDetailBinding.inflate(getLayoutInflater());
        View rootView=binding.getRoot();
        if (getArguments()!=null) {
            configProduct(getArguments());
            configViews();
            configValues();
            configRatingBar();

        }
        mPresenter.onCreate();
        return rootView;
    }

    private void configProduct(Bundle arg) {
        mProduct= new Product();
        mProduct.setId(arg.getString(Product.ID));
        mProduct.setName(arg.getString(Product.NAME));
        mProduct.setQuantity(arg.getInt(Product.QUANTITY));
        mProduct.setPhotoUrl(arg.getString(Product.PHOTO_URL));
        mProduct.setScore(arg.getDouble(Product.SCORE));
        mProduct.setTotalVotes(arg.getLong(Product.TOTAL_VOTES));

    }

    private void configValues() {
        ietName.setText(mProduct.getName());
        ietQuantity.setText(String.valueOf(mProduct.getQuantity()));
        ietUrl.setText(mProduct.getPhotoUrl());

        configImage(mProduct.getPhotoUrl());
    }
    private void configRatingBar(){
        binding.rbScoreAvg.setRating((float)mProduct.getScore());
        binding.rbMyScore.setRating((float)mProduct.getScore());
    }
    private void configImage(String photoUrl) {
        RequestOptions options= new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();
        Glide.with(getActivity())
                .load(photoUrl)
                .apply(options)
                .into(imgProduct);
    }

    private void configViews() {
        ietName= binding.ietNameDetail;
        ietQuantity= binding.ietQuantityDetail;
        ietUrl= binding.editTextPhotoUri;
        imgProduct= binding.imageViewDetail;
        btnSave= binding.btnSaveDetail;
        pbLoadingDetail= binding.progressBarFragmentDialog;
        ietUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String url=ietUrl.getText().toString().trim();
                if(url.isEmpty()){
                    imgProduct.setImageDrawable(null);
                }
                else{
                    configImage(url);
                }
            }
        });
        btnSave.setOnClickListener(click->{
            if (CommonUtils.validateProducts(getActivity(), ietName,  ietUrl,ietQuantity)) {
                mProduct.setName(ietName.getText().toString().trim());
                mProduct.setQuantity(Integer.valueOf(ietQuantity.getText().toString().trim()));
                mProduct.setPhotoUrl(ietUrl.getText().toString().trim());
                mProduct.setScore(binding.rbMyScore.getRating());
                mPresenter.updateProduct(mProduct);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void showProgress() {
        pbLoadingDetail.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        pbLoadingDetail.setVisibility(View.GONE);
    }

    @Override
    public void enableUIDetailProduct() {
        ietName.setEnabled(true);
        ietQuantity.setEnabled(true);
        ietUrl.setEnabled(true);
        btnSave.setEnabled(true);
    }

    @Override
    public void disableUIDetailProduct() {
        ietName.setEnabled(false);
        ietQuantity.setEnabled(false);
        ietUrl.setEnabled(false);
        btnSave.setEnabled(false);
    }

    @Override
    public void updateSuccess() {
        Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.product_added_success, Snackbar.LENGTH_LONG)
                .setAction(R.string.back_list, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (getActivity() !=null) {
                            getActivity().onBackPressed();
                        }
                    }
                }).show();
    }

    @Override
    public void updateError() {
        Snackbar.make(getActivity().findViewById(android.R.id.content),R.string.error_product_add, Snackbar.LENGTH_LONG).show();
    }
}