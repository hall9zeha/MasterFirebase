package com.barryzea.firechat.ChatModule.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.barryzea.firechat.R;
import com.barryzea.firechat.databinding.FragmentImageZoomBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

public class ImageZoomFragment extends DialogFragment implements DialogInterface.OnShowListener {
    private FragmentImageZoomBinding binding;
    public ImageZoomFragment() {
    }



    @NonNull

    @Override
    public Dialog onCreateDialog(@Nullable  Bundle savedInstanceState) {

        super.onCreateDialog(savedInstanceState);
        binding = FragmentImageZoomBinding.inflate(LayoutInflater.from(getActivity()), null, false);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.DialogFragmentTheme_FullScreen)
                .setTitle(R.string.app_name)
                .setPositiveButton(R.string.common_label_ok, null);
        builder.setView(binding.getRoot());
        AlertDialog dialog =builder.create();
        dialog.show();
        if (getActivity() !=null) {
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_timer_sand_160);

            Glide.with(getActivity())

                    .load(((OnImageZoom)getActivity()).getMessageSelected().getPhotoUrl())
                    .apply(options)
                    .into(binding.pvZoom);
        }
        return dialog;


    }
    @Override
    public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        if(window !=null){
            window.setLayout(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
            window.setGravity(Gravity.CENTER);
        }

        binding.contentMainZoom.setGravity(Gravity.CENTER);
    }
    @Override
    public void onShow(DialogInterface dialogInterface) {
       /* if(getActivity() !=null){
            RequestOptions options = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.drawable.ic_timer_sand_160);

            Glide.with(getActivity())

                    .load(((OnImageZoom)getActivity()).getMessageSelected().getPhotoUrl())
                    .apply(options)
                    .into(binding.pvZoom);
        }*/
    }
}
