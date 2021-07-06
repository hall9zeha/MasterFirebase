package com.barryzea.inventariofirebase.MainModule.model.dataAccess;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barryzea.inventariofirebase.MainModule.events.MainEvent;
import com.barryzea.inventariofirebase.R;
import com.barryzea.inventariofirebase.common.BasicErrorCallback;
import com.barryzea.inventariofirebase.common.model.DataAccess.FirebaseDbApi;
import com.barryzea.inventariofirebase.common.pojo.Product;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.HashMap;
import java.util.Map;


public class FirebaseFirestore {


    private FirebaseDbApi mFirebaseApi;
    private EventListener<QuerySnapshot> mEventListener;
    private ListenerRegistration mRegistration;

    public FirebaseFirestore() {
        mFirebaseApi =FirebaseDbApi.getInstance();
    }

    public void subscribeToProductsListener(ProductEventListener listener){
        if(mEventListener ==null){

            mEventListener = new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable  QuerySnapshot snapshot, @Nullable  FirebaseFirestoreException error) {
                    if(error !=null){
                        if(error.getCode()==FirebaseFirestoreException.Code.PERMISSION_DENIED){
                            listener.onError(R.string.permission_denied_inventario);
                        }
                        else{
                            listener.onError(R.string.error_in_server);
                        }
                    }
                    else if(snapshot != null){
                        for(DocumentChange dc: snapshot.getDocumentChanges()){
                            switch (dc.getType()){
                                case ADDED:
                                    listener.onChildAdded(getProduct(dc));
                                    break;
                                case MODIFIED:
                                    listener.onChildUpdate(getProduct(dc));
                                    break;
                                case REMOVED:
                                    listener.onChildRemove(getProduct(dc));
                                    break;
                            }
                        }
                    }
                }
            };

        }
        // lo comentamos momentaneamente
       // mRegistration=mFirebaseApi.getProductReference().addSnapshotListener(mEventListener);

        /*
        * Usaremos las consultas en firestore
        * */
        //que nostraiga productos mayores que  5
        //Query query=mFirebaseApi.getProductReference().whereGreaterThan(Product.QUANTITY,5);
        //ahora mayores o iguales que 5
        //Query query=mFirebaseApi.getProductReference().whereGreaterThanOrEqualTo(Product.QUANTITY,5);
        //menor que 5
        //Query query=mFirebaseApi.getProductReference().whereLessThan(Product.QUANTITY,5);
        //Ahora mayor a 2 pero menor a 6
        /*Query query=mFirebaseApi.getProductReference()
                .whereGreaterThan(Product.QUANTITY,2)
                .whereLessThan(Product.QUANTITY, 6);*/
        //ordenados de menor a mayor
        //Query query=mFirebaseApi.getProductReference().orderBy(Product.QUANTITY,Query.Direction.ASCENDING);
        //finalmente ordenado pero con límites de objetos mostrados en 3
        /*Query query=mFirebaseApi.getProductReference()
                .orderBy(Product.QUANTITY,Query.Direction.DESCENDING)
                .limit(3);
*/
        Query query=mFirebaseApi.getProductReference()
                .orderBy(Product.QUANTITY,Query.Direction.DESCENDING);
        mRegistration=query.addSnapshotListener(mEventListener);


    }
    private Product getProduct(DocumentChange documentChange){
        Product product = documentChange.getDocument().toObject(Product.class);
        product.setId(documentChange.getDocument().getId());

        return product;
    }
    public void unsubscribeToProduct(){
        if(mRegistration !=null){
            mRegistration.remove();

        }
    }
    public void removeProduct(Product product, BasicErrorCallback callback){
        /*
        * Haremos una modificación de varios documentos con un solo evento o modificación por lotes como se llaman
        * */
        WriteBatch batch=mFirebaseApi.getmFirestore().batch();

        batch.delete(mFirebaseApi.getProductReference().document(product.getId()));
        DocumentReference lastUpdatesReference=mFirebaseApi.getmFirestore()
                .collection("updates").document("inventario");

        Map<String, Object> updates= new HashMap<>();
        updates.put("lastUpdate", FieldValue.serverTimestamp());
        batch.set(lastUpdatesReference,updates);

        batch.commit()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        callback.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if(((FirebaseFirestoreException)e).getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED){
                            callback.onError(MainEvent.ERROR_REMOVE, R.string.remove_message_error);
                        }
                        else{
                            callback.onError(MainEvent.ERROR_REMOVE, R.string.error_in_server);
                        }
                    }
                });

           /* mFirebaseApi.getProductReference().document(product.getId())
                    .delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            callback.onSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            if(((FirebaseFirestoreException)e).getCode() == FirebaseFirestoreException.Code.PERMISSION_DENIED){
                                callback.onError(MainEvent.ERROR_REMOVE, R.string.remove_message_error);
                            }
                            else{
                                callback.onError(MainEvent.ERROR_REMOVE, R.string.error_in_server);
                            }
                        }
                    });*/

    }
}
