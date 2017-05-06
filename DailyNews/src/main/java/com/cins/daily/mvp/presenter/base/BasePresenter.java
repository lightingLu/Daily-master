package com.cins.daily.mvp.presenter.base;

import android.support.annotation.NonNull;

import com.cins.daily.mvp.view.base.BaseView;

/**
 * Create by threelu on 2017/1/16.
 */

public interface BasePresenter {
//    void onResume();

    void onCreate();

    void attachView(@NonNull BaseView view);

    void onDestroy();
}
