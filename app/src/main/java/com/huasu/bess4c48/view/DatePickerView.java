package com.huasu.bess4c48.view;

import android.content.Context;
import android.util.ArrayMap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.huasu.bess4c48.R;
import com.huasu.bess4c48.bean.SelectDate;
import com.huasu.bess4c48.utils.ToolUtil;
import com.huasu.bess4c48.view.wheelview.OnWheelChangedListener;
import com.huasu.bess4c48.view.wheelview.OnWheelScrollListener;
import com.huasu.bess4c48.view.wheelview.WheelView;
import com.huasu.bess4c48.view.wheelview.adapter.AbstractWheelTextAdapter1;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class DatePickerView extends LinearLayout {

    private static final int TAG_YEAR = 1;
    private static final int TAG_MONTH = 2;
    private static final int TAG_DAY = 3;
    private static final int TAG_HOUR = 4;
    private static final int TAG_MINUTE = 5;
    private static final int TAG_SECOND = 6;

    private static int YEAR_MIN = 2000;
    private static int YEAR_MAX = 2020;
    private static boolean isShowDHM = false;//是否显示时分秒

    private int year = YEAR_MIN;
    private int month = 1;
    private int day = 1;
    private int hour = 1;
    private int minute = 1;
    private int second = 0;

    private ArrayList<Integer> yearList;
    private ArrayList<Integer> monthList;
    private ArrayList<Integer> dayList;
    private ArrayList<Integer> hourList;
    private ArrayList<Integer> minuteList;
    private ArrayList<Integer> secondList;

    private Map<Integer, ArrayList<Integer>> monthMap = new ArrayMap<>();
    private Map<String, ArrayList<Integer>> dayMap = new ArrayMap<>();

    private DateTextAdapter yearAdapter;
    private DateTextAdapter monthAdapter;
    private DateTextAdapter dayAdapter;
    private DateTextAdapter hourAdapter;
    private DateTextAdapter minuteAdapter;
    private DateTextAdapter secondAdapter;

    private Context context;
    private WheelView wheelViewYear;
    private WheelView wheelViewMonth;
    private WheelView wheelViewDay;
    private WheelView wheelViewHour;
    private WheelView wheelViewMinute;
    private WheelView wheelViewSecond;
    private static String UNIT_YEAR;
    private static String UNIT_MONTH;
    private static String UNIT_DAY;
    private static String UNIT_HOUR;
    private static String UNIT_MINUTE;
    private static String UNIT_SECOND;

    public DatePickerView(Context context) {
        this(context, null);
    }

    public DatePickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DatePickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        UNIT_YEAR = context.getString(R.string.year);
        UNIT_MONTH = context.getString(R.string.month);
        UNIT_DAY = context.getString(R.string.day);
        UNIT_HOUR = context.getString(R.string.hour);
        UNIT_MINUTE = context.getString(R.string.minute);
        UNIT_SECOND = context.getString(R.string.second);
        init();
    }

    //只查询最近三个月的数据
    private void init() {
        Calendar endCalendar = Calendar.getInstance();
        Calendar startCalendar = Calendar.getInstance();
        startCalendar.add(Calendar.MONTH, -3);
        YEAR_MIN = startCalendar.get(Calendar.YEAR);
        int minMonth = startCalendar.get(Calendar.MONTH) + 1;
        int minDay = startCalendar.get(Calendar.DATE);

        YEAR_MAX = endCalendar.get(Calendar.YEAR);
        int maxMonth = endCalendar.get(Calendar.MONTH) + 1;
        int maxDay = endCalendar.get(Calendar.DATE);
//        yearList = DateUtil.getBetweenYearList(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());
        if (yearList == null)
            yearList = new ArrayList<>();
        yearList.clear();
        for (int year = YEAR_MIN; year <= YEAR_MAX; year++) {
            yearList.add(year);
        }
        monthList = new ArrayList<>();
        dayList = new ArrayList<>();
        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();
        secondList = new ArrayList<>();

        int startMonth = 1;
        int endMonth = 12;
        for (int year = YEAR_MIN; year <= YEAR_MAX; year++) {
            if (year == YEAR_MIN) {
                startMonth = minMonth;
            } else
                startMonth = 1;
            if (year == YEAR_MAX) {
                endMonth = maxMonth;
            } else
                endMonth = 12;
            ArrayList<Integer> months = new ArrayList<>();
            for (int month = startMonth; month <= endMonth; month++) {
                months.add(month);
                dayMap.put(year + "-" + month, generateDays(year, month));
            }
            monthMap.put(year, months);
        }
        for (int i = 0; i < 24; i++) {
            hourList.add(i);
        }
        for (int i = 0; i < 60; i++) {
            minuteList.add(i);
        }
        for (int i = 0; i < 60; i++) {
            secondList.add(i);
        }
        LayoutInflater.from(context).inflate(R.layout.widget_date_picker, this);

        wheelViewYear = findViewById(R.id.wheelViewYear);
        wheelViewMonth = findViewById(R.id.wheelViewMonth);
        wheelViewDay = findViewById(R.id.wheelViewDay);
        wheelViewHour = findViewById(R.id.wheelViewHour);
        wheelViewMinute = findViewById(R.id.wheelViewMinute);
        wheelViewSecond = findViewById(R.id.wheelViewSecond);
        wheelViewYear.setPaddingLeft(ToolUtil.dp2px(10));
        wheelViewYear.setCyclic(false);// 可循环滚动
        wheelViewMonth.setCyclic(false);// 可循环滚动

        yearAdapter = new DateTextAdapter(context, TAG_YEAR);
        monthAdapter = new DateTextAdapter(context, TAG_MONTH);
        dayAdapter = new DateTextAdapter(context, TAG_DAY);
        hourAdapter = new DateTextAdapter(context, TAG_HOUR);
        minuteAdapter = new DateTextAdapter(context, TAG_MINUTE);
        secondAdapter = new DateTextAdapter(context, TAG_SECOND);

        yearAdapter.setList(yearList);
        hourAdapter.setList(hourList);
        minuteAdapter.setList(minuteList);
        secondAdapter.setList(secondList);

        wheelViewYear.setViewAdapter(yearAdapter);
        wheelViewMonth.setViewAdapter(monthAdapter);
        wheelViewDay.setViewAdapter(dayAdapter);
        wheelViewHour.setViewAdapter(hourAdapter);
        wheelViewMinute.setViewAdapter(minuteAdapter);
        wheelViewSecond.setViewAdapter(secondAdapter);

        wheelViewYear.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                year = YEAR_MIN + newValue;
                monthList = monthMap.get(year);
                monthAdapter.setList(monthList);
                month = monthList.get(0);
                monthAdapter.currentIndex = 0;
                wheelViewMonth.setCurrentItem(monthAdapter.currentIndex);
                dayList = dayMap.get(year + "-" + month);
                dayAdapter.setList(dayList);
                day = dayList.get(0);
                dayAdapter.currentIndex = 0;
                wheelViewDay.setCurrentItem(dayAdapter.currentIndex);
                if (onSelectedChangedListener != null) {
                    onSelectedChangedListener.OnSelectedChanged(parseInt2Array(YEAR_MIN + oldValue, month, day, hour, minute, second), getSelectDate());
                }
            }
        });

        wheelViewMonth.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                month = monthList.get(0) + newValue;
                dayList = dayMap.get(year + "-" + month);
                dayAdapter.setList(dayList);
                day = dayList.get(0);
                dayAdapter.currentIndex = 0;
                wheelViewDay.setCurrentItem(dayAdapter.currentIndex);
                if (onSelectedChangedListener != null) {
                    onSelectedChangedListener.OnSelectedChanged(parseInt2Array(year, monthList.get(0) + oldValue, day, hour, minute, second), getSelectDate());
                }
            }
        });
        wheelViewDay.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                day = dayList.get(0) + newValue;
                if (onSelectedChangedListener != null) {
                    onSelectedChangedListener.OnSelectedChanged(parseInt2Array(year, month, oldValue + dayList.get(0), hour, minute, second), getSelectDate());
                }
            }
        });
        wheelViewHour.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                hour = newValue;
                if (onSelectedChangedListener != null) {
                    onSelectedChangedListener.OnSelectedChanged(parseInt2Array(year, month, day, oldValue, minute, second), getSelectDate());
                }
            }
        });
        wheelViewMinute.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                minute = newValue;
                if (onSelectedChangedListener != null) {
                    onSelectedChangedListener.OnSelectedChanged(parseInt2Array(year, month, day, hour, oldValue, second), getSelectDate());
                }
            }
        });
        wheelViewSecond.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                second = newValue;
                if (onSelectedChangedListener != null) {
                    onSelectedChangedListener.OnSelectedChanged(parseInt2Array(year, month, day, hour, minute, oldValue), getSelectDate());
                }
            }
        });
        wheelViewYear.addScrollingListener(onWheelScrollListener);
        wheelViewMonth.addScrollingListener(onWheelScrollListener);
        wheelViewDay.addScrollingListener(onWheelScrollListener);
        wheelViewHour.addScrollingListener(onWheelScrollListener);
        wheelViewMinute.addScrollingListener(onWheelScrollListener);
        wheelViewSecond.addScrollingListener(onWheelScrollListener);
    }

    OnWheelScrollListener onWheelScrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            setTextViewStyle();
        }
    };


    private ArrayList<Integer> generateDays(int year, int month, int minDay, int maxDay, int startMonth, int endMonth) {
        int startDay = 1;
        int endDay = 31;
        if (month == startMonth) {
            startDay = minDay;
        } else
            startDay = 1;
        if (month == endMonth) {
            endDay = maxDay;
        } else
            endDay = calcDay(year, month);

        ArrayList<Integer> days = new ArrayList<>();
        for (int day = startDay; day <= endDay; day++) {
            days.add(day);
        }
        return days;
    }

    private ArrayList<Integer> generateDays(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month-1);
        int maxDay = calendar.getActualMaximum(Calendar.DATE);
        ArrayList<Integer> days = new ArrayList<>();
        for (int day = 1; day <= maxDay; day++) {
            days.add(day);
        }
        return days;
    }

    private void setTextViewStyle() {
        setTextViewSize(getText(year, UNIT_YEAR), yearAdapter);
        setTextViewSize(getText(month, UNIT_MONTH) + "", monthAdapter);
        setTextViewSize(getText(day, UNIT_DAY) + "", dayAdapter);
        setTextViewSize(getText(hour, UNIT_HOUR) + "", hourAdapter);
        setTextViewSize(getText(minute, UNIT_MINUTE) + "", minuteAdapter);
        setTextViewSize(getText(second, UNIT_SECOND) + "", secondAdapter);
    }


    private void setTextViewSize(String currentItemText, AbstractWheelTextAdapter1 adapter) {
        ArrayList<View> arrayList = adapter.getTextViews();
        int size = arrayList.size();
        String currentText;
        TextView textView;
        boolean current;
        for (int i = 0; i < size; i++) {
            textView = (TextView) arrayList.get(i);
            currentText = textView.getText().toString();
            current = currentItemText.equals(currentText);
            textView.setTextColor(current ? adapter.selected_text_color : adapter.un_selected_text_color);
            textView.setTextSize(current ? adapter.selected_text_size : adapter.un_selected_text_size);
        }
    }


    private void generateTime(int monthAgo) {
        int minMonth = 0;
        int minDay = 0;

        int maxMonth = 0;
        int maxDay = 0;
        if (monthAgo > 0) {
            Calendar endCalendar = Calendar.getInstance();
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.add(Calendar.MONTH, -monthAgo);
            YEAR_MIN = startCalendar.get(Calendar.YEAR);
            minMonth = startCalendar.get(Calendar.MONTH) + 1;
            minDay = startCalendar.get(Calendar.DATE);

            YEAR_MAX = endCalendar.get(Calendar.YEAR);
            maxMonth = endCalendar.get(Calendar.MONTH) + 1;
            maxDay = endCalendar.get(Calendar.DATE);
            if (yearList == null)
                yearList = new ArrayList<>();
            yearList.clear();
            for (int year = YEAR_MIN; year <= YEAR_MAX; year++) {
                yearList.add(year);
            }
//            yearList = DateUtil.getBetweenYearList(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());
        } else {
            Calendar endCalendar = Calendar.getInstance();
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.set(Calendar.YEAR, 2000);
            startCalendar.set(Calendar.MONTH, 1);
            startCalendar.set(Calendar.DATE, 1);
            startCalendar.set(Calendar.HOUR, 0);
            startCalendar.set(Calendar.MINUTE, 0);
            startCalendar.set(Calendar.SECOND, 0);
            YEAR_MIN = startCalendar.get(Calendar.YEAR);
            minMonth = 1;
            minDay = 1;

            endCalendar.set(Calendar.YEAR, 2037);
            endCalendar.set(Calendar.MONTH, 12);
            endCalendar.set(Calendar.DATE, 31);
            endCalendar.set(Calendar.HOUR, 23);
            endCalendar.set(Calendar.MINUTE, 59);
            endCalendar.set(Calendar.SECOND, 59);
            YEAR_MAX = endCalendar.get(Calendar.YEAR);
            maxMonth = 12;
            maxDay = 31;
//            yearList = DateUtil.getBetweenYearList(startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis());
            if (yearList == null)
                yearList = new ArrayList<>();
            yearList.clear();
            for (int year = YEAR_MIN; year <= YEAR_MAX; year++) {
                yearList.add(year);
            }
        }
        monthList = new ArrayList<>();
        dayList = new ArrayList<>();
        hourList = new ArrayList<>();
        minuteList = new ArrayList<>();
        secondList = new ArrayList<>();

        int startMonth = 1;
        int endMonth = 12;
        for (int year = YEAR_MIN; year <= YEAR_MAX; year++) {
            if (year == YEAR_MIN) {
                startMonth = minMonth;
            } else
                startMonth = 1;
            if (year == YEAR_MAX) {
                endMonth = maxMonth;
            } else
                endMonth = 12;
            ArrayList<Integer> months = new ArrayList<>();
            for (int month = startMonth; month <= endMonth; month++) {
                months.add(month);
                dayMap.put(year + "-" + month, generateDays(year, month));
            }
            monthMap.put(year, months);
        }

        yearAdapter.setList(yearList);
    }

    /**
     * 设置控件的初始值
     *
     * @param selectDate   默认选中的时间
     * @param isShowDHM    是否显示日 时 分的选择
     * @param isShowSecond 是否显示秒的选择
     * @param monthAgo     控制列表显示最近几个月
     */
    public void setDate(SelectDate selectDate, boolean isShowDHM, boolean isShowSecond, int monthAgo) {
        generateTime(monthAgo);
        this.isShowDHM = isShowDHM;
        if (isShowSecond) {
            wheelViewSecond.setPaddingRight(ToolUtil.dp2px(16));
            wheelViewDay.setVisibility(View.VISIBLE);
            wheelViewHour.setVisibility(View.VISIBLE);
            wheelViewMinute.setVisibility(View.VISIBLE);
            wheelViewSecond.setVisibility(View.VISIBLE);
        } else {
            if (isShowDHM) {
                wheelViewMinute.setPaddingRight(ToolUtil.dp2px(16));
                wheelViewDay.setVisibility(View.VISIBLE);
                wheelViewHour.setVisibility(View.VISIBLE);
                wheelViewMinute.setVisibility(View.VISIBLE);
            } else {
                wheelViewMonth.setPaddingRight(ToolUtil.dp2px(16));
            }
        }

        year = selectDate.getYear();
        minute = selectDate.getMinute();
        hour = selectDate.getHour();
        second = selectDate.getSecond();

        //验证合法性
        if (year > YEAR_MAX || year < YEAR_MIN) {
            year = YEAR_MIN;
        }

        yearAdapter.currentIndex = DatePickerView.this.year - YEAR_MIN;
        wheelViewYear.setCurrentItem(yearAdapter.currentIndex);
        month = selectDate.getMonth();
        monthList = monthMap.get(year);
        if (month > monthList.get(monthList.size() - 1) || month < monthList.get(0)) {
            month = monthList.get(0);
        }
        for (int i = 0; i < monthList.size(); i++) {
            if (month == monthList.get(i)) {
                monthAdapter.currentIndex = i;
                break;
            }
        }
        monthAdapter.setList(monthList);
        wheelViewMonth.setCurrentItem(monthAdapter.currentIndex);

        dayList = dayMap.get(year + "-" + month);
        this.day = selectDate.getDay();
        if (day > dayList.get(dayList.size() - 1) || day < dayList.get(0)) {
            day = dayList.get(0);
        }
        for (int i = 0; i < dayList.size(); i++) {
            if (day == dayList.get(i)) {
                dayAdapter.currentIndex = i;
                break;
            }
        }
        dayAdapter.setList(dayList);
        wheelViewDay.setCurrentItem(dayAdapter.currentIndex);
        hourAdapter.currentIndex = DatePickerView.this.hour;
        minuteAdapter.currentIndex = DatePickerView.this.minute;
        secondAdapter.currentIndex = DatePickerView.this.second;
        wheelViewHour.setCurrentItem(hourAdapter.currentIndex);
        wheelViewMinute.setCurrentItem(minuteAdapter.currentIndex);
        wheelViewSecond.setCurrentItem(secondAdapter.currentIndex);
    }


    /**
     * 设置控件的初始值
     *
     * @param selectDate   默认选中的时间
     * @param isShowDHM    是否显示日 时 分的选择
     * @param isShowMinute 是否显示分的选择
     * @param isShowSecond 是否显示秒的选择
     * @param monthAgo     控制列表显示最近几个月
     */
    public void setDate(SelectDate selectDate, boolean isShowDHM, boolean isShowMinute, boolean isShowSecond, int monthAgo) {
        generateTime(monthAgo);
        this.isShowDHM = isShowDHM;
        if (isShowSecond) {
            wheelViewSecond.setPaddingRight(ToolUtil.dp2px(16));
            wheelViewDay.setVisibility(View.VISIBLE);
            wheelViewHour.setVisibility(View.VISIBLE);
            wheelViewMinute.setVisibility(View.VISIBLE);
            wheelViewSecond.setVisibility(View.VISIBLE);
        } else {
            if (isShowDHM) {
                wheelViewMinute.setPaddingRight(ToolUtil.dp2px(16));
                wheelViewDay.setVisibility(View.VISIBLE);
                wheelViewHour.setVisibility(View.VISIBLE);
                wheelViewMinute.setVisibility(isShowMinute? View.VISIBLE:GONE);
            } else {
                wheelViewMonth.setPaddingRight(ToolUtil.dp2px(16));
            }
        }

        year = selectDate.getYear();
        minute = selectDate.getMinute();
        hour = selectDate.getHour();
        second = selectDate.getSecond();

        //验证合法性
        if (year > YEAR_MAX || year < YEAR_MIN) {
            year = YEAR_MIN;
        }

        yearAdapter.currentIndex = DatePickerView.this.year - YEAR_MIN;
        wheelViewYear.setCurrentItem(yearAdapter.currentIndex);
        month = selectDate.getMonth();
        monthList = monthMap.get(year);
        if (month > monthList.get(monthList.size() - 1) || month < monthList.get(0)) {
            month = monthList.get(0);
        }
        for (int i = 0; i < monthList.size(); i++) {
            if (month == monthList.get(i)) {
                monthAdapter.currentIndex = i;
                break;
            }
        }
        monthAdapter.setList(monthList);
        wheelViewMonth.setCurrentItem(monthAdapter.currentIndex);

        dayList = dayMap.get(year + "-" + month);
        this.day = selectDate.getDay();
        if (day > dayList.get(dayList.size() - 1) || day < dayList.get(0)) {
            day = dayList.get(0);
        }
        for (int i = 0; i < dayList.size(); i++) {
            if (day == dayList.get(i)) {
                dayAdapter.currentIndex = i;
                break;
            }
        }
        dayAdapter.setList(dayList);
        wheelViewDay.setCurrentItem(dayAdapter.currentIndex);
        hourAdapter.currentIndex = DatePickerView.this.hour;
        minuteAdapter.currentIndex = DatePickerView.this.minute;
        secondAdapter.currentIndex = DatePickerView.this.second;
        wheelViewHour.setCurrentItem(hourAdapter.currentIndex);
        wheelViewMinute.setCurrentItem(minuteAdapter.currentIndex);
        wheelViewSecond.setCurrentItem(secondAdapter.currentIndex);
    }


    /**
     * 设置控件的初始值

     */
    public void setMinuteAndSecond() {
        wheelViewMinute.setVisibility(View.VISIBLE);
        wheelViewHour.setVisibility(View.VISIBLE);
        wheelViewYear.setVisibility(View.GONE);
        wheelViewMonth.setVisibility(View.GONE);
        wheelViewDay.setVisibility(View.GONE);
        wheelViewSecond.setVisibility(View.GONE);


        hourAdapter.currentIndex = DatePickerView.this.hour;
        minuteAdapter.currentIndex = DatePickerView.this.minute;
        wheelViewHour.setCurrentItem(hourAdapter.currentIndex);
        wheelViewMinute.setCurrentItem(minuteAdapter.currentIndex);
    }

    public void addOnSelectedChangingListener(OnSelectedChangedListener onSelectedChangedListener) {
        this.onSelectedChangedListener = onSelectedChangedListener;
    }

    private OnSelectedChangedListener onSelectedChangedListener;

    public interface OnSelectedChangedListener {
        void OnSelectedChanged(int[] oldValue, int[] newValue);
    }

    private int[] parseInt2Array(int year, int month, int day, int hour, int minute, int second) {
        return new int[]{year, month, day, hour, minute, second};
    }

    private int[] getSelectDate() {
        return new int[]{year, month, day, hour, minute, second};
    }

    private ArrayList<Integer> getDayList(int days) {
        if (days <= 0) {
            return null;
        }
        ArrayList<Integer> list = new ArrayList(days);
        for (int i = 1; i < days + 1; i++) {
            list.add(i);
        }
        return list;
    }

    private int calcDay(int year, int month) {
        int days = 0;
        switch (month) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                days = 30;
                break;
            case 2:
                days = (year % 400 == 0 || (year % 100 != 0 && year % 4 == 0)) ? 29 : 28;
                break;
        }
        return days;
    }

    private class DateTextAdapter extends AbstractWheelTextAdapter1 {
        ArrayList<Integer> list;
        private int tag;

        public DateTextAdapter(Context context, int tag) {
            super(context, R.layout.layout_date_item);
            this.tag = tag;
//            super(context, R.layout.item_birth_year);
//            setItemTextResource(R.id.tempValue);

//            item_birth_year.xml 内容如下
//            <?xml version="1.0" encoding="utf-8"?>
//            <LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
//                android:layout_width="match_parent"
//                android:layout_height="35dip"
//                android:gravity="center"
//                android:orientation="horizontal">
//                <TextView
//                   android:id="@+id/tempValue"
//                   android:layout_width="match_parent"
//                   android:layout_height="match_parent"
//                   android:gravity="center"
//                   android:textColor="#ffff0000"/>
//            </LinearLayout>
        }

        public void setList(ArrayList<Integer> list) {
            this.list = list;
            notifyDataChangedEvent();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list == null ? "" : getText(list.get(index));
        }

        private String getText(int text) {
            String desc = "";
            switch (tag) {
                case TAG_YEAR:
                    desc = getText(text, UNIT_YEAR);
                    break;
                case TAG_MONTH:
                    desc = getText(text, UNIT_MONTH);
                    break;
                case TAG_DAY:
                    desc = getText(text, UNIT_DAY);
                    break;
                case TAG_HOUR:
                    desc = getText(text, UNIT_HOUR);
                    break;
                case TAG_MINUTE:
                    desc = getText(text, UNIT_MINUTE);
                    break;
                case TAG_SECOND:
                    desc = getText(text, UNIT_SECOND);
                    break;
            }
            return desc;
        }

        public String getText(int text, String unit) {
            return text < 10 ? "0" + text + unit : text + unit;
        }

        @Override
        public int getItemsCount() {
            return list == null ? 0 : list.size();
        }
    }

    public String getText(int text, String unit) {
        return text < 10 ? "0" + text + unit : text + unit;
    }
}
