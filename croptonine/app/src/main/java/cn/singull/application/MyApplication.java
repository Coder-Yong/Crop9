package cn.singull.application;

import android.app.Application;

import java.io.File;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinou03 on 2016/1/21 0021.
 */
public class MyApplication extends Application {
    private List<List<File>> framePaths;
    private List<File> templatePaths;
    public static MyApplication appli;

    @Override
    public void onCreate() {
        super.onCreate();
        appli = this;
    }

    public List<File> getTemplatePaths() {
        return templatePaths;
    }

    public void setTemplatePaths(List<File> templatePaths) {
        if (templatePaths.size() != 0) {
            this.templatePaths = templatePaths;
        } else {
            this.templatePaths = null;
        }
    }

    public List<List<File>> getFramePaths() {
        return framePaths;
    }

    public void setFramePaths(List<List<File>> framePaths) {
        if (framePaths.size() != 0) {
            this.framePaths = framePaths;
        } else {
            this.framePaths = null;
        }
    }
}
