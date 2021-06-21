package com.barryzea.inventariofirebase.MainModule.model.dataAccess;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.barryzea.inventariofirebase.MainModule.events.MainEvent;
import com.barryzea.inventariofirebase.MainModule.model.dataAccess.ProductEventListener;
import com.barryzea.inventariofirebase.MainModule.view.MainView;
import com.barryzea.inventariofirebase.R;
import com.barryzea.inventariofirebase.common.BasicErrorCallback;
import com.barryzea.inventariofirebase.common.model.DataAccess.FirebaseDbApi;
import com.barryzea.inventariofirebase.common.pojo.Product;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

public class RealtimeDatabase {


    private FirebaseDbApi mDatabaseApi;
    private ChildEventListener mChildEventListener;

    public RealtimeDatabase() {
        mDatabaseApi=FirebaseDbApi.getInstance();
    }

    public void subscribeToProductsListener(ProductEventListener listener){
        if(mChildEventListener==null){
            mChildEventListener =new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, String previousChildName) {
                        listener.onChildAdded(getProduct(snapshot));
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot,  String previousChildName) {
                        listener.onChildUpdate(getProduct(snapshot));
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                        listener.onChildRemove(getProduct(snapshot));
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot,  String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    switch (error.getCode()){
                        case DatabaseError.PERMISSION_DENIED:
                            listener.onError(R.string.permission_denied_inventario);
                            break;
                        default:
                            listener.onError(R.string.error_in_server);
                            break;

                    }
                }
            };
        }
        mDatabaseApi.getProductReference().addChildEventListener(mChildEventListener);
    }
    private Product getProduct(DataSnapshot snapshot){
        Product product = snapshot.getValue(Product.class);
        if(product!=null){
            product.setId(snapshot.getKey());
        }
        return product;
    }
    public void unsubscribeToProduct(){
        if(mChildEventListener !=null){
            mDatabaseApi.getProductReference().removeEventListener(mChildEventListener);
        }
    }
    public void removeProduct(Product product, BasicErrorCallback callback){
            mDatabaseApi.getProductReference().child(product.getId())
                    .removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(@Nullable  DatabaseError error, @NonNull DatabaseReference ref) {
                            if(error==null){
                                callback.onSuccess();
                            }
                            else{
                                switch (error.getCode()){
                                    case DatabaseError.PERMISSION_DENIED:
                                        callback.onError(MainEvent.ERROR_REMOVE, R.string.remove_message_error);
                                        break;
                                    default:
                                        callback.onError(MainEvent.ERROR_REMOVE, R.string.error_in_server);
                                        break;
                                }
                            }
                        }
                    });
    }
}
