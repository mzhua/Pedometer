package com.wonders.xlab.pedometer.base;

/**
 * Created by hua on 16/8/19.
 */
public interface BaseContract {
    interface View {
        void showToastMessage(String message);
    }

    interface Presenter {
        void onDestroy();
    }

    interface Model {
        interface Callback<T> {
            void onSuccess(T t);

            void onFail(DefaultException e);
        }

        void onDestroy();
    }
}
