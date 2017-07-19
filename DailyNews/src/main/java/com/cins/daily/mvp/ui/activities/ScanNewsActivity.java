package com.cins.daily.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.widget.FrameLayout;
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

public class ScanNewsActivity extends BaseActivity {
    @BindView(R.id.fr_collect)
    FrameLayout frameLayout;
    @BindView(R.id.recycler_c)
    RecyclerView recyclerView;
    List<NewsSum> dataList;
    AVUser user;
    private String userObjectId;

    @Override
    public int getLayoutId() {
        return R.layout.activity_collection;
    }

    @Override
    public void initInjector() {
    }

    @Override
    public void initViews() {
        dataList=new ArrayList<>();
        user = AVUser.getCurrentUser();
        if (user != null) {
            userObjectId = user.getObjectId();
            getCollection();
        } else {
            Intent intentGet = getIntent();
            dataList = (List<NewsSum>) intentGet.getSerializableExtra("scanlist");
            if (dataList.size() > 0) {
                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                recyclerView.setLayoutManager(layoutManager);
                CollectinCCAdapter adapter = new CollectinCCAdapter(dataList);
                //为RecylcerView设置adapter
                recyclerView.setAdapter(adapter);
            } else {
                Toast.makeText(this, "没有浏览新闻", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    public void getCollection() {
        AVQuery<AVObject> query = new AVQuery<>("ScanNews");
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                AVObject.fetchAllInBackground(list, new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        NewsSum newsSummary;
                        for (AVObject av : list) {
                            AVUser belong = (AVUser) av.get("sbelong");
                            String belongid = belong.getObjectId();

                            if (!TextUtils.isEmpty(belongid) && belongid.equals(userObjectId)) {
                                newsSummary = new NewsSum();
                                newsSummary.setTitle((String) av.get("stitle"));
                                newsSummary.setImgsrc((String) av.get("simgurl"));
                                newsSummary.setPtime((String) av.get("sTime"));
                                newsSummary.setDigest((String) av.get("sdigist"));
                                dataList.add(newsSummary);
                            }

                        }
                        if (dataList.size() > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    LinearLayoutManager layoutManager = new LinearLayoutManager(ScanNewsActivity.this);
                                    recyclerView.setLayoutManager(layoutManager);
                                    CollectinCCAdapter adapter = new CollectinCCAdapter(dataList);
                                    //为RecylcerView设置adapter
                                    recyclerView.setAdapter(adapter);
                                }
                            });
                        }
                    }
                });

            }
        });
    }
}
