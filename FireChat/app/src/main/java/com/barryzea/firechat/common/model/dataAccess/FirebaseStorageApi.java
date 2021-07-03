package com.barryzea.firechat.common.model.dataAccess;

import com.barryzea.firechat.common.utils.UtilsCommon;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class FirebaseStorageApi {
    private FirebaseStorage mFirebaseStorage;

    public static class Singleton{
        private static final FirebaseStorageApi INSTANCE=new FirebaseStorageApi();
    }

    public static FirebaseStorageApi getInstance(){
        return Singleton.INSTANCE;
    }

    private FirebaseStorageApi(){
        this.mFirebaseStorage=FirebaseStorage.getInstance();
    }
    public FirebaseStorage getmFirebaseStorage(){
        return mFirebaseStorage;
    }
    public StorageReference  getPhotosReferenceByEmail(String email){
        return mFirebaseStorage.getReference().child(UtilsCommon.getEmailEncoded(email));
    }

}
