package com.barryzea.inventariofirebase.MainModule.view.adapter;

import com.barryzea.inventariofirebase.common.pojo.Product;

public interface ListenerEventsProducts {
    void onProductClick(Product product);
    void onProductLongClick(Product product);
}
