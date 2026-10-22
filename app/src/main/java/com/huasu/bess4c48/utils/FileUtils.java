package com.huasu.bess4c48.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.huasu.bess4c48.MyApplication;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 数据库文件、记录的日志等保存在内置sdcard中，电池历史数据放在外置sd卡中
 */
public class FileUtils {


    public static String getDiskCacheDir() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            if (MyApplication.mApplication.getExternalCacheDir() != null) {
                return MyApplication.mApplication.getExternalCacheDir().getPath();
            }
        }
        return MyApplication.mApplication.getCacheDir().getPath();
    }


    public static String getAppFileDir(String folderName) {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            File folder = MyApplication.mApplication.getExternalFilesDir(folderName);
            if (folder != null) {
                return folder.getAbsolutePath();
            }
        }
        return MyApplication.mApplication.getFilesDir().getPath();
    }


    private static String getExtSDCardPath() {
        return getSDCardPath();
    }

    /**
     * /**
     * 获取内置sd卡根路径
     *
     * @return
     */
    private static String getSDCardPath() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return Environment.getExternalStorageDirectory().getAbsolutePath(); // filePath:  /sdcard/
        } else {
            return MyApplication.mApplication.getFilesDir().getPath(); // filePath:  /data/data/com.huasu.hmi.energy/files
        }
    }

    /**
     * 获取内置sd卡数据保存的根路径:/sdcard/c48
     *
     * @return
     */
    public static String getSDCardRootPath() {
        String folderPath = getSDCardPath() + File.separator + ConstantKit.BATTERY_DATA_FOLDER + File.separator;
        File folder = new File(folderPath);
        if (!folder.exists() || folder.isFile()) {
            folder.mkdir();
        }
        return folderPath;
    }

    /**
     * 获取导出告警数据的文件夹地址
     * /sdcard/c48/export/
     *
     * @return
     */
    public static String getExportDataPath() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return getAppFileDir("Export");
        }
        String exportPath = getSDCardRootPath() + ConstantKit.BATTERY_DATA_EXPORT_FOLDER + File.separator;
        File folder = new File(exportPath);
        if (!folder.exists() || folder.isFile()) {
            folder.mkdir();
        }
        return exportPath;
    }

    /**
     * 获取崩溃日志
     * /sdcard/c48/export/
     *
     * @return
     */
    public static String getCrashDataPath() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            return getAppFileDir("Crash");
        }
        String exportPath = getSDCardRootPath() + "Crash" + File.separator;
        File folder = new File(exportPath);
        if (!folder.exists() || folder.isFile()) {
            folder.mkdir();
        }
        return exportPath;
    }

    public static byte[] fileToBytes(String filePath) {
        File file = new File(filePath);
        // 校验文件是否存在且不是目录
        if (!file.exists() || file.isDirectory()) {
            return null;
        }

        FileInputStream fis = null;
        byte[] bytes = null;

        try {
            // 创建文件输入流
            fis = new FileInputStream(file);
            // 创建与文件大小匹配的字节数组
            bytes = new byte[(int) file.length()];
            // 将文件内容读取到字节数组中
            fis.read(bytes);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 关闭流，避免资源泄漏
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return bytes;
    }


    public static byte[] inputStreamToBytes(InputStream inputStream) {
        if (inputStream == null) {
            Log.e("StreamUtil", "InputStream is null");
            return null;
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024]; // 1KB缓冲区，可根据需求调整（如4096）
        int length;

        try {
            // 循环读取流内容到缓冲区，直到流结束
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            // 将输出流转换为字节数组
            return outputStream.toByteArray();
        } catch (IOException e) {
            Log.e("StreamUtil", "InputStream to bytes failed: " + e.getMessage());
            return null;
        } finally {
            // 关闭流，避免资源泄漏
            try {
                outputStream.close();
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 专为Android4.4设计的从Uri获取文件绝对路径，以前的方法已不好使
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
        try {
            // DocumentProvider
            if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                //一些三方的文件浏览器会进入到这个方法中，例如ES
                //QQ文件管理器不在此列
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    } else {
                    }
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {
                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{split[1]};

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            } else if ("content".equalsIgnoreCase(uri.getScheme())) {// MediaStore (and general)
                // Return the remote address
                if (isGooglePhotosUri(uri))
                    return uri.getLastPathSegment();

                if (isQQMediaDocument(uri)) {
                    String path = uri.getPath();
                    File fileDir = Environment.getExternalStorageDirectory();
                    File file = new File(fileDir, path.substring("/QQBrowser".length(), path.length()));
                    return file.exists() ? file.toString() : null;
                }

                return getDataColumn(context, uri, null, null);
            } else if ("file".equalsIgnoreCase(uri.getScheme())) {// File

                return uri.getPath();
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {

        Cursor cursor = null;
        final String column = MediaStore.MediaColumns.DATA;
        final String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    public static String getRealPathFromURI(Context context, Uri contentUri) {
        return getDataColumn(context, contentUri, null, null);
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    /**
     * 使用第三方qq文件管理器打开
     *
     * @param uri
     * @return
     */
    public static boolean isQQMediaDocument(Uri uri) {
        return "com.tencent.mtt.fileprovider".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
