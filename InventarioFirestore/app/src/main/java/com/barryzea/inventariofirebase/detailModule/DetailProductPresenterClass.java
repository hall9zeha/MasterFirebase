package com.barryzea.inventariofirebase.detailModule;

import com.barryzea.inventariofirebase.common.pojo.Product;
import com.barryzea.inventariofirebase.detailModule.events.DetailProductEvent;
import com.barryzea.inventariofirebase.detailModule.model.DetailProductInteractor;
import com.barryzea.inventariofirebase.detailModule.model.DetailProductInteractorClass;
import com.barryzea.inventariofirebase.detailModule.view.DetailProductView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class  DetailProductPresenterClass implements DetailProductPresenter {
    private DetailProductInteractor mInteractor;
    private DetailProductView mView;

    public DetailProductPresenterClass(DetailProductView mView) {
        this.mView = mView;
        mInteractor=new DetailProductInteractorClass();

    }

    @Override
    public void onCreate() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        if(mView !=null){
            mView=null;
        }
    }

    @Override
    public void updateProduct(Product product) {
        if(setProgress()){
            mInteractor.updateProduct(product);
        }
    }

    private boolean setProgress() {
        if(mView != null){
            mView.showProgress();
            mView.disableUIDetailProduct();
            return true;
        }
        return false;
    }

    @Subscribe
    @Override
    public void onEventListener(DetailProductEvent event) {
        if(mView !=null){
            mView.hideProgress();
            mView.enableUIDetailProduct();
            switch(event.getTypeEvent()){
                case DetailProductEvent.UPDATE_SUCCESS:
                    mView.updateSuccess();
                    break;
                case DetailProductEvent.ERROR_SERVER:
                    mView.updateError();
                    break;
            }
        }
    }
}
