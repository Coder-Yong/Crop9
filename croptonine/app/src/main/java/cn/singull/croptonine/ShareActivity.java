package cn.singull.croptonine;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.singull.adapter.GridViewAdapter;
import cn.singull.adapter.SharedGridViewAdapter;
import cn.singull.bean.ImageBean;
import cn.singull.helper.ToNineHelper;
import cn.singull.utils.UIUtils;

public class ShareActivity extends AppCompatActivity {
    private Button btnShare;
    private CheckBox checkBox;
    private GridView gridView;
    private ArrayList<Uri> nineUris;
    private List<ImageBean> beans;
    private SharedGridViewAdapter adapter;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);
        initView();
        initData();
        initViewOpea();
        initViewClick();
    }

    private void initView() {
        btnShare = (Button) findViewById(R.id.share_btn);
        checkBox = (CheckBox) findViewById(R.id.shared_checkbox);
        gridView = (GridView) findViewById(R.id.share_gridview);
        dialog = new ProgressDialog(this);
        dialog.setCanceledOnTouchOutside(false);
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        beans = (List<ImageBean>) bundle.getSerializable(MainActivity.INTENT_BEAN);
        nineUris = new ArrayList<>();
        adapter = new SharedGridViewAdapter(this, nineUris);
        gridView.setAdapter(adapter);
        dialog.setMessage("图片生成中...");
        dialog.show();
        final String time = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 9; i++) {
                    ImageBean b = beans.get(i);
                    nineUris.add(ToNineHelper.mergeImage(ShareActivity.this, b, ToNineHelper.NINE_FOLDER + "CropToNine_" + time + "_" + i + ".jpg"));
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        dialog.cancel();
                        UIUtils.toast(ShareActivity.this, "图片已保存至" + ToNineHelper.NINE_FOLDER);
                    }
                });
            }
        }).start();
    }

    private void initViewOpea() {

    }

    private void initViewClick() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ShareActivity.this, PagerActivity.class);
                intent.putExtra("count", position);
                intent.putExtra("nineUris", nineUris);
                startActivity(intent);
            }
        });
        btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkBox.isChecked()) {
                    dialog.setMessage("拼图生成中...");
                    dialog.show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(2000);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        sharedDemo("https://www.baidu.com/");
                                    }
                                });
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                } else {
                    sharedDemo(null);
                }
            }
        });
    }

    private void sharedDemo(String url) {
        ArrayList<Uri> nineUris = new ArrayList<>();
        for (ImageBean b : beans) {
            if (b.getImagePath() != null || !b.getImagePath().equals("")) {
                nineUris.add(Uri.fromFile(new File(b.getImagePath())));
            }
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //九张图片
        shareIntent.putParcelableArrayListExtra(
                Intent.EXTRA_STREAM, nineUris);
        shareIntent.setType("image/*");
        //附加文本
        if(url!=null){
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.shared_text) + url);
        }
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.shared_select)));
    }
}
