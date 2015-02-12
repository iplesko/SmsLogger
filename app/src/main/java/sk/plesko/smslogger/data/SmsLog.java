package sk.plesko.smslogger.data;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ivan on 12.2.2015.
 */
public class SmsLog {

    private String senderNumber;
    private String message;
    private String dateReceived;

    public SmsLog(String rawLog) {
        parseRawLog(rawLog);
    }

    public SmsLog(long dateReceived, String senderNumber, String message) {
        Date date = new Date(dateReceived);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        this.dateReceived = simpleDateFormat.format(date);
        this.senderNumber = senderNumber;
        this.message = message;
    }

    private void parseRawLog(String rawLog) {
        if (rawLog != null) {
            Pattern p = Pattern.compile("(?:(.+?) \\|) FROM: (.+) \\| MESSAGE: (.+)");
            Matcher m = p.matcher(rawLog);
            if (m.find()) {
                dateReceived = m.group(1);
                senderNumber = m.group(2);
                message = m.group(3);
            }
        }
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public String getMessage() {
        return message;
    }

    public String getSenderNumber() {
        return senderNumber;
    }

    @Override
    public String toString() {
        return dateReceived + " | FROM: " + senderNumber + " | MESSAGE: " + message.replace("\n", " ").replace("\r", "");
    }
}
