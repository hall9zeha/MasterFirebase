package com.barryzea.inventariofirebase.addModule.model.dataAccess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barryzea.inventariofirebase.R;
import com.barryzea.inventariofirebase.addModule.events.AddProductEvent;
import com.barryzea.inventariofirebase.common.BasicErrorCallback;
import com.barryzea.inventariofirebase.common.model.DataAccess.FirebaseDbApi;
import com.barryzea.inventariofirebase.common.pojo.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestoreException;


public class FirebaseFirestoreAdd {
        private FirebaseDbApi mFirestoreApi;

    public FirebaseFirestoreAdd() {
        mFirestoreApi =FirebaseDbApi.getInstance();
    }
    public void addProduct(Product product, BasicErrorCallback callback){
        mFirestoreApi.getProductReference()
                .add(product)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            callback.onSuccess();
                        }
                        if(task.getException() != null){
                            try {
                                throw task.getException();
                            }
                            catch(FirebaseFirestoreException e){
                                callback.onError(AddProductEvent.ERROR_MAX_VALUE, R.string.error_max_value);

                            }
                            catch (Exception e) {
                                callback.onError(AddProductEvent.ERROR_SERVER, R.string.error_default_add_product);
                                e.printStackTrace();
                            }
                        }
                    }


                });

    }
}
