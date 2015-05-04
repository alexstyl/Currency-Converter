package com.alexstyl.currency.util;

import android.database.Cursor;
import android.os.Build;
import android.util.Log;

import com.alexstyl.currency.BuildConfig;

import java.util.List;
import java.util.Map;

/**
 * Debug logging. Wrapper class of {@link Log}. Will only print to the log if
 * DeLog.{@link #ALLOW_LOGS} is set to true
 *
 * @author Alex
 */
public class DeLog {

    private static final boolean ALLOW_LOGS = BuildConfig.DEBUG; // don't print logs if we are in production

    public static void d(String tag, String string) {
        if (ALLOW_LOGS && string != null)
            Log.d(tag, string);
    }

    public static void w(String tag, String string) {
        if (ALLOW_LOGS && string!=null)
            Log.w(tag, string);
    }

    public static void e(String tag, String string) {
        if (ALLOW_LOGS && string!=null)
            Log.e(tag, string);
    }

    public static void i(String tag, String string) {
        if (ALLOW_LOGS && string!=null)
            Log.i(tag, string);
    }

    public static void v(String tag, String string) {
        if (ALLOW_LOGS && string!=null)
            Log.v(tag, string);

    }

    public static void log(Exception e) {
        log(e, false);
    }

    public static void log(Exception e, boolean sendToBugsense) {
        if (ALLOW_LOGS && e != null) {
            e.printStackTrace();
//            if(sendToBugsense){
//                BugsenseTracking.track(e);
//            }
        }

    }

    public static <T> void d(String tag, List<T> list) {
        if (list == null) {
            Log.e(tag, "Given list was NULL");
            return;
        }
        if (list.isEmpty()) {
            Log.e(tag, "Given list was empty");
            return;
        }
        StringBuilder str = new StringBuilder();
        for (T o : list) {
            str.append(String.valueOf(o) + "\n");
        }

        Log.d(tag, str.toString());
    }

    /**
     * Prints the content of the given cursor.
     * <p>
     * This method will try to move the cursor to its starting position when the
     * logging is done
     * </p>
     *
     * @param tag    The Tag to log the message under
     * @param cursor The cursor to log
     */
    public static void d(String tag, Cursor cursor) {
        if (cursor == null) {
            Log.e(tag, "Given cursor was NULL");
            return;
        }

        if (cursor.getCount() <= 0) {
            Log.w(tag, "Given cursor was empty");
            return;

        }
        int startingPos = cursor.getPosition();
        int colCount = cursor.getColumnCount();
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            StringBuilder d = new StringBuilder();
            for (int i = 0; i < colCount; i++) {
                d.append(cursor.getColumnName(i) + ": ");
                try {
                    d.append(cursor.getString(i) + "\n");
                } catch (Exception e) {
                    d.append("ERROR \n");
                }
            }
            Log.d(tag, d.toString());

        }

        if (!cursor.moveToPosition(startingPos)) {
            Log.w(tag, "Failed to move cursor to starting position("
                    + startingPos + ")");
        }
        return;
    }

    public static <K, T> void d(String tag, Map<K, T> map) {
        if (map == null) {
            Log.e(tag, "Given map was NULL");
            return;
        }
        if (map.isEmpty()) {
            Log.e(tag, "Given map was empty");
            return;
        }

        for (K k : map.keySet()) {
            T t = map.get(k);
            Log.d(tag, String.valueOf(k) + " " + String.valueOf(t));
        }
    }

}
