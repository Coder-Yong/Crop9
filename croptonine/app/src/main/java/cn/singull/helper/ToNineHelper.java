package cn.singull.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.singull.utils.FileUtils;

/**
 * Created by xinou03 on 2016/1/7 0007.
 */
public class ToNineHelper {
    // SD卡路径
    public static final String SDCARD_PATH = Environment
            .getExternalStorageDirectory().toString();
    // 当用户选择保存时的存储路径
    public static final String NINE_FOLDER = SDCARD_PATH + File.separator
            + "CropToNine" + File.separator;
    // 裁剪后的9宫格存储路径
    public static final String NINE_FOLDER_DATA = SDCARD_PATH + File.separator
            + "CropToNine" + File.separator + "data" + File.separator;

    /**
     * 将bitmap裁成9份并保存
     *
     * @param bitmap 图片
     */
    public static List<String> toNine(Bitmap bitmap) {
        // TODO Auto-generated method stub
        List<String> listPath = new ArrayList<>();
        String time = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        int n = bitmap.getWidth() / 3;
        int num = 0;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                num++;
                int x = j * n;
                int y = i * n;
                Bitmap bitmap2 = Bitmap.createBitmap(bitmap, x, y, n, n);
                // 图像保存到文件中,同时将路径存入集合
                listPath.add(saveImage(bitmap2,time+"_"+num));
            }
        }
        return listPath;
    }

    /**
     * 保存图片
     * @param b Bitmap
     * @param timeAndNum 文件的中间名，可使用当前时间
     * @return
     */
    public static String saveImage(Bitmap b,String timeAndNum){
        FileOutputStream foutput = null;
        File file = new File(NINE_FOLDER_DATA + "CropToNine_" + timeAndNum + ".ctn");
        FileUtils.addFile(file);
        try {
            foutput = new FileOutputStream(file);
            b.compress(Bitmap.CompressFormat.JPEG, 100, foutput);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (null != foutput) {
                try {
                    foutput.close();
                    b.recycle();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file.getAbsolutePath();
    }
    /**
     * 通过URI取得绝对路径（兼容4.4以上）
     *
     * @param act Activity
     * @param uri URI
     * @return
     */
    public static String getImageAbsolutePath(Activity act, Uri uri) {

        if (!uri.getPath().contains("file://")) {
            return uri.getPath();
        }
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= 19) {
            return getImageAbsolutePath44(act, uri);
        } else {
            return getImageAbsolutePath0(act, uri);
        }
    }

    // 4。4以下
    private static String getImageAbsolutePath0(Activity act, Uri uri) {
        // can post image
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = act.managedQuery(uri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();

        return cursor.getString(column_index);
    }

    // 4.4及以上
    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static String getImageAbsolutePath44(Activity context, Uri imageUri) {
        if (context == null || imageUri == null)
            return null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
                && DocumentsContract.isDocumentUri(context, imageUri)) {
            if (isExternalStorageDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/"
                            + split[1];
                }
            } else if (isDownloadsDocument(imageUri)) {
                String id = DocumentsContract.getDocumentId(imageUri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"),
                        Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            } else if (isMediaDocument(imageUri)) {
                String docId = DocumentsContract.getDocumentId(imageUri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};
                return getDataColumn(context, contentUri, selection,
                        selectionArgs);
            }
        } // MediaStore (and general)
        else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(imageUri))
                return imageUri.getLastPathSegment();
            return getDataColumn(context, imageUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
            return imageUri.getPath();
        }
        return null;
    }

    public static String getDataColumn(Context context, Uri uri,
                                       String selection, String[] selectionArgs) {
        Cursor cursor = null;
        String column = MediaStore.Images.Media.DATA;
        String[] projection = {column};
        try {
            cursor = context.getContentResolver().query(uri, projection,
                    selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri
                .getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri
                .getAuthority());
    }
}
