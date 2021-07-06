package com.barryzea.inventariofirebase.common.model.DataAccess;


import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FirebaseDbApi {
    private FirebaseFirestore mFirestore;
    private static FirebaseDbApi INSTANCE=null;
    private final static  String COLL_PRODUCTS="products";

    private FirebaseDbApi() {
        mFirestore = FirebaseFirestore.getInstance();
    }
    public static FirebaseDbApi getInstance(){
        if(INSTANCE==null){
            INSTANCE=new FirebaseDbApi();
        }
        return  INSTANCE;
    }

    public FirebaseFirestore getmFirestore() {
        return mFirestore;
    }

    public CollectionReference getProductReference(){
        return mFirestore.collection(COLL_PRODUCTS);
    }
}
