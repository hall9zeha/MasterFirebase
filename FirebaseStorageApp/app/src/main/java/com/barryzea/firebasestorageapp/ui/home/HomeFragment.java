package com.barryzea.firebasestorageapp.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.barryzea.firebasestorageapp.R;
import com.barryzea.firebasestorageapp.databinding.FragmentHomeBinding;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private static final int RC_GALLERY= 21;
    private static final int RC_CAMERA=22;

    private static final int RP_CAMERA=121;
    private static final int RP_STORAGE=122;

    private static final String IMAGE_DIRECTORY="/MyPhotoApp";
    private static final String MY_PHOTO="my_photo";
    private static final String PATH_PROFILE="profile";
    private static final String PATH_PHOTO_URL="photo_url";


    private HomeViewModel homeViewModel;
    private FragmentHomeBinding binding;
    private Button btnUpload;
    private ImageView imgPhoto;
    private ImageButton imbClose;

    private String mCurrentPhotoPhat;
    private Uri mUriSelectedPhoto;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        btnUpload=binding.btnUpload;
        imgPhoto=binding.imageViewPhoto;
        imbClose= binding.btnDelete;

        fromGallery();
        View root = binding.getRoot();


        return root;
    }

    private void fromGallery() {


        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_GALLERY);
      /*registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
              new ActivityResultCallback<ActivityResult>() {
                  @Override
                  public void onActivityResult(ActivityResult result) {
                      if(result.getResultCode()==RESULT_OK)
                      {

                      }
                      Toast.makeText(getActivity(), result.getData().toString(), Toast.LENGTH_SHORT).show();
                  }
              }).launch( intent);*/

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK)
        {
            if(requestCode==RC_GALLERY){
                switch(requestCode){
                    case RC_CAMERA:
                        break;
                    case  RC_GALLERY:
                        mUriSelectedPhoto=data.getData();
                        try {
                            Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), mUriSelectedPhoto);
                            imgPhoto.setImageBitmap(bitmap);
                            imbClose.setVisibility(View.GONE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}