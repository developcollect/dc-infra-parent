package com.developcollect.core.utils;

import cn.hutool.core.date.DateUnit;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 时间工具类
 * 继承自hutool，在已有的对Date类型的日期操作的基础上
 * 扩展了对java8中新增的LocalDateTime、LocalDate、LocalTime的支持
 */
public class DateUtil extends cn.hutool.core.date.DateUtil {

    public static final String COMMON_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final String COMMON_DATE_PATTERN = "yyyy-MM-dd";
    public static final String COMMON_TIME_PATTERN = "HH:mm:ss";
    public static final String COMMON_HOUR_MINUTE_PATTERN = "HH:mm";

    private static final Map<String, DateTimeFormatter> DATETIME_FORMATTER_MAP;

    static {
        DATETIME_FORMATTER_MAP = new ConcurrentHashMap<>();
        DATETIME_FORMATTER_MAP.put(COMMON_DATE_TIME_PATTERN, DateTimeFormatter.ofPattern(COMMON_DATE_TIME_PATTERN));
        DATETIME_FORMATTER_MAP.put(COMMON_DATE_PATTERN, DateTimeFormatter.ofPattern(COMMON_DATE_PATTERN));
        DATETIME_FORMATTER_MAP.put(COMMON_TIME_PATTERN, DateTimeFormatter.ofPattern(COMMON_TIME_PATTERN));
    }

    /**
     * 以指定的格式来格式化日期和时间
     *
     * @param dateTime
     * @param format
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:30
     */
    public static String format(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = getFormatter(format);
        return dateTime.format(formatter);
    }

    /**
     * 以默认的格式(yyyy-MM-dd HH:mm:ss)来格式化日期和时间
     *
     * @param dateTime
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:30
     */
    public static String format(LocalDateTime dateTime) {
        return formatDateTime(dateTime);
    }

    /**
     * 以默认的格式(yyyy-MM-dd HH:mm:ss)来格式化日期和时间
     *
     * @param dateTime
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:35
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        return format(dateTime, COMMON_DATE_TIME_PATTERN);
    }

    /**
     * 以默认的日期格式(yyyy-MM-dd)来格式化日期和时间
     *
     * @param dateTime
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:35
     */
    public static String formatDate(LocalDateTime dateTime) {
        return format(dateTime, COMMON_DATE_PATTERN);
    }

    public static String formatDate(LocalDate date) {
        return format(date, COMMON_DATE_PATTERN);
    }

    /**
     * 以默认的时间格式(HH:mm:ss)来格式化日期和时间
     *
     * @param dateTime
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:35
     */
    public static String formatTime(LocalDateTime dateTime) {
        return format(dateTime, COMMON_TIME_PATTERN);
    }

    /**
     * 以默认的时间格式(HH:mm:ss)来格式化时间
     *
     * @param time
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:35
     */
    public static String formatTime(LocalTime time) {
        return format(time, COMMON_TIME_PATTERN);
    }

    /**
     * 以指定的格式来格式化日期
     *
     * @param date
     * @param format
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:29
     */
    public static String format(LocalDate date, String format) {
        DateTimeFormatter formatter = getFormatter(format);
        return date.format(formatter);
    }

    /**
     * 以默认的格式(yyyy-MM-dd)来格式化日期
     *
     * @param date
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:29
     */
    public static String format(LocalDate date) {
        return format(date, COMMON_DATE_PATTERN);
    }

    /**
     * 以指定的格式来格式化时间
     *
     * @param time
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:29
     */
    public static String format(LocalTime time, String format) {
        DateTimeFormatter formatter = getFormatter(format);
        return time.format(formatter);
    }

    /**
     * 以默认的格式(HH:mm:ss)来格式化时间
     *
     * @param time
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:29
     */
    public static String format(LocalTime time) {
        return format(time, COMMON_TIME_PATTERN);
    }

    /**
     * 以默认的格式(yyyy-MM-dd HH:mm:ss)来格式化日期
     *
     * @param date
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:29
     */
    public static String format(Date date) {
        return format(date, COMMON_DATE_TIME_PATTERN);
    }

    /**
     * 以指定格式来格式化当前日期和时间
     *
     * @param format
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:32
     */
    public static String formatNow(String format) {
        return format(LocalDateTime.now(), format);
    }

    /**
     * 以默认格式(yyyy-MM-dd HH:mm:ss)来格式化当前日期和时间
     *
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:32
     */
    public static String formatNow() {
        return format(LocalDateTime.now(), COMMON_DATE_TIME_PATTERN);
    }


    /**
     * 以指定的格式从字符串中解析出一个LocalDateTime对象
     *
     * @param charSequence
     * @param format
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:37
     */
    public static LocalDateTime parseLocalDateTime(CharSequence charSequence, String format) {
        String dateStr = charSequence.toString();
        DateTimeFormatter formatter = getFormatter(format);
        LocalDateTime ldt = LocalDateTime.parse(dateStr, formatter);
        return ldt;
    }

    /**
     * 以默认的格式(yyyy-MM-dd HH:mm:ss)从字符串中解析出一个LocalDateTime对象
     *
     * @param charSequence
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:37
     */
    public static LocalDateTime parseLocalDateTime(CharSequence charSequence) {
        return parseLocalDateTime(charSequence, COMMON_DATE_TIME_PATTERN);
    }

    /**
     * 以指定的格式从字符串中解析出一个LocalDate对象
     *
     * @param charSequence
     * @param format
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:37
     */
    public static LocalDate parseLocalDate(CharSequence charSequence, String format) {
        String dateStr = charSequence.toString();
        DateTimeFormatter formatter = getFormatter(format);
        LocalDate ld = LocalDate.parse(dateStr, formatter);
        return ld;
    }

    /**
     * 以默认的格式(yyyy-MM-dd)从字符串中解析出一个LocalDate对象
     *
     * @param charSequence
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:37
     */
    public static LocalDate parseLocalDate(CharSequence charSequence) {
        return parseLocalDate(charSequence, COMMON_DATE_PATTERN);
    }

    /**
     * 以指定的格式从字符串中解析出一个LocalTime对象
     *
     * @param charSequence
     * @param format
     * @return java.time.LocalTime
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:37
     */
    public static LocalTime parseLocalTime(CharSequence charSequence, String format) {
        String timeStr = charSequence.toString();
        DateTimeFormatter formatter = getFormatter(format);
        LocalTime lt = LocalTime.parse(timeStr, formatter);
        return lt;
    }

    /**
     * 以默认的格式(HH:mm:ss)从字符串中解析出一个LocalTime对象
     *
     * @param charSequence
     * @return java.time.LocalTime
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:37
     */
    public static LocalTime parseLocalTime(CharSequence charSequence) {
        return parseLocalTime(charSequence, COMMON_TIME_PATTERN);
    }


    /**
     * 以东8区时区取出指定日期距离1970-01-01T00:00:00Z.的毫秒数<br>
     *
     * @param localDateTime 日期
     * @return 毫秒数
     */
    public static long toMilli(LocalDateTime localDateTime) {
        return localDateTime.toInstant(zone()).toEpochMilli();
    }


    /**
     * 判断两个日期相差的时长，只保留绝对值
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @param unit      相差的单位：相差 天{@link DateUnit#DAY}、小时{@link DateUnit#HOUR} 等
     * @return 日期差
     * @author Zhu Kaixiao
     * @date 2019-10-16
     */
    public static long between(LocalDateTime beginDate, LocalDateTime endDate, DateUnit unit) {
        return between(beginDate, endDate, unit, true);
    }


    /**
     * 判断两个日期相差的时长
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @param unit      相差的单位：相差 天{@link DateUnit#DAY}、小时{@link DateUnit#HOUR} 等
     * @param isAbs     日期间隔是否只保留绝对值正数
     * @return 日期差
     * @author Zhu Kaixiao
     * @date 2019-10-16
     */
    public static long between(LocalDateTime beginDate, LocalDateTime endDate, DateUnit unit, boolean isAbs) {
        long beginMilli = toMilli(beginDate);
        long endMilli = toMilli(endDate);
        long diffMilli = endMilli - beginMilli;
        long r = diffMilli / unit.getMillis();

        return isAbs ? Math.abs(r) : r;
    }


    /**
     * 以东8区时区把毫秒时间戳转为日期<br>
     *
     * @param timestamp 毫秒时间戳
     * @return 日期
     */
    public static LocalDateTime localDateTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), zone());
    }


    /**
     * 将Date对象转换成LocalDateTime对象
     *
     * @param date
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:41
     */
    public static LocalDateTime localDateTime(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(date.toInstant(), zone());
        return localDateTime;
    }


    /**
     * 判断给定时间是否在指定的起始时间(含)和结束时间(含)之间
     * startTime <= time <= endTime
     *
     * @param time      时间
     * @param startTime 起始时间
     * @param endTime   结束时间
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:43
     */
    public static boolean isIn(LocalTime time, LocalTime startTime, LocalTime endTime) {
        final long start = startTime.toNanoOfDay();
        final long end = endTime.toNanoOfDay();
        final long specify = time.toNanoOfDay();
        return specify >= start && specify <= end;
    }

    /**
     * 判断给定日期是否在指定的起始日期(含)和结束日期(含)之间
     * startDate <= date <= endDate
     *
     * @param date      时间
     * @param startDate 起始时间
     * @param endDate   结束时间
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:43
     */
    public static boolean isIn(LocalDate date, LocalDate startDate, LocalDate endDate) {
        final long start = startDate.toEpochDay();
        final long end = endDate.toEpochDay();
        final long specify = date.toEpochDay();
        return specify >= start && specify <= end;
    }

    /**
     * 判断给定日期时间是否在指定的起始日期时间(含)和结束日期时间(含)之间
     * startDateTime <= dateTime <= endDateTime
     *
     * @param dateTime      时间
     * @param startDateTime 起始时间
     * @param endDateTime   结束时间
     * @return boolean
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:43
     */
    public static boolean isIn(LocalDateTime dateTime, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        final long start = toMilli(startDateTime);
        final long end = toMilli(endDateTime);
        final long specify = toMilli(dateTime);
        return specify >= start && specify <= end;
    }


    /**
     * 获取时区，当前固定为东八区
     *
     * @return java.time.ZoneOffset
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:46
     */
    private static ZoneOffset zone() {
        //ZoneOffset.of("+8") 东八区
        //ZoneId.systemDefault() 系统时区
        return ZoneOffset.of("+8");
    }

    /**
     * 根据格式化字符串获取一个日期格式化对象
     *
     * @param format
     * @return java.time.format.DateTimeFormatter
     * @author Zhu Kaixiao
     * @date 2020/10/20 14:47
     */
    private static DateTimeFormatter getFormatter(String format) {
        return DATETIME_FORMATTER_MAP.computeIfAbsent(format, DateTimeFormatter::ofPattern);
    }

    private static Pattern DURATION_FORMAT_PATTERN = Pattern.compile("(H*)([^H]*?)(m+)([^m]*?)(s{1,2})");


    /**
     * 时长格式化, 将时长以默认的格式转换为时分秒的格式
     *
     * @param milli 毫秒
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2020/11/17 11:37
     */
    public static String formatDuration(long milli) {
        return formatDuration(milli, "HH:mm:ss");
    }

    /**
     * 时长格式化, 将时长格式化为时分秒的格式
     * <p>
     * 例如：
     * 把视频时长转换成时间格式
     * HH:mm:ss
     *
     * @param milli  毫秒
     * @param format 格式  常用: HH:mm:ss, H:m:s, mm:ss, m:s
     *               小时占位符(H)可以省略, 但是分钟占位符(m)和秒占位符(s)不能缺少
     * @return
     */
    public static String formatDuration(long milli, String format) {
        Matcher matcher = DURATION_FORMAT_PATTERN.matcher(format);
        if (matcher.find()) {
            String hFormat = matcher.group(1);
            String sp1 = matcher.group(2);
            String mFormat = matcher.group(3);
            String sp2 = matcher.group(4);
            String sFormat = matcher.group(5);
            if (StrUtil.isNotBlank(mFormat) && StrUtil.isNotBlank(sFormat)) {
                final String mPlaceholder;
                final String sPlaceholder = sFormat.length() == 1 ? "%d" : "%02d";
                // 不需要小时
                if (StrUtil.isBlank(hFormat)) {
                    long m = milli / 1000 / 60;
                    long s = (milli - m * 1000 * 60) / 1000;
                    String mStr = String.valueOf(m);
                    if (mStr.length() < mFormat.length()) {
                        char[] pend = new char[mFormat.length() - mStr.length()];
                        Arrays.fill(pend, '0');
                        mStr = String.copyValueOf(pend) + mStr;

                    }
                    return matcher.replaceAll(String.format("%s" + sp2 + sPlaceholder, mStr, s));
                } else {
                    long h = milli / 1000 / 60 / 60;
                    long m = (milli - h * 1000 * 60 * 60) / 1000 / 60;
                    long s = (milli - h * 1000 * 60 * 60 - m * 1000 * 60) / 1000;
                    String hStr = String.valueOf(h);

                    if (hStr.length() < hFormat.length()) {
                        char[] pend = new char[hFormat.length() - hStr.length()];
                        Arrays.fill(pend, '0');
                        hStr = String.copyValueOf(pend) + hStr;
                    }
                    mPlaceholder = mFormat.length() == 1 ? "%d" : "%02d";
                    return matcher.replaceAll(String.format("%s" + sp1 + mPlaceholder + sp2 + sPlaceholder, hStr, m, s));
                }
            }
        }

        throw new IllegalArgumentException("格式字符串不正确");

    }

    /**
     * 时长格式化, 将时长格式化为时分秒的格式
     *
     * @param duration 时长
     * @param timeUnit 单位
     * @param format   格式
     * @return java.lang.String
     * @author Zhu Kaixiao
     * @date 2019/12/21 16:19
     */
    public static String formatDuration(long duration, TimeUnit timeUnit, String format) {
        return formatDuration(timeUnit.toMillis(duration), format);
    }


    /**
     * 今日起始时间（零点）
     * 获取时间格式为 2020/10/07 00:00:00.000
     *
     * @return
     */
    public static LocalDateTime beginOfDay() {
        return beginOfDay(LocalDateTime.now());
    }

    /**
     * 今日结束时间（23:59:59.999999999）
     * 获取时间格式为 2020/10/07 23:59:59.999999999
     *
     * @return
     */
    public static LocalDateTime endOfDay() {
        return endOfDay(LocalDateTime.now());
    }


    /**
     * 获取指定日期起始时间(零时零分零秒)
     * 例如传入的是 2020/04/28 12:15:58, 将返回2020/04/28 00:00:00
     *
     * @return
     */
    public static LocalDateTime beginOfDay(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 获取指定日期起始时间(零时零分零秒)
     * 例如传入的是 2020/04/28, 将返回2020/04/28 00:00:00
     *
     * @return
     */
    public static LocalDateTime beginOfDay(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    /**
     * 获取指定日期结束时间(23:59:59.999999999)
     * 例如传入的是 2020/04/28 12:15:58, 将返回2020/04/28 23:59:59.999999999
     *
     * @return
     */
    public static LocalDateTime endOfDay(LocalDateTime dateTime) {
        return endOfDay(dateTime.toLocalDate());
    }

    /**
     * 获取指定日期结束时间(23:59:59.999999999)
     * 例如传入的是 2020/04/28, 将返回2020/04/28 23:59:59.999999999
     *
     * @return
     */
    public static LocalDateTime endOfDay(LocalDate time) {
        return LocalDateTime.of(time, LocalTime.MAX);
    }

    /**
     * 获取当前月份的第一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/04/01 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfMonth() {
        return beginOfMonth(LocalDateTime.now());
    }

    /**
     * 获取指定日期所在月份的第一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/04/01 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfMonth(LocalDateTime dateTime) {
        final LocalDateTime firstDay = LocalDateTime.of(
                beginOfMonthLocalDate(dateTime.toLocalDate()),
                LocalTime.MIN
        );
        return firstDay;
    }

    /**
     * 获取当前月份的第一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/04/01
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfMonthLocalDate() {
        return beginOfMonthLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在月份的第一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/04/01 00:00:00
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfMonthLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfMonth());
        return firstDay;
    }


    /**
     * 获取当前日期所在月份的最后一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/04/30 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfMonth() {
        return endOfMonth(LocalDateTime.now());
    }

    /**
     * 获取当前日期所在月份的最后一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/04/30
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfMonthLocalDate() {
        return endOfMonthLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在月份的最后一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/04/30 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfMonth(LocalDateTime dateTime) {
        final LocalDateTime firstDay = LocalDateTime.of(
                endOfMonthLocalDate(dateTime.toLocalDate()),
                LocalTime.MAX
        );
        return firstDay;
    }

    /**
     * 获取指定日期所在月份的最后一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/04/30
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfMonthLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(TemporalAdjusters.lastDayOfMonth());
        return firstDay;
    }


    /**
     * 获取当前日期所在年份的第一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/01/01 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfYear() {
        return beginOfYear(LocalDateTime.now());
    }

    /**
     * 获取当前日期所在年份的第一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/01/01
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfYearLocalDate() {
        return beginOfYearLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在年份的第一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/01/01 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfYear(LocalDateTime dateTime) {
        final LocalDateTime firstDay = LocalDateTime.of(
                beginOfYearLocalDate(dateTime.toLocalDate()),
                LocalTime.MIN
        );
        return firstDay;
    }

    /**
     * 获取指定日期所在年份的第一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/01/01
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfYearLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(TemporalAdjusters.firstDayOfYear());
        return firstDay;
    }

    /**
     * 获取当前日期所在年份的最后一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/12/31 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfYear() {
        return endOfYear(LocalDateTime.now());
    }

    /**
     * 获取当前日期所在年份的最后一天
     * 例如现在是2020-04-28号，调用该方法获取的就是2020/12/31
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfYearLocalDate() {
        return endOfYearLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在年份的最后一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/12/31 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfYear(LocalDateTime dateTime) {
        final LocalDateTime firstDay = LocalDateTime.of(
                endOfYearLocalDate(dateTime.toLocalDate()),
                LocalTime.MAX
        );
        return firstDay;
    }

    /**
     * 获取指定日期所在年份的最后一天
     * 例如传入的是2020-04-28号，调用该方法获取的就是2020/12/31
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfYearLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(TemporalAdjusters.lastDayOfYear());
        return firstDay;
    }


    /**
     * 获取当前日期所在星期的第一天(星期一)
     * 例如现在是2020-04-28号(星期二)，调用该方法获取的就是2020/04/27 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfWeek() {
        return beginOfWeek(LocalDateTime.now());
    }

    /**
     * 获取指定日期所在星期的第一天(星期一)
     * 例如传入的是2020-04-28号(星期二)，调用该方法获取的就是2020/04/27 00:00:00
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime beginOfWeek(LocalDateTime date) {
        final LocalDateTime firstDay = LocalDateTime.of(beginOfWeekLocalDate(date.toLocalDate()), LocalTime.MIN);
        return firstDay;
    }

    /**
     * 获取当前日期所在星期的第一天(星期一)
     * 例如现在是2020-04-28号(星期二)，调用该方法获取的就是2020/04/27
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfWeekLocalDate() {
        return beginOfWeekLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在星期的第一天(星期一)
     * 例如传入的是2020-04-28号(星期二)，调用该方法获取的就是2020/04/27
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate beginOfWeekLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(DayOfWeek.MONDAY);
        return firstDay;
    }

    /**
     * 获取当前日期所在星期最后一天(星期天)
     * 例如现在是2020-04-28号(星期二)，调用该方法获取的就是2020/05/03
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfWeekLocalDate() {
        return beginOfWeekLocalDate(LocalDate.now());
    }

    /**
     * 获取指定日期所在星期最后一天(星期天)
     * 例如传入的是2020-04-28号(星期二)，调用该方法获取的就是2020/05/03
     *
     * @return java.time.LocalDate
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDate endOfWeekLocalDate(LocalDate date) {
        final LocalDate firstDay = date.with(DayOfWeek.SUNDAY);
        return firstDay;
    }


    /**
     * 获取当前日期所在星期最后一天(星期天)
     * 例如现在是2020-04-28号(星期二)，调用该方法获取的就是2020/05/03 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @author Zhu Kaixiao
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfWeek() {
        return endOfWeek(LocalDateTime.now());
    }

    /**
     * 获取指定日期所在星期最后一天(星期天)
     * 例如传入的是2020-04-28号(星期二)，调用该方法获取的就是2020/05/03 23:59:59.999999999
     *
     * @return java.time.LocalDateTime
     * @date 2020/4/28 12:00
     */
    public static LocalDateTime endOfWeek(LocalDateTime date) {
        final LocalDateTime firstDay = LocalDateTime.of(endOfWeekLocalDate(date.toLocalDate()), LocalTime.MAX);
        return firstDay;
    }

    /**
     * localDateTime 转 Date
     *
     * @param localDateTime
     * @return
     * @date 2021/1/5 14:00
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDate 转 Date
     *
     * @param localDate
     * @return
     * @date 2021/1/5 14:00
     */
    public static Date localDateToDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date 转 LocalDate
     *
     * @param date
     * @return
     * @date 2021/1/5 14:00
     */
    public static LocalDate dateToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * 取最大值
     *
     * @param dateTimes
     * @return
     */
    public static LocalDateTime max(LocalDateTime... dateTimes) {
        if (dateTimes == null) {
            return null;
        }
        if (dateTimes.length == 1) {
            return dateTimes[0];
        }
        return Arrays.stream(dateTimes).max(LocalDateTime::compareTo).get();
    }

    /**
     * 取最小值
     *
     * @param dateTimes
     * @return
     */
    public static LocalDateTime min(LocalDateTime... dateTimes) {
        if (dateTimes == null) {
            return null;
        }
        if (dateTimes.length == 1) {
            return dateTimes[0];
        }
        return Arrays.stream(dateTimes).min(LocalDateTime::compareTo).get();
    }

    /**
     * 获取“多久前”的时间
     *
     * @param date
     * @return
     */
    public static String fromatStr(LocalDateTime date) {
        String format = null;
        LocalDateTime now = LocalDateTime.now();
        int dateYear = date.getYear();
        int nowYear = now.getYear();
        if (dateYear < nowYear) {
            format = format(date, "yyyy年MM月dd日 HH:mm");
        } else if (dateYear == nowYear) {
            long dayBetween = between(date.toLocalDate().atTime(0, 0), now.toLocalDate().atTime(0, 0), DateUnit.DAY);
            if (dayBetween >= 1L && dayBetween < 2L) {
                format = format(date, "昨天 HH:mm");
            } else if (dayBetween >= 2L) {
                format = format(date, "MM月dd日 HH:mm");
            } else if (dayBetween == 0L) {
                long hourBetween = between(date, now, DateUnit.HOUR);
                if (hourBetween >= 1L) {
                    format = hourBetween + "小时前";
                } else if (hourBetween == 0L) {
                    long minBetween = between(date, now, DateUnit.MINUTE);
                    if (minBetween == 0L) {
                        long secondBetween = between(date, now, DateUnit.SECOND);
                        format = secondBetween + "秒前";
                    } else {
                        format = minBetween + "分钟前";
                    }
                }
            }
        } else {
            format = format(date, "yyyy年MM月dd日 HH:mm");
        }

        return format;
    }

    /**
     * 按指定的周期获取起始时间
     *
     * @param time   指定的时间，毫秒时间
     * @param period 周期
     * @return 指定周期的起始时间，毫秒时间
     */
    public static long beginOfPeriod(long time, long period) {
        long m = time % period;
        return time - m;
    }
}


