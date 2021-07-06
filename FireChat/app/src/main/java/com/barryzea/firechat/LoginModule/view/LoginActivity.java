package com.barryzea.firechat.LoginModule.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.barryzea.firechat.LoginModule.LoginPresenter;
import com.barryzea.firechat.LoginModule.LoginPresenterClass;
import com.barryzea.firechat.MainModule.view.MainActivity;
import com.barryzea.firechat.R;
import com.barryzea.firechat.common.Constants;
import com.barryzea.firechat.databinding.ActivityLoginBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements LoginView{
    private ActivityLoginBinding binding;

    public static final int RC_SIGN_IN = 21;
    private LoginPresenter mPresenter;
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mPresenter = new LoginPresenterClass(this);
        mPresenter.onCreate();
        mPresenter.getStatusAuth();
        mFirebaseAnalytics= FirebaseAnalytics.getInstance(this);


    }

    @Override
    protected void onResume() {
        super.onResume();
        mPresenter.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mPresenter.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,  Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.result(requestCode, resultCode, data);
    }
    /*
    * LoginView interface
    * */

    @Override
    public void showProgress() {
        binding.progressBarLogin.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        binding.progressBarLogin.setVisibility(View.GONE);
    }

    @Override
    public void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void openUILogin() {
        AuthUI.IdpConfig googleIdp= new AuthUI.IdpConfig.GoogleBuilder().build();

        startActivityForResult(AuthUI.getInstance()
                    .createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setTosAndPrivacyPolicyUrls("www.privacy.com","www.policy.com")
                .setAvailableProviders(Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build(),
                        googleIdp))
                .setTheme(R.style.BlueTheme)
                .setLogo(R.mipmap.ic_launcher)
                .build(),RC_SIGN_IN
        );
    }

    @Override
    public void showLoginSuccessFull(Intent data) {
        IdpResponse response= IdpResponse.fromResultIntent(data);
        String emailUser="";
        String dominio;
        if(response != null){
            emailUser= response.getEmail();
            if(emailUser!= null && emailUser.contains("@")) {
                dominio=emailUser.substring(emailUser.indexOf("@") +1);
                mFirebaseAnalytics.setUserProperty(Constants.EMAIL_PROVIDER, dominio);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, null);
                //email_provider
            }
        }
        Toast.makeText(this, getString(R.string.login_message_success, emailUser), Toast.LENGTH_LONG).show();


    }

    @Override
    public void showMessageStarting() {
        binding.textViewLoginMessage.setText(R.string.login_message_loading);
    }

    @Override
    public void showError(int resMsg) {
        Toast.makeText(this, resMsg, Toast.LENGTH_LONG).show();
    }
}