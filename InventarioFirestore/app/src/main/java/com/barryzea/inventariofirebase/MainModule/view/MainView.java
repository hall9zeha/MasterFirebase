package com.barryzea.inventariofirebase.MainModule.view;

import com.barryzea.inventariofirebase.common.pojo.Product;

public interface MainView {
    void showProgress();
    void hideProgress();

    void add(Product product);
    void update(Product product);
    void remove(Product product);

    void removeFail();
    void errorMsg(int msg);
}
