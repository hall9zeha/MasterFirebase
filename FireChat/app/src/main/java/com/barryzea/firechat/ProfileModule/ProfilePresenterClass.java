package com.barryzea.firechat.ProfileModule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.barryzea.firechat.ProfileModule.events.ProfileEvent;
import com.barryzea.firechat.ProfileModule.model.ProfileInteractor;
import com.barryzea.firechat.ProfileModule.model.ProfileInteractorClass;
import com.barryzea.firechat.ProfileModule.view.ProfileActivity;
import com.barryzea.firechat.ProfileModule.view.ProfileView;
import com.barryzea.firechat.common.pojo.User;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ProfilePresenterClass  implements ProfilePresenter{
    private ProfileView mView;
    private ProfileInteractor mInteractor;

    private boolean isEdit= false;
    private User mUser;

    public ProfilePresenterClass(ProfileView mView) {
        this.mView = mView;
        this.mInteractor= new ProfileInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mView=null;
    }

    @Override
    public void setUpUser(String username, String email, String photoUrl) {
        mUser= new User();
        mUser.setUserName(username);
        mUser.setEmail(email);
        mUser.setPhotoUrl(photoUrl);

        mView.showUserData(username, email, photoUrl);

    }

    @Override
    public void checkMode() {
        if(isEdit){
            mView.launchGallery();
        }
    }

    @Override
    public void updateUsername(String username) {
        if(isEdit){
            if(setProgress()){
                mView.showProgress();
                mInteractor.updateUsername(username);
                mUser.setUserName(username);
            }

        }
        else{
            isEdit=true;
            mView.menuEditMode();
            mView.enableUIElements();

        }
    }

    private boolean setProgress() {
        if(mView!=null){
            mView.disableUIElements();
            return true;
        }
        return false;
    }

    @Override
    public void updateImage(Uri uri) {
            if(setProgress()){
                mView.showProgressImage();
                mInteractor.updateImage(uri, mUser.getPhotoUrl());
            }
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
            if(resultCode == Activity.RESULT_OK){
                switch(requestCode){
                    case ProfileActivity.RC_PHOTO_PICKER:
                        mView.openDialogPreview(data);
                        break;
                }
            }
    }

    @Subscribe
    @Override
    public void onEventListener(ProfileEvent event) {
        if(mView!= null){
            mView.hideProgress();
            switch(event.getTypeEvent()){
                case ProfileEvent.ERROR_USERNAME:
                    mView.enableUIElements();
                    mView.onError(event.getResMsg());
                    break;
                case ProfileEvent.ERROR_PROFILE:
                    break;
                case ProfileEvent.ERROR_SERVER:
                    break;
                case ProfileEvent.ERROR_IMAGE:
                    mView.enableUIElements();
                    mView.onErrorUpload(event.getResMsg());
                    break;
                case ProfileEvent.SAVE_USERNAME:
                    mView.saveUserNameSuccess();
                    saveSuccess();
                    break;
                case ProfileEvent.UPLOAD_IMAGE:
                    mView.updateImageSuccess(event.getPhotoUrl());
                    mUser.setPhotoUrl(event.getPhotoUrl());
                    saveSuccess();
                    break;
            }
        }
    }

    private void saveSuccess() {
        mView.menuNormalMode();
        mView.setResultOk(mUser.getUserName(), mUser.getPhotoUrl());
        isEdit=false;
    }
}
