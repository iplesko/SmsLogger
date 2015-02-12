package sk.plesko.smslogger.parser;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sk.plesko.smslogger.SmsLogger;
import sk.plesko.smslogger.data.SmsLog;

public class ParseLogAsyncTask extends AsyncTask<Void, Void, List<SmsLog>> {

    private final String LOG_TAG = ParseLogAsyncTask.class.getSimpleName();

    private LogListReceiver logListReceiver;
    private Context context;

    public ParseLogAsyncTask(Context context, LogListReceiver logListReceiver) {
        this.logListReceiver = logListReceiver;
        this.context = context;
    }

    protected List<SmsLog> doInBackground(Void... data) {

        List<SmsLog> logList = new ArrayList<SmsLog>();

        File logFile = SmsLogger.getLogFile(context);

        BufferedReader reader = null;
        String line;
        try {
            reader = new BufferedReader(new FileReader(logFile));
            while ((line = reader.readLine()) != null) {
                logList.add(new SmsLog(line));
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while reading log file: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while closing log file: " + e.getMessage());
            }
        }

        return logList;
    }

    @Override
    protected void onPostExecute(List<SmsLog> logs) {
        if (logs != null) {
            for (SmsLog log : logs) {
                logListReceiver.addLogToList(log);
            }
        }
    }

    public interface LogListReceiver {
        public void addLogToList(SmsLog log);
    }
}