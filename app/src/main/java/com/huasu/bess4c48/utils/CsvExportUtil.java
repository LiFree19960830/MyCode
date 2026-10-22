package com.huasu.bess4c48.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @ClassName CsvExportUtil
 * @Description TODO CSV是一种通用的、相对简单的文件格式,其文件以纯文本形式存储表格数据（数字和文本）。
 * @Description TODO 由于CSV是一种纯文本的文件,因此其具有以下几个特点
 * @Description TODO 1.支持追加模式写入，节省内存。
 * @Description TODO 2.CSV的文件行数没有限制。
 * @Description TODO 3.CSV是纯文本文件，可以使用任何文本编辑器进行编辑。
 * @Version 1.0
 * TODO 在模拟器上测试导出csv文件没有问题，但是在工控屏上导出文件内容为空,可以导出txt文件
 **/
public class CsvExportUtil {
    /**
     * CSV文件列分隔符
     */
    private static final String CSV_COLUMN_SEPARATOR = ",";

    /**
     * CSV文件行分隔符
     */
    private static final String CSV_ROW_SEPARATOR = "\r\n";


    /**
     * @param dataList 集合数据
     * @param titleArr 表头部数据
     * @param keys     表内容的键值
     * @param file
     */
    public static void doExport(List<Map<String, Object>> dataList, String[] titleArr, String keys, File file) throws Exception {

        //TODO 1.保证线程安全
        StringBuffer buf = new StringBuffer();
        String[] keyArr = null;
        keyArr = keys.split(",");
        //TODO 2.组装表头
        for (String title : titleArr) {
            buf.append(title).append(CSV_COLUMN_SEPARATOR);
        }
        buf.deleteCharAt(buf.length()-1);
        buf.append(CSV_ROW_SEPARATOR);
        //TODO 3.组装数据
        if (dataList != null && !dataList.isEmpty()) {
            for (Map<String, Object> data : dataList) {
                for (String key : keyArr) {
                    buf.append(data.get(key)).append(CSV_COLUMN_SEPARATOR);
                }
                buf.deleteCharAt(buf.length()-1);
                buf.append(CSV_ROW_SEPARATOR);
            }
        }
        byte[] content = buf.toString().getBytes("UTF-8");
        OutputStream os = new FileOutputStream(file);
        os.write(new byte[]{(byte) 0XEF, (byte) 0XBB, (byte) 0XBF});
        //TODO 4.写出响应
        os.write(content);
        os.flush();
        os.close();
    }

}

