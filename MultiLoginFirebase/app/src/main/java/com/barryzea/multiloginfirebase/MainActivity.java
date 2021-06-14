package com.barryzea.multiloginfirebase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import android.widget.Toast;

import com.barryzea.multiloginfirebase.databinding.ActivityMainBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 20;
    private static final String FIREBASE_EMAIL_PASSWORD="password";
    private static final String FACEBOOK_PROVIDER="facebook.com";
    private static final String GOOGLE_PROVIDER="google.com";
    private static final String PROVIDER_UNKNOWN = "proveedor desconocido";
    private static final int RC_IMAGE_AVATAR = 21;
    private static final String PHOTO_URL_AUTH = "photo_auth";
    private static final String PATH_PROFILE = "profile";

    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener  mAuthStateListener;
    private ActivityMainBinding binding;
    private TextView tvUserName, tvEmail, tvProvider, tvProgress;
    private ProgressBar pbUpload;
    private CircleImageView imgAvatar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityMainBinding.inflate(getLayoutInflater());
        View view=binding.getRoot();
        setContentView(view);


        tvUserName= binding.textViewUserName;
        tvEmail= binding.textViewEmail;
        tvProvider= binding.textViewProvider;
        imgAvatar=binding.imageViewPhotoProfile;
        pbUpload=binding.progressBarUpload;
        tvProgress=binding.textViewProgress;
        mFirebaseAuth = FirebaseAuth.getInstance();



        mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull  FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user !=null){
                    setDataUser(user.getDisplayName(),user.getEmail(),user.getProviderData().size()>0 ? user.getProviderData().get(1).getProviderId() : PROVIDER_UNKNOWN);
                    loadImageAvatar(user.getPhotoUrl());
                }
                else{
                    onsSignOutClean();
                    //añadimos más proveedores
                    AuthUI.IdpConfig facebookIdp= new AuthUI.IdpConfig.FacebookBuilder()
                                .build();
                    AuthUI.IdpConfig googleIdp = new AuthUI.IdpConfig.GoogleBuilder()
                            .build();
                    //para cargar una vista del login y provveedores personalizada con los botones y acciones

                    AuthMethodPickerLayout customLayout= new AuthMethodPickerLayout.Builder(R.layout.custom_view_login)
                            .setEmailButtonId(R.id.buttonEmail)
                            .setGoogleButtonId(R.id.buttonGoogle)
                            .setFacebookButtonId(R.id.buttonFacebook)
                            .setTosAndPrivacyPolicyId(R.id.tvPolice)
                            .build();

                    startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setIsSmartLockEnabled(false)
                    //.setTosUrl("https://termsfeed.com/blog/terms-conditions-mobile-apps/")
                            .setTosAndPrivacyPolicyUrls(
                                    "https://termsfeed.com/blog/terms-conditions-mobile-apps/",
                                    "https://termsfeed.com/blog/terms-conditions-mobile-apps/"
                            )
                            .setTheme(R.style.Theme_AppCompat)
                            .setLogo(R.drawable.img_multi_login)
                            //le pasamos la vista personalizada
                            .setAuthMethodPickerLayout(customLayout)
                    .setAvailableProviders(
                            Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                                    facebookIdp,googleIdp)

                    ).build(),RC_SIGN_IN);
                }
            }
        };
        imgAvatar.setOnClickListener(view1 -> {
            selectImageAvatar();
        });
    }

    private void selectImageAvatar() {
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_IMAGE_AVATAR);
    }

    private void onsSignOutClean() {
        setDataUser("","","");
    }

    private void setDataUser(String userName, String email, String userProvider) {

        int drawableRes;

        tvUserName.setText(userName);

        tvEmail.setText(email);

        switch(userProvider){
            case FIREBASE_EMAIL_PASSWORD:
                drawableRes=R.drawable.ic_mail;
                break;
            case FACEBOOK_PROVIDER:
                drawableRes=R.drawable.ic_facebook;
                break;
            case GOOGLE_PROVIDER:
                drawableRes=R.drawable.ic_google;
                break;
            default:
                drawableRes=R.drawable.ic_block;
                userProvider=PROVIDER_UNKNOWN;
                break;

        }
        tvProvider.setCompoundDrawablesRelativeWithIntrinsicBounds(drawableRes, 0,0,0);
        tvProvider.setText(userProvider);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
           case R.id.actionLogOut:
               AuthUI.getInstance().signOut(this);
               return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode==RESULT_OK){
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(this, "Error revisa porfis", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == RC_IMAGE_AVATAR && resultCode==RESULT_OK){
            if(true){
                uploadImageTask(data.getData());
                return;
            }
            else{

                uploadImageInFirebaseStorage(data.getData());
            }
        }
    }

    private void uploadImageTask(Uri url) {
        pbUpload.setVisibility(View.VISIBLE);
        FirebaseStorage  storage= FirebaseStorage.getInstance();
        final StorageReference  storageRef= storage.getReference().child(PATH_PROFILE).child(PHOTO_URL_AUTH);

        Bitmap bitmap;
        //Uri url=data.getData();

        try {
            bitmap=MediaStore.Images.Media.getBitmap(this.getContentResolver(),url);
            bitmap=resizeBitmap(bitmap, 480);
            ByteArrayOutputStream bos=new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
            byte[] data=bos.toByteArray();
            UploadTask uploadTask =storageRef.putBytes(data);
            uploadTask.addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                    double progress=(100* snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                    pbUpload.setProgress((int)progress);
                    tvProgress.setText(String.format("%s%%", progress));
                    tvProgress.animate().alpha(1).setDuration(200);
                }
            })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull  Task<UploadTask.TaskSnapshot> task) {
                            pbUpload.setVisibility(View.GONE);
                            tvProgress.setText("Listo!");
                            tvProgress.animate().alpha(0).setDuration(2000);
                        }
                    })

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                                    if(user!=null) {
                                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(uri)
                                                .build();
                                        user.updateProfile(request)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull  Task<Void> task) {
                                                        loadImageAvatar(user.getPhotoUrl());
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int size) {
        int width= bitmap.getWidth();
        int height=bitmap.getHeight();

        if(width<=size && height<=size){
            return bitmap;
        }
        float bitmapRatio=(float)width / (float)height;
        if(bitmapRatio>1){
            width=size;
            height= (int)(width/ bitmapRatio);
        }
        else{
            height=size;
            width= (int)(height * bitmapRatio);
        }

        return Bitmap.createScaledBitmap(bitmap,width,height,true);

    }

    private void uploadImageInFirebaseStorage(Uri url) {
        pbUpload.setVisibility(View.VISIBLE);
        FirebaseStorage  storage= FirebaseStorage.getInstance();
        final StorageReference  storageRef= storage.getReference().child(PATH_PROFILE).child(PHOTO_URL_AUTH);
        //Uri url=data.getData();
        if(url!=null){
            storageRef.putFile(url)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                            double progress=(100* snapshot.getBytesTransferred() / snapshot.getTotalByteCount());
                            pbUpload.setProgress((int)progress);
                            tvProgress.setText(String.format("%s%%", progress));
                            tvProgress.animate().alpha(1).setDuration(200);
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull  Task<UploadTask.TaskSnapshot> task) {
                            pbUpload.setVisibility(View.GONE);
                            tvProgress.setText("Listo!");
                            tvProgress.animate().alpha(0).setDuration(2000);
                        }
                    })

                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
                                    if(user!=null) {
                                        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                                .setPhotoUri(uri)
                                                .build();
                                        user.updateProfile(request)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull  Task<Void> task) {
                                                        loadImageAvatar(user.getPhotoUrl());
                                                    }
                                                });
                                    }
                                }
                            });
                        }
                    });
        }
    }

    private void loadImageAvatar(Uri photoUrl) {
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop();

        Glide.with(MainActivity.this)
                .load(photoUrl)
                .apply(options)
                .into(imgAvatar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mAuthStateListener!=null){
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }
}