package sk.plesko.smslogger.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import sk.plesko.smslogger.R;
import sk.plesko.smslogger.data.SmsLog;

/**
 * Created by Ivan on 12.2.2015.
 * LogList adapter is used in MainActivity to view SMS messages from the log file
 */
public class LogListAdapter extends ArrayAdapter<SmsLog> {

    private int mListItemView;

    public LogListAdapter(Context context, int resource) {
        super(context, resource);

        mListItemView = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(mListItemView, parent, false);

        SmsLog smsLog = getItem(getCount() - position - 1);

        ((TextView) rowView.findViewById(R.id.message_sender_number)).setText(smsLog.getSenderNumber());
        ((TextView) rowView.findViewById(R.id.message_date)).setText(smsLog.getDateReceived());
        ((TextView) rowView.findViewById(R.id.message_body)).setText(smsLog.getMessage());

        return rowView;
    }
}

