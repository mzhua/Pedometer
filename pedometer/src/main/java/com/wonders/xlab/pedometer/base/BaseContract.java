package com.wonders.xlab.pedometer.base;

import android.support.annotation.NonNull;

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

            void onFail(@NonNull DefaultException e);
        }

        void onDestroy();
    }
}
