package com.barryzea.remoteconfigapp;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.barryzea.remoteconfigapp.databinding.ActivityRegisterTurnBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class RegisterTurnActivity extends AppCompatActivity {

    private final String F_MAIN_MESSAGE="main_message";
    private final String F_SHOW_NAME="show_name";
    private final String F_COLOR_PRIMARY="color_primary";
    private final String F_COLOR_TEXT_MESSAGE="color_text_message";
    private final String F_COLOR_BUTTON="color_button";

    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private final Handler mHideHandler = new Handler();
    private TextInputEditText edtName, edtPhone;
    private View mContentView;
    private TextView tvMessageMain;
    private FrameLayout contentMain;
    private Button btnTurn;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;

    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };


    private ActivityRegisterTurnBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterTurnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        mControlsView = binding.fullscreenContentControls;
        mContentView = binding.fullscreenContent;
        edtName= binding.edtName;
        edtPhone = binding.edtPhone;
        contentMain=binding.contentMain;
        tvMessageMain= binding.fullscreenContent;
        btnTurn= binding.dummyButton;

        configFirebaseRemote();
        btnTurn.setOnClickListener( button -> {
            View view= this.getCurrentFocus();
                if(view!=null){
                    InputMethodManager imm=(InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if(imm!=null){
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                edtName.setText("");
                edtPhone.setText("");
                tvMessageMain.setText("Gracias por registrarse, le llamaremos pronto");
        });




    }

    private void configFirebaseRemote() {
        mFirebaseRemoteConfig= FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(BuildConfig.DEBUG? 0:3600)
                .build();
        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(R.xml.remote_config_default);
        fetchRemoteConfig();
    }

    private void fetchRemoteConfig() {
        mFirebaseRemoteConfig.fetch(0).addOnCompleteListener(this, new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull  Task<Void> task) {
                if(task.isSuccessful()){
                    mFirebaseRemoteConfig.fetchAndActivate();
                    Snackbar.make(findViewById(android.R.id.content), "Configuración remota obtenida", Snackbar.LENGTH_LONG).show();
                }
                else{
                    Snackbar.make(findViewById(android.R.id.content), "Configuración local", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        displayMainMessage();
    }

    private void displayMainMessage() {
        edtName.setVisibility(mFirebaseRemoteConfig.getBoolean(F_SHOW_NAME) ? View.VISIBLE:View.GONE);

        String messageRemote=mFirebaseRemoteConfig.getString(F_MAIN_MESSAGE);
        messageRemote=messageRemote.replace("\\n", "\n");
        tvMessageMain.setText(messageRemote);
        displayRemoteColors();
    }

    private void displayRemoteColors() {
        contentMain.setBackgroundColor(Color.parseColor(mFirebaseRemoteConfig.getString(F_COLOR_PRIMARY)));
        tvMessageMain.setTextColor(Color.parseColor(mFirebaseRemoteConfig.getString(F_COLOR_TEXT_MESSAGE)));
        btnTurn.setBackgroundColor(Color.parseColor(mFirebaseRemoteConfig.getString(F_COLOR_BUTTON)));


    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }



    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.VISIBLE);


        // Schedule a runnable to remove the status and navigation bar after a delay

        mHideHandler.postDelayed(mHidePart2Runnable, 100);
    }


    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}