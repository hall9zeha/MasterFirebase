package com.barryzea.inventariofirebase.common.model.DataAccess;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseDbApi {
    private DatabaseReference databaseReference;
    private static FirebaseDbApi INSTANCE=null;
    private final static  String PATH_PRODUCTS="products";

    private FirebaseDbApi() {
        databaseReference= FirebaseDatabase.getInstance().getReference();
    }
    public static FirebaseDbApi getInstance(){
        if(INSTANCE==null){
            INSTANCE=new FirebaseDbApi();
        }
        return  INSTANCE;
    }
    public DatabaseReference getReference(){
        return databaseReference;
    }
    public DatabaseReference getProductReference(){
        return getReference().child(PATH_PRODUCTS);
    }
}
