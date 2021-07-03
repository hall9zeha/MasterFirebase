package com.barryzea.firechat.common.model.dataAccess;

import androidx.annotation.NonNull;

import com.barryzea.firechat.common.utils.UtilsCommon;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class FirebaseCloudMessagesApi {
    private FirebaseMessaging firebaseMessaging;

    private static class singletonHolder{
        private static final FirebaseCloudMessagesApi INSTANCE= new FirebaseCloudMessagesApi();
    }

    public static FirebaseCloudMessagesApi getInstance() {
        return singletonHolder.INSTANCE;
    }

    public FirebaseCloudMessagesApi() {
        this.firebaseMessaging=FirebaseMessaging.getInstance();
    }
    public void subscribeToMyTopic(String myEmail){
        firebaseMessaging.subscribeToTopic(UtilsCommon.getEmailToTopic(myEmail))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task) {
                        if(!task.isSuccessful()){
                            // TODO: 03/07/2021
                        }
                    }
                });
    }
    public void unSubscribeToMyTopic(String myEmail) {
        firebaseMessaging.unsubscribeFromTopic(UtilsCommon.getEmailToTopic(myEmail))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull  Task<Void> task) {

                    }
                });
    }
}
