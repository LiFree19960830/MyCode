package com.huasu.bess4c48.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.github.mikephil.charting.data.PieEntry;
import com.huasu.bess4c48.MyApplication;
import com.huasu.bess4c48.utils.constant.BaseData;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


@SuppressLint("NewApi")
public class ToolUtil {
    public final static int KBCount = 1024;
    public final static int MBCount = KBCount * 1024;

    // 如强制隐藏软键盘
    public static void hideInPut(Activity act) {
        InputMethodManager imm = (InputMethodManager) act.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(act.getWindow().getDecorView().getWindowToken(), 0);
        }
    }


    /**
     * 隐藏软键盘(只适用于Activity，不适用于Fragment)
     */
    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }


    /**
     * 隐藏软键盘(可用于Activity，Fragment)
     */
    public static void hideSoftKeyboard(View view) {
        if (view == null) return;
        InputMethodManager inputMethodManager = (InputMethodManager) MyApplication.mApplication.getSystemService(Activity.INPUT_METHOD_SERVICE);

        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }


    @SuppressLint("MissingPermission")
    public static String getDeviceId() {
        @SuppressLint("ServiceCast") TelephonyManager tm = (TelephonyManager) MyApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取屏幕密度
     *
     * @return
     */
    public static float getScreenDensity() {
        DisplayMetrics dm = MyApplication.mApplication.getResources().getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获取屏幕大小
     *
     * @param activity
     */
    public static void initDisplayMetrics(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics(); // 屏幕分辨率容器

        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        // 获得手机的宽度和高度像素单位为px
        BaseData.ScreenWidth = dm.widthPixels;
        BaseData.ScreenHeight = dm.heightPixels;
        System.out.println("手机屏幕分辨率为:" + dm.widthPixels + "* " + dm.heightPixels + "*" + getScreenDensity() + ";" + dm);
    }

    /**
     * 获取屏幕大小
     */
    public static void initDisplayRealMetrics() {
        WindowManager windowManager =
                (WindowManager) MyApplication.mApplication.getSystemService(Context.
                        WINDOW_SERVICE);
        final Display display = windowManager.getDefaultDisplay();
        Point outPoint = new Point();
        if (checkDeviceHasNavigationBar())
            display.getRealSize(outPoint);
        else
       /* if (Build.VERSION.SDK_INT >= 19) {
            // 可能有虚拟按键的情况
            display.getRealSize(outPoint);
        } else*/ {
            // 不可能有虚拟按键
            display.getSize(outPoint);
        }
        BaseData.ScreenRealWidth = outPoint.x;
        BaseData.ScreenRealHeight = outPoint.y;
    }

    public static int getScreenHeight(Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().heightPixels;
        }
        return 0;
    }

    public static int getScreenWidth(Context context) {
        if (context != null) {
            return context.getResources().getDisplayMetrics().widthPixels;
        }
        return 0;
    }

    private static final int PORTRAIT = 0;
    private static final int LANDSCAPE = 1;

    private volatile static Point[] mRealSizes = new Point[2];

    public static int getScreenRealHeight(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return getScreenHeight(context);
        }

        int orientation = context.getResources().getConfiguration().orientation;
        orientation = orientation == Configuration.ORIENTATION_PORTRAIT ? PORTRAIT : LANDSCAPE;

        if (mRealSizes[orientation] == null) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            if (windowManager == null) {
                return getScreenHeight(context);
            }
            Display display = windowManager.getDefaultDisplay();
            Point point = new Point();
            display.getRealSize(point);
            mRealSizes[orientation] = point;
        }
        return mRealSizes[orientation].y;
    }


    /**
     * 将dp转换为px
     *
     * @param dp
     * @return
     */
    public static int pxTOdp(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, MyApplication.mApplication.getResources().getDisplayMetrics());
    }

    // 转换dip为px
    public static int dp2px(int dip) {
        float scale = MyApplication.mApplication.getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f * (dip >= 0 ? 1 : -1));
    }

    //获取唯一识别码
    public static String getSerialNumber() {
        return Build.SERIAL;
    }

    /**
     * 防止InputMethodManager内存泄漏
     *
     * @param destContext
     */
    public static void fixInputMethodManagerLeak(Context destContext) {
        fixLeak(destContext);
    }

    private static Field field;
    private static boolean hasField = true;

    public static void fixLeak(Context context) {
        if (!hasField) {
            return;
        }
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[]{"mNextView"};
        for (String param : arr) {
            try {
                if (field == null) {
                    field = imm.getClass().getDeclaredField(param);
                }
                if (field == null) {
                    hasField = false;
                }
                if (field != null) {
                    field.setAccessible(true);
                    field.set(imm, null);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    /**
     * 获取statusBar高度
     *
     * @param mContext
     * @return
     */
    public static int getStatusBarHeight(Context mContext) {
        int height = 0;
        try {
            Class c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            height = mContext.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }


    /**
     * 获取statusBar高度
     *
     * @param mContext
     * @return
     */
    public static int getNavigationBarHeight(Context mContext) {
        int height = 0;
        if (!checkDeviceHasNavigationBar())
            return height;
        try {
            Class c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("navigation_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            height = mContext.getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return height;
    }

    //获取是否存在NavigationBar
    public static boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = MyApplication.mApplication.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;

    }


    /**
     * 得到内置存储空间的总容量
     *
     * @return
     */
    public static long getInternalToatalSpace() {
        String path = Environment.getDataDirectory().getPath();
        StatFs statFs = new StatFs(path);
        long blockSize = statFs.getBlockSize();//返回Int ，获取当前系统Block占的大小，以字节为单位
        long totalBlocks = statFs.getBlockCount(); //返回Int，获取当前系统存储空间总的Block数。
        long availableBytes = statFs.getAvailableBytes();  //返回Int，获取当前系统可用存储空间的字节数。
        return availableBytes;
    }


    public static <T> T getUnique(List<T> list) {
        if (list != null && !list.isEmpty()) {
            if (list.size() > 1) {
                // LogUtil.i("More than one result :" + list.size() + " " + list.get(0).getClass());
            }
            return list.get(0);
        }
        return null;
    }

    /**
     * 格式化数据
     *
     * @param data
     * @param decimal 小数点后保留多少位,例如decimal=2，那么pattern格式=#,##
     * @return
     */
    public static String formatString(BigDecimal data, int decimal) {
        if (data == null)
            return "--";
        String pattern = "0";
        if (data != null) {
            if (decimal > 0) {
                pattern = "0.";
                for (int i = 0; i < decimal; i++) {
                    pattern = pattern + "0";
                }
            }
        }
//        DecimalFormat df = new DecimalFormat("#,###");
        DecimalFormat df = new DecimalFormat(pattern);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(symbols);
        return df.format(data);
    }


    /**
     * 获取系统语言
     *
     * @return
     */
    public static String getSysLanguage() {
        Locale local = MyApplication.getInstance().getResources().getConfiguration().locale;
        if ("zh".equals(local.getLanguage()) && (!"CN".equals(local.getCountry())))
            return "TW";
        return local.getLanguage();
    }


    public static boolean isEnglish() {
        if (!"zh".equals(getSysLanguage()) && !"TW".equals(getSysLanguage()))
            return true;
        return false;
    }

    /**
     * 是否是俄语
     *
     * @return
     */
    public static boolean isRU() {
        if ("ru".equals(getSysLanguage()))
            return true;
        return false;
    }

    /**
     * 是否是中文
     *
     * @return
     */
    public static boolean isCN() {
        if ("zh".equals(getSysLanguage()) || "TW".equals(getSysLanguage()) || "HK".equals(getSysLanguage())) {
            return true;
        } else {
            return false;
        }
    }

    public static ArrayList<String> arrayToList(String[] array) {
        if (array == null || array.length == 0)
            return null;
        return new ArrayList(Arrays.asList(array));
    }

    public static String getExceptionAllInfo(Exception ex) {
        ByteArrayOutputStream out = null;
        PrintStream pout = null;
        String ret = "";
        try {
            out = new ByteArrayOutputStream();
            pout = new PrintStream(out);
            ex.printStackTrace(pout);
            ret = new String(out.toByteArray());
            out.close();
        } catch (Exception e) {
            return ex.getMessage();
        } finally {
            if (pout != null) {
                pout.close();
            }
        }
        return ret;
    }


    public static void fullScreen(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }

    /**
     * 生成设备编号
     *
     * @param deviceId       设备编号
     * @param parentDeviceId 父设备编号
     * @return
     */
    public static String generateDeviceId(String deviceId, String parentDeviceId) {
        if (TextUtils.isEmpty(deviceId) || TextUtils.isEmpty(parentDeviceId))
            return "";
        return parentDeviceId + "-" + deviceId;
    }


    /**
     * 获取剩余存储空间
     *
     * @return
     */
    public static long getAvailableSize() {
        StatFs sf = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long blockSize = sf.getBlockSizeLong();
        long totalBlocks = sf.getBlockCountLong();
        long availableBlocks = sf.getAvailableBlocksLong();
        long availableSize = blockSize * availableBlocks / 1024;   //KB

        return availableSize;
    }


    public static String getMd5FromFile(File file) {
        if (file == null) {
            return "";
        }
        String value = null;
        FileInputStream in = null;
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0,
                    file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());
            value = bi.toString(16);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }


    static ThreadLocal<SimpleDateFormat> getDataTimeFormatter = new ThreadLocal<SimpleDateFormat>() {

        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        }
    };

    public static String formatDate(Long time) {
        if (time != null) {
            String timeString = getDataTimeFormatter.get().format(new Date(time));
            return timeString;
        }
        return "--";
    }

    /**
     * 校验权限
     *
     * @param view       控件
     * @param visibility 控件是否可见
     */
    public static void inspectionAuthority(View view, int visibility) {
        if (view != null) {
            if (MyApplication.getInstance().isSysAdmin()) {
                view.setEnabled(true);
                view.setVisibility(View.VISIBLE);
            } else {
                view.setEnabled(false);
                view.setVisibility(visibility);
            }
        }
    }

    // 给数字num的第bit位（从0开始）置1
    public static int setBitTo1(int num, int bit) {
        // 构造掩码：1左移bit位（比如bit=2，掩码就是00000100）
        int mask = 1 << bit;
        // 按位或：目标位会变成1，其余位不变
        return num | mask;
    }

    // 给数字num的第bit位（从0开始）置0
    public static int setBitTo0(int num, int bit) {
        // 构造掩码：~(1<<bit) （比如bit=2，掩码就是11111011）
        int mask = ~(1 << bit);
        // 按位与：目标位会变成0，其余位不变
        return num & mask;
    }

    /**
     * hex
     *
     * @param readHexVal
     * @param zoom
     * @param offsetVal
     * @param precision
     * @return
     */
    public static String convertHexToVal(String readHexVal, float zoom, int offsetVal, int precision) {
        if (TextUtils.isEmpty(readHexVal))
            return "--";
        try {
            int decimal = Integer.parseInt(readHexVal, 16);
            decimal = decimal - offsetVal;
            float after = decimal * zoom;
            String formattedString = String.format("%." + precision + "f", after);
            return formattedString;
        } catch (Exception e) {
            e.printStackTrace();
            return "--";
        }
    }

    /**
     * 获取版本号名称
     *
     * @return
     */
    public static String getVerName() {
        String verName = "";
        try {
            PackageInfo packageInfo;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // ✅ Android 13 (API33) 及以上：必须传标志位 PackageManager.GET_META_DATA
                packageInfo = MyApplication.mApplication.getPackageManager().getPackageInfo(MyApplication.mApplication.getPackageName(), PackageManager.PackageInfoFlags.of(0L));
            } else {
                // ✅ Android 12 及以下：传 0 即可
                packageInfo = MyApplication.mApplication.getPackageManager().getPackageInfo(MyApplication.mApplication.getPackageName(), 0);
            }
            verName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    /**
     * 时间格式转换-yyyy-MM-dd HH:mm:ss
     *
     * @param currentTime
     * @return
     */
    public static String getDataTime(Long currentTime) {
        if (currentTime == null)
            return "--";
        try {
            return getDataTimeFormatter.get().format(new Date(currentTime));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return currentTime.toString();
    }

    public static Calendar getLastYear(int number) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -number);
        return cal;
    }

    public static String formatNumber(int number) {
        return String.format(Locale.getDefault(), "%02d", number);
    }

    public static List<byte[]> splitBytesBy50(byte[] source) {
        int SPLIT_LENGTH = 100;
        List<byte[]> resultList = new ArrayList<>();
        // 空数组直接返回空集合，避免空指针
        if (source == null || source.length == 0) {
            return resultList;
        }
        // 1. 计算需要分割的总段数
        int totalLength = source.length;
        int totalCount = (totalLength + SPLIT_LENGTH - 1) / SPLIT_LENGTH;

        // 2. 循环分段截取
        for (int i = 0; i < totalCount; i++) {
            // 计算当前段的起始索引
            int start = i * SPLIT_LENGTH;
            // 计算当前段的结束索引：防止最后一段越界，取「起始+50」和「数组总长度」的最小值
            int end = Math.min(start + SPLIT_LENGTH, totalLength);
            // 新建当前段的字节数组，长度 = end - start
            byte[] subBytes = new byte[end - start];
            // 核心方法：数组拷贝，完成截取
            System.arraycopy(source, start, subBytes, 0, end - start);
            // 存入结果集合
            resultList.add(subBytes);
        }
        return resultList;
    }

    public static void main(String[] args) {
        byte[] bytes = new byte[]{(byte) 0xAA, 0x55, (byte) 0x80, 0X10, 0x00, 0x00, 0x00, 0x05, (byte) 0x4e, 0x63, (byte) 0xf0, (byte) 0xf0, (byte) 0xe8};//TODO
        List<byte[]> list = splitBytesBy50(bytes);
        for (byte[] item : list) {
            System.out.println("item=" + ByteUtils.bytesToHexString(item));
        }
        ;
    }
}
