package com.eric.school.fastrecycler.sign;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.eric.school.fastrecycler.R;
import com.eric.school.fastrecycler.tools.base.BaseActivity;
import com.eric.school.fastrecycler.tools.bean.FRUser;
import com.eric.school.fastrecycler.tools.user.UserEngine;
import com.eric.school.fastrecycler.tools.util.AndroidUtils;
import com.eric.school.fastrecycler.tools.util.Navigation;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

/**
 * @author 泽乾
 * createAt 2019/3/22 0022 17:22
 */
public class SignInActivity extends BaseActivity {

    private EditText mUsernameEditText;
    private EditText mPasswordEditText;
    private TextView mSignInButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        initView();
    }

    private void initView() {
        mUsernameEditText = findViewById(R.id.et_username);
        mPasswordEditText = findViewById(R.id.et_password);
        mSignInButton = findViewById(R.id.bt_sign_in);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingDialog();
                FRUser user = new FRUser();
                user.setUsername(mUsernameEditText.getText().toString().trim());
                user.setPassword(mPasswordEditText.getText().toString().trim());
                UserEngine.getInstance().signIn(user, new SaveListener<FRUser>() {
                    @Override
                    public void done(FRUser fitnessUser, BmobException e) {
                        dismissLoadingDialog();
                        if (e == null) {
                            Navigation.goToMapActivity(getContext());
                        } else {
                            AndroidUtils.showWarning(e.getMessage());
                        }
                    }
                });
            }
        });
    }

    @Override
    protected boolean needCheckLogin() {
        return false;
    }
}
