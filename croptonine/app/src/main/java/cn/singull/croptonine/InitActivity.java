package cn.singull.croptonine;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.soundcloud.android.crop.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.singull.application.MyApplication;
import cn.singull.helper.DataHelper;
import cn.singull.helper.HttpProcessor;
import cn.singull.service.MaterialService;

public class InitActivity extends AppCompatActivity {
    //下载图片的总数
    private int download_num;
    //下载完成的数量
    private int finish_num;
    private Timer timer;
    private String code;
    private TextView textView;
    private LinearLayout linearProgress;
    private TextView textProgress;
    private MaterialBroadcast broadcast;
    private Comparator<File> comparator = new Comparator<File>() {
        @Override
        public int compare(File o1, File o2) {
            return o2.getName().compareTo(o1.getName());
        }
    };
    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            android.util.Log.i("conn:", "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            android.util.Log.i("conn:", "onServiceDisconnected");
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        //百度自动更新sdk（静默更新）
        BDAutoUpdateSDK.silenceUpdateAction(getApplicationContext());
        initView();
        initData();
        initOpea();
        broadcast = new MaterialBroadcast();
        IntentFilter filter = new IntentFilter();
        filter.addAction("cn.singull.croptonine.init");
        InitActivity.this.registerReceiver(broadcast, filter);
        Intent intent = new Intent(this, MaterialService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
    }

    private void initView() {
        textView = (TextView) findViewById(R.id.init_text_code);
        linearProgress = (LinearLayout) findViewById(R.id.init_linearProgress);
        textProgress = (TextView) findViewById(R.id.init_textProgress);
        try {
            code = getPackageManager().getPackageInfo(getPackageName(), PackageInfo.INSTALL_LOCATION_AUTO).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        textView.setText(code);
    }

    private void initData() {
        File fileTemplate = new File(DataHelper.getTemplatePath(this));
        File fileFrame = new File(DataHelper.getFramePath(this));
        if (!fileFrame.exists()) {
            fileFrame.mkdirs();
        }
        if (!fileTemplate.exists()) {
            fileTemplate.mkdirs();
        }
//        HttpProcessor.haveNewMaterial(fileFrame, fileTemplate);
        MyApplication.appli.setFramePaths(loadFrameFile(fileFrame));
        MyApplication.appli.setTemplatePaths(loadTemplateFile(fileTemplate));
    }
    private void initOpea() {

    }
    /**
     * @param fileFrame
     * @return
     */
    private List<List<File>> loadFrameFile(File fileFrame) {
        //frame文件夹下的子文件夹
        List<File> frames = Arrays.asList(fileFrame.listFiles());
        //子文件夹们的排序
        Collections.sort(frames);
        List<List<File>> framePath = new ArrayList<>();
        for (File f : frames) {
            if (!f.isDirectory()) {
                continue;
            }
            List<File> list = new ArrayList<>();
            for (File i : f.listFiles()) {
                if (i.isFile()) {
                    list.add(i);
                }
            }
            Collections.sort(list, comparator);
        }
        return framePath;
    }

    /**
     * @param fileTemplate
     * @return
     */
    private List<File> loadTemplateFile(File fileTemplate) {
        //template文件夹下的子文件夹
        List<File> frames = Arrays.asList(fileTemplate.listFiles());
        for (File f : frames) {
            if (!f.isFile())
                frames.remove(f);
        }
        //子文件夹们的排序
        Collections.sort(frames);
        return frames;
    }

    @Override
    protected void onDestroy() {
        unbindService(conn);
        unregisterReceiver(broadcast);
        super.onDestroy();
    }

    public void intentToMain() {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(InitActivity.this, MainActivity.class);
                        startActivity(intent);
                        timer.cancel();
                        InitActivity.this.finish();
                    }
                });
            }
        }, 500, 3000);
    }
    public class MaterialBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            android.util.Log.i(" MaterialBroadcast:", "onReceive");
            switch (intent.getStringExtra(MaterialService.MESSAGE)) {
                case MaterialService.MESSAGE_WIFI_NONE:
                    textProgress.setText("没有检测到WIFI环境,启动玩图宝");
                    intentToMain();
                    break;
                case MaterialService.MESSAGE_CHECK_NONE:
                    textProgress.setText("没有新的素材,启动玩图宝");
                    intentToMain();
                    break;
                case MaterialService.MESSAGE_DOWNLOADING:
                    download_num = intent.getIntExtra(MaterialService.MESSAGE_NUM, 0);
                    finish_num = 0;
                    textProgress.setText("正在下载中...(" + finish_num + "/" + download_num + ")");
                    break;
                case MaterialService.MESSAGE_DOWNLOADED:
                    textProgress.setText("下载完成,启动玩图宝");
                    intentToMain();
                    break;
                case MaterialService.MESSAGE_ONE_LOAD_FINISH:
                    finish_num++;
                    textProgress.setText("正在下载中...(" + finish_num + "/" + download_num + ")");
                    break;
            }
        }
    }

}
