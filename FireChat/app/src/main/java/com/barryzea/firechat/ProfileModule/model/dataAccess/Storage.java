package com.barryzea.firechat.ProfileModule.model.dataAccess;

import android.app.Activity;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.barryzea.firechat.R;
import com.barryzea.firechat.common.model.StorageUploadImageCallback;
import com.barryzea.firechat.common.model.dataAccess.FirebaseStorageApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;

public class Storage {
    private static final String  PATH_PROFILE="profile";
    private FirebaseStorageApi mStorageApi;

    public Storage(){
        mStorageApi= FirebaseStorageApi.getInstance();
    }

    public void uploadImageProfile(Uri imageUri, String email, StorageUploadImageCallback callback){
        if(imageUri.getLastPathSegment() != null){
            final StorageReference photoRef= mStorageApi.getPhotosReferenceByEmail(email)
                    .child(PATH_PROFILE).child(imageUri.getLastPathSegment());
            photoRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            if (uri!=null) {
                                                callback.onSuccess(uri);
                                            }
                                            else{
                                                callback.onError(R.string.profile_error_imageUpdated);
                                            }
                                        }
                                    });
                        }
                    });
        }
        else{
                callback.onError(R.string.profile_error_invalid_image);
        }
    }

    public void deleteOldImage(String oldPhotoUrl, String downloadUrl){
        if(oldPhotoUrl != null && !oldPhotoUrl.isEmpty()){
            StorageReference storageReference=mStorageApi.getmFirebaseStorage().getReferenceFromUrl(downloadUrl);
            StorageReference oldStorageReference=null;
            try {
                mStorageApi.getmFirebaseStorage().getReferenceFromUrl(oldPhotoUrl);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(oldStorageReference !=null && !oldStorageReference.getPath().equals(storageReference.getPath())){
                oldStorageReference.delete().addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {

                    }
                });
            }

        }
    }
}
