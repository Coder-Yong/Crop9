package cn.singull.bean;

import java.io.Serializable;

/**
 * Created by xinou03 on 2016/1/7 0007.
 */
public class ImageBean implements Serializable {
    private int frameId;
    private String imagePath;
    private String tempPath;

    public int getFrameId() {
        return frameId;
    }

    public void setFrameId(int frameId) {
        this.frameId = frameId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        this.tempPath = tempPath;
    }
}
