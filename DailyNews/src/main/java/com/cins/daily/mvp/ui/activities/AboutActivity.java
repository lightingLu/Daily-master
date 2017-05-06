package com.cins.daily.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.FrameLayout;

import com.cins.daily.R;
import com.cins.daily.mvp.ui.activities.base.BaseActivity;
import com.vansuita.materialabout.builder.AboutMe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Create by threelu  on 2017/2/15.
 */

public class AboutActivity extends BaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        loadAboutMe();
    }

    private void loadAboutMe() {
        final FrameLayout flHolder = (FrameLayout) findViewById(R.id.aboutme);

        flHolder.addView(
                AboutMe.with(this)
                        .setAppIcon(R.mipmap.ic_launcher)
                        .setAppName(R.string.app_name)
                        .setPhoto(R.mipmap.profile_picture)
                        .setCover(R.mipmap.profile_cover)
                        .setLinksAnimated(false)
                        .setDividerDashGap(13)
                        .setName("Three Lu")
                        .setSubTitle("ludaguang0917@163.com")
                        .setLinksColumnsCount(4)
                        .setBrief("I'm warmed of mobile technologies.")
                        .addIntroduceAction((Intent) null)
                        .addHelpAction((Intent) null)
                        .addChangeLogAction((Intent) null)
                        .addRemoveAdsAction((Intent) null)
                        .addDonateAction((Intent) null)
                        .build());
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_about;
    }

    @Override
    public void initInjector() {
    }

    @Override
    public void initViews() {

    }
}
