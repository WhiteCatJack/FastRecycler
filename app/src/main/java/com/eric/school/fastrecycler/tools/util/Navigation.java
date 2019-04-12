package com.eric.school.fastrecycler.tools.util;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.eric.school.fastrecycler.map.MapActivity;
import com.eric.school.fastrecycler.sign.SignInActivity;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/1 0001 14:37
 */
public class Navigation {
    private static void noExtraNavigation(Context context, Class target) {
        if (context == null) {
            return;
        }
        context.startActivity(new Intent(context, target));
    }

    public static void goToSignInActivity(@NonNull Context context) {
        noExtraNavigation(context, SignInActivity.class);
    }

    public static void goToMapActivity(@NonNull Context context) {
        noExtraNavigation(context, MapActivity.class);
    }
}
