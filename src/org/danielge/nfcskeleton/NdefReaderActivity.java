package org.danielge.nfcskeleton;

import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;

/**
 * This class is a base class that can be used as the parent of all Activities
 * that need to be able to detect and read NDEF tags. For a more general
 * purpose class, you can consider using {@link NfcReaderActivity}.
 *
 * To use this class, call
 * {@link #enableReadTagMode(android.content.IntentFilter[])} to start
 * listening for NFC intents. Implement
 * {@link #onNdefMessage(android.nfc.NdefMessage)} to perform actions based
 * on the NDEF messages read from the tag.
 *
 * @see NfcReaderActivity
 *
 * @author Daniel Ge
 */
public abstract class NdefReaderActivity extends NfcReaderActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
     * Method overriden to look for NDEF tags specifically.
     *
     * @param intent    The intent delivered to this Activity
     * @return  {@code true} the intent has the ACTION_NDEF_DISCOVERED action.
     *          {@code false} otherwise.
     */
    @Override
    protected boolean nfcIntentFilter(Intent intent) {
        String action = intent.getAction();
        return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action);
    }

    /**
     * Method overriden to call {@link #onNdefMessage(android.nfc.NdefMessage)}
     * using the cached NDEF messages from a NFC tag.
     *
     * @param tag   The Tag object read from the tag.
     */
    @Override
    protected void onTagRead (Tag tag) {
        Ndef ndef = Ndef.get(tag);
        NdefMessage message = ndef.getCachedNdefMessage();

        onNdefMessage(message);
    }

}