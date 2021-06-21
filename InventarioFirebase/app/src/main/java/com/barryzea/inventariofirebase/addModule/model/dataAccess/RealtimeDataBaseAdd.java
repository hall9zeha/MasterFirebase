package com.barryzea.inventariofirebase.addModule.model.dataAccess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barryzea.inventariofirebase.R;
import com.barryzea.inventariofirebase.addModule.events.AddProductEvent;
import com.barryzea.inventariofirebase.common.BasicErrorCallback;
import com.barryzea.inventariofirebase.common.model.DataAccess.FirebaseDbApi;
import com.barryzea.inventariofirebase.common.pojo.Product;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class RealtimeDataBaseAdd {
        private FirebaseDbApi mFirebaseApi;

    public RealtimeDataBaseAdd() {
        mFirebaseApi=FirebaseDbApi.getInstance();
    }
    public void addProduct(Product product, BasicErrorCallback callback){
        mFirebaseApi.getProductReference().push().setValue(product, new DatabaseReference.CompletionListener(){

            @Override
            public void onComplete(@Nullable  DatabaseError error, @NonNull DatabaseReference ref) {
                if(error==null){
                    callback.onSuccess();
                }
                else{
                    switch(error.getCode()){
                        case DatabaseError.PERMISSION_DENIED:
                            callback.onError(AddProductEvent.ERROR_MAX_VALUE, R.string.error_max_value);
                            break;
                        default:
                            callback.onError(AddProductEvent.ERROR_SERVER, R.string.error_default_add_product);
                            break;

                    }

                }
            }
        });
    }
}
