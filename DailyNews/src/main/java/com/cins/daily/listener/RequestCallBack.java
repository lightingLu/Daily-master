package com.cins.daily.listener;

/**
 * Create by threelu on 2017/1/17.
 */

public interface RequestCallBack<T> {

    void beforeRequest();

    void success(T data);

    void onError(String errorMsg);
}
