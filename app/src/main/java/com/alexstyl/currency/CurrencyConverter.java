package com.alexstyl.currency;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.alexstyl.currency.util.DeLog;
import com.alexstyl.currency.util.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by alexstyl on 12/02/15.
 */
public class CurrencyConverter {


    private static final String FILENAME_CURRENCIES = "currencies.dat";
    private Context mContext;

    private CurrencyConverter(Context context) {
        mContext = context.getApplicationContext();
        mCurrencies = initialiseCurrencies();

    }

    /**
     * Returns any previously stored currency file saved to disk. If not found, the currency file from the Resources is returned instead.
     */
    private JSONObject initialiseCurrencies() {
        JSONObject curs = loadCurrenciesfromDisk();
        if (curs == null) {
            return loadCurrencies(mContext);
        }
        return curs;
    }

    private final Set<CurrencyChangeListener> mListeners = new HashSet<CurrencyChangeListener>(1);
    private final JSONObject mCurrencies;

    private void notifyListeners(String from, String to, Double value) {
        synchronized (mListeners) {
            for (CurrencyChangeListener l : mListeners) {
                l.onCurrencyChanged(from, to, value);
            }
        }
    }

    public void addCurrencyChangeListener(CurrencyChangeListener l) {
        synchronized (mListeners) {
            mListeners.add(l);
        }
    }

    public void removeCurrencyChangeListener(CurrencyChangeListener l) {
        synchronized (mListeners) {
            mListeners.remove(l);
        }
    }

    public interface CurrencyChangeListener {

        /**
         * Called whenever the value of a currency is changed.
         *
         * @param value The new value
         */
        void onCurrencyChanged(String from, String to, Double value);
    }

    public static CurrencyConverter getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new CurrencyConverter(context);
        }
        return sInstance;
    }

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String VALUE = "value";
    private static final String TIMESTAMP = "time";
    private static final long VALUE_VALIDITY = DateUtils.DAY_IN_MILLIS;
    private static CurrencyConverter sInstance;

    /**
     * Converts the given value from one currency to the other. By calling this method, the CurrencyConverter will try to update the values
     * for the specific conversion, if the device is online and the value is no longer valid (more than {@linkplain #VALUE_VALIDITY})ms time
     *
     * @param from  The currency to convert from
     * @param to    The currency to convert to
     * @param value The value to convert
     * @return The converted value
     */
    public Double convert(String from, String to, String value) {
        if (TextUtils.isEmpty(value)) {
            return 0d;
        }
        try {
            JSONObject fromJSON = (JSONObject) mCurrencies.get(from);
            JSONObject toJSON = (JSONObject) fromJSON.get(to);

            long timestamp = toJSON.getLong(TIMESTAMP);

            if (SystemClock.elapsedRealtime() - timestamp > VALUE_VALIDITY) {
                if (Utils.isOnline(mContext)) {
                    updateCurrencyfor(from, to);
                } else {
                    DeLog.e(TAG, "Tried to update value of " + from + "->" + to + " but no connection found");
                }
            } else
                DeLog.e(TAG, "No need for update");

            return toJSON.getDouble(VALUE) * Double.valueOf(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return 0d;
    }

    /**
     * Fetches the values for the requested currency from the server asynchronously
     *
     * @param from
     * @param to
     */
    private void updateCurrencyfor(final String from, final String to) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                DeLog.d(TAG, "Requesting currency for " + from + "->" + to);
                String callUrl = String.format(Locale.US, API_CALL, from, to, "1");
                String jsonResponse = connect(callUrl);
                try {
                    if (jsonResponse == null) {
                        DeLog.e(TAG, "Got null JSON response!");
                        return;
                    }
                    DeLog.d(TAG, jsonResponse);
                    JSONObject json = new JSONObject(jsonResponse);
                    Double value = (Double) json.get("v");
                    synchronized (mCurrencies) {
                        JSONObject fromJSON = (JSONObject) mCurrencies.get(from);
                        JSONObject toJSON = (JSONObject) fromJSON.get(to);
                        toJSON.put(VALUE, value);
                        toJSON.put(TIMESTAMP, SystemClock.elapsedRealtime());
                        storeCurrencies();
                    }
                    DeLog.d(TAG, "Currency updated. Notifying listeners");
                    notifyListeners(from, to, value);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }

    /**
     * Stores the loaded currencies into the disk.
     * <p>The call is synchronised. All operations to the currencies object will be blocked</p>
     *
     * @return True if the currenies were stored successfully
     */
    private boolean storeCurrencies() {
        synchronized (mCurrencies) {
            FileOutputStream fos = null;
            try {
                fos = mContext.openFileOutput(FILENAME_CURRENCIES, Context.MODE_PRIVATE);
                ObjectOutputStream os = new ObjectOutputStream(fos);
                os.writeObject(mCurrencies.toString());
                os.close();
                fos.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * Loads the currencies from disk
     *
     * @return
     */
    private JSONObject loadCurrenciesfromDisk() {
        FileInputStream fis = null;
        try {
            fis = mContext.openFileInput(FILENAME_CURRENCIES);
            ObjectInputStream is = new ObjectInputStream(fis);
            String jsonToString = (String) is.readObject();

            is.close();
            fis.close();
            return new JSONObject(jsonToString);
        } catch (FileNotFoundException fx) {
            DeLog.d(TAG, "Previous stored currencies file not found");
        } catch (IOException | ClassNotFoundException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


    /*
    Code taken from: http://stackoverflow.com/a/6349913/1315110
     */
    private static JSONObject loadCurrencies(Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.currency_values);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader;
            try {
                reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            }
            int n;
            try {
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        } finally {
            try {
                is.close();
            } catch (IOException e) {
                DeLog.log(e);
            }
        }

        String jsonString = writer.toString();
        try {
            return new JSONObject(jsonString);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The url that makes the calls.
     */
    private static final String API_CALL = "http://rate-exchange.appspot.com/currency?from=%s&to=%s&q=%s";
    private static final String TAG = "CurrencyConverter";

    /**
     * Converts the given value from one currency to the other.
     *
     * @param context  The context to use
     * @param from     The currency to convert from
     * @param to       The currency to convert to
     * @param quantity The amount to convert
     * @return The converted value or null if we couldn't connect
     * @throws RuntimeException
     */
    public Double fetchConvertTo(Context context, String from, String to, String quantity) throws RuntimeException {
        String callUrl = String.format(Locale.US, API_CALL, from, to, quantity);
        String JsonResponse = connect(callUrl);
        try {
            // {"to": "EUR", "rate": 0.88161900000000004, "from": "USD", "v": 0.88161900000000004}
            JSONObject json = new JSONObject(JsonResponse);
            return (Double) json.get("v");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return -1d;
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private String connect(String url) {
        if (!Utils.isOnline(mContext)) {
            DeLog.v(TAG, "Can't use the JSON API while offline!");
            return null;
        }
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(url);
        HttpResponse response;
        try {
            response = httpclient.execute(httpget);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream instream = entity.getContent();
                String result = convertStreamToString(instream);
                instream.close();
                return result;
            }
        } catch (ClientProtocolException e) {
            DeLog.log(e);
        } catch (IOException e) {
            DeLog.log(e);
        }
        return null;
    }


    /**
     * DEBUG METHOD: method that downloads all currencies using the JSON API and stores all the values into a
     * JSON object. The string representation of the object is then sent via email
     *
     * @param activityContext
     */
    public void debug(final Context activityContext) {
        DeLog.v(TAG, "--- DEBUG ---");
        new Thread(new Runnable() {
            @Override
            public void run() {

                Context con = activityContext.getApplicationContext();
                JSONObject finaljson = new JSONObject();
                String[] countryNames = con.getResources().getStringArray(R.array.country);
                long timestamp = 0l;
                try {
                    for (String from : countryNames) {
                        JSONObject currentCountry = new JSONObject();
                        for (String to : countryNames) {
                            Double val = fetchConvertTo(con, from, to, "1");
                            JSONObject country = new JSONObject();
                            country.put(CurrencyConverter.VALUE, val);
                            country.put(CurrencyConverter.TIMESTAMP, timestamp);
                            currentCountry.put(to, country);
                            //{"to": "EUR", "rate": 0.88427699999999998, "from": "USD", "v": 132.64155}

                        }
                        finaljson.put(from, currentCountry);
                        DeLog.d(TAG, "Added: " + currentCountry);
                    }

                    activityContext.startActivity(getEmailIntent(activityContext, "", "", finaljson.toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }


    public static Intent getEmailIntent(Context context, String to, String subject, String text) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", to, null));
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);

        return emailIntent;
    }
}

