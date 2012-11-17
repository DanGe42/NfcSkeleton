package org.danielge.nfcskeleton;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;

/**
 * This class is a base class that can be used as the parent of all Activities
 * that need to be able to detect and read NFC tags.
 *
 * To use this class, call
 * {@link #enableReadTagMode(android.content.IntentFilter[])} to start
 * listening for NFC-related intents. To specify the intents to respond to,
 * override the {@link #nfcIntentFilter(android.content.Intent)}. Then,
 * implement {@link #onTagRead(android.nfc.Tag)}, which is called
 * when an NFC tag has been read.
 *
 * For reading NDEF-formatted tags, you can use {@link NdefReaderActivity}.
 *
 * @author Daniel Ge
 */
public abstract class NfcReaderActivity extends Activity {

    private boolean readMode = false;

    /**
     * Overrides the default {@code Activity.onResume} method to test whether
     * a delivered intent has an NFC action, such as
     * {@code ACTION_NDEF_DISCOVERED}, as specified by the user, and if so,
     * calls {@link #onTagRead(android.nfc.Tag)} with the {@link Tag} object
     * read from the tag.
     *
     * If you override this method, make sure to include {@code
     * super.onResume()}.
     */
    @Override
    protected void onResume() {
        super.onResume();

        if (getIntent() != null && nfcIntentFilter(getIntent())) {
            doNfcIntent(getIntent());
        }
    }

    /**
     * Overrides the default {@code Activity.onNewIntent} method to test whether
     * a delivered intent has an NFC action, such as
     * {@code ACTION_NDEF_DISCOVERED}, as specified by the user, and if so
     * calls {@link #onTagRead(android.nfc.Tag)} with the with the {@link Tag}
     * object read from the tag.
     *
     * If you override this method, make sure to include {@code
     * super.onNewIntent(intent)}.
     *
     * @param intent    The intent delivered to the Activity.
     */
    @Override
    protected void onNewIntent(Intent intent) {
        if (nfcIntentFilter(intent)) {
            doNfcIntent(intent);
        }
    }

    private void doNfcIntent (Intent intent) {
        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        onTagRead(tag);
    }

    /**
     * Defines the action to perform on a {@link Tag} object created from
     * reading an NFC tag.
     *
     * @param tag   The Tag object read from the tag.
     */
    protected abstract void onTagRead (Tag tag);

    /**
     * Defines whether a given intent should cause
     * {@link #onTagRead(android.nfc.Tag)} to be invoked. This
     * method is called by default in {@link #onResume()} and
     * {@link #onNewIntent(android.content.Intent)}.
     *
     * @param intent    The intent delivered to this Activity
     * @return  {@link true} if the intent passes the filter, {@link false}
     *          if not.
     */
    protected abstract boolean nfcIntentFilter(Intent intent);

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
     * {@link #nfcIntentFilter}.
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