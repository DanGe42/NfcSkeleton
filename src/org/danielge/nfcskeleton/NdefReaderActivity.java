package org.danielge.nfcskeleton;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;

/**
 * This class is a base class that can be used as the parent of all Activities
 * that need to be able to detect and read an NDEF-formatted NFC tag.
 *
 * To use this class, call
 * {@link #enableReadTagMode(android.content.IntentFilter[])} to start
 * listening for {@code NfcAdapter.ACTION_NDEF_DISCOVERED} intent. Then,
 * implement {@link #onNdefMessage(android.nfc.NdefMessage)}, which is called
 * when an NDEF tag has been read.
 *
 * @author Daniel Ge
 */
public abstract class NdefReaderActivity extends Activity {

    private boolean readMode = false;

    /**
     * Overrides the default {@code Activity.onResume} method to test whether
     * a delivered intent has the action {@code ACTION_NDEF_DISCOVERED} and,
     * if so, calls {@link #onNdefMessage(android.nfc.NdefMessage)} with the
     * {@code NdefMessage}s read from the tag.
     *
     * If you override this method, make sure to include {@code
     * super.onResume()}.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && ndefTagFilter(getIntent())) {
            doNdefIntent(getIntent());
        }
    }

    /**
     * Overrides the default {@code Activity.onNewIntent} method to test whether
     * a delivered intent has the action {@code ACTION_NDEF_DISCOVERED} and,
     * if so, calls {@link #onNdefMessage(android.nfc.NdefMessage)} with the
     * {@code NdefMessage}s read from the tag.
     *
     * If you override this method, make sure to include {@code
     * super.onNewIntent(intent)}.
     *
     * @param intent    The intent delivered to the Activity.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if (ndefTagFilter(intent)) {
            doNdefIntent(intent);
        }
    }

    private void doNdefIntent(Intent intent) {
        Ndef ndef = Ndef.get((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
        NdefMessage message = ndef.getCachedNdefMessage();

        onNdefMessage(message);
    }

    /**
     * Performs an action on the NDEF messages read from a discovered
     * NDEF-formatted NFC tag. Called by {@link #onResume()} and
     * {@link #onNewIntent(android.content.Intent)}. All Activities extending
     * this class must implement this method.
     *
     * @param message   The cached NDEF messages from the NDEF-formatted tag.
     */
    protected abstract void onNdefMessage (NdefMessage message);

    /**
     * Defines whether a given intent should cause
     * {@link #onNdefMessage(android.nfc.NdefMessage)} to be invoked. This
     * method is called by default in {@link #onResume()} and
     * {@link #onNewIntent(android.content.Intent)}.
     *
     * By default, this filter tests whether the intent delivered has the
     * action {@code NfcAdapter.ACTION_NDEF_DISCOVERED}.
     *
     * @param intent    The intent delivered to this Activity
     * @return  {@link true} if the intent passes the filter, {@link false}
     *          if not.
     */
    protected boolean ndefTagFilter (Intent intent) {
        String action = intent.getAction();
        return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action);
    }

    /**
     * Tests whether this Activity is currently listening for NDEF tags.
     *
     * @return  {@code true} if read mode is active, {@code false} if not
     * @see #enableReadTagMode(android.content.IntentFilter[])
     * @see #disableReadTagMode()
     */
    protected final boolean inReadMode() {
        return readMode;
    }

    /**
     * Turns on read mode for this Activity. This Activity, when in the
     * foreground (note: this cannot be used in {@code Activity.onCreate}),
     * will intercept intents that pass
     * {@link #ndefTagFilter(android.content.Intent)}.
     *
     * @param filters   The IntentFilters to be used to decide whether the
     *                  intent will be received by this Activity
     */
    protected void enableReadTagMode(IntentFilter[] filters) {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        adapter.enableForegroundDispatch(this, pendingIntent, filters, null);

        readMode = true;
    }

    /**
     * Turns off read mode for this Activity.
     */
    protected void disableReadTagMode() {
        readMode = false;

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.disableForegroundDispatch(this);
    }
}