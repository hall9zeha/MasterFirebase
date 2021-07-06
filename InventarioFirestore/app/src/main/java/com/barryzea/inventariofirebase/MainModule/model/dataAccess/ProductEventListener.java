package com.barryzea.inventariofirebase.MainModule.model.dataAccess;

import com.barryzea.inventariofirebase.common.pojo.Product;

public interface ProductEventListener {
    void onChildAdded(Product product);
    void onChildUpdate(Product product);
    void onChildRemove(Product product);

    void onError(int Msg);

}
