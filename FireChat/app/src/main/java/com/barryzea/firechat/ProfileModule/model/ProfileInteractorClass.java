package com.barryzea.firechat.ProfileModule.model;

import android.app.Activity;
import android.net.Uri;

import com.barryzea.firechat.ProfileModule.events.ProfileEvent;
import com.barryzea.firechat.ProfileModule.model.dataAccess.Authentication;
import com.barryzea.firechat.ProfileModule.model.dataAccess.RealtimeDatabaseProfile;
import com.barryzea.firechat.ProfileModule.model.dataAccess.Storage;

import com.barryzea.firechat.ProfileModule.model.dataAccess.UpdateUserListener;
import com.barryzea.firechat.common.model.EventErrorTypeListener;
import com.barryzea.firechat.common.model.StorageUploadImageCallback;
import com.barryzea.firechat.common.pojo.User;

import org.greenrobot.eventbus.EventBus;

public class ProfileInteractorClass implements ProfileInteractor {
    private Authentication mAuthentication;
    private RealtimeDatabaseProfile mDatabase;
    private Storage mStorage;
    private User mMyUser;

    public ProfileInteractorClass() {
        mAuthentication= new Authentication();
        mDatabase= new RealtimeDatabaseProfile();
        mStorage= new Storage();
    }
    private User getCurrentUser(){
        if(mMyUser ==null){
            mMyUser=mAuthentication.getmAuthenticationApi().getAuthUser();
        }
        return mMyUser;
    }

    @Override
    public void updateUsername(String username) {
        final User myUser= getCurrentUser();
        myUser.setUserName(username);
        mDatabase.changeUsername(myUser, new UpdateUserListener() {
            @Override
            public void onSuccess() {
                mAuthentication.updateUsernameFirebaseProfile(myUser, new EventErrorTypeListener() {
                    @Override
                    public void onError(int typeEvent, int resMsg) {
                        post(typeEvent, null, resMsg);
                    }
                });
            }

            @Override
            public void onNotifyContacts() {
                postUsernameSuccess();
            }

            @Override
            public void onError(int resMsg) {
                post(ProfileEvent.ERROR_USERNAME, null, resMsg);
            }
        });
    }


    @Override
    public void updateImage(Uri uri, String oldPhotoUrl) {
        mStorage.uploadImageProfile(uri, getCurrentUser().getEmail(), new StorageUploadImageCallback() {
            @Override
            public void onSuccess(Uri uri) {
                mDatabase.updatePhotoUrl(uri, getCurrentUser().getUid(), new StorageUploadImageCallback() {
                    @Override
                    public void onSuccess(Uri newUri) {
                        post(ProfileEvent.UPLOAD_IMAGE,  newUri.toString(), 0);
                    }

                    @Override
                    public void onError(int resMsg) {
                        post(ProfileEvent.ERROR_SERVER, resMsg);
                    }
                });
                mAuthentication.updateImageFirebaseProfile(uri, new StorageUploadImageCallback() {
                    @Override
                    public void onSuccess(Uri newUri) {
                        mStorage.deleteOldImage(oldPhotoUrl, newUri.toString());
                    }

                    @Override
                    public void onError(int resMsg) {
                        post(ProfileEvent.ERROR_PROFILE, resMsg);
                    }
                });
            }

            @Override
            public void onError(int resMsg) {
                post(ProfileEvent.ERROR_IMAGE, resMsg);
            }
        });
    }

    private void post(int typeEvent, int resMsg) {
        post(typeEvent, null ,resMsg);
    }

    private void postUsernameSuccess() {
        post(ProfileEvent.SAVE_USERNAME,null, 0);
    }
    private void post(int typeEvent, String photoUrl, int resMsg) {
        ProfileEvent event = new ProfileEvent();
        event.setPhotoUrl(photoUrl);
        event.setTypeEvent(typeEvent);
        event.setResMsg(resMsg);
        EventBus.getDefault().post(event);
    }
}
