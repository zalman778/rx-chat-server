package com.hwx.rx_chat.common.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static Date addNhrsToDate(Date inDate, int deltaHrs) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inDate);
        cal.add(Calendar.HOUR_OF_DAY, deltaHrs);
        return cal.getTime();
    }

    public static Date addNMinsToDate(Date inDate, int deltaMins) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(inDate);
        cal.add(Calendar.MINUTE, deltaMins);
        return cal.getTime();
    }


}
