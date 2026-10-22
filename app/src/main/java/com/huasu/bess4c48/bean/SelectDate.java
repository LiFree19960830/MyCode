package com.huasu.bess4c48.bean;

import com.huasu.bess4c48.utils.ToolUtil;

import java.io.Serializable;
import java.util.Calendar;

public class SelectDate implements Serializable {
    public int year;
    public int month;
    public int day;
    public int hour;
    public int minute;
    public int second=0;

    public SelectDate() {
    }

    public SelectDate(int year, int month, int day, int hour, int minute) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
    }

    public SelectDate(int year, int month, int day, int hour, int minute, int second) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }


    public void updateData(int[] value) {
        year = value[0];
        month = value[1];
        day = value[2];
        hour = value[3];
        minute = value[4];
        second = value[5];
    }

    public String getTime() {
        Calendar cal = Calendar.getInstance();
        // 如果想设置为某个日期，可以一次设置年月日时分秒，由于月份下标从0开始赋值月份要-1
        // cal.set(year, month, date, hourOfDay, minute, second);
        cal.set(year, month-1 , day, hour, minute, second);
        return ToolUtil.getDataTime(cal.getTimeInMillis());
    }

    public String getTime(int milliSecond) {
        Calendar cal = Calendar.getInstance();
        // 如果想设置为某个日期，可以一次设置年月日时分秒，由于月份下标从0开始赋值月份要-1
        // cal.set(year, month, date, hourOfDay, minute, second);
        cal.set(year, month-1 , day, hour, minute, second);
        cal.set(Calendar.MILLISECOND,milliSecond);
        return ToolUtil.getDataTime(cal.getTimeInMillis());
    }

    public Long getTimeLong() {
        Calendar cal = Calendar.getInstance();
        // 如果想设置为某个日期，可以一次设置年月日时分秒，由于月份下标从0开始赋值月份要-1
        // cal.set(year, month, date, hourOfDay, minute, second);
        cal.set(year, month-1 , day, hour, minute, second);
        return cal.getTimeInMillis();
    }


    public String getYMDTime() {
        return getYear() + "-" + String.format("%02d", month) + "-" + String.format("%02d", day);
    }


    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getHour() {
        return hour;
    }
    public String getHourStr() {
        return String.format("%02d", hour);
    }
    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }
    public String getMinuteStr() {
        return String.format("%02d", minute);
    }
    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}
