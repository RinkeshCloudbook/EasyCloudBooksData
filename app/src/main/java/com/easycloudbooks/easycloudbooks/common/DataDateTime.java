package com.easycloudbooks.easycloudbooks.common;

import android.text.format.DateUtils;
import android.util.Log;

import com.easycloudbooks.easycloudbooks.app.App;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * Created by Mitul on 01-05-2017.
 */

public class DataDateTime {
    static final String DATEFORMAT = "yyyy-MM-dd HH:mm:ss";
    public static  long Offset = 0;

    public  static DateTime  Now()
    {
        org.joda.time.DateTime now = new org.joda.time.DateTime();
        return now.toDateTime( org.joda.time.DateTimeZone.UTC );
    }

    public  static long  NowUnix()
    {
        DateTime nowUTC = Now();
        return nowUTC.toDate().getTime();
    }

    //Not Using
    public static Date GetUTCdatetimeAsDate()
    {
        //note: doesn't check for null
        return StringDateToDate(GetUTCdatetimeAsString());
    }

    public static String GetUTCdatetimeAsLong()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());
        return utcTime;
    }

    public static String GetUTCdatetimeAsString()
    {
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        final String utcTime = sdf.format(new Date());

        return utcTime;
    }

    public static Date StringDateToDate(String StrDate)
    {
        Date dateToReturn = null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATEFORMAT);

        try
        {
            dateToReturn = (Date)dateFormat.parse(StrDate);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }

        return dateToReturn;
    }

    private static Date addMinutes( Date beforeTime,int minutes){
        final long ONE_MINUTE_IN_MILLIS = 60000;//millisecs

        long curTimeInMs = beforeTime.getTime();
        Date afterAddingMins = new Date(curTimeInMs + (minutes * ONE_MINUTE_IN_MILLIS));
        return afterAddingMins;
    }


    public  static  DateTime getLocalJodaTime(DateTime utcTime) {
        if(utcTime != null) {
            TimeZone timezone = TimeZone.getDefault();
            return new DateTime(utcTime, DateTimeZone.forID(timezone.getID()));
        }
        return  null;
    }

    public  static String stringify(DateTime date) {
        if(date != null)
        {
            return  date.toLocalDateTime().toString("MMM d, y h:m:s a");
        }
        return  "";
    }

    public  static  String GetFromNow(DateTime utclastUpdated)
    {
        DateTime CD = DataDateTime.getLocalJodaTime(utclastUpdated);
        return (String)android.text.format.DateUtils.getRelativeTimeSpanString((CD).getMillis());

        /*long now = System.currentTimeMillis();
        return DateUtils.getRelativeTimeSpanString(lastUpdated.getMillis(), now, DateUtils.DAY_IN_MILLIS).toString();*/
    }

        public  static  DateTime getJodaTime(String passedDate)
    {
        if(passedDate == null)
        {
            return  null;
        }
        else {
            int length = passedDate.length();
            boolean haveZ = passedDate.endsWith("Z");
            DateTimeFormatter df = null;
            //09/17/2019 05:34:30
            if(length == 24)
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            else if(length == 28)
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
            else if(length == 28)
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
            else if(length == 27 && haveZ)
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
            else if(length == 27) {
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSSZ");
                passedDate = passedDate+"Z";
            }
            else if(length == 26 && haveZ)
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSZ");
            else if(length == 26){
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ");
                passedDate = passedDate+"Z";
            }
            else  if(length == 23) {
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                passedDate = passedDate+"Z";
            }
            else  if(length == 20)
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
            else  if(length == 19) {
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ssZ");
                passedDate = passedDate+"Z";
            }
            else if(length > 19){
                df = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                passedDate = passedDate+"Z";
        }
            if(df != null) {
                long millis = df.parseMillis(passedDate);
                return new DateTime(millis , DateTimeZone.UTC);
            }
            else
                return new DateTime(passedDate, DateTimeZone.UTC);
        }
    }


}
