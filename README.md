NFC Skeleton
============
Current version 0.0.1

A simple framework that makes it easy to write Android activities that can read
or write NFC tags.

The NFC Skeleton supports the reading and writing/formatting of NDEF tags.

Limitations
-------------------
Currently, this library does not support reading _and_ writing in the same
class. This library also does not explicitly support any format that is not
NDEF; however, it does provide a framework for reading any arbitrary tag.

Usage
-----
Begin by changing your Activity so that it subclasses one of these classes:

* `NdefReaderActivity`
* `NfcReaderActivity`
* `NfcWriterActivity`

Note that the `NdefReaderActivity` is a subclass of `NfcReaderActivity`. If
reading NDEF tags, you should use `NdefReaderActivity`.

There is also an `NfcUtils` class that contains a number of utility methods for
NFC tags.

### NfcReaderActivity ###

This class is a base class that can be used as the parent of all Activities
that need to be able to detect and read NFC tags.

To use this class, begin by extending the class:

    public class SomeActivity extends NfcReaderActivity {

Two `abstract` methods need to be implemented:

        @Override
        protected void onTagRead (Tag tag) {
            // ...
        }

        @Override
        protected boolean nfcIntentFilter (Intent intent) {
            // ...
        }
    }

Call `enableReadTagMode(IntentFilter[])` to start listening for NFC-related
intents. The `IntentFilter[]` should specify which intents the Activity should
listen for when in the foreground. To specify the intents to respond to,
override the `nfcIntentFilter(Intent)`.

Finally, implement `onTagRead(Tag)`, which is called when an NFC tag has been
read.

For reading NDEF-formatted tags, you should use `NdefReaderActivity`.

### NdefReaderActivity ###

This class works similarly to `NfcReaderActivity`, but has been tailored
specifically for NDEF tags. To use this class, begin by extending the class:

    public class SomeActivity extends NdefReaderActivity {

One `abstract` method needs to be implemented:

        @Override
        protected void onNdefMessage (NdefMessage message) {
            // ..
        }
    }

As before, call `enableReadTagMode(IntentFilter[])` to start listening for
NDEF-related intents. You do not need to override `nfcIntentFilter(Intent)`; by
default, it will read for an `ACTION_NDEF_DISCOVERED`. However, if you deem it
necessary, you may override it (I am not sure how best to describe when it would
be necessary to override this method, so I suggest looking at the source.)

Finally, implement `onNdefMessage(NdefMessage)`, which specifies the action to
be performed on the cached NDEF messages from a read tag.

### NfcWriterActivity ###

This class is a base class that can be used as the parent to all Activities
that need to be able to detect and write to an NFC tag. Tags need not be
already NDEF-formatted; this Activity will listen for the `TAG_DISCOVERED`
intent action. That said, this class will format tags with NDEF. To use this
class, begin by extending the class:

    public class SomeActivity extends NfcWriterActivity {

One `abstract` method needs to be implemented:

        @Override
        protected void onTagDiscovered (Tag tag) {
            // ...
        }
    }

Call enableWriteTagMode() to start listening for the `ACTION_TAG_DISCOVERED`
intent. Implement onTagDiscovered(Tag) to specify what to do when the intent is
fired. Because writing to a tag is a blocking operation, it is recommended to
launch an `AsyncTask` to perform the write.

### NfcUtils ###

Currently, this class contains one utility method, `writeNdefTag(NdefMessage,
Tag)`, which writes a `NdefMessage` to a given `Tag`. this method can be useful
in conjunction with `NfcWriterActivity`. Note that this method can throw two
exceptions: a `TagNotWritableException` and a `NdefMessageTooLongException`.


Contributing
------------
This is still an alpha library. If you have any suggestions (including
improvements to the documentation), feel free to send me a email, create an
issue, or, even better, make a pull request. I will do my best to answer your
request.
