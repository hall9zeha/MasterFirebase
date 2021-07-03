package com.barryzea.firechat.LoginModule.view;

import android.content.Intent;

public interface LoginView {
    void showProgress();
    void hideProgress();
    void openMainActivity();
    void openUILogin();
    void showLoginSuccessFull(Intent data);
    void showMessageStarting();
    void showError(int resMsg);


}
