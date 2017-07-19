package com.cins.daily.listener;

import android.view.View;

/**
 * Created by light on 2017/6/6.
 */

public interface OnItemListener<T> {
        void onItemClickLister(View view, int position, T t);

}
