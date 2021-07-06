package com.barryzea.inventariofirebase.detailModule.model.dataAccess;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barryzea.inventariofirebase.common.BasicCallbackAddProduct;
import com.barryzea.inventariofirebase.common.model.DataAccess.FirebaseDbApi;
import com.barryzea.inventariofirebase.common.pojo.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Transaction;

import java.util.HashMap;
import java.util.Map;

public class FirebaseFirestoreDetail {

    private FirebaseDbApi mFirestoreApi;

    public FirebaseFirestoreDetail(){
        mFirestoreApi =FirebaseDbApi.getInstance();
    }
    public void updateProduct(Product product, final BasicCallbackAddProduct callback){

        final DocumentReference documentReference= mFirestoreApi.getProductReference().document(product.getId());
        mFirestoreApi.getmFirestore().runTransaction(new Transaction.Function<Void>() {
            @Nullable

            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot= transaction.get(documentReference);
                /*
                * En una transaccion primero debes realizar la operación de lectura de datos luego la actualización e insersion
                * */
                double currentScore=0.0;
                long currentTotalVotes=0;
                Product currentProduct = snapshot.toObject(Product.class);
                if(currentProduct != null){
                    currentScore= currentProduct.getScore();
                    currentTotalVotes= currentProduct.getTotalVotes();
                }

                long newTotalVotes=currentTotalVotes + 1;
                double newScore=((currentScore * currentTotalVotes ) + product.getScore()) / newTotalVotes;

                Map<String, Object> updates=new HashMap<>();
                updates.put(Product.NAME, product.getName());
                updates.put(Product.QUANTITY, product.getQuantity());
                updates.put(Product.PHOTO_URL, product.getPhotoUrl());
                updates.put(Product.SCORE, newScore);
                updates.put(Product.TOTAL_VOTES, newTotalVotes);

                transaction.update(documentReference, updates);
                return null;
            }
        })
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });


    }

}
