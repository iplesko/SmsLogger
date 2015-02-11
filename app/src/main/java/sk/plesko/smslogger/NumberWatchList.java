package sk.plesko.smslogger;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Ivan on 11.2.2015.
 */
public class NumberWatchList {

    private final String LOG_TAG = NumberWatchList.class.getSimpleName();

    private Context context;
    private Map<String, Boolean> watchList;

    public NumberWatchList(Context context) {
        this.context = context;
        watchList = new HashMap<>();
        parseWatchList();
    }

    public boolean inList(String number) {
        if (watchList.containsKey(number)) {
            return watchList.get(number);
        }
        return false;
    }

    private void parseWatchList() {
        InputStream in;
        BufferedReader reader = null;
        String line;
        try {
            in = context.getAssets().open("numberWatchList.txt");
            reader = new BufferedReader(new InputStreamReader(in));
            while ((line = reader.readLine()) != null) {
                watchList.put(line, true);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error while reading number watch list file: " + e.getMessage());
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error while closing number watch list file reader: " + e.getMessage());
            }
        }
    }

}
