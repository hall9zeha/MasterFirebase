package com.barryzea.inventariofirebase.MainModule;

import android.widget.Toast;

import com.barryzea.inventariofirebase.MainModule.events.MainEvent;
import com.barryzea.inventariofirebase.MainModule.model.MainInteractor;
import com.barryzea.inventariofirebase.MainModule.model.MainInteractorClass;
import com.barryzea.inventariofirebase.MainModule.view.MainView;
import com.barryzea.inventariofirebase.common.pojo.Product;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class MainPresenterClass  implements MainPresenter{
    private MainView mView;
    private MainInteractor mInteractor;

    public MainPresenterClass(MainView mView) {
        this.mView = mView;
        this.mInteractor= new MainInteractorClass();
    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        mInteractor.unsubscribeToProducts();
    }

    @Override
    public void onResume() {
        mInteractor.subscribeToProducts();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mView=null;
    }

    @Override
    public void remove(Product product) {
        if(setProgress()){
            mInteractor.removeProduct(product);

        }
    }

    @Subscribe
    @Override
    public void onEventListener(MainEvent event) {
        if(mView !=null){
            mView.hideProgress();
            switch (event.getTypeEvent()){
                case MainEvent.SUCCESS_ADD:
                    mView.add(event.getProduct());
                    break;
                case MainEvent.SUCCESS_UPDATE:
                    mView.update(event.getProduct());

                    break;
                case MainEvent.SUCCESS_REMOVE:
                    mView.remove(event.getProduct());
                    break;
                case MainEvent.ERROR_SERVER:
                    mView.errorMsg(event.getResMsg());
                    break;
                case MainEvent.ERROR_REMOVE:
                    mView.removeFail();
                    break;
            }
        }
    }

    private boolean setProgress() {
        if(mView!=null){
            mView.showProgress();
            return true;
        }
        return false;
    }


}
