package com.eric.school.fastrecycler.tools.util;

import java.util.Calendar;
import java.util.Date;

/**
 * Description.
 *
 * @author 泽乾
 * createAt 2019/4/13 0013 18:04
 */
public class Utils {

    public static Date fromISO(String isoTime) {
        try {
            String[] str_list = isoTime.replace("-", ":").replace(" ", ":").split(":");
            int year = Integer.valueOf(str_list[0]);
            int month = Integer.valueOf(str_list[1]);
            int day = Integer.valueOf(str_list[2]);
            int hour = Integer.valueOf(str_list[3]);
            int minute = Integer.valueOf(str_list[4]);
            int second = Integer.valueOf(str_list[5]);

            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day, hour, minute, second);
            return calendar.getTime();
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
