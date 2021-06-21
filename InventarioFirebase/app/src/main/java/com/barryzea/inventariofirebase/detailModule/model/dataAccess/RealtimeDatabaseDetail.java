package com.barryzea.inventariofirebase.detailModule.model.dataAccess;

import androidx.annotation.NonNull;

import com.barryzea.inventariofirebase.common.BasicCallbackAddProduct;
import com.barryzea.inventariofirebase.common.model.DataAccess.FirebaseDbApi;
import com.barryzea.inventariofirebase.common.pojo.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class RealtimeDatabaseDetail {

    private FirebaseDbApi mDataBaseApi;

    public RealtimeDatabaseDetail(){
        mDataBaseApi=FirebaseDbApi.getInstance();
    }
    public void updateProduct(Product product, final BasicCallbackAddProduct callback){
        mDataBaseApi.getProductReference().child(product.getId()).setValue(product)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onError();
                    }
                });
    }

}
