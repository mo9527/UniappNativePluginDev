package com.wanyi.plugins.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

/**
 * 时间工具类
 * 
 */
public class DateUtils extends org.apache.commons.lang3.time.DateUtils
{

    public static final String YYYY_YEAR_MM_MONTH = "yyyy年MM月";

    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    public static final String YYYY_MM = "yyyy-MM";

    public static final String YYYYMMDD = "yyyyMMdd";

    public static final String YYYYMMDDHHMMSS = "yyyyMMddHHmmss";

    public static final String YYYYMMDDHHMMSSS = "yyyyMMddHHmmssSSS";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";

    private static final String[] parsePatterns = {
            "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm", "yyyy-MM", 
            "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm", "yyyy/MM",
            "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm", "yyyy.MM",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX"};

    public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);

    private static final ZoneId DEFAULT_ZONE_ID = ZoneOffset.ofHours(8);

    /**
     * 获取当前Date型日期
     * 
     * @return Date() 当前日期
     */
    public static Date getNowDate()
    {
        return Date.from(Instant.now());
    }

    /**
     * 获取当前日期, 默认格式为yyyy-MM-dd
     * 
     * @return String
     */
    public static String getDate()
    {
        return dateTimeNow(YYYY_MM_DD);
    }

    /**
     * yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static String getDateTime()
    {
        return dateTimeNow(YYYY_MM_DD_HH_MM_SS);
    }

    public static String dateTimeNow()
    {
        return dateTimeNow(YYYYMMDDHHMMSS);
    }

    public static String dateTimeNow(final String format)
    {
        return parseDateToStr(format, getNowDate());
    }

    public static String dateTime(final Date date)
    {
        return parseDateToStr(YYYY_MM_DD, date);
    }

    public static String parseDateToStr(final String format, final Date date)
    {
        return parseDateToStr(format,date.toInstant().atZone(DEFAULT_ZONE_ID));
    }

    public static String parseDateToStr(String format, TemporalAccessor temporal)
    {
        return DateTimeFormatter.ofPattern(format).format(temporal);
    }

    public static Date dateTime(final String format, final String dateStr)
    {
        try
        {
            return parseDate(dateStr, format);
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期路径 即年/月/日 如2018/08/08
     */
    public static String datePath()
    {
        return DateTimeFormatter.ofPattern("yyyy/MM/dd").format(LocalDateTime.now().atZone(DEFAULT_ZONE_ID));
    }

    /**
     * 日期路径 即年/月/日 如20180808
     */
    public static String dateTime()
    {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(LocalDateTime.now().atZone(DEFAULT_ZONE_ID));
    }

    /**
     * 日期型字符串转化为日期 格式
     */
    public static Date parseDate(Object str)
    {
        if (str == null)
        {
            return null;
        }
        try
        {
            return parseDate(str.toString(), parsePatterns);
        }
        catch (ParseException e)
        {
            return null;
        }
    }
    

    /**
     * 计算两个时间差
     */
    public static String getDatePoor(Date endDate, Date nowDate)
    {
        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        return day + "天" + hour + "小时" + min + "分钟";
    }


    /**
     * 获取较大的时间
     */
    public static Date getMaxDate(Date d1, Date d2){
        return compareDate(d1, d2, false);
    }

    /**
     * 获取较小的时间
     */
    public static Date getMinDate(Date d1, Date d2){
        return compareDate(d1, d2, true);
    }

    /**
     * 时间大小比较
     */
    public static Date compareDate(Date d1, Date d2,boolean asc){
        if (null == d1 && null == d2) return null;
        if (null == d1) return d2;
        if (null == d2) return d1;

        return asc ?  d1.before(d2) ? d1 : d2
                :  d1.after(d2)  ? d1 : d2;
    }


    public static Long millsBetweenNowAndTheTime(int plusDays, int hour) {
        // 获取当前日期
        LocalDate today = LocalDate.now();
        // 获取 plusDays天 的日期
        LocalDate tomorrow = today.plusDays(plusDays);
        // 创建代表 hour点minute分 的LocalTime对象
        LocalTime timeHourMinute = LocalTime.of(hour, 0);
        // 将日期和时间组合成ZonedDateTime对象
        ZonedDateTime theTime = ZonedDateTime.of(tomorrow, timeHourMinute, ZoneId.systemDefault());
        // 输出结果
        return Math.abs(
                        theTime.toInstant().toEpochMilli() - Instant.now().toEpochMilli()
        );
    }


}
