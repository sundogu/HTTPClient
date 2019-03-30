import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.StringTokenizer;

public class MyDateParser {

    public static String parseDate(String date) { // input pattern: www, dd MMM yyyy hh:mm:ss z
        String formattedDate = "";
        StringTokenizer strtok = new StringTokenizer(date);
        strtok.nextToken();

        formattedDate += strtok.nextToken() + "/";
        formattedDate += parseMonth(strtok.nextToken()) + "/";
        formattedDate += strtok.nextToken() + " ";
        formattedDate += strtok.nextToken();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        Date gmtDate = new Date();
        try {
            gmtDate= sdf.parse(formattedDate);
        } catch (ParseException pe) {
            pe.printStackTrace();
            System.exit(1);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(gmtDate);
        calendar.add(Calendar.HOUR_OF_DAY, 10);

        return sdf.format(calendar.getTime()) + " AEST";
    }

    private static String parseMonth(String month) {
        switch (month) {
            case "Jan": {
                return "01";
            }case "Feb": {
                return "02";
            }case "Mar": {
                return "03";
            }case "Apr": {
                return "04";
            }case "May": {
                return "05";
            }case "Jun": {
                return "06";
            }case "Jul": {
                return "07";
            }case "Aug": {
                return "08";
            }case "Sep": {
                return "09";
            }case "Oct": {
                return "10";
            }case "Nov": {
                return "11";
            }default: {
                return "12";
            }
        }
    }
}
