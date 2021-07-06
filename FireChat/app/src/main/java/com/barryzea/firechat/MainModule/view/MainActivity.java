package com.barryzea.firechat.MainModule.view;

import android.app.ActivityOptions;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.barryzea.firechat.AddModule.view.AddFragment;
import com.barryzea.firechat.ChatModule.view.ChatActivity;
import com.barryzea.firechat.LoginModule.view.LoginActivity;
import com.barryzea.firechat.MainModule.MainPresenter;
import com.barryzea.firechat.MainModule.MainPresenterClass;
import com.barryzea.firechat.MainModule.view.adapters.EventClickListener;
import com.barryzea.firechat.MainModule.view.adapters.RequestAdapter;
import com.barryzea.firechat.MainModule.view.adapters.UserAdapter;
import com.barryzea.firechat.ProfileModule.view.ProfileActivity;
import com.barryzea.firechat.R;
import com.barryzea.firechat.common.Constants;
import com.barryzea.firechat.common.pojo.User;
import com.barryzea.firechat.common.utils.UtilsCommon;
import com.barryzea.firechat.databinding.ActivityMainBinding;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class MainActivity extends AppCompatActivity implements EventClickListener,  MainView {

    private static final int RC_PROFILE = 23;
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    private CircleImageView imgProfile;
    private RecyclerView rvRequest, rvUsers;
    private Toolbar toolbarMain;

    private RequestAdapter mRequestAdapter;
    private UserAdapter mUserAdapter;
    private User mUser;

    private MainPresenter mPresenter;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mPresenter=new MainPresenterClass(this);
        mPresenter.onCreate();
        mUser= mPresenter.getCurrentUser();
        mFirebaseAnalytics= FirebaseAnalytics.getInstance(this);
        setUpViews();
        setUpToolbar();
        setUpAdapters();
        setUpRecyclerView();
        configTutorial();


    }

    private void setUpViews() {
        imgProfile= binding.circleImageProfile;
        rvRequest=binding.contentImport.rvRequest;
        rvUsers=binding.contentImport.rvUsers;
        toolbarMain=binding.toolbar;
        binding.fab.setOnClickListener(click->{
            new AddFragment().show(getSupportFragmentManager(), getString(R.string.addFriend_title));
        });
    }
    private void setUpToolbar(){
        toolbarMain.setTitle(mUser.getUsernameValid());
        UtilsCommon.loadImage(this,mUser.getPhotoUrl(), imgProfile);
        setSupportActionBar(binding.toolbar);
    }
    private void setUpAdapters(){

        mRequestAdapter= new RequestAdapter(new ArrayList<>(), this);
        mUserAdapter = new UserAdapter(new ArrayList<>(), this);
    }
    private void setUpRecyclerView(){

        rvRequest.setLayoutManager(new LinearLayoutManager(this));


        rvRequest.setAdapter(mRequestAdapter);

        rvUsers.setLayoutManager(new LinearLayoutManager(this));

        rvUsers.setAdapter(mUserAdapter);
    }
    private void configTutorial(){
        new MaterialShowcaseView.Builder(this)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent))
                .setContentTextColor(ContextCompat.getColor(this,R.color.blue_a100))
                .setDismissTextColor(ContextCompat.getColor(this, android.R.color.white))
                .setMaskColour(ContextCompat.getColor(this,R.color.gray_900_t))
                .setTarget(binding.fab)
                .setTargetTouchable(true)
                .setDismissStyle(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD_ITALIC))
                .setTitleText(R.string.app_name)
                .setContentText(R.string.tuto_message)

                .setDismissText(getString(R.string.msg_tuto_ok))
                .setDelay(2000)
                .setFadeDuration(600)
                .setDismissOnTouch(true)
                .setDismissOnTargetTouch(true)
                //esta propiedad solo sirve una vez si quieres que el tutorial se vuelva a ejecutar debes cambiar otro id
                .singleUse(getString(R.string.fabIdShowCase))
                
                .show();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.action_logout:
                mPresenter.signOff();
                Intent intent = new Intent(this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                |Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.action_profile:
                Intent intentProfile= new Intent(this, ProfileActivity.class);
                intentProfile.putExtra(User.USERNAME, mUser.getUserName());
                intentProfile.putExtra(User.EMAIL, mUser.getEmail());
                intentProfile.putExtra(User.PHOTO_URL, mUser.getPhotoUrl());

                if(UtilsCommon.hasMaterialDesign()){
                    startActivityForResult(intentProfile, RC_PROFILE
                            , ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
                }
                else{
                    startActivityForResult(intentProfile, RC_PROFILE);
                }
                break;
            case R.id.action_about:
                openAbout();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode ==RESULT_OK){
            switch(requestCode){
                case RC_PROFILE:
                    if(data != null){
                        mUser.setUserName(data.getStringExtra(User.USERNAME));
                        mUser.setPhotoUrl(data.getStringExtra(User.PHOTO_URL));
                        setUpToolbar();
                    }
            }
        }
    }

    private void openAbout() {
        LayoutInflater inflater = (LayoutInflater)getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view= inflater.inflate(R.layout.about_dialog, null);
        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this, R.style.DialogFragmentTheme)
                .setTitle(R.string.main_menu_about)
                .setView(view)
                .setPositiveButton(R.string.common_label_ok , null)
                .setNeutralButton(R.string.about_privacy, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent= new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/hall9zeha"));
                        startActivity(intent);
                    }
                });
        AlertDialog dialog= builder.create();
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
        clearNotificationManager();
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
    private void clearNotificationManager(){
        NotificationManager notification =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        if(notification != null){
            notification.cancelAll();
        }
    }


    /*
    * Interface EventClickListener
    *
    * */
    @Override
    public void onItemClick(User user) {
        Intent intent= new Intent(this, ChatActivity.class);
        intent.putExtra(User.UID, user.getUid());
        intent.putExtra(User.USERNAME, user.getUserName());
        intent.putExtra(User.EMAIL, user.getEmail());
        intent.putExtra(User.PHOTO_URL, user.getPhotoUrl());
        if (UtilsCommon.hasMaterialDesign()) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onItemLongClick(User user) {
        new AlertDialog.Builder(this, R.style.DialogFragmentTheme)
                .setTitle(getString(R.string.main_dialog_title_confirmDelete))
                .setMessage(String.format(Locale.ROOT,getString(R.string.main_dialog_message_confirmDelete), user.getUsernameValid()))
                .setPositiveButton(R.string.main_dialog_accept, (dialogInterface, i) -> {
                    mPresenter.removeFriends(user.getUid());

                })
                .setNegativeButton(R.string.common_label_cancel, null)
                .show();

    }

    @Override
    public void onAcceptRequest(User user) {
        mPresenter.acceptRequest(user);
    }

    @Override
    public void onDeniedRequest(User user) {
        mPresenter.denyRequest(user);
    }
    /*
    * Interface MainView
    * */

    @Override
    public void friendAdded(User user) {
        mUserAdapter.add(user);
    }

    @Override
    public void friendUpdated(User user) {
        mUserAdapter.update(user);
    }

    @Override
    public void friendRemoved(User user) {
        mUserAdapter.remove(user);
    }

    @Override
    public void requestAdded(User user) {
        mRequestAdapter.add(user);

    }

    @Override
    public void requestUpdated(User user) {
        mRequestAdapter.update(user);

    }

    @Override
    public void requestRemoved(User user) {
        mRequestAdapter.remove(user);
    }

    @Override
    public void showRequestAccepted(String name) {
        Snackbar.make(findViewById(android.R.id.content), getString(R.string.main_message_request_accepted, name),
        Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showRequestDenied() {
        Snackbar.make(findViewById(android.R.id.content), R.string.main_message_request_denied,
                Snackbar.LENGTH_LONG).show();
        mFirebaseAnalytics.logEvent(Constants.EVENT_FRIEND_DENIED, null);
    }

    @Override
    public void showFriendRemoved() {
        Snackbar.make(findViewById(android.R.id.content), R.string.main_message_user_removed,
                Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showError(int resMsg) {
        Snackbar.make(findViewById(android.R.id.content), resMsg,
                Snackbar.LENGTH_LONG).show();
    }
}