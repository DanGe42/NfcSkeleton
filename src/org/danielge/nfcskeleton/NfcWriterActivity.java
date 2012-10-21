package org.danielge.nfcskeleton;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;

public abstract class NfcWriterActivity extends Activity {
    public static final String TAG = NfcWriterActivity.class.getSimpleName();

    private static final IntentFilter WRITE_FILTER = buildWriteFilter();

    private static IntentFilter buildWriteFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addCategory("android.intent.category.DEFAULT");

        return filter;
    }

    private boolean writeMode = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();

        if (writeMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Log.i(TAG, "Discovered tag");

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            onTagDiscovered(tag);
        }
    }

    protected abstract void onTagDiscovered (Tag tag);

    protected void enableWriteTagMode(IntentFilter[] additionalFilters, String[][] techlist) {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        IntentFilter[] filters = Arrays.copyOf(additionalFilters, additionalFilters.length + 1);
        filters[additionalFilters.length - 1] = WRITE_FILTER;

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        adapter.enableForegroundDispatch(this, pendingIntent, additionalFilters, techlist);

        writeMode = true;
    }

    protected void disableWriteTagMode() {
        writeMode = false;
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.disableForegroundDispatch(this);
    }

    protected final boolean inWriteMode() {
        return writeMode;
    }
}