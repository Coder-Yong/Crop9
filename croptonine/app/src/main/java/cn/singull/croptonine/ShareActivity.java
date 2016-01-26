package cn.singull.croptonine;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import cn.singull.adapter.SharedRecyclerAdapter;
import cn.singull.bean.ImageBean;
import cn.singull.bean.ShareBean;
import cn.singull.helper.ToNineHelper;
import cn.singull.utils.UIUtils;

public class ShareActivity extends AppCompatActivity {
    //    private Button btnShare;
    private RecyclerView recyclerView;
    private CheckBox checkBox;
    private GridView gridView;
    private ArrayList<Uri> nineUris;
    private List<ImageBean> beans;
    private SharedGridViewAdapter adapter;
    private ProgressDialog dialog;
    private SharedRecyclerAdapter sharedRecyclerAdapter;
    private List<ShareBean> listShare;
    private String puzzleUrl = null;//拼图链接

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
//        btnShare = (Button) findViewById(R.id.share_btn);
        recyclerView = (RecyclerView) findViewById(R.id.shared_recycler);
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
        //分享列表
        listShare = new ArrayList<>();
        listShare.add(new ShareBean(R.mipmap.wechat, R.string.shared_wechat, R.string.wechat_package));
        listShare.add(new ShareBean(R.mipmap.weibo, R.string.shared_weibo, R.string.weibo_package));
        listShare.add(new ShareBean(R.mipmap.more, R.string.more, 0));

        sharedRecyclerAdapter = new SharedRecyclerAdapter(listShare, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(sharedRecyclerAdapter);
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
    }

    public void share(final int position) {
        if (checkBox.isChecked()) {
            if(puzzleUrl != null){
                share("https://www.baidu.com/", position);
                return;
            }
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
                                share("https://www.baidu.com/", position);
                                dialog.cancel();
                            }
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            share(null, position);
        }
    }

    /**
     * 分享
     *
     * @param url
     * @param position
     */
    private void share(String url, int position) {
        ShareBean bean = listShare.get(position);
        String packageName = null;
        /*
            如果用户选中了非“更多”选项
                取到包名
                如果用户没有安装对应的APP
                    提醒：错误
                    返回方法
         */
        if (bean.getSharedPackageId() != 0) {
            packageName = getResources().getString(bean.getSharedPackageId());
            if (!ToNineHelper.checkPackage(this, packageName)) {
                UIUtils.toast(this, getResources().getString(R.string.shared_error) + getResources().getString(bean.getSharedNameId()));
                return;
            }
        }
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //跳转到的应用
        if (bean.getSharedPackageId() != 0) {
            switch (packageName) {
                case "com.tencent.mm":
                    ComponentName comp = new ComponentName(packageName,
                            "com.tencent.mm.ui.tools.ShareToTimeLineUI");
                    shareIntent.setComponent(comp);
                    break;
                case "com.sina.weibo":
                    shareIntent.setPackage(getResources().getString(bean.getSharedPackageId()));
                    break;
            }
        }
        //九张图片
        shareIntent.putParcelableArrayListExtra(
                Intent.EXTRA_STREAM, nineUris);
        shareIntent.setType("image/*");
        //附加文本
        if (url != null) {
            UIUtils.toast(this, "拼图小游戏链接已复制到剪贴板");
            UIUtils.copyString(getResources().getString(R.string.shared_text) + url, this);
            shareIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.shared_text) + url);
        }
        startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.shared_select)));
    }
}
