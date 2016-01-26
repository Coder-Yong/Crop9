package cn.singull.helper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xinou03 on 2016/1/21 0021.
 */
public class HttpProcessor {
    public static void haveNewMaterial(File frame, File template) {
        JSONObject json = new JSONObject();
        try {
            json.put("frame", HttpHelper.filesToJson(frame));
            json.put("template", HttpHelper.filesToJson(template));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        android.util.Log.i("json:",json.toString());
    }
    public static void updatePicture(String path){

    }
}
