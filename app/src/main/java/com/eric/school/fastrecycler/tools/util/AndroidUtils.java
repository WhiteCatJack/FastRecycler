package com.eric.school.fastrecycler.tools.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.Base64;
import android.widget.Toast;

import com.eric.school.fastrecycler.R;

import java.io.ByteArrayOutputStream;

import cn.bmob.v3.Bmob;
import es.dmoral.toasty.Toasty;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/1 0001 20:06
 */
public class AndroidUtils {
    public static Context getApplicationContext() {
        return Bmob.getApplicationContext();
    }

    private static String getString(@StringRes int id) {
        return getApplicationContext().getResources().getString(id);
    }

    public static void showUnknownErrorToast() {
        showError(R.string.unknown_error);
    }

    public static void showToast(String content) {
        Toasty.normal(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(@StringRes int contentId) {
        showToast(getString(contentId));
    }

    public static void showWarning(String content) {
        Toasty.warning(getApplicationContext(), content, Toast.LENGTH_SHORT, true).show();
    }

    public static void showWarning(@StringRes int contentId) {
        showWarning(getString(contentId));
    }

    public static void showError(String content) {
        Toasty.error(getApplicationContext(), content, Toast.LENGTH_SHORT, true).show();
    }

    public static void showError(@StringRes int contentId) {
        showError(getString(contentId));
    }

    public static void showAlert(Context context, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(content);
        builder.create().show();
    }

    public static void showAlert(Context context, @StringRes int contentId) {
        showAlert(context, getString(contentId));
    }
}
