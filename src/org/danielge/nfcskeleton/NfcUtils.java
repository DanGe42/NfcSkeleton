package org.danielge.nfcskeleton;

import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.util.Log;

import java.io.IOException;

public class NfcUtils {
    public static final String TAG = NfcUtils.class.getSimpleName();

    public static boolean writeNdefTag (NdefMessage message, Tag tag)
            throws IOException, FormatException, TagNotWritableException, NdefMessageTooLongException {
        int size = message.toByteArray().length;

        Ndef ndef = Ndef.get(tag);
        if (ndef != null) {
            ndef.connect();

            if (!ndef.isWritable()) {
                throw new TagNotWritableException();
            }
            if (ndef.getMaxSize() < size) {
                throw new NdefMessageTooLongException();
            }

            ndef.writeNdefMessage(message);

            Log.i(TAG, "Successfully wrote to NDEF tag");
            return true;
        } else {
            NdefFormatable format = NdefFormatable.get(tag);
            if (format != null) {
                format.connect();
                format.format(message);

                Log.i(TAG, "Successfully formatted and wrote to NDEF tag");
                return true;
            }
        }

        return false;
    }

    public static class TagNotWritableException extends Exception {
        public TagNotWritableException() {
            super();
        }

        public TagNotWritableException(String msg) {
            super(msg);
        }

        public TagNotWritableException(String msg, Throwable thr) {
            super(msg, thr);
        }

        public TagNotWritableException(Throwable thr) {
            super(thr);
        }
    }

    public static class NdefMessageTooLongException extends Exception {
        public NdefMessageTooLongException() {
            super();
        }

        public NdefMessageTooLongException(String msg) {
            super(msg);
        }

        public NdefMessageTooLongException(String msg, Throwable thr) {
            super(msg, thr);
        }

        public NdefMessageTooLongException(Throwable thr) {
            super(thr);
        }
    }
}
