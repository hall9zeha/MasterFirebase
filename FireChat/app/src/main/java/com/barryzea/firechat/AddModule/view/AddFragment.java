package com.barryzea.firechat.AddModule.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.barryzea.firechat.AddModule.AddPresenter;
import com.barryzea.firechat.AddModule.AddPresenterClass;
import com.barryzea.firechat.R;
import com.barryzea.firechat.common.utils.UtilsCommon;
import com.barryzea.firechat.databinding.FragmentAddBinding;

public class AddFragment extends DialogFragment implements DialogInterface.OnShowListener,AddView {


    private FragmentAddBinding binding;
    private Button positiveButton;
    private AddPresenter mPresenter;
    public AddFragment() {
        // Required empty public constructor
        mPresenter= new AddPresenterClass(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable  Bundle savedInstanceState) {

        AlertDialog.Builder builder= new AlertDialog.Builder(getActivity())
                .setTitle(R.string.addFriend_title)
                .setPositiveButton(R.string.common_label_accept, null)
                .setNegativeButton(R.string.common_label_cancel, null);

        binding=FragmentAddBinding.inflate(getLayoutInflater());
        builder.setView(binding.getRoot());
        AlertDialog dialog= builder.create();
        dialog.setOnShowListener(this);

        return dialog;
    }


    @Override
    public void onShow(DialogInterface dialogInterface) {
        final AlertDialog dialog= (AlertDialog)getDialog();
        if(dialog != null){
            positiveButton= dialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button negativeButton= dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            positiveButton.setOnClickListener(click ->{
                if(UtilsCommon.validateEmail(getActivity(), binding.etEmail)) {
                    mPresenter.addFriend(binding.etEmail.getText().toString().trim());
                }
            });
            negativeButton.setOnClickListener(click->{
                dismiss();
            });
        }
        mPresenter.onShow();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public void enableUIElements() {
        binding.etEmail.setEnabled(true);
        positiveButton.setEnabled(true);
    }

    @Override
    public void disableUIElements() {
        binding.etEmail.setEnabled(false);
        positiveButton.setEnabled(false);
    }

    @Override
    public void showProgress() {
        binding.progressBarAdd.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBarAdd.setVisibility(View.GONE);
    }

    @Override
    public void friendAdded() {
        Toast.makeText(getActivity(), R.string.addFriend_message_request_dispatched, Toast.LENGTH_SHORT).show();
        dismiss();
    }

    @Override
    public void friendNotAdded() {
        binding.etEmail.setError(getString(R.string.addFriend_error_message));
        binding.etEmail.requestFocus();
    }
}