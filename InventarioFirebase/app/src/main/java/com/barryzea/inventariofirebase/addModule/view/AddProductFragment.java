package com.barryzea.inventariofirebase.addModule.view;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.barryzea.inventariofirebase.R;
import com.barryzea.inventariofirebase.addModule.AddProductPresenter;
import com.barryzea.inventariofirebase.addModule.AddProductPresenterClass;
import com.barryzea.inventariofirebase.common.Utils.CommonUtils;
import com.barryzea.inventariofirebase.common.pojo.Product;
import com.barryzea.inventariofirebase.databinding.FragmentProductAddBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.snackbar.Snackbar;


public class AddProductFragment extends DialogFragment implements DialogInterface.OnShowListener, AddProductView {

    private AddProductPresenter mPresenter;
    private FragmentProductAddBinding binding;


    public AddProductFragment() {
        mPresenter= new AddProductPresenterClass(this);
    }


    public static AddProductFragment newInstance(String param1, String param2) {
        AddProductFragment fragment = new AddProductFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable  Bundle savedInstanceState) {
        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity())
                .setTitle(R.string.add_product_title)
                .setPositiveButton(R.string.add_ok, null)
                .setNegativeButton(R.string.add_cancel, null);


        binding=FragmentProductAddBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot());
        configFocus();
        configEditText();
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(this);
        return dialog;

    }

    private void configEditText() {
        binding.editTextPhotoUri.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                final String photoUrl=binding.editTextPhotoUri.getText().toString().trim();
                if(photoUrl.isEmpty()){
                    binding.imageViewPhoto.setImageDrawable(null);

                }
                else{
                    if(getActivity() != null){
                        RequestOptions options= new RequestOptions()
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL);

                        Glide.with(getActivity())
                                .load(photoUrl)
                                .apply(options)
                                .into(binding.imageViewPhoto);


                    }
                }
            }
        });


    }

    private void configFocus() {
        binding.inputEditTextName.requestFocus();
    }


    /**
     * interfaz DialogInterface
     * **/
    @Override
    public void onShow(DialogInterface dialogInterface) {
        final AlertDialog dialog= (AlertDialog)getDialog();
        if(dialog !=null){
            Button positiveButton=dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton= dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            positiveButton.setOnClickListener(click-> {
                if (CommonUtils.validateProducts(getActivity(), binding.inputEditTextName, binding.editTextPhotoUri, binding.inputEditTextQuantity)) {
                    Product prod= new Product();
                    prod.setName(binding.inputEditTextName.getText().toString().trim());
                    prod.setQuantity(Integer.valueOf(binding.inputEditTextQuantity.getText().toString().trim()));
                    prod.setPhotoUrl(binding.editTextPhotoUri.getText().toString().trim());
                    mPresenter.addProduct(prod);
                }
            });
            negativeButton.setOnClickListener(click ->{
                dismiss();
            });
        }
        mPresenter.onShow();
    }

    /**
     * Interface AddProductView
     *
     * **/
    @Override
    public void enableUIElement() {
        binding.inputEditTextName.setEnabled(true);
        binding.inputEditTextQuantity.setEnabled(true);
        binding.editTextPhotoUri.setEnabled(true);
    }

    @Override
    public void disableUIElement() {
        binding.inputEditTextName.setEnabled(false);
        binding.inputEditTextQuantity.setEnabled(false);
        binding.editTextPhotoUri.setEnabled(false);
    }

    @Override
    public void showProgress() {
        binding.progressBarFragmentDialog.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBarFragmentDialog.setVisibility(View.GONE);
    }

    @Override
    public void productAdded() {
        Toast.makeText(getActivity(), "Producto guardado correctamente", Toast.LENGTH_LONG).show();
        dismiss();
    }

    @Override
    public void showError(int resMsg) {
        Snackbar.make(binding.getRoot(), resMsg, Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        }).show();
    }

    @Override
    public void maxValueError(int resMsg) {
        binding.inputEditTextQuantity.setError(getString(resMsg));
        binding.inputEditTextQuantity.requestFocus();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }
}