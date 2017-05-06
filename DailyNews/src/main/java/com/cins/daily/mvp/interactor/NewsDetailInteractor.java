package com.cins.daily.mvp.interactor;

import com.cins.daily.listener.RequestCallBack;
import rx.Subscription;

/**
 * Create by threelu on 2017/1/18.
 */

public interface NewsDetailInteractor<T> {
    Subscription loadNewsDetail(RequestCallBack<T> callBack, String postId);
}
