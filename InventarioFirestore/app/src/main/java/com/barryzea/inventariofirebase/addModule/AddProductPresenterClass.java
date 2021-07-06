package com.barryzea.inventariofirebase.addModule;

import com.barryzea.inventariofirebase.addModule.events.AddProductEvent;
import com.barryzea.inventariofirebase.addModule.model.AddProductInteractor;
import com.barryzea.inventariofirebase.addModule.model.AddProductInteractorClass;
import com.barryzea.inventariofirebase.addModule.view.AddProductView;
import com.barryzea.inventariofirebase.common.pojo.Product;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

public class AddProductPresenterClass  implements AddProductPresenter{

    private AddProductView mView;
    private AddProductInteractor mInteractor;

    public AddProductPresenterClass(AddProductView mView) {
        this.mView = mView;
        this.mInteractor = new AddProductInteractorClass();
    }

    @Override
    public void onShow() {
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        mView=null;
    }

    @Override
    public void addProduct(Product product) {
        if(setProgress()){
            mInteractor.addProduct(product);
        }
    }

    private boolean setProgress() {
        if(mView != null){
            mView.disableUIElement();
            mView.showProgress();
            return true;
        }
        return false;
    }

    @Subscribe
    @Override
    public void onEventListener(AddProductEvent event) {

        if(mView != null){
            mView.hideProgress();
            mView.enableUIElement();
            switch(event.getTypeEvent()){
                case AddProductEvent.SUCCESS_ADD:
                    mView.productAdded();
                    break;
                case AddProductEvent.ERROR_MAX_VALUE:
                    mView.maxValueError(event.getResMsg());
                    break;
                case AddProductEvent.ERROR_SERVER:
                    mView.showError(event.getResMsg());
                    break;

            }
        }
    }
}
