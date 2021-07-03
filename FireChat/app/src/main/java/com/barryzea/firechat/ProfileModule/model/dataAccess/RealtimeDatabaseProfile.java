package com.barryzea.firechat.ProfileModule.model.dataAccess;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.barryzea.firechat.R;
import com.barryzea.firechat.common.model.StorageUploadImageCallback;
import com.barryzea.firechat.common.model.dataAccess.FirebaseRealtimeDatabaseApi;
import com.barryzea.firechat.common.pojo.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class RealtimeDatabaseProfile {
    private FirebaseRealtimeDatabaseApi mDatabaseApi;

    public RealtimeDatabaseProfile() {
        mDatabaseApi=FirebaseRealtimeDatabaseApi.getInstance();
    }

    public void changeUsername(final User myUser,  final UpdateUserListener listener){
        if(mDatabaseApi.getUserReferenceByUid(myUser.getUid()) != null){
            Map<String, Object> updates=new HashMap<>();
            updates.put(User.USERNAME, myUser.getUserName());
            mDatabaseApi.getUserReferenceByUid(myUser.getUid()).updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            listener.onSuccess();
                            notifyContactsUsername(myUser, listener);
                        }
                    });

        }
    }

    private void notifyContactsUsername(User myUser, UpdateUserListener listener) {

        mDatabaseApi.getContactReference(myUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                    for(DataSnapshot child: snapshot.getChildren()){
                        String friendUid=child.getKey();
                        DatabaseReference reference= getContactsReference(friendUid,  myUser.getUid());
                        Map<String, Object> updates= new HashMap<>();
                        updates.put(User.USERNAME, myUser.getUserName());
                        reference.updateChildren(updates);

                    }
                    listener.onNotifyContacts();
            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                    listener.onError(R.string.profile_error_userUpdated);
            }
        });
    }

    private DatabaseReference getContactsReference(String mainUid, String myUid) {
        return mDatabaseApi.getUserReferenceByUid(mainUid)
                .child(FirebaseRealtimeDatabaseApi.PATH_CONTACTS).child(myUid);
    }
    public void updatePhotoUrl(final Uri downloadUri, final String myUid, final StorageUploadImageCallback callback){
        if(mDatabaseApi.getUserReferenceByUid(myUid) != null){
            Map<String, Object> updates = new HashMap<>();
            updates.put(User.PHOTO_URL, downloadUri.toString());
            mDatabaseApi.getUserReferenceByUid(myUid).updateChildren(updates)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            callback.onSuccess(downloadUri);
                            notifyContactsPhoto(downloadUri.toString(),myUid, callback);
                        }
                    });
        }
    }

    private void notifyContactsPhoto(String photoUrl, String myUid, StorageUploadImageCallback callback) {
        mDatabaseApi.getContactReference(myUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull  DataSnapshot snapshot) {
                for(DataSnapshot child: snapshot.getChildren()){
                    String friendUid=child.getKey();
                    DatabaseReference reference= getContactsReference(friendUid,  myUid);
                    Map<String, Object> updates= new HashMap<>();
                    updates.put(User.PHOTO_URL, photoUrl);
                    reference.updateChildren(updates);

                }

            }

            @Override
            public void onCancelled(@NonNull  DatabaseError error) {
                callback.onError(R.string.profile_error_imageUpdated);
            }
        });
    }
}
