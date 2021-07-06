package com.barryzea.inventariofirebase.MainModule.model;

import com.barryzea.inventariofirebase.common.pojo.Product;

public interface MainInteractor {
    void subscribeToProducts();
    void unsubscribeToProducts();

    void removeProduct(Product product);
}
