package com.may.routeplansystem.util;

import com.may.routeplansystem.constant.Constant;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author 10587
 */
public class DateUtil {

    /**
     * 格式化LocalDateTime
     * @param localDateTime
     * @param format
     * @return
     */
    public static String formatDateTimeToString(LocalDateTime localDateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(localDateTime);
    }

    /**
     * 格式化LocalDate
     * @param localDate
     * @param format
     * @return
     */
    public static String formatDateToString(LocalDate localDate, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return formatter.format(localDate);
    }

    /**
     * 格式化当前LocalDateTime
     * @param format
     * @return
     */
    public static String formatNowDateTimeToString(String format) {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format));
    }
}
