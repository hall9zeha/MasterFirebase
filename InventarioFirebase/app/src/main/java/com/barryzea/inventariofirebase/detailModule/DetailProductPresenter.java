package com.barryzea.inventariofirebase.detailModule;

import com.barryzea.inventariofirebase.common.pojo.Product;
import com.barryzea.inventariofirebase.detailModule.events.DetailProductEvent;

public interface DetailProductPresenter {
    //void onCreateMethod();
    void onCreate();
    void onDestroy();
    void updateProduct(Product product);
    void onEventListener(DetailProductEvent event);
}
