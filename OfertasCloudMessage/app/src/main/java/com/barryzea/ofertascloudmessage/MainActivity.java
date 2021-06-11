package com.barryzea.ofertascloudmessage;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.barryzea.ofertascloudmessage.databinding.ActivityMainBinding;
import com.google.firebase.iid.FirebaseInstanceIdReceiver;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashSet;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private ActivityMainBinding binding;
    private static final String SP_TOPICS="sharedPreferencesTopics";
    private Set<String> mTopicSet;
    private SharedPreferences mSharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= ActivityMainBinding.inflate(getLayoutInflater());
        View view= binding.getRoot();

        setContentView(view);

        binding.btnSuscribir.setOnClickListener(this);
        binding.btnDeSuscribir.setOnClickListener(this);

        configSharedPreference();
        //imprimimos el token al iniciar la actividad
        if(FirebaseMessaging.getInstance().getToken() != null){
            Log.i("TokenDevice", FirebaseMessaging.getInstance().getToken().toString());
        }




    }

    private void configSharedPreference() {
        mSharedPreferences = getPreferences(Context.MODE_PRIVATE);
        mTopicSet=mSharedPreferences.getStringSet(SP_TOPICS, new HashSet<String>());
        showTopics();
    }

    private void showTopics() {
        binding.tvTopics.setText(mTopicSet.toString());
    }


    @Override
    public void onClick(View view) {

        String topics=getResources().getStringArray(R.array.topicsValues)[binding.spTopics.getSelectedItemPosition()];
       int id=view.getId();
       switch(id){
           case R.id.btnSuscribir:
               if (!mTopicSet.contains(topics)) {
                   FirebaseMessaging.getInstance().subscribeToTopic(topics);
                   mTopicSet.add(topics);
                   saveSharedPreference();
               }
               break;
           case R.id.btnDeSuscribir:
               if (mTopicSet.contains(topics)) {
                   FirebaseMessaging.getInstance().unsubscribeFromTopic(topics);
                   mTopicSet.remove(topics);
                   saveSharedPreference();

               }
               break;
       }

    }

    private void saveSharedPreference() {
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.clear();
        editor.putStringSet(SP_TOPICS, mTopicSet);
        editor.apply();
        showTopics();
    }
}