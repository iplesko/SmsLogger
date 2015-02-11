package sk.plesko.smslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.telephony.SmsMessage;

/**
 * Created by Ivan on 11.2.2015.
 */
public class SmsReceiver extends BroadcastReceiver {

    private final String LOG_TAG = SmsReceiver.class.getSimpleName();
    private final String SMS_RECEIVED_ACTION = "android.provider.Telephony.SMS_RECEIVED";

    private NumberWatchList numberWatchList = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (SMS_RECEIVED_ACTION.equals(intent.getAction())) {

            if (numberWatchList == null) {
                numberWatchList = new NumberWatchList(context);
            }

            SmsLogger smsLogger = new SmsLogger(context);
            SmsMessage[] smsMessages = getMessagesFromIntent(intent);
            for (SmsMessage smsMessage : smsMessages) {
                if (numberWatchList.inList(smsMessage.getDisplayOriginatingAddress())) {
                    smsLogger.logSms(smsMessage);
                    muteNotificationSound(context);
                } else {
                    unmuteNotificationSound(context);
                }
            }
        }
    }

    /**
     * Gets SMS messages from intent.
     *
     * NOTE: implementation of getMessagesFromIntent() was added in API level 19, so we have to take it (and slightly modify) from the Android sources:
     * https://android.googlesource.com/platform/frameworks/opt/telephony/+/master/src/java/android/provider/Telephony.java#1118
     *
     * @param intent the intent received
     * @return ArrayList of received messages
     */
    private SmsMessage[] getMessagesFromIntent(Intent intent) {
        Object[] messages = (Object[]) intent.getSerializableExtra("pdus");

        int pduCount = messages.length;
        SmsMessage[] msgs = new SmsMessage[pduCount];

        for (int i = 0; i < pduCount; i++) {
            byte[] pdu = (byte[]) messages[i];
            msgs[i] = SmsMessage.createFromPdu(pdu);
        }
        return msgs;
    }

    private void muteNotificationSound(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) != 0) {
            audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, true);
        }
    }

    private void unmuteNotificationSound(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamMute(AudioManager.STREAM_NOTIFICATION, false);
    }

}