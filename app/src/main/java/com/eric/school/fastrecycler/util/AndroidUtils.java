package com.eric.school.fastrecycler.util;

import android.content.Context;
import android.widget.Toast;

import com.eric.school.fastrecycler.R;


import cn.bmob.v3.Bmob;
import es.dmoral.toasty.Toasty;

/**
 * Description.
 *
 * @author Joi
 * createAt 2019/4/1 0001 20:06
 */
public class AndroidUtils {
    public static Context getApplicationContext() {
        return Bmob.getApplicationContext();
    }

    public static void showUnknownErrorToast() {
        Toasty.error(getApplicationContext(), R.string.unknown_error, Toast.LENGTH_SHORT, true).show();
    }

    public static void showToast(String content) {
        Toasty.normal(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
    }
}
