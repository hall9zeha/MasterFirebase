package com.barryzea.firechat.ChatModule;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.barryzea.firechat.ChatModule.events.ChatEvent;
import com.barryzea.firechat.ChatModule.model.ChatInteractor;
import com.barryzea.firechat.ChatModule.model.ChatInteractorClass;
import com.barryzea.firechat.ChatModule.view.ChatView;
import com.barryzea.firechat.common.Constants;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class ChatPresenterClass implements ChatPresenter {
    private ChatView mView;
    private ChatInteractor mInteractor;
    private String mFriendUid, mFriendEmail;

    public ChatPresenterClass(ChatView mView) {
        this.mView= mView;
        mInteractor= new ChatInteractorClass();
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
    public void onPause() {
        if(mView!=null){
            mInteractor.unsubscribeToFriend(mFriendUid);
            mInteractor.unsubscribeToMessage();
        }
    }

    @Override
    public void onResume() {
        if(mView != null){
            mInteractor.subscribeToFriend(mFriendUid, mFriendEmail);
            mInteractor.subscribeToMessage();
        }
    }

    @Override
    public void setUpFriend(String uid, String email) {
        mFriendUid=uid;
        mFriendEmail=email;
    }

    @Override
    public void sendMessage(String msg) {
        if(mView != null){
            mInteractor.sendMessage(msg);
        }
    }

    @Override
    public void sendImage(Activity activity, Uri imageUri) {
        if(mView !=null){
            mView.showProgress();
            mInteractor.sendImage(activity, imageUri);
        }
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if(requestCode == Constants.RC_PERMISSION_STORAGE && resultCode==Activity.RESULT_OK){
            mView.openDialogPreview(data);
        }
    }
    @Subscribe
    @Override
    public void onEventListener(ChatEvent event) {
        if(mView != null){
            switch(event.getTypeEvent()){
                case ChatEvent.MESSAGE_ADDED:
                    mView.onMessageReceived(event.getMessage());
                    break;
                case ChatEvent.IMAGE_UPLOAD_SUCCESS:
                    mView.hideProgress();
                    break;
                case ChatEvent.GET_STATUS_FRIEND:
                    mView.onStatusUser(event.isConnected(), event.getLastConnection());
                    break;
                case ChatEvent.ERROR_PROCESS_DATA:
                    break;
                case ChatEvent.IMAGE_UPLOAD_FAIL:
                    break;
                case ChatEvent.ERROR_SERVER:
                    break;
                case ChatEvent.ERROR_VOLLEY:
                    mView.hideProgress();
                    mView.onError(event.getResMsg());
                    break;


            }
        }
    }
}
