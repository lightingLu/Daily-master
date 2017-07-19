package com.cins.daily.mvp.ui.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.cins.daily.R;
import com.cins.daily.mvp.entity.NewsSum;
import com.cins.daily.mvp.ui.activities.base.BaseActivity;
import com.cins.daily.mvp.ui.adapter.CollectinCCAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CollectionActivity extends BaseActivity {
    @BindView(R.id.recycler_scan)
    RecyclerView srecyclerView;
    List<NewsSum> dataList;
    AVUser user;
    private String userid;


    @Override
    public int getLayoutId() {
        return R.layout.activity_scan;
    }

    @Override
    public void initInjector() {

    }

    @Override
    public void initViews() {
        dataList = new ArrayList<>();
        user = AVUser.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "当前用户未登录", Toast.LENGTH_SHORT).show();
        } else {
            userid = user.getObjectId();
            getCollection();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void getCollection() {
        AVQuery<AVObject> query = new AVQuery<>("Article");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                AVObject.fetchAllInBackground(list, new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        NewsSum newsSummary;
                        for (AVObject av : list) {
                            AVUser belong = (AVUser) av.get("abelong");
                            String belongid = belong.getObjectId();
                            if (!TextUtils.isEmpty(belongid) && belongid.equals(userid)) {
                                newsSummary = new NewsSum();
                                newsSummary.setTitle((String) av.get("atitle"));
                                newsSummary.setImgsrc((String) av.get("aimgurl"));
                                newsSummary.setPtime((String) av.get("atime"));
                                newsSummary.setDigest((String) av.get("adigist"));
                                dataList.add(newsSummary);
                            }

                        }
                        if (dataList.size() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(CollectionActivity.this);
                                    srecyclerView.setLayoutManager(layoutManager);
                                    CollectinCCAdapter adapter = new CollectinCCAdapter(dataList);
                                    //为RecylcerView设置adapter
                                    srecyclerView.setAdapter(adapter);
                                }
                            });
                        } else {
                            Toast.makeText(CollectionActivity.this, "当前用户没有收藏新闻", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

            }
        });
    }
}
