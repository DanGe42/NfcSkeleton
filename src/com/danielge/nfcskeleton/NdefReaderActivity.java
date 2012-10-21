package com.danielge.nfcskeleton;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;

public abstract class NdefReaderActivity extends Activity {

    private boolean readMode = false;

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (ndefTagFilter(intent)) {
            Ndef ndef = Ndef.get((Tag) intent.getParcelableExtra(NfcAdapter.EXTRA_TAG));
            NdefMessage message = ndef.getCachedNdefMessage();

            onNdefMessage(message);
        }
    }

    protected abstract void onNdefMessage (NdefMessage message);

    protected boolean ndefTagFilter (Intent intent) {
        String action = intent.getAction();
        return NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action);
    }

    protected final boolean inReadMode() {
        return readMode;
    }

    protected void enableReadTagMode(IntentFilter[] filters) {
        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        Intent intent = new Intent(this, getClass());
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        adapter.enableForegroundDispatch(this, pendingIntent, filters, null);

        readMode = true;
    }

    protected void disableReadTagMode() {
        readMode = false;

        NfcAdapter adapter = NfcAdapter.getDefaultAdapter(this);
        adapter.disableForegroundDispatch(this);
    }
}