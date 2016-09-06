package com.wonders.xlab.pedometer.base;

/**
 * Created by hua on 16/8/19.
 */
public abstract class MVPFragment<P extends BaseContract.Presenter> extends BaseFragment {

    public abstract P getPresenter();

    @Override
    public void onDestroy() {
        super.onDestroy();
        BaseContract.Presenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onDestroy();
            presenter = null;
        }
    }
}
