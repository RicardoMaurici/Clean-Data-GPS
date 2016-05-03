package br.ufsc.src.controle;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import br.ufsc.src.persistencia.exception.TimeStampException;

public class Utils {
	
	public static String getTimeStamp(String date, String time, String dateFormat, String timeFormat, boolean usaTimeStamp) throws TimeStampException {
		String dt = "";
		if(usaTimeStamp){  
			long newdate = Long.parseLong(date)/1000; //Considering miliseconds
			java.sql.Timestamp timeStampDate = new Timestamp(newdate);
			dt = timeStampDate.toString();
		}else if(dateFormat.indexOf('T') != -1){ //dateFormat with timezone
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
			Date result;
			try {
				result = df.parse(date);
			    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			    sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
			    dt = sdf.format(result);
			}catch (Exception e) {
				throw new TimeStampException(e.getMessage());
			}

		}else{ //dateFormat to dateFormat
			if(timeFormat.length() > 0)
				timeFormat = " "+timeFormat;
			try {
				DateFormat formatter;
				formatter = new SimpleDateFormat(dateFormat+""+timeFormat);
				Date newdate = (Date) formatter.parse(date + " " + time);
				java.sql.Timestamp timeStampDate = new Timestamp(newdate.getTime());
				dt = timeStampDate.toString();
				} catch (ParseException e) {
					throw new TimeStampException(e.getMessage());
				}
		}
		return dt;
	}
	
	public static String getDurationBreakdown(long millis)
    {
        if(millis < 0)
        {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);
        long milliseconds = TimeUnit.MILLISECONDS.toMillis(millis);

        StringBuilder sb = new StringBuilder(64);
        if(days != 0){
        	sb.append(days);
        	sb.append(" Days ");
        }if(hours != 0){
        	sb.append(hours);
        	sb.append(" Hours ");
        }if(minutes != 0){
        	sb.append(minutes);
        	sb.append(" Minutes ");
        }if(seconds != 0){
        	sb.append(seconds);
        	sb.append(" Seconds ");
        }
       	sb.append(milliseconds);
        sb.append(" MilliSeconds");

        return(sb.toString());
    }
	
	
	
	
}
