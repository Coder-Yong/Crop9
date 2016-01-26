package cn.singull.utils;

import android.text.TextUtils;

import java.io.File;
import java.io.IOException;

/**
 * Created by xinou03 on 2016/1/7 0007.
 */
public class FileUtils {
    /**
     * 创建一个文件，如果已存在，则删除后重新创建
     * @param f File对象
     */
    public static void addFile(File f){
        if(!f.exists()){
            if(!f.getParentFile().exists()){
                f.getParentFile().mkdirs();
            }
            try {
                f.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            f.delete();
            addFile(f);
        }
    }
    /**
     * 删除指定目录下文件及目录
     * @param filePath 目录路径
     * @param deleteThisPath 是否删除本身
     * @return
     */
    public static void deleteFolderFile(String filePath, boolean deleteThisPath) {
        if (!TextUtils.isEmpty(filePath)) {
            try {
                File file = new File(filePath);
                if (file.isDirectory()) {// 处理目录
                    File files[] = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        deleteFolderFile(files[i].getAbsolutePath(), true);
                    }
                }
                if (deleteThisPath) {
                    if (!file.isDirectory()) {// 如果是文件，删除
                        file.delete();
                    } else {// 目录
                        if (file.listFiles().length == 0) {// 目录下没有文件或者目录，删除
                            file.delete();
                        }
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
}
