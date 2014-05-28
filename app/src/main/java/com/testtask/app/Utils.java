package com.testtask.app;

import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import org.apache.http.HttpResponse;
import org.apache.http.client.params.HttpClientParams;

/**
 *
 */
@SuppressWarnings("unused")
public class Utils {

    private Utils() {
        throw new AssertionError();
    }

    static AndroidHttpClient httpClient;

    public static boolean isNotValidUrl(String url) {
        return !Patterns.WEB_URL.matcher(url).matches();
    }

    public static AndroidHttpClient getHttpClient(Context context) {
        if (httpClient == null) {
            synchronized (Utils.class) {
                if (httpClient == null) {
                    httpClient = AndroidHttpClient.newInstance(System.getProperty("http.agent", "Android"));
                    HttpClientParams.setRedirecting(httpClient.getParams(), true);
                }
            }
        }
        return httpClient;
    }

    public static void closeHttpClient() {
        try {
            httpClient.close();
        } catch (Exception ignored) {
            // do not care
        }
    }

    public static String getCharsetFromResponse(HttpResponse httpResponse) {
        return httpResponse.getEntity().getContentType().getValue().split("=")[1];
    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean isNotConnected(Context context) {
        return !isConnected(context);
    }

    public static void showWarning(final FragmentManager fragmentManager, final String message) {
        Handler handler = new Handler(Looper.myLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Bundle bundle = new Bundle();
                //noinspection ThrowableResultOfMethodCallIgnored
                bundle.putString(WarningDialog.MESSAGE, message);
                WarningDialog simpleDialog = new WarningDialog();
                simpleDialog.setArguments(bundle);
                simpleDialog.show(fragmentManager, WarningDialog.DEFAULT_TAG);
            }
        });
    }

    public static String getString(CharSequence charSequence) {
        return TextUtils.isEmpty(charSequence) ? null : charSequence.toString();
    }

    public static void hideKeyboard(Context context, EditText editText) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
}
