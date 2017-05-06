package com.cins.daily.mvp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.LogInCallback;
import com.avos.avoscloud.SignUpCallback;
import com.cins.daily.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.cins.daily.R.id.login_findpasswords;
import static com.cins.daily.R.id.login_regist;

public class LoginInActivity extends AppCompatActivity {
    @BindView(R.id.login_layout)
    LinearLayout login;
    @BindView(R.id.regist_layout)
    LinearLayout regist;
    @BindView(login_regist)
    TextView tvRegiset;
    @BindView(login_findpasswords)
    TextView tvFind;
    @BindView(R.id.loginup_login)
    Button btnLogin;
    @BindView(R.id.regist_user)
    Button btnRegist;
    @BindView(R.id.loginup_username)
    EditText edtUsername;
    @BindView(R.id.loginup_password)
    EditText edtPassword;
    @BindView(R.id.login_close)
    ImageView imgClose;
    @BindView(R.id.regist_close)
    ImageView imgRegistClose;
    @BindView(R.id.regist_useremail)
    EditText edtRegistUseremail;
    @BindView(R.id.regist_username)
    EditText edtRegistUsername;
    @BindView(R.id.regist_password)
    EditText edtRegistpassword;
    @BindView(R.id.regist_password2)
    EditText edtRegistpassword2;


    private String username;
    private String password;
    private String usernameRegist;
    private String useremailRegist;
    private String userpasswordRegist;
    private String userpasswordRegist2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_in);
        ButterKnife.bind(this);
    }

    @OnClick({login_regist, login_findpasswords, R.id.loginup_login, R.id.login_close
    ,R.id.regist_close,R.id.regist_user})

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.loginup_login:
                username = edtUsername.getText().toString().trim();
                password = edtPassword.getText().toString().trim();
                if (TextUtils.isEmpty(username)||TextUtils.isEmpty(password)){
                    Toast.makeText(this, "用户名或密码不能输入为空", Toast.LENGTH_SHORT).show();

                }else{
                    AVUser.logInInBackground(username, password, new LogInCallback<AVUser>() {
                        @Override
                        public void done(AVUser avUser, AVException e) {
                            if (null==e){
                                Toast.makeText(LoginInActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                Intent intent =new Intent(LoginInActivity.this,NewsActivity.class);
                                startActivity(intent);
                                LoginInActivity.this.finish();
                            }else {
                                Toast.makeText(LoginInActivity.this, "用户名或密码错误", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }

                break;
            case login_regist:
                login.setVisibility(View.GONE);
                regist.setVisibility(View.VISIBLE);
                break;
            case login_findpasswords:
                break;
            case R.id.login_close:
                this.finish();
                break;
            case R.id.regist_close:
                regist.setVisibility(View.GONE);
                login.setVisibility(View.VISIBLE);
                break;
            case R.id.regist_user:
                usernameRegist = edtRegistUsername.getText().toString().trim();
                useremailRegist = edtRegistUseremail.getText().toString().trim();
                userpasswordRegist = edtRegistpassword.getText().toString().trim();
                userpasswordRegist2 = edtRegistpassword2.getText().toString().trim();
                if (TextUtils.isEmpty(useremailRegist)||TextUtils.isEmpty(usernameRegist)||TextUtils.isEmpty(userpasswordRegist)||TextUtils.isEmpty(userpasswordRegist2)){
                    Toast.makeText(this, "注册信息输入不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!userpasswordRegist.equals(userpasswordRegist2)){
                    Toast.makeText(this, "密码输入不一致，请重新输入", Toast.LENGTH_SHORT).show();
                }else {

                    AVUser user = new AVUser();
                    user.setUsername(usernameRegist);
                    user.setPassword(userpasswordRegist);
                    user.setEmail(useremailRegist);

                    user.signUpInBackground(new SignUpCallback() {
                        @Override
                        public void done(AVException e) {
                            if (e == null) {
                                Toast.makeText(LoginInActivity.this, "注册成功,请登录", Toast.LENGTH_SHORT).show();
                                regist.setVisibility(View.GONE);
                                login.setVisibility(View.VISIBLE);
                            } else {
                                // 失败的原因可能有多种，常见的是用户名已经存在。
                                Toast.makeText(LoginInActivity.this, "注册失败，请重新注册", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }


                break;
        }

    }

}
