package com.testtask.app.loaders;

import android.content.AsyncTaskLoader;
import android.content.Context;

/**
 * Helper for {@link android.content.Loader}.
 */
public abstract class SimpleLoader<D extends SimpleResult<?>> extends AsyncTaskLoader<D> {

    private D result;

    public SimpleLoader(Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
        if (result != null) {
            deliverResult(result);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(D data) {
        if (isReset()) {
            onRelease();
        } else {
            result = data;
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStopLoading() {
        super.onStopLoading();
        cancelLoad();
    }

    @Override
    protected void onReset() {
        super.onReset();
        onRelease();
    }

    @Override
    protected void onForceLoad() {
        onRelease();
        super.onForceLoad();
    }

    protected void onRelease() {
        result = null;
    }
}