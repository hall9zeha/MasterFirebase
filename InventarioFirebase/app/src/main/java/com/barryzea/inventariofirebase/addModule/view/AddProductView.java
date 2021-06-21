package com.barryzea.inventariofirebase.addModule.view;

public interface AddProductView {
    void enableUIElement();
    void disableUIElement();
    void showProgress();
    void hideProgress();
    void productAdded();
    void showError(int resMsg);
    void maxValueError(int resMsg);

}
