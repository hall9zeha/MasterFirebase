package com.barryzea.firechat.ProfileModule.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.barryzea.firechat.ProfileModule.ProfilePresenter;
import com.barryzea.firechat.ProfileModule.ProfilePresenterClass;
import com.barryzea.firechat.R;
import com.barryzea.firechat.common.pojo.User;
import com.barryzea.firechat.common.utils.UtilsCommon;
import com.barryzea.firechat.common.utils.UtilsImage;
import com.barryzea.firechat.databinding.ActivityProfileBinding;
import com.barryzea.firechat.databinding.DialogImagePreviewDialogBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;

public class ProfileActivity extends AppCompatActivity implements ProfileView {

    public static final int RC_PHOTO_PICKER = 22;
    private ActivityProfileBinding binding;

    private MenuItem mCurrentMenuItem;
    private ProfilePresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPresenter= new ProfilePresenterClass(this);
        mPresenter.onCreate();
        mPresenter.setUpUser(getIntent().getStringExtra(User.USERNAME),
                getIntent().getStringExtra(User.EMAIL), getIntent().getStringExtra(User.PHOTO_URL));
        configActionBar();
        setClickInImageProfile();

    }

    private void setClickInImageProfile() {
        binding.imgProfile.setOnClickListener(click->{
            mPresenter.checkMode();
        });
        binding.btnEditPhoto.setOnClickListener(click->{
            mPresenter.checkMode();
        });
    }

    private void configActionBar() {
        ActionBar actionBar =getSupportActionBar();
        if(actionBar !=null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setImageProfile(String photoUrl){
        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .centerCrop()
                .placeholder(R.drawable.ic_timer_sand)
                .error(R.drawable.ic_emoticon_sad);

        Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .apply(options)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable  GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        hideProgressImage();
                        binding.imgProfile.setImageDrawable(ContextCompat.getDrawable(ProfileActivity.this,
                                R.drawable.ic_upload));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        binding.imgProfile.setImageBitmap(resource);
                        hideProgressImage();
                        return true;
                    }
                })
                .into(binding.imgProfile);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.saveProfile:
                mCurrentMenuItem=item;

                    if(binding.etUsernameProfile.getText() != null){
                        mPresenter.updateUsername(binding.etUsernameProfile.getText().toString().trim());
                    }
                break;
            case android.R.id.home:
                if(UtilsCommon.hasMaterialDesign()){
                    finishAfterTransition();
                }
                else{
                    finish();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode, data);
    }

    /*
    * Interface ProfileView
    * */
    @Override
    public void enableUIElements() {
        setInputs(true);
    }

    @Override
    public void disableUIElements() {
        setInputs(false);
    }

    private void setInputs(boolean enable) {
        binding.etUsernameProfile.setEnabled(enable);
        binding.btnEditPhoto.setVisibility(enable? View.VISIBLE: View.GONE);
        if(mCurrentMenuItem != null){
            mCurrentMenuItem.setEnabled(enable);
        }
    }

    @Override
    public void showProgress() {
        binding.progressBarUserNameProfile.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBarUserNameProfile.setVisibility(View.GONE);
    }

    @Override
    public void showProgressImage() {
        binding.progressBarImageProfile.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressImage() {
        binding.progressBarImageProfile.setVisibility(View.GONE);
    }

    @Override
    public void showUserData(String username, String email, String photoUrl) {
        setImageProfile(photoUrl);
        binding.etUsernameProfile.setText(username);
        binding.etEmailProfile.setText(email);
    }

    @Override
    public void launchGallery() {
        Intent intent =new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RC_PHOTO_PICKER);
    }

    @Override
    public void openDialogPreview(Intent data) {
        final String localUrl=data.getDataString();
        final ViewGroup nullParent=null;
        DialogImagePreviewDialogBinding bindDialog= DialogImagePreviewDialogBinding.inflate(getLayoutInflater());
        final ImageView imgDialog=bindDialog.imgDialog;
        final TextView tvMessage=bindDialog.tvMessage;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogFragmentTheme)
                .setTitle(R.string.profile_dialog_title)
                .setPositiveButton(R.string.profile_dialog_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.updateImage(Uri.parse(localUrl));
                        UtilsCommon.showSnackBar(binding.contentMainProfile, R.string.profile_message_imageUploading,
                                Snackbar.LENGTH_LONG);
                    }
                })
                .setNegativeButton(R.string.common_label_cancel, null);
        builder.setView(bindDialog.getRoot());
        AlertDialog dialog= builder.create();
        dialog.setOnShowListener(dialogInterface -> {
                int sizeImagePreview=getResources().getDimensionPixelSize(R.dimen.chat_size_img_preview);
                Bitmap bitmap = UtilsImage.reduceBitmap(ProfileActivity.this, binding.contentMainProfile,
                        localUrl,sizeImagePreview, sizeImagePreview);
                if(bitmap != null){
                    imgDialog.setImageBitmap(bitmap);
                }
                tvMessage.setText(R.string.profile_dialog_message);
        });
        dialog.show();
    }

    @Override
    public void menuEditMode() {
        mCurrentMenuItem.setIcon(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ic_check));

    }

    @Override
    public void menuNormalMode() {
        if(mCurrentMenuItem != null){
            mCurrentMenuItem.setEnabled(true);
            mCurrentMenuItem.setIcon(ContextCompat.getDrawable(ProfileActivity.this, R.drawable.ic_pencil));
        }

    }

    @Override
    public void saveUserNameSuccess() {
        UtilsCommon.showSnackBar(binding.contentMainProfile, R.string.profile_message_userUpdated);
    }

    @Override
    public void updateImageSuccess(String photoUrl) {
        setImageProfile(photoUrl);
        UtilsCommon.showSnackBar(binding.contentMainProfile, R.string.profile_message_imageUpdated);
    }

    @Override
    public void setResultOk(String username, String photoUrl) {
        Intent data = new Intent();
        data.putExtra(User.USERNAME, username);
        data.putExtra(User.PHOTO_URL, photoUrl);
        setResult(RESULT_OK,  data);
    }

    @Override
    public void onErrorUpload(int resMsg) {
        UtilsCommon.showSnackBar(binding.contentMainProfile, resMsg);
    }

    @Override
    public void onError(int resMsg) {
        binding.etUsernameProfile.requestFocus();
        binding.etUsernameProfile.setError(getString(resMsg));
    }
}