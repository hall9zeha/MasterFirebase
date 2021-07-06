package com.barryzea.inventariofirebase.addModule;

import com.barryzea.inventariofirebase.addModule.events.AddProductEvent;
import com.barryzea.inventariofirebase.common.pojo.Product;

public interface AddProductPresenter {
    void onShow();
    void onDestroy();

    void addProduct(Product product);
    void onEventListener(AddProductEvent event);

}
