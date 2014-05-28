package com.testtask.app.loaders;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.text.TextUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;

import static com.testtask.app.Utils.getCharsetFromResponse;
import static com.testtask.app.Utils.getHttpClient;
import static org.apache.http.HttpStatus.SC_OK;

/**
 *
 */
public class UrlDataLoader extends AbstractLoader<Integer> {

    public static final int ID = 1;
    public static final String URL = "url";
    public static final String TMP_FILE = "tmp.file";

    String url;
    AndroidHttpClient httpClient;

    public UrlDataLoader(Context context, Bundle bundle) {
        super(context);
        url = bundle.getString(URL);
    }

    @Override
    public Integer doInBackground() throws Exception {
        // default case
        if (url == null) {
            return -1;
        }

        // building client
        httpClient = getHttpClient(getContext());

        // preparing request and executing
        if (!url.contains("http")) {
            // according to task it should be http request
            // so we just add scheme at beginning if user didn't
            url = "http://" + url;
        }
        HttpGet httpGet = new HttpGet(url);
        HttpResponse httpResponse = httpClient.execute(httpGet);

        // simple response validation
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        if (statusCode != SC_OK) {
            throw new RuntimeException(String.format("Wrong status code from url: %s, expected: %s, " +
                    "actual: %s", url, SC_OK, statusCode));
        }

        // get charset from response
        String encoding = getCharsetFromResponse(httpResponse);

        // writing to file
        OutputStream os = getContext().openFileOutput(TMP_FILE, Context.MODE_PRIVATE);
        copy(httpResponse.getEntity().getContent(), os);

        // now counting symbols
        InputStream is = getContext().openFileInput(TMP_FILE);
        return countSymbols(is, encoding);
    }

    static void copy(InputStream is, OutputStream os) throws Exception {
        try {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }

    static int countSymbols(InputStream is, String encoding) throws Exception {
        int result = -1;
        final char[] buffer = new char[1024];
        final Reader in = new InputStreamReader(is, encoding);
        try {
            while (in.read(buffer, 0, buffer.length) != -1) {
                result += countSymbols(buffer);
            }
        } finally {
            in.close();
        }
        return result;
    }

    static int countSymbols(char[] data) {
        int result = 0;
        // indexed loop instead of foreach because we do not want
        // to create iterator each time when method will be invoked
        //noinspection ForLoopReplaceableByForEach
        for (int i = 0; i < data.length; i++) {
            if (TextUtils.isGraphic(data[i])) {
                result++;
            }
        }
        return result;
    }
}
