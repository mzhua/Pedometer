package com.wonders.xlab.pedometer.base;

/**
 * Created by hua on 16/8/19.
 */
public abstract class MVPActivity<P extends BaseContract.Presenter> extends BaseActivity {

    public abstract P getPresenter();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseContract.Presenter presenter = getPresenter();
        if (presenter != null) {
            presenter.onDestroy();
            presenter = null;
        }
    }
}
