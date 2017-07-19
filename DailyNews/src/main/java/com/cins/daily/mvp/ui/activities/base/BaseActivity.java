package com.cins.daily.mvp.ui.activities.base;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.SaveCallback;
import com.cins.daily.App;
import com.cins.daily.R;
import com.cins.daily.annotation.BindValues;
import com.cins.daily.common.Constants;
import com.cins.daily.di.component.ActivityComponent;
import com.cins.daily.di.component.DaggerActivityComponent;
import com.cins.daily.di.module.ActivityModule;
import com.cins.daily.mvp.entity.NewsSum;
import com.cins.daily.mvp.presenter.base.BasePresenter;
import com.cins.daily.mvp.ui.activities.AboutActivity;
import com.cins.daily.mvp.ui.activities.CollectionActivity;
import com.cins.daily.mvp.ui.activities.LoginInActivity;
import com.cins.daily.mvp.ui.activities.NewsActivity;
import com.cins.daily.mvp.ui.activities.NewsDetailActivity;
import com.cins.daily.mvp.ui.activities.ScanNewsActivity;
import com.cins.daily.utils.MyUtils;
import com.cins.daily.utils.NetUtil;
import com.cins.daily.utils.SharedPreferencesUtil;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.socks.library.KLog;
import com.squareup.leakcanary.RefWatcher;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import rx.Subscription;

/**
 * Created by threelu on 2017/1/16.
 */
public abstract class BaseActivity<T extends BasePresenter> extends AppCompatActivity {
    protected ActivityComponent mActivityComponent;
    private boolean mIsChangeTheme;
    public Activity mActivity;
    public View headerView;
    public ImageView userhead;
    List<NewsSum> scanlist;
    public TextView loginout;
    public TextView login;

    public ActivityComponent getActivityComponent() {
        return mActivityComponent;
    }

    public static final int PHOTO_REQUEST_CAREMA = 1;// 拍照
    public static final int PHOTO_REQUEST_GALLERY = 2;// 从相册中选择
    public static final int PHOTO_REQUEST_CUT = 3;// 结果
    private static final String PHOTO_FILE_NAME = "temp_photo.jpg";
    private File tempFile;
    private WindowManager mWindowManager = null;
    private View mNightView = null;
    private boolean mIsAddedView;
    protected T mPresenter;
    protected boolean mIsHasNavigationView;
    private DrawerLayout mDrawerLayout;
    private Class mClass;

    public abstract int getLayoutId();

    public abstract void initInjector();

    public abstract void initViews();

    protected Subscription mSubscription;
    protected NavigationView mBaseNavView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        scanlist = new ArrayList<>();
        mActivity = this;
        KLog.i(getClass().getSimpleName());
        initAnnotation();
        //检查网恋
        NetUtil.isNetworkErrThenShowMsg();
        //创建容器
        initActivityComponent();
        //设置沉浸氏通知栏
        setStatusBarTranslucent();
        //布局引用
        int layoutId = getLayoutId();
        setContentView(layoutId);
        //绑定容器和activity
        initInjector();
        //butterknife的初始化
        ButterKnife.bind(this);
        initToolBar();
        initViews();
        if (mIsHasNavigationView) {
            //
            // 初始化Drawerlayout,做一些侧边栏的操作
            initDrawerLayout();
        }
        if (mPresenter != null) {
            mPresenter.onCreate();
        }
        //白天的主题
        initNightModeSwitch();
    }


    /*
        * 剪切图片
        */
    private void crop(Uri uri) {
        // 裁剪图片意图
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        // 裁剪框的比例，1：1
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // 裁剪后输出图片的尺寸大小
        intent.putExtra("outputX", 250);
        intent.putExtra("outputY", 250);
        intent.putExtra("outputFormat", "JPEG");// 图片格式
        intent.putExtra("noFaceDetection", true);// 取消人脸识别
        intent.putExtra("return-data", true);
        // 开启一个带有返回值的Activity，请求码为PHOTO_REQUEST_CUT
        startActivityForResult(intent, PHOTO_REQUEST_CUT);
    }

    /*
         * 判断sdcard是否被挂载
         */
    private boolean hasSdcard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_REQUEST_GALLERY) {
            // 从相册返回的数据
            if (data != null) {
                // 得到图片的全路径
                Uri uri = data.getData();
                crop(uri);
            }

        } else if (requestCode == PHOTO_REQUEST_CAREMA) {
            // 从相机返回的数据
            if (hasSdcard()) {
                crop(Uri.fromFile(tempFile));
            } else {
                Toast.makeText(this, "未找到存储卡，无法存储照片！", Toast.LENGTH_SHORT).show();
            }

        } else if (requestCode == PHOTO_REQUEST_CUT) {//剪切图片
            // 从剪切图片返回的数据
            if (data != null) {
                Bitmap bitmap = data.getParcelableExtra("data");
                this.userhead.setImageBitmap(bitmap);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                final AVFile files = new AVFile("usehead.png", baos.toByteArray());
                files.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(AVException e) {
                        if (null == e) {
                            String url = files.getUrl();
                            AVUser.getCurrentUser().put("userheadurl", url);
                            AVUser.getCurrentUser().saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (null == e) {
                                        Log.v(Constants.LOGSTRING, "上传头像成功");
                                    }
                                }
                            });
                        }
                    }
                });
            }
            try {

                // 将临时文件删除
                tempFile.delete();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * 字节数组转bitmap
     *
     * @param bytes
     * @param opts
     * @return
     */
    public static Bitmap getPicFromBytes(byte[] bytes,
                                         BitmapFactory.Options opts) {
        if (bytes != null) {
            if (opts != null) {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length,
                        opts);
            } else {
                return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            }
        }
        return null;
    }


    private void initAnnotation() {
        if (getClass().isAnnotationPresent(BindValues.class)) {
            BindValues annotation = getClass().getAnnotation(BindValues.class);
            mIsHasNavigationView = annotation.mIsHasNavigationView();
        }
    }


    private void initActivityComponent() {
        mActivityComponent = DaggerActivityComponent.builder()
                .applicationComponent(((App) getApplication()).getApplicationComponent())
                .activityModule(new ActivityModule(this))
                .build();
    }


    private void initToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void initNightModeSwitch() {
        if (this instanceof NewsActivity) {
            MenuItem menuNightMode = mBaseNavView.getMenu().findItem(R.id.nav_night_mode);
            SwitchCompat dayNightSwitch = (SwitchCompat) MenuItemCompat
                    .getActionView(menuNightMode);
            setCheckedState(dayNightSwitch);
            setCheckedEvent(dayNightSwitch);
        }
    }

    private void setCheckedState(SwitchCompat dayNightSwitch) {
        boolean isNight = SharedPreferencesUtil.getBoolean(this, Constants.ISNIGHT, false);
        if (isNight) {
            dayNightSwitch.setChecked(true);
        } else {
            dayNightSwitch.setChecked(false);
        }
    }

    private void setCheckedEvent(SwitchCompat dayNightSwitch) {
        dayNightSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferencesUtil.setBoolean(mActivity, Constants.ISNIGHT, true);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    SharedPreferencesUtil.setBoolean(mActivity, Constants.ISNIGHT, false);
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                mIsChangeTheme = true;
                mDrawerLayout.closeDrawer(GravityCompat.START);
            }
        });
    }

    private void initDrawerLayout() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        NavigationView navView = (NavigationView) findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        if (navView != null) {
            navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.nav_news:
                            Log.d(Constants.LOGSTRING, "新闻");
                            mClass = NewsActivity.class;
                            break;
                        //TODO 此处是收藏和浏览记录相关的点击操作
                        case R.id.nav_collection:
//                            mClass = PhotoActivity.class;
                            mClass = ScanNewsActivity.class;

                            break;
                        case R.id.nav_scan:
                            mClass = CollectionActivity.class;
                            break;
                        case R.id.nav_about:
                            mClass = AboutActivity.class;
                            break;
                        case R.id.nav_night_mode:
                            break;

                    }
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    return false;
                }
            });
        }

        headerView = navView.getHeaderView(0);
        login = (TextView) headerView.findViewById(R.id.username_tv);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AVUser.getCurrentUser()!=null){
                    Toast.makeText(BaseActivity.this, "当前用户已经登录", Toast.LENGTH_SHORT).show();
                }else {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                    Intent intent = new Intent(BaseActivity.this, LoginInActivity.class);
                    startActivity(intent);
                }

            }
        });
        loginout = (TextView) headerView.findViewById(R.id.user_loginout);
        loginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(GravityCompat.START);
                if (AVUser.getCurrentUser() != null) {
                    AVUser.logOut();// 清除缓存用户对象
                    if (AVUser.getCurrentUser()==null){
                        loginout.setText("逝者如斯夫，不舍昼夜");
                        login.setText("点击登录");
                    }
                }
            }
        });
        mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (mClass != null) {
                    if (mClass == ScanNewsActivity.class) {
                        Intent intent = new Intent(BaseActivity.this, mClass);
                        if (AVUser.getCurrentUser() == null) {
                            intent.putExtra("scanlist", (Serializable) scanlist);
                        }
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(BaseActivity.this, mClass);
                        // 此标志用于启动一个Activity的时候，若栈中存在此Activity实例，则把它调到栈顶。不创建多一个
//                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(intent);
                    }
                    overridePendingTransition(0, 0);//activity退出的时候实现动画淡入淡出的效果
                    mClass = null;
                }
                //主题切换
                if (mIsChangeTheme) {
                    mIsChangeTheme = false;
                    getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
                    recreate();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mIsHasNavigationView && mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    //colorPrimaryDark
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucent() {
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                !(this instanceof NewsDetailActivity /*|| this instanceof PhotoActivity
                        || this instanceof PhotoDetailActivity*/))
                || (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT
                && this instanceof NewsDetailActivity)) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if (mIsHasNavigationView) {
            overridePendingTransition(0, 0);
        }
//        getWindow().getDecorView().invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            } else {
                finish();
            }
        } else if (id == R.id.action_day_night_yes) {
            int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                SharedPreferencesUtil.setBoolean(mActivity, Constants.ISNIGHT, false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            } else {
                SharedPreferencesUtil.setBoolean(mActivity, Constants.ISNIGHT, true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            //调用 recreate(); 使设置生效
            getWindow().setWindowAnimations(R.style.WindowAnimationFadeInOut);
            recreate();
            return true;
        } else if (id == R.id.action_settings) {

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mIsHasNavigationView) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = App.getRefWatcher(this);
        refWatcher.watch(this);

        if (mPresenter != null) {
            mPresenter.onDestroy();
        }
        //removeNightModeMask();
        MyUtils.cancelSubscription(mSubscription);
        MyUtils.fixInputMethodManagerLeak(this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void setScanSummery(NewsSum summary) {
        scanlist.add(summary);
    }

    public List<NewsSum> getScanSummery() {
        return scanlist;
    }
}
