package cn.singull.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import cn.singull.croptonine.InitActivity;
import cn.singull.helper.HttpHelper;

public class MaterialService extends Service {
    public static final String INTENT_INIT = "cn.singull.croptonine.init";
    public static final String MESSAGE = "material";
    public static final String MESSAGE_NUM = "material_num";
    public static final String MESSAGE_WIFI_NONE = "wifi_none";
    public static final String MESSAGE_CHECK_NONE = "material_none";
    public static final String MESSAGE_DOWNLOADING = "material_download";
    public static final String MESSAGE_DOWNLOADED = "material_downloaded";
    public static final String MESSAGE_ONE_LOAD_FINISH = "one_load_finish";

    private Handler handler;

    public MaterialService() {
        android.util.Log.i("MaterialService:", "MaterialService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        android.util.Log.i("MaterialService:", "oncreate");
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        //当前不在wifi环境下
                        android.util.Log.i("MaterialService:", "handler-what0");
                        Intent i = new Intent(INTENT_INIT);
                        i.putExtra(MESSAGE, MESSAGE_WIFI_NONE);
                        sendBroadcast(i);
                        break;
                    case 1:
                        //找到资源，正在下载
                        android.util.Log.i("MaterialService:", "handler-what1");
                        Intent i1 = new Intent(INTENT_INIT);
                        i1.putExtra(MESSAGE, MESSAGE_DOWNLOADING);
                        i1.putExtra(MESSAGE_NUM, msg.getData().getInt(MESSAGE_NUM));
                        sendBroadcast(i1);
                        break;
                    case 2:
                        //下载完成
                        android.util.Log.i("MaterialService:", "handler-what2");
                        Intent i2 = new Intent(INTENT_INIT);
                        i2.putExtra(MESSAGE, MESSAGE_DOWNLOADED);
                        sendBroadcast(i2);
                        MaterialService.this.onDestroy();
                        break;
                    case 3:
                        //下载完一张图片
                        android.util.Log.i("MaterialService:", "handler-what3");
                        Intent i3 = new Intent(INTENT_INIT);
                        i3.putExtra(MESSAGE, MESSAGE_ONE_LOAD_FINISH);
                        sendBroadcast(i3);
                        break;
                    case 4:
                        //没有更新
                        android.util.Log.i("MaterialService:", "handler-what4");
                        Intent i4 = new Intent(INTENT_INIT);
                        i4.putExtra(MESSAGE, MESSAGE_CHECK_NONE);
                        sendBroadcast(i4);
                        break;
                }
            }
        };
    }

    @Override
    public IBinder onBind(Intent intent) {
        android.util.Log.i("MaterialService:", "onBind");
        if (!HttpHelper.isWifiConnected(this)) {
            handler.sendEmptyMessage(0);
            return null;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    Thread.sleep(1500);
                    handler.sendEmptyMessage(4);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return null;
    }


}
