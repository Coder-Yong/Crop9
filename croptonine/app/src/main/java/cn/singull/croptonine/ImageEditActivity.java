package cn.singull.croptonine;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import cn.singull.adapter.TemplateRecyclerAdapter;
import cn.singull.bean.ImageBean;
import cn.singull.helper.AnimHelper;
import cn.singull.helper.DataHelper;
import cn.singull.helper.ToNineHelper;
import cn.singull.utils.FileUtils;
import cn.singull.utils.UIUtils;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;

public class ImageEditActivity extends AppCompatActivity {
    public static String MUTI_TYPE = "muti_type";
    public static int MUTI_TYPE_IMAGE = -2;
    public static int MUTI_TYPE_TEMP = -1;
    private RecyclerView recyclerView;
    private ImageButton btnTemplate, btnOk;
    private Button btnTempOk, btnTempClear;
    private ImageView imageView, imageTemplate;
    private LinearLayout linearTemlpate;
    private int imageX = 0;
    //    数据对象
    private ImageBean bean = null;
    //    模板数据
    private TemplateRecyclerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_edit);
        initView();
        initData();
        initViewOpea();
        initViewClick();
    }

    private void initView() {
        recyclerView = (RecyclerView) findViewById(R.id.edit_recycler);
        linearTemlpate = (LinearLayout) findViewById(R.id.edit_template_linear);
        imageView = (ImageView) findViewById(R.id.edit_imageview);
        imageTemplate = (ImageView) findViewById(R.id.edit_imageview_template);
        btnOk = (ImageButton) findViewById(R.id.edit_btn_ok);
        btnTemplate = (ImageButton) findViewById(R.id.edit_btn_template);
        btnTempClear = (Button) findViewById(R.id.edit_template_btn_clear);
        btnTempOk = (Button) findViewById(R.id.edit_template_btn_ok);
    }

    private void initData() {
        bean = (ImageBean) getIntent().getSerializableExtra(MainActivity.INTENT_BEAN);
        if (bean == null) {
            bean = new ImageBean();
        }
        adapter = new TemplateRecyclerAdapter(DataHelper.templatesId, R.layout.edit_recycler_item, this);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.HORIZONTAL);
//        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
    }

    private void initViewOpea() {
        imageX = UIUtils.getScreenData(this, "width") * 2 / 3;
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageView.getLayoutParams();
        params.height = imageX;
        params.width = imageX;
        imageView.setLayoutParams(params);
        if (bean.getImagePath() != null) {
            setImageView(bean.getImagePath());
        }
        if (bean.getTempId() != 0) {
            setImageTemplate(bean.getTempId());
        } else if (bean.getTempPath() != null) {
            setImageTemplate(bean.getTempPath());
        }
        ViewGroup.LayoutParams params2 = linearTemlpate.getLayoutParams();
        params2.height = UIUtils.getScreenData(this, "height") * 3 / 7;
        linearTemlpate.setLayoutParams(params2);
    }

    private void initViewClick() {
        OnTempClickListener clickListener = new OnTempClickListener();
        btnTemplate.setOnClickListener(clickListener);
        btnTempClear.setOnClickListener(clickListener);
        btnTempOk.setOnClickListener(clickListener);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMultiImageSelector(MUTI_TYPE_IMAGE);
            }
        });
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = null;
                if (android.os.Build.VERSION.SDK_INT > 10) {
                    builder = new AlertDialog.Builder(ImageEditActivity.this, AlertDialog.THEME_HOLO_LIGHT);
                } else {
                    builder = new AlertDialog.Builder(ImageEditActivity.this);
                }
                builder.setMessage("你挑选的模板将应用到").setPositiveButton("所有方块", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        backMain(true);
                        ImageEditActivity.super.finish();
                    }
                }).setNegativeButton("当前方块", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        backMain(false);
                        ImageEditActivity.super.finish();
                    }
                }).create().show();
            }
        });
    }

    /**
     * 返回首页
     */
    private void backMain(boolean all) {
        Intent data = getIntent();
        data.putExtra(MainActivity.INTENT_BEAN, bean);
        data.putExtra(MainActivity.INTENT_PATH, all);
        setResult(RESULT_OK, data);
    }

    /**
     * 设置模板显示宽高
     */
    public void setImageTemplate(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageTemplate.getLayoutParams();
        int x = 0;
        int y = 0;
        if (options.outHeight > options.outWidth) {
            params.width = imageX;
            x = imageX;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            imageTemplate.setLayoutParams(params);
        } else {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = imageX;
            y = imageX;
            imageTemplate.setLayoutParams(params);
        }
        bean.setTempId(0);
        bean.setTempPath(path);
        Picasso.with(this).load(new File(path)).resize(x, y).into(imageTemplate);
//        Glide.with(this).load(path).into(imageTemplate);
        imageTemplate.setVisibility(View.VISIBLE);
    }

    public void setImageTemplate(int resId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), resId, options);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imageTemplate.getLayoutParams();
        int x = 0;
        int y = 0;
        if (options.outHeight > options.outWidth) {
            params.width = imageX;
            x = imageX;
            params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
            imageTemplate.setLayoutParams(params);
        } else {
            params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
            params.height = imageX;
            y = imageX;
            imageTemplate.setLayoutParams(params);
        }
        bean.setTempPath(null);
        bean.setTempId(resId);
        imageTemplate.setVisibility(View.VISIBLE);
        Picasso.with(this).load(resId).resize(x, y).into(imageTemplate);
//        Glide.with(this).load(resId).into(imageTemplate);
    }

    /**
     * 设置图片显示
     */
    private void setImageView(String path) {
        Picasso.with(this).load(new File(path)).skipMemoryCache().into(imageView);
//        Glide.with(this).load(path).into(imageView);
    }


    //模板选择框相关的单击事件
    private class OnTempClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.edit_btn_template:
                    linearTemlpate.setVisibility(View.VISIBLE);
                    new AnimHelper(ImageEditActivity.this, R.anim.bottom_layout_in).into(linearTemlpate).start();
                    break;
                case R.id.edit_template_btn_clear:
                    imageTemplate.setVisibility(View.INVISIBLE);
                    bean.setTempPath(null);
                    imageTemplate.setImageResource(0);
                case R.id.edit_template_btn_ok:
                    linearTemlpate.setVisibility(View.INVISIBLE);
                    new AnimHelper(ImageEditActivity.this, R.anim.bottom_layout_out).into(linearTemlpate).start();
                    break;
            }
        }
    }

    /**
     * 去图片选择器页面
     *
     * @param type
     */
    public void toMultiImageSelector(int type) {
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        // 是否显示调用相机拍照
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        // 最大图片选择数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        // 将position传至图片选择器，便于回传
        intent.putExtra(ImageEditActivity.MUTI_TYPE, type);
        startActivityForResult(intent, MainActivity.REQUEST_IMAGE);
    }

    /**
     * 去图片裁剪器页面
     *
     * @param path
     * @param type
     */
    private void toCrop(String path, int type) {
        Uri source = Uri.fromFile(new File(path));
        File f = new File(ToNineHelper.NINE_FOLDER_DATA, "cropped.ctn");
        FileUtils.addFile(f);
//        File f = new File(bean.getImagePath());
        Uri outputUri = Uri.fromFile(f);
        new Crop(source).output(outputUri).asSquare().putExtra(ImageEditActivity.MUTI_TYPE, type).start(this);
    }

    /**
     * 从图片裁剪器返回时调用的方法
     *
     * @param uri
     * @param type
     */
    private void resultCrop(Uri uri, int type) {
        if (type == MUTI_TYPE_IMAGE) {
            String time = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
            File f = new File(ToNineHelper.NINE_FOLDER_DATA + "CropToNine_" + time + ".ctn");
            FileUtils.addFile(f);
            Bitmap bitmap = UIUtils.setDigree(ToNineHelper.getImageAbsolutePath(this, uri), 640, 640, Bitmap.Config.RGB_565);
            OutputStream outputStream = null;
            try {
                outputStream = new FileOutputStream(f);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                bitmap.recycle();
                outputStream.close();
                Picasso.with(this).load(f).fit().skipMemoryCache().into(imageView);
                bean.setImagePath(f.getPath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.gc();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainActivity.REQUEST_IMAGE) {
            //从图片选择器返回
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                int type = data.getIntExtra(ImageEditActivity.MUTI_TYPE, -1);
                if (type == MUTI_TYPE_IMAGE)
                    toCrop(path.get(0), type);
                else if (type == MUTI_TYPE_TEMP)
                    setImageTemplate(path.get(0));
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
            //从图片裁剪器返回
            if (resultCode == RESULT_OK) {
                resultCrop(Crop.getOutput(data), -2);
            } else if (resultCode == Crop.RESULT_ERROR) {

            }
        }
    }

    @Override
    public void onBackPressed() {
        if (linearTemlpate.getVisibility() == View.VISIBLE) {
            linearTemlpate.setVisibility(View.INVISIBLE);
        } else {
            super.onBackPressed();
        }
    }
}
