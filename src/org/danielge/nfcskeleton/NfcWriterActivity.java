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

/**
 * This class is a base class that can be used as the parent to all Activities
 * that need to be able to detect and write to an NFC tag. Tags need not be
 * already NDEF-formatted; this Activity will listen for the
 * {@code android.nfc.action.TAG_DISCOVERED} intent action and perform an action
 * defined by {@link #onTagDiscovered(android.nfc.Tag)}.
 *
 * To use this class, simply call {@link #enableWriteTagMode()} or
 * {@link #enableWriteTagMode(IntentFilter[], String[][])} to start listening
 * for the {@code NfcAdapter.ACTION_TAG_DISCOVERED} intent. Then, implement
 * {@link #onTagDiscovered(android.nfc.Tag)}. Because writing to a tag is a
 * blocking operation, I recommend launching an {@code AsyncTask} to perform
 * the actual writing. You may use the
 * {@link NfcUtils#writeNdefTag(android.nfc.NdefMessage, android.nfc.Tag)} to
 * help with writing to the tag.
 *
 * @author Daniel Ge
 */
public abstract class NfcWriterActivity extends Activity {
    public static final String TAG = NfcWriterActivity.class.getSimpleName();

    private static final IntentFilter WRITE_FILTER = buildWriteFilter();

    private static IntentFilter buildWriteFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(NfcAdapter.ACTION_TAG_DISCOVERED);
        filter.addCategory(Intent.CATEGORY_DEFAULT);

        return filter;
    }

    private boolean writeMode = false;

    /**
     * Overrides the default {@code Activity.onNewIntent} method to perform the
     * action defined by {@link #onTagDiscovered(android.nfc.Tag)} when write
     * mode is active (see {@link #enableWriteTagMode()}) and when the Activity
     * receives an {@code NfcAdapter.ACTION_TAG_DISCOVERED} intent.
     *
     * If you override this method, make sure that you make the call to
     * {@code super.onNewIntent(intent)}.
     *
     * @param intent    The intent delivered to the Activity. See the official
     *                  documentation on {@code Activity.onNewIntent(Intent)}
     *                  for more information.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        String action = intent.getAction();

        if (writeMode && NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Log.i(TAG, "Discovered tag");

            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            onTagDiscovered(tag);
        }
    }

    /**
     * Performs an action on a discovered NFC tag. Called by
     * {@link #onNewIntent(android.content.Intent)}. All Activities extending this
     * class must implement this method.
     *
     * @param tag   The discovered NFC tag.
     */
    protected abstract void onTagDiscovered (Tag tag);

    /**
     * Equivalent to {@code enableWriteTagMode(null, null)}.
     *
     * @see #enableWriteTagMode(android.content.IntentFilter[], String[][])
     */
    public void enableWriteTagMode() {
        enableWriteTagMode(null, null);
    }

    /**
     * Turns on write mode for this Activity. When activated, this Activity, when
     * in the foreground (note: this cannot be called in {@code onCreate(Bundle)}),
     * will intercept all intents with the action
     * {@code NfcAdapter.ACTION_TAG_DISCOVERED}.
     *
     * This method calls {@code NfcAdapter.enableForegroundDispatch} with a default
     * {@code IntentFilter} that filters for {@code ACTION_TAG_DISCOVERED}. You can
     * add additional filters with the first argument.
     *
     * @param additionalFilters Additional {@code IntentFilter}s
     * @param techlist          See {@code NfcAdapter.enableForegroundDispatch} for
     *                          more information on this parameter.
     */
    public void enableWriteTagMode(IntentFilter[] additionalFilters, String[][] techlist) {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // If additionalFilters is nonempty, copy the array to a new one with
        // an additional slot, and set that slot to the default filter
        IntentFilter[] filters;
        if (additionalFilters != null && additionalFilters.length > 0) {
            filters = Arrays.copyOf(additionalFilters, additionalFilters.length + 1);
            filters[additionalFilters.length - 1] = WRITE_FILTER;
        } else {
            filters = new IntentFilter[] { WRITE_FILTER };
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        adapter.enableForegroundDispatch(this, pendingIntent, filters, techlist);

        writeMode = true;
    }

    /**
     * Turns off write mode for this Activity. This Activity will no longer
     * intercept intents with the action {@code NfcAdapter.ACTION_TAG_DISCOVERED}.
     */
    public void disableWriteTagMode() {
        writeMode = false;
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.disableForegroundDispatch(this);
    }

    /**
     * Returns whether write mode is active and that the Activity is looking for
     * tags.
     *
     * @return  {@code true} if write mode is active, {@code false} if not.
     * @see #enableWriteTagMode(android.content.IntentFilter[], String[][])
     * @see #disableWriteTagMode()
     */
    public final boolean inWriteMode() {
        return writeMode;
    }
}