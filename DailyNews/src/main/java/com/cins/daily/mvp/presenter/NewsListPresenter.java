package com.cins.daily.mvp.presenter;

import com.cins.daily.mvp.presenter.base.BasePresenter;

/**
 * Create by threelu on 2017/1/17.
 */

public interface NewsListPresenter extends BasePresenter {
    void setNewsTypeAndId(String newsType, String newsId);

    void refreshData();

    void loadMore();
}
