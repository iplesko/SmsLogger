package sk.plesko.smslogger;

import android.content.Context;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import sk.plesko.smslogger.data.SmsLog;

/**
 * Created by Ivan on 11.2.2015.
 */
public class SmsLogger {

    public static final String LOG_TAG = SmsLogger.class.getSimpleName();
    public static final String DIR_NAME = "smsLog";
    public static final String FILE_NAME = "log.txt";

    private Context context;

    public SmsLogger(Context context) {
        this.context = context;
    }

    public SmsLog logSms(SmsMessage smsMessage) {
        SmsLog smsLog = new SmsLog(smsMessage.getTimestampMillis(), smsMessage.getDisplayOriginatingAddress(), smsMessage.getMessageBody());
        File logFile = getLogFile(context);
        if (logFile == null) {
            return null;
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(logFile, true);
            fileWriter.append(smsLog.toString() + System.getProperty("line.separator"));
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while logging SMS: " + e.getMessage());
        } finally {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error while closing sms log file output stream: " + e.getMessage());
                }
            }
        }
        return smsLog;
    }

    public static File getLogFile(Context context) {
        if (isExternalStorageWritable()) {
            File filePath = new File(context.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS), DIR_NAME);
            if (!filePath.exists()) {
                if (!filePath.mkdirs()) {
                    Log.e(LOG_TAG, "Error while creating directory");
                    return null;
                }
            }
            return new File(filePath, FILE_NAME);
        }
        return null;
    }

    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
