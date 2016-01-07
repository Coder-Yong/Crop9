package cn.singull.utils;

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
}
