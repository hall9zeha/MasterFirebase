package com.barryzea.firechat.ChatModule.model.dataAccess;

import android.app.Activity;
import android.net.Uri;

import com.barryzea.firechat.R;
import com.barryzea.firechat.common.model.StorageUploadImageCallback;
import com.barryzea.firechat.common.model.dataAccess.FirebaseStorageApi;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Storage {
    public static final String PATH_CHATS="chats";
    private FirebaseStorageApi mStorageApi;

    public Storage() {
        mStorageApi = FirebaseStorageApi.getInstance();
    }
    public void uploadImageChat(Activity activity, final Uri imageUri, String myEmail, final StorageUploadImageCallback callback){
        if(imageUri.getLastPathSegment() != null){
            StorageReference photoRef= mStorageApi.getPhotosReferenceByEmail(myEmail).child(PATH_CHATS)
                    .child(imageUri.getLastPathSegment());
            photoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            if(uri != null){
                                callback.onSuccess(uri);
                            }
                            else{
                                callback.onError(R.string.chat_error_imageUpload);
                            }
                        }
                    });
                }
            });
        }
    }
}
