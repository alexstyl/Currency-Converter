package com.alexstyl.currency.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;


/**
 * This class contains static utility methods.
 */
public class Utils {

    public static final long NANO_IN_MILLIS = 1000000;


    // Prevents instantiation.
    private Utils() {
    }

    /**
     * Uses static final constants to detect if the device's platform version is
     * ICS or later.
     */
    public static boolean hasICS() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    public static boolean hasJellyBeanMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
    }


    /**
     * Emulate the as operator of C#. If the object can be cast to type it will
     * be casted. If not this returns null.
     */
    public static <T> T as(Class<T> type, Object o) {
        if (type.isInstance(o)) {
            return type.cast(o);
        }
        return null;
    }

    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    }


    public static boolean isRunningKitKat() {
        return Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT;
    }

    /**
     * Uses static final constants to detect if the device's platform version is
     * Lollipop or later.
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    /**
     * Sets the visiblity of the view
     *
     * @param view       The view to hide/show
     * @param setVisible True will make the view VISIBLE, else GONE
     */
    public static void toggleViewVisibility(View view, boolean setVisible) {
        if (view == null)
            return;
        int visibility = View.GONE;
        if (setVisible) {
            visibility = View.VISIBLE;
        }
        view.setVisibility(visibility);

    }

    /**
     * Checks if the device is currently connected to the webz!
     *
     * @param context The context to use
     * @return Whether the device is online or not... duh
     */
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        }
        return ni.isConnectedOrConnecting();
    }


}
