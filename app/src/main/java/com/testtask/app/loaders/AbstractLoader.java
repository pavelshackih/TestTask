package com.testtask.app.loaders;

import android.content.Context;
import com.testtask.app.L;

/**
 *
 */
abstract class AbstractLoader<T> extends SimpleLoader<SimpleResult<T>> {

    static final String TAG = "AbstractSbtLoader";

    public AbstractLoader(Context context) {
        super(context);
    }

    @Override
    public final SimpleResult<T> loadInBackground() {
        Exception exc = null;
        T result = null;
        try {
            result = doInBackground();
        } catch (Exception e1) {
            L.e(TAG, "Error in loader", e1);
            exc = e1;
        }
        return new SimpleResult<T>(exc, result);
    }

    public abstract T doInBackground() throws Exception;
}
