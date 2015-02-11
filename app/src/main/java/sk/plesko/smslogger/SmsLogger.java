package sk.plesko.smslogger;

import android.content.Context;
import android.os.Environment;
import android.telephony.SmsMessage;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ivan on 11.2.2015.
 */
public class SmsLogger {

    private final String LOG_TAG = SmsLogger.class.getSimpleName();
    private final String DIR_NAME = "smsLog";
    private final String FILE_NAME = "log.txt";

    private Context context;

    public SmsLogger(Context context) {
        this.context = context;
    }

    public void logSms(SmsMessage smsMessage) {
        if (isExternalStorageWritable()) {
            File filePath = new File(context.getExternalFilesDir(Environment.DIRECTORY_NOTIFICATIONS), DIR_NAME);
            if (!filePath.exists()) {
                if (!filePath.mkdirs()) {
                    Log.e(LOG_TAG, "Error while creating directory");
                    return;
                }
            }
            FileWriter fileWriter = null;
            try {
                Log.d(LOG_TAG, filePath.getCanonicalPath() + FILE_NAME);
                fileWriter = new FileWriter(new File(filePath, FILE_NAME), true);
                fileWriter.append(getLogLine(smsMessage));
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
        }
    }

    private String getLogLine(SmsMessage smsMessage) {
        Date date = new Date(smsMessage.getTimestampMillis());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(date) + " | FROM: "+smsMessage.getDisplayOriginatingAddress() + " | MESSAGE: " + smsMessage.getMessageBody() + System.getProperty("line.separator");
    }

    private boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

}
