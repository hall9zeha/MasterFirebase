package com.barryzea.firechat.ChatModule.view;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.barryzea.firechat.ChatModule.ChatPresenter;
import com.barryzea.firechat.ChatModule.ChatPresenterClass;
import com.barryzea.firechat.ChatModule.view.adapters.ChatAdapter;
import com.barryzea.firechat.ChatModule.view.adapters.OnItemClickListener;
import com.barryzea.firechat.MainModule.view.MainActivity;
import com.barryzea.firechat.ProfileModule.view.ProfileActivity;
import com.barryzea.firechat.R;
import com.barryzea.firechat.common.Constants;
import com.barryzea.firechat.common.pojo.Message;
import com.barryzea.firechat.common.pojo.User;
import com.barryzea.firechat.common.utils.UtilsCommon;
import com.barryzea.firechat.common.utils.UtilsImage;
import com.barryzea.firechat.common.utils.UtilsNetwork;
import com.barryzea.firechat.databinding.ActivityChatBinding;
import com.barryzea.firechat.databinding.DialogImagePreviewDialogBinding;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements OnItemClickListener, ChatView , OnImageZoom{

    private AppBarConfiguration appBarConfiguration;
    private ActivityChatBinding binding;
    private ChatAdapter mAdapter;
    private ChatPresenter mPresenter;
    private Message messageSelected;

    private CircleImageView imgPhoto;
    private TextView tvName, tvStatus;
    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private CoordinatorLayout contentMain;
    private ImageButton btnSendMessage, btnGallery;
    private EditText etMessage;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        mPresenter = new ChatPresenterClass(this);
        mPresenter.onCreate();
       setUpsViews();
       configAdapter();
       configRecyclerView();
       configToolbar(getIntent());







    }

    private void setUpsViews() {
        imgPhoto=binding.contentToolbarChat.imageViewPhoto;
        tvName=binding.contentToolbarChat.tvName;
        tvStatus=binding.contentToolbarChat.tvStatus;
        toolbar=binding.toolbar;
        recyclerView=binding.contentChat.recyclerViewItemChat;
        progressBar=binding.contentChat.progressBarChat;
        contentMain= binding.contentMain;
        btnSendMessage=binding.contentChat.btnSendMessage;
        etMessage=binding.contentChat.etMessage;
        btnGallery=binding.contentChat.btnGallery;
        btnSendMessage.setOnClickListener(click->{
            if(UtilsCommon.validateMessage(etMessage)) {
                mPresenter.sendMessage(etMessage.getText().toString().trim());
                etMessage.setText("");
            }
        });
        btnGallery.setOnClickListener(click->{
            checkPermissionToApp(Manifest.permission.READ_EXTERNAL_STORAGE, Constants.RC_PERMISSION_STORAGE);
        });
    }
    private void configAdapter() {
        mAdapter= new ChatAdapter(new ArrayList<Message>(), this);
    }

    private void configRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(mAdapter);
    }

    private void configToolbar(Intent data) {
        String uid=data.getStringExtra(User.UID);
        String email=data.getStringExtra(User.EMAIL);
        mPresenter.setUpFriend(uid,email);
        String photoUrl=data.getStringExtra(User.PHOTO_URL);
        tvName.setText(data.getStringExtra(User.USERNAME));
        tvStatus.setVisibility(View.VISIBLE);

        RequestOptions options = new RequestOptions()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .placeholder(R.drawable.ic_emoticon_happy)
                .centerCrop();
        Glide.with(this)
                .asBitmap()
                .load(photoUrl)
                .apply(options)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable  GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        imgPhoto.setImageDrawable(ContextCompat.getDrawable(ChatActivity.this,
                                R.drawable.ic_emoticon_sad));
                        return true;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        imgPhoto.setImageBitmap(resource);
                        return true;
                    }
                })
                .into(imgPhoto);
        setSupportActionBar(binding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar !=null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }


    }

    @Override
    protected void onResume() {
        super.onResume();

        if(UtilsNetwork.isOnline(this)){
            mPresenter.onResume();
        }
        else
        {
            UtilsCommon.showSnackBar(contentMain, R.string.common_message_noInternet, Snackbar.LENGTH_LONG);
        }
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        if(UtilsCommon.hasMaterialDesign()){
            finishAfterTransition();
        }
        else{
            finish();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode,resultCode,data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull  String[] permissions, @NonNull  int[] grantResults) {
        if(grantResults.length>0 &&grantResults[0]==PackageManager.PERMISSION_GRANTED){
            switch(requestCode){
                case Constants.RC_PERMISSION_STORAGE:
                    fromGallery();
                    break;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void fromGallery(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, Constants.RC_PERMISSION_STORAGE);

    }
    private void  checkPermissionToApp(String permissionStr, int requestPermission){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, permissionStr)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, new String[]{permissionStr},requestPermission);
                return;
            }
        }
        switch(requestPermission){
            case Constants.RC_PERMISSION_STORAGE:
                fromGallery();
                break;
        }
    }
    /*
    * onItemClick
    * */

    @Override
    public void onImageLoad() {
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void onClickImage(Message message) {
        new ImageZoomFragment().show(getSupportFragmentManager(), getString(R.string.app_name));
        messageSelected=message;
    }

    @Override
    public Message getMessageSelected() {
        return this.messageSelected;
    }
    /*
    * ChatView
    * */

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStatusUser(boolean connected, long lastConnection) {

        if(connected){
            tvStatus.setText(R.string.chat_status_connected);
        }
        else{
            tvStatus.setText(getString(R.string.chat_status_last_connection,
                    new SimpleDateFormat("dd-MM-yyyy - HH:mm", Locale.ROOT).format(new Date(lastConnection))));

        }
    }

    @Override
    public void onError(int resMsg) {
        UtilsCommon.showSnackBar(contentMain, resMsg);
    }

    @Override
    public void onMessageReceived(Message msg) {
        mAdapter.add(msg);
        recyclerView.scrollToPosition(mAdapter.getItemCount() - 1);
    }

    @Override
    public void openDialogPreview(Intent data) {
        final String localUrl=data.getDataString();
        final ViewGroup nullParent=null;
        DialogImagePreviewDialogBinding bindDialog= DialogImagePreviewDialogBinding.inflate(getLayoutInflater());
        final ImageView imgDialog=bindDialog.imgDialog;
        final TextView tvMessage=bindDialog.tvMessage;

        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogFragmentTheme)
                .setTitle(R.string.chat_dialog_sendImage_title)
                .setPositiveButton(R.string.chat_dialog_sendImage_accept, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mPresenter.sendImage(ChatActivity.this, Uri.parse(localUrl));
                    }
                })
                .setNegativeButton(R.string.common_label_cancel, null);
        builder.setView(bindDialog.getRoot());
        AlertDialog dialog= builder.create();
        dialog.setOnShowListener(dialogInterface -> {
            int sizeImagePreview=getResources().getDimensionPixelSize(R.dimen.chat_size_img_preview);
            Bitmap bitmap = UtilsImage.reduceBitmap(ChatActivity.this, binding.contentMain,
                    localUrl,sizeImagePreview, sizeImagePreview);
            if(bitmap != null){
                imgDialog.setImageBitmap(bitmap);
            }
            tvMessage.setText(String.format(Locale.ROOT,
                    getString(R.string.chat_dialog_sendImage_message),tvName.getText()));
        });
        dialog.show();
    }
}