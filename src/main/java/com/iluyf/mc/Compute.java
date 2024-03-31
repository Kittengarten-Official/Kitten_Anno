package com.iluyf.mc;

import static net.kyori.adventure.text.format.NamedTextColor.*;
import static net.kyori.adventure.text.format.TextDecoration.*;

import net.kyori.adventure.text.Component;

public class Compute {
    final static short commonYearMonthCount = 27; // 平年的月数
    private final static short commonMonthDayCount = 20; // 小月的天数
    private final static short yearCycle = 29; // 闰年周期的年数
    private final static short monthCycle = 10; // 每周期的闰年数
    private final static short cycleLeapYearCount = 10; // 每周期的闰年数
    private final static short cycleGreaterMonthCount = 3; // 每周期的大月数
    private final static short yearCycleMonthCount = yearCycle * commonYearMonthCount + cycleLeapYearCount; // 闰年周期的月数
    private final static short monthCycleDayCount = monthCycle * commonMonthDayCount + cycleGreaterMonthCount; // 大月周期的天数
    private final static String numberString = "〇一二三四五六七八九";
    private final static String monthString = "寂雪海夜彗凉芷茸雨花梦音晴岚萝苏茜梨荷茶茉铃信瑶风叶霜奈";
    private final static String dayString = "初十廿";

    short yearCycleFirstmonthMonth[] = new short[yearCycle]; // 闰年周期中，每年的首月所处的月数戳
    short monthCycleFirstdayDay[] = new short[monthCycle]; // 大月周期中，每月的首日所处的天数戳

    class YearMonth {
        long year;
        short month;
    }

    class MonthDay {
        long month;
        short day;
    }

    public Component output(long annoDay) {
        return annoToString(annoDay);
    }

    // 计算出闰年和大月
    public Compute() {
        yearCycleFirstmonthMonthCompute();
        monthCycleFirstdayDayCompute();
    }

    // 计算闰年
    private final void yearCycleFirstmonthMonthCompute() {
        yearCycleFirstmonthMonth[0] = 0;
        for (short i = 1; yearCycle > i; ++i) {
            if (isCommonYear(i - 1))
                yearCycleFirstmonthMonth[i] = (short) (yearCycleFirstmonthMonth[i - 1] + commonYearMonthCount);
            else
                yearCycleFirstmonthMonth[i] = (short) (yearCycleFirstmonthMonth[i - 1] + commonYearMonthCount + 1);
        }
    }

    // 判断是否平年
    private final boolean isCommonYear(long year) {
        short netYear = (short) (year % yearCycle);
        final short years[] = { 1, 4, 7, 10, 13, 15, 18, 21, 24, 27 };
        for (short v : years) {
            if (netYear == v)
                return false;
        }
        return true;
    }

    // 计算大月
    private final void monthCycleFirstdayDayCompute() {
        monthCycleFirstdayDay[0] = 0;
        for (short i = 1; monthCycle > i; ++i) {
            if (isCommonMonth(i - 1))
                monthCycleFirstdayDay[i] = (short) (monthCycleFirstdayDay[i - 1] + commonMonthDayCount);
            else
                monthCycleFirstdayDay[i] = (short) (monthCycleFirstdayDay[i - 1] + commonMonthDayCount + 1);
        }
    }

    // 判断是否小月
    private final boolean isCommonMonth(long month) {
        short netMonth = (short) (month % monthCycle);
        final short months[] = { 1, 4, 8 };
        for (short v : months) {
            if (netMonth == v)
                return false;
        }
        return true;
    }

    // 返回月数戳对应的年数戳、月份
    private final YearMonth toYearMonth(long month) {
        YearMonth yearMonthNumber = new YearMonth();
        long yearCycleCount = month / yearCycleMonthCount;
        short netMonth = (short) (month % yearCycleMonthCount);
        short i = 0;
        while (yearCycle > i && netMonth >= yearCycleFirstmonthMonth[i])
            ++i;
        yearMonthNumber.year = yearCycleCount * yearCycle + i - 1;
        yearMonthNumber.month = (short) (netMonth - yearCycleFirstmonthMonth[i - 1] + 1);
        // 如果是闰年，月份序号整体减少 1
        if (!isCommonYear(yearMonthNumber.year)) {
            yearMonthNumber.month--;
        }
        return yearMonthNumber;
    }

    // 返回天数戳对应的月数戳、日期
    private final MonthDay toMonthDay(long day) {
        MonthDay monthDayNumber = new MonthDay();
        long monthCycleCount = day / monthCycleDayCount;
        short netDay = (short) (day % monthCycleDayCount);
        short i = 0;
        while (monthCycle > i && netDay >= monthCycleFirstdayDay[i])
            ++i;
        monthDayNumber.month = monthCycleCount * monthCycle + i - 1;
        monthDayNumber.day = (short) (netDay - monthCycleFirstdayDay[i - 1] + 1);
        return monthDayNumber;
    }

    private Component annoToString(long annoDay) {
        MonthDay monthDay = new MonthDay();
        monthDay = toMonthDay(annoDay);
        short dayNumber = (short) monthDay.day;
        YearMonth yearMonth = new YearMonth();
        yearMonth = toYearMonth(monthDay.month);
        short monthNumber = (short) yearMonth.month;
        long yearNumber = yearMonth.year + 1;
        if (0 < yearNumber && 0 <= monthNumber && 0 < dayNumber) {
            return Component.text()
                    .append(Component.text(yearConvert(yearNumber)).decoration(BOLD, true))
                    .append(monthConvert(monthNumber))
                    .append(Component.text(dayConvert((short) dayNumber))).build();
        } else {
            return Component.text("");
        }
    }

    public long[] annoToValue(long annoDay) {
        MonthDay monthDay = new MonthDay();
        monthDay = toMonthDay(annoDay);
        short dayNumber = (short) monthDay.day;
        YearMonth yearMonth = new YearMonth();
        yearMonth = toYearMonth(monthDay.month);
        short monthNumber = (short) yearMonth.month;
        long yearNumber = yearMonth.year;
        if (0 < yearNumber && 0 <= monthNumber && 0 < dayNumber) {
            long[] returnValue = { yearNumber, monthNumber, dayNumber };
            return returnValue;
        } else {
            long[] returnValue = { -1L, -1L, -1L };
            return returnValue;
        }
    }

    // 只允许传入 0～9 的整数
    private final String numberConvert(Short number) {
        return String.valueOf(numberString.charAt(number));
    }

    private final String yearConvert(long yearNumber) {
        short yearLength = (short) Long.toString(yearNumber).length();
        String returnValue = "";
        String yearConvertMemory[][] = new String[yearLength][2];
        for (long yearNumber_ = yearNumber; yearNumber_ > 0; yearNumber_ /= 10) {
            short Circulate = 0; // 0 表示个位，1 表示十位，以此类推
            yearConvertMemory[Circulate][0] = String.valueOf(yearNumber_ % 10);
            try {
                yearConvertMemory[Circulate][1] = numberConvert(
                        (short) Integer.parseInt(yearConvertMemory[Circulate][0]));
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            returnValue = yearConvertMemory[Circulate][1] + returnValue;
        }
        if (yearNumber == 1L) {
            return "世界树纪元元年";
        }
        return "世界树纪元" + returnValue + "年";
    }

    private final Component monthConvert(short monthNumber) {
        String str = String.valueOf(monthString.charAt(monthNumber)) + "月";
        if (7 > monthNumber) {
            return Component.text(str, AQUA);
        } else if (14 > monthNumber) {
            return Component.text(str, GREEN);
        } else if (21 > monthNumber) {
            return Component.text(str, RED);
        } else {
            return Component.text(str, GOLD);
        }
    }

    private final String dayConvert(short dayNumber) {
        String dayConvertMemory[][] = new String[2][2];
        dayConvertMemory[1][0] = String.valueOf(dayNumber / 10);
        dayConvertMemory[0][0] = String.valueOf(dayNumber % 10);
        dayConvertMemory[1][1] = String.valueOf(dayString.charAt(Integer.parseInt(dayConvertMemory[1][0])));
        dayConvertMemory[0][1] = numberConvert((short) Integer.parseInt(dayConvertMemory[0][0].toString()));
        switch (dayNumber) {
            case 10:
                return "初十";
            case 20:
                return "二十";
            default:
                return dayConvertMemory[1][1] + dayConvertMemory[0][1];
        }
    }
}