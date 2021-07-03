package com.barryzea.firechat.ProfileModule.model.dataAccess;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.barryzea.firechat.ProfileModule.events.ProfileEvent;
import com.barryzea.firechat.R;
import com.barryzea.firechat.common.model.EventErrorTypeListener;
import com.barryzea.firechat.common.model.StorageUploadImageCallback;
import com.barryzea.firechat.common.model.dataAccess.FirebaseAuthenticationApi;
import com.barryzea.firechat.common.pojo.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class Authentication {
    private FirebaseAuthenticationApi mAuthenticationApi;

    public Authentication() {
        mAuthenticationApi=FirebaseAuthenticationApi.getInstance();
    }

    public FirebaseAuthenticationApi getmAuthenticationApi() {
        return mAuthenticationApi;
    }

    public void updateUsernameFirebaseProfile(User myUser, EventErrorTypeListener listener){
        FirebaseUser user= mAuthenticationApi.getCurrentUser();
        if(user != null){
            UserProfileChangeRequest updateProfile=new UserProfileChangeRequest.Builder()
                    .setDisplayName(myUser.getUserName())
                    .build();

            user.updateProfile(updateProfile).addOnCompleteListener(task -> {
                    if(!task.isSuccessful()){
                        listener.onError(ProfileEvent.ERROR_PROFILE, R.string.profile_error_userUpdated);
                    }
            });
        }
    }
    public void updateImageFirebaseProfile(final Uri downloadUri, final StorageUploadImageCallback callback){
        FirebaseUser user= mAuthenticationApi.getCurrentUser();
        if(user != null){
            UserProfileChangeRequest updateProfile=new UserProfileChangeRequest.Builder()
                    .setPhotoUri(downloadUri)
                    .build();

            user.updateProfile(updateProfile).addOnCompleteListener(task -> {
                if(task.isSuccessful()){
                   callback.onSuccess(downloadUri);
                }
            })
            .addOnFailureListener(e -> {
                callback.onError(R.string.profile_error_imageUpdated);
            });


        }
    }
}
