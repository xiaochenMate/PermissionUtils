package com.lxc.permissionutil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;

import java.util.ArrayList;

/**
 * Created by liuxc on 2017/5/19
 */

public class PermissionUtils extends Activity {
    private Activity activity;
    private int permissionCode = 0x999;
    private OnPermissionResultListener listener;

    public PermissionUtils(Activity activity, OnPermissionResultListener listener) {
        this.activity = activity;
        this.listener = listener;
    }

    public void getPermission(String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> lacks = new ArrayList<>();
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(activity, permission) != PackageManager.PERMISSION_GRANTED) {
                    lacks.add(permission);
                }
            }
            if (lacks.isEmpty())
                listener.onPermissionSuccess();
            else {
                String[] lacksPermissions = new String[lacks.size()];
                lacksPermissions = lacks.toArray(lacksPermissions);
                activity.requestPermissions(lacksPermissions, permissionCode);
            }
        }
    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == permissionCode) {
            if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                listener.onPermissionSuccess();
            } else {
                listener.onPermissionFailed();
            }
        }
    }

    public interface OnPermissionResultListener {
        void onPermissionSuccess();

        void onPermissionFailed();
    }

    public static void startAppSettings(Activity context, int requestCode) {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        startForResult(context, intent, requestCode);
    }

    private static void startForResult(Object object, Intent intent, int requestCode) {
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof android.support.v4.app.Fragment) {
            ((android.support.v4.app.Fragment) object).startActivityForResult(intent, requestCode);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).startActivityForResult(intent, requestCode);
        }
    }
}
