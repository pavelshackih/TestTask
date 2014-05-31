package com.testtask.app;

import android.app.Activity;
import android.content.*;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.testtask.app.loaders.SimpleLoaderCallbacks;
import com.testtask.app.loaders.SimpleResult;
import com.testtask.app.loaders.UrlDataLoader;

import javax.annotation.Nonnull;

import static android.text.TextUtils.isEmpty;
import static com.testtask.app.Utils.*;

public class MainActivity extends Activity implements View.OnClickListener, TextView.OnEditorActionListener,
        SimpleLoaderCallbacks<Integer> {

    static final String STATE = "state";
    static final String TEXT = "text";

    TextView text;
    EditText input;
    Button button;

    enum State {
        NORMAL, IN_PROGRESS, NOT_CONNECTED
    }

    State state = State.NORMAL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = (TextView) findViewById(android.R.id.text1);
        input = (EditText) findViewById(android.R.id.input);
        button = (Button) findViewById(android.R.id.button1);

        input.setOnEditorActionListener(this);
        button.setOnClickListener(this);

        if (savedInstanceState == null) {
            if (isNotConnected(this)) {
                state = State.NOT_CONNECTED;
                showWarning(getFragmentManager(), getString(R.string.please_check_network_connection));
            }
        } else {
            state = (State) savedInstanceState.getSerializable(STATE);
            text.setText(savedInstanceState.getString(TEXT));
        }

        getLoaderManager().initLoader(UrlDataLoader.ID, Bundle.EMPTY, this);

        refreshState();
    }

    void perform() {
        Editable editableUrl = input.getText();
        if (isEmpty(editableUrl)) {
            Toast.makeText(this, R.string.url_cant_be_empty, Toast.LENGTH_LONG).show();
            return;
        }
        //noinspection ConstantConditions
        String notEmptyUrl = editableUrl.toString();
        if (isNotValidUrl(notEmptyUrl)) {
            Toast.makeText(this, R.string.invalid_url, Toast.LENGTH_LONG).show();
            return;
        }
        refreshState(State.IN_PROGRESS);
        Bundle bundle = new Bundle();
        bundle.putString(UrlDataLoader.URL, notEmptyUrl);
        getLoaderManager().restartLoader(UrlDataLoader.ID, bundle, this);
    }

    @Override
    public void onClick(@Nonnull View v) {
        perform();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_NULL) {
            hideKeyboard(this, input);
            perform();
            return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(broadcastReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(@Nonnull Context context,@Nonnull Intent intent) {
            if (state != State.IN_PROGRESS) {
                if (isNotConnected(context)) {
                    state = State.NOT_CONNECTED;
                } else {
                    state = State.NORMAL;
                }
                refreshState();
            }
        }
    };

    void refreshState() {
        switch (state) {
            case NORMAL:
                setProgressBarIndeterminateVisibility(Boolean.FALSE);
                button.setEnabled(true);
                break;
            case IN_PROGRESS:
                setProgressBarIndeterminateVisibility(Boolean.TRUE);
                button.setEnabled(true);
                break;
            case NOT_CONNECTED:
                setProgressBarIndeterminateVisibility(Boolean.FALSE);
                button.setEnabled(false);
                break;
        }
    }

    void refreshState(State state) {
        this.state = state;
        refreshState();
    }

    @Override
    public Loader<SimpleResult<Integer>> onCreateLoader(int id, Bundle args) {
        return id == UrlDataLoader.ID ? new UrlDataLoader(getApplicationContext(), args) : null;
    }

    @Override
    public void onLoadFinished(Loader<SimpleResult<Integer>> loader, final SimpleResult<Integer> data) {
        button.setEnabled(true);
        if (data.containsError()) {
            //noinspection ThrowableResultOfMethodCallIgnored
            showWarning(getFragmentManager(), data.getError().getMessage());
            getLoaderManager().restartLoader(UrlDataLoader.ID, Bundle.EMPTY, this);
        } else {
            if (data.getResult() == -1) {
                text.setText(R.string.hello_world);
            } else {
                text.setText(getString(R.string.result_is, data.getResult()));
            }
        }
        refreshState(State.NORMAL);
    }

    @Override
    protected void onSaveInstanceState(@Nonnull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(STATE, state);
        outState.putString(TEXT, Utils.getString(text.getText()));
    }

    @Override
    public void onLoaderReset(Loader<SimpleResult<Integer>> loader) {
    }
}
