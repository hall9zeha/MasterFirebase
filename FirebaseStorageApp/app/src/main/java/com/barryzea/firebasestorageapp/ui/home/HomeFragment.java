package com.barryzea.firebasestorageapp.ui.home;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.barryzea.firebasestorageapp.databinding.FragmentHomeBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private Button btnUpload, btnFindImage, btnCamera;
    private ImageView imgPhoto;
    private ImageButton imageButtonDelete;
    private ConstraintLayout container;
    private String mCurrentPhotoPath;
    private Uri mUriSelectedPhoto;
    private DatabaseReference mDatabaseReference;
    private StorageReference mStorageReference;
    private Uri mCameraUri;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        btnUpload=binding.btnUpload;
        imgPhoto=binding.imageViewPhoto;
        imageButtonDelete = binding.btnDelete;
        btnFindImage= binding.btnFindImage;
        btnCamera= binding.btnCamera;
        container=binding.getRoot();
        initFirebase();
        configPhotoProfile();


        View root = binding.getRoot();

        events();

        return root;
    }

    private void initFirebase() {
        mStorageReference= FirebaseStorage.getInstance().getReference();
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        mDatabaseReference=db.getReference().child(PATH_PROFILE).child(PATH_PHOTO_URL);
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ROOT).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        mCameraUri=Uri.fromFile(image);
        mUriSelectedPhoto=mCameraUri;
        return image;
    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.barryzea.firebasestorageapp.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                Dexter.withContext(getActivity()).withPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {

                                startActivityForResult(takePictureIntent, RC_CAMERA);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

                            }
                        }).check();

            }
        }
    }

    private void configPhotoProfile() {
        RequestOptions optionsGlide=new RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        //se puede obtener de dos formas primero desde StorageDatabase
       /* mStorageReference.child(PATH_PROFILE).child(MY_PHOTO).getDownloadUrl()
                .addOnSuccessListener( uri->{
                    Glide.with(getActivity())
                            .load(uri)
                            .apply(optionsGlide)
                            .into(imgPhoto);
                    imageButtonDelete.setVisibility(View.VISIBLE);
                })
                .addOnFailureListener(error->{
                    Snackbar.make(binding.getRoot(), "Error al traer la url", Snackbar.LENGTH_LONG).show();
                    Log.e("Error", error.toString());
                    imageButtonDelete.setVisibility(View.GONE);
                });*/
        //y también la url almacenada en firebase realtime database
        mDatabaseReference
        .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                Glide.with(getActivity())
                        .load(snapshot.getValue())
                        .apply(optionsGlide)
                        .into(imgPhoto);
                imageButtonDelete.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {
                Snackbar.make(binding.getRoot(), "Error al traer la url", Snackbar.LENGTH_LONG).show();
                Log.e("Error", error.toString());
                imageButtonDelete.setVisibility(View.GONE);
            }
        });

    }

    private void events(){
        btnFindImage.setOnClickListener(view->{
            fromGallery();
        });
        btnUpload.setOnClickListener(view ->{
            StorageReference profileReference = mStorageReference.child(PATH_PROFILE);
            StorageReference photoReference= profileReference.child(MY_PHOTO);
            photoReference.putFile(mUriSelectedPhoto).addOnSuccessListener(taskSnapshot -> {
                Snackbar.make(binding.getRoot(), "Foto subida correctamente", Snackbar.LENGTH_LONG).show();
                Task<Uri> url=taskSnapshot.getStorage().getDownloadUrl();
                url.addOnSuccessListener(uri -> {
                    Uri downloadUrl=uri;
                    savePhotoUrl(downloadUrl);
                });

                 imageButtonDelete.setVisibility(View.VISIBLE);
            })
                    .addOnFailureListener(e -> {
                        Snackbar.make(binding.getRoot(), "Error al subir la foto", Snackbar.LENGTH_LONG).show();
                        Log.e("Error", e.toString());
                    });
        });
        imageButtonDelete.setOnClickListener(view -> {
            mStorageReference.child(PATH_PROFILE).child(MY_PHOTO).delete()
                    .addOnSuccessListener(unused -> {
                        mDatabaseReference.removeValue();
                        imgPhoto.setImageBitmap(null);
                        imageButtonDelete.setVisibility(View.GONE);
                        Snackbar.make(binding.getRoot(), "Imagén eliminada correctamente", Snackbar.LENGTH_LONG).show();
                    })
                    .addOnFailureListener(unused ->{
                        Snackbar.make(binding.getRoot(), "Error la imágen no se eliminó", Snackbar.LENGTH_LONG).show();
                    });
        });
        btnCamera.setOnClickListener(view->{
            //fromCamera();
            dispatchTakePictureIntent();
        });

    }

    private void savePhotoUrl(Uri downloadUrl) {
        mDatabaseReference.setValue(downloadUrl.toString());
    }

    private void fromCamera(){
        Intent intent= new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        startActivityForResult(intent, RC_CAMERA);
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

                switch(requestCode){
                    case RC_CAMERA:
                       // Bundle extras=data.getExtras();
                       // Bitmap bitmapCamera = (Bitmap)extras.get("data");


                        //imgPhoto.setImageBitmap(bitmapCamera);
                        imgPhoto.setImageURI(mCameraUri);
                        imageButtonDelete.setVisibility(View.GONE);

                        break;
                    case  RC_GALLERY:
                        mUriSelectedPhoto=data.getData();
                        try {
                            Bitmap bitmap=MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), mUriSelectedPhoto);
                            imgPhoto.setImageBitmap(bitmap);
                            imageButtonDelete.setVisibility(View.GONE);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    default:
                        throw new IllegalStateException("Unexpected value: " + requestCode);
                }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}