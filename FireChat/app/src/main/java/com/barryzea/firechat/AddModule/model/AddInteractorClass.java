package com.barryzea.firechat.AddModule.model;

import com.barryzea.firechat.AddModule.events.AddEvent;
import com.barryzea.firechat.AddModule.model.dataAccess.RealtimeDatabaseAdd;
import com.barryzea.firechat.common.model.BasicEventsCallback;
import com.barryzea.firechat.common.model.dataAccess.FirebaseAuthenticationApi;

import org.greenrobot.eventbus.EventBus;

public class AddInteractorClass  implements AddInteractor{
    private RealtimeDatabaseAdd mDatabase;
    private FirebaseAuthenticationApi mAuthentication;

    public AddInteractorClass() {
        mDatabase=new RealtimeDatabaseAdd();
        mAuthentication=FirebaseAuthenticationApi.getInstance();
    }


    @Override
    public void addFriend(String email) {
        mDatabase.addFriends(email, mAuthentication.getAuthUser(), new BasicEventsCallback() {
            @Override
            public void onSuccess() {
                post(AddEvent.SEND_REQUEST_SUCCESS);
            }

            @Override
            public void onError() {
                post(AddEvent.ERROR_SERVER);
            }
        });
    }

    private void post(int typeEvent) {
        AddEvent event= new AddEvent();
        event.setTypeEvent(typeEvent);
        EventBus.getDefault().post(event);
    }
}
