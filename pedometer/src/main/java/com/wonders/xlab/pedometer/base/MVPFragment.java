package com.wonders.xlab.pedometer.base;

/**
 * Created by hua on 16/8/19.
 */
public abstract class MVPFragment<P extends BaseContract.Presenter> extends BaseFragment {

    public abstract P getPresenter();

    /**
     * for the step broadcast to trigger view refresh
     */
    public abstract void refreshView();

    protected abstract boolean hasViewCreated();

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
