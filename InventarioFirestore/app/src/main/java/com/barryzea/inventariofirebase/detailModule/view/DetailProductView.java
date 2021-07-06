package com.barryzea.inventariofirebase.detailModule.view;

public interface DetailProductView {
    void showProgress();
    void hideProgress();
    void enableUIDetailProduct();
    void disableUIDetailProduct();

    void updateSuccess();
    void updateError();

}
