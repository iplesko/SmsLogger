package sk.plesko.smslogger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import sk.plesko.smslogger.adapter.LogListAdapter;
import sk.plesko.smslogger.data.SmsLog;
import sk.plesko.smslogger.parser.ParseLogAsyncTask;


public class MainActivity extends ActionBarActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private BroadcastReceiver mBroadcastReceiver;
    private LogListAdapter mLogListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLogListAdapter = new LogListAdapter(this, R.layout.log_list_item);
        ListView logListView = (ListView) findViewById(R.id.log_list_view);
        logListView.setAdapter(mLogListAdapter);

        ParseLogAsyncTask parseLogAsyncTask = new ParseLogAsyncTask(this, new LogListReceiver());
        parseLogAsyncTask.execute();

        mBroadcastReceiver = new WatchedSmsReceivedBroadcastReceiver();
    }

    private class LogListReceiver implements ParseLogAsyncTask.LogListReceiver {
        @Override
        public void addLogToList(SmsLog log) {
            mLogListAdapter.add(log);
        }
    }

    private class WatchedSmsReceivedBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && SmsReceiver.SMS_RECEIVED_BROADCAST_ACTION.equals(intent.getAction()) && intent.hasExtra(SmsReceiver.SMS_EXTRA_TAG)) {
                String[] smsMessages = intent.getStringArrayExtra(SmsReceiver.SMS_EXTRA_TAG);

                for (String smsMessage : smsMessages) {
                    mLogListAdapter.add(new SmsLog(smsMessage));

                }

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter iff = new IntentFilter();
        iff.addAction(SmsReceiver.SMS_RECEIVED_BROADCAST_ACTION);
        this.registerReceiver(mBroadcastReceiver,iff);
    }
    @Override
    public void onPause() {
        super.onPause();
        this.unregisterReceiver(mBroadcastReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void removeLogs() {
        File logFile = SmsLogger.getLogFile(this);
        if (logFile != null) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(logFile);
                fileWriter.write("");
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while deleting log file: " + e.getMessage());
                Toast.makeText(this, R.string.remove_log_unsuccessful, Toast.LENGTH_SHORT).show();
            } finally {
                if (fileWriter != null) {
                    try {
                        fileWriter.close();
                        Toast.makeText(this, R.string.remove_log_successful, Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        Log.e(LOG_TAG, "Error while closing deleted log file: " + e.getMessage());
                        Toast.makeText(this, R.string.remove_log_unsuccessful, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        mLogListAdapter.clear();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_delete_logs) {
            removeLogs();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
