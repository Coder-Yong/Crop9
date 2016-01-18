package cn.singull.croptonine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.soundcloud.android.crop.CropImageActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.singull.adapter.FrameRecyclerAdapter;
import cn.singull.adapter.GridViewAdapter;
import cn.singull.bean.ImageBean;
import cn.singull.helper.AnimHelper;
import cn.singull.helper.DataHelper;
import cn.singull.helper.ToNineHelper;
import cn.singull.utils.BlurUtils;
import cn.singull.utils.FileUtils;
import cn.singull.utils.UIUtils;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import me.nereo.multi_image_selector.MultiImageSelectorFragment;

public class MainActivity extends AppCompatActivity implements MultiImageSelectorFragment.Callback {
    //从图片选择器返回
    public static int REQUEST_IMAGE = 2;
    //从图片裁剪返回
    public static int REQUEST_CROP = 3;
    //从模板编辑器返回
    public static int REQUEST_TEMP = 4;
    //Intent将List传至其他对象时使用的字段
    public static String INTENT_BEAN = "images_date";
    //Intent将gridview选择时的position传至其他对象时使用的字段(-1时为所有)
    public static String INTENT_POSITION = "image_position";
    //Intent将选择的图片路径传至裁剪器时的字段
    public static String INTENT_PATH = "image_path";
    //Intent将选择的图片裁剪后传回时的字段
    public static String INTENT_BITMAP = "image_bitmap";
    private SlidingDrawer slidingDrawer;
    private boolean firstOnStart = true;
    private GridView gridView;
    public ImageView imageView;
    private ImageView imageBg;
    private RecyclerView recyclerView;
    private ImageButton btnFrame, btnShared;
    private Button btnFrameOk, btnFrameClear;
    private LinearLayout linearFrame;
    //    返回选择图片
    private ArrayList<String> resultList = new ArrayList<>();

    // bgImage的bitmap
    private List<Bitmap> bitmapBg;
    //    以上
//    图片数据
    public ArrayList<ImageBean> list;
    public GridViewAdapter adapter;
    //    相框数据
    private FrameRecyclerAdapter frameRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGallery();
        initView();
        initData();
        initViewOpea();
        initViewClick();
        fromSend();
    }

    private void fromSend() {
        // TODO Auto-generated method stub
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if (type.startsWith("image/")) {
                Uri imageUri = (Uri) intent
                        .getParcelableExtra(Intent.EXTRA_STREAM);
                if (imageUri != null) {
                    // Update UI to reflect image being shared
                    toCrop(ToNineHelper.getImageAbsolutePath(this, imageUri), -1);
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (firstOnStart) {
            final Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            slidingDrawer.animateOpen();
                            timer.cancel();
                        }
                    });
                }
            }, 1000, 1000);
            firstOnStart = false;
        }
    }

    //加载相册
    private void initGallery() {
        Intent intent = getIntent();
        Bundle bundle = new Bundle();
        // 最大图片选择数量
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_COUNT, 9);
        // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, MultiImageSelectorFragment.MODE_SINGLE);
        // 是否显示调用相机拍照
        bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, false);
        bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_frame, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();
    }

    private void initView() {
        linearFrame = (LinearLayout) findViewById(R.id.main_frame_linear);
        slidingDrawer = (SlidingDrawer) findViewById(R.id.main_slidingdrawer);
        gridView = (GridView) findViewById(R.id.main_gridview);
        imageView = (ImageView) findViewById(R.id.main_imageview);
        imageBg = (ImageView) findViewById(R.id.main_image_bg);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);
        btnFrame = (ImageButton) findViewById(R.id.main_btn_frame);
        btnShared = (ImageButton) findViewById(R.id.main_btn_shared);
        btnFrameOk = (Button) findViewById(R.id.main_frame_btn_ok);
        btnFrameClear = (Button) findViewById(R.id.main_frame_btn_clear);
    }

    private void initData() {
        bitmapBg = Collections.synchronizedList(new ArrayList<Bitmap>());
        //GridView的数据
        list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(new ImageBean());
        }
        adapter = new GridViewAdapter(this, list);
        gridView.setAdapter(adapter);
        //相框的数据
        frameRecyclerAdapter = new FrameRecyclerAdapter(DataHelper.framesId, R.layout.main_recycler_item, this);
        //创建默认的线性LayoutManager
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(this, 2);
        mGridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mGridLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(frameRecyclerAdapter);
    }

    private void initViewOpea() {
        ViewGroup.LayoutParams params = slidingDrawer.getLayoutParams();
        params.height = UIUtils.getScreenData(this, "height") * 5 / 6;
        slidingDrawer.setLayoutParams(params);
        ViewGroup.LayoutParams params2 = linearFrame.getLayoutParams();
        params2.height = UIUtils.getScreenData(this, "height") * 3 / 7;
        linearFrame.setLayoutParams(params2);
    }

    private void initViewClick() {
        OnFrameClickListener frameClick = new OnFrameClickListener();
        btnFrame.setOnClickListener(frameClick);
        btnFrameOk.setOnClickListener(frameClick);
        btnFrameClear.setOnClickListener(frameClick);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (null == list.get(position).getImagePath() || "".equals(list.get(position).getImagePath())) {
//                    图片选择器
                    toMultiImageSelector(position);
                } else {
//                    模板编辑器
                    toTemplate(list.get(position), position);
                }
            }
        });
        btnShared.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toShare();
            }
        });
    }

    /**
     * 跳转到分享页面
     */
    private void toShare() {
        boolean flag = true;
        for (ImageBean b : list) {
            if (b.getImagePath() == null || b.getImagePath().equals("")) {
                flag = false;
                break;
            }
        }
        if (flag) {
            Intent intent = new Intent(this, ShareActivity.class);
            intent.putExtra(MainActivity.INTENT_BEAN, list);
            startActivity(intent);
        } else {
            UIUtils.toast(this, "您还有格子没有选图片");
        }
    }

    //双击退出
    private long millTime = 0;

    @Override
    public void onBackPressed() {
        if (linearFrame.getVisibility() == View.VISIBLE) {
            btnFrameOk.performClick();
        } else if (slidingDrawer.isOpened()) {
            slidingDrawer.animateClose();
        } else {
            long l = System.currentTimeMillis();
            if (l - millTime < 1800) {
                super.onBackPressed();
            } else {
                UIUtils.toast(this, "再点击一次退出");
                millTime = l;
            }
        }
    }

    /**
     * 去模板编辑页面
     */
    private void toTemplate(ImageBean bean, int position) {
        Intent intent = new Intent(this, ImageEditActivity.class);
        intent.putExtra(MainActivity.INTENT_BEAN, bean);
        // 将position传至图片选择器，便于回传
        intent.putExtra(MainActivity.INTENT_POSITION, position);
        startActivityForResult(intent, REQUEST_TEMP);
    }

    /**
     * 去图片选择器页面
     *
     * @param position
     */
    private void toMultiImageSelector(int position) {
        Intent intent = new Intent(this, MultiImageSelectorActivity.class);
        // 是否显示调用相机拍照
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
        // 最大图片选择数量
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
        // 设置模式 (支持 单选/MultiImageSelectorActivity.MODE_SINGLE 或者 多选/MultiImageSelectorActivity.MODE_MULTI)
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_SINGLE);
        // 将position传至图片选择器，便于回传
        intent.putExtra(MainActivity.INTENT_POSITION, position);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    /**
     * 去图片裁剪器页面
     *
     * @param path
     * @param position
     */
    private void toCrop(String path, int position) {
        Uri source = Uri.fromFile(new File(path));
        File f = new File(ToNineHelper.NINE_FOLDER_DATA, "cropped.ctn");
        FileUtils.addFile(f);
        Uri outputUri = Uri.fromFile(f);
        new Crop(source).output(outputUri).asSquare().putExtra(MainActivity.INTENT_POSITION, position).start(this);
    }

    /**
     * 从图片裁剪器返回时调用的方法
     *
     * @param uri
     * @param position
     */
    private void resultCrop(Uri uri, int position) {
        setBackground(BitmapFactory.decodeFile(ToNineHelper.getImageAbsolutePath(this, uri)));
        if (position == -1) {
            List<String> l = toCropNine(ToNineHelper.getImageAbsolutePath(this, uri));
            for (int i = 0; i < l.size(); i++) {
                list.get(i).setImagePath(l.get(i));
            }
        } else {
            list.get(position).setImagePath(ToNineHelper.getImageAbsolutePath(this, uri));
        }
        adapter.notifyDataSetChanged();
    }


    /**
     * 将图片裁剪为九张
     *
     * @param path
     * @return 返回裁剪完成的文件路径
     */
    private List<String> toCropNine(String path) {
        return ToNineHelper.toNine(this, UIUtils.setDigree(path));
    }

    @Override
    public void onSingleImageSelected(String path) {
// 当选择模式设定为 单选/MODE_SINGLE, 这个方法就会接受到Fragment返回的数据
        toCrop(path, -1);
        slidingDrawer.animateClose();
    }

    @Override
    public void onImageSelected(String path) {
// 一个图片被选择是触发，这里可以自定义的自己的Actionbar行为
    }

    @Override
    public void onImageUnselected(String path) {
// 一个图片被反选是触发，这里可以自定义的自己的Actionbar行为
    }

    @Override
    public void onCameraShot(File imageFile) {
// 当设置了使用摄像头，用户拍照后会返回照片文件
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            //从图片选择器返回
            if (resultCode == RESULT_OK) {
                // 获取返回的图片列表
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                int position = data.getIntExtra(MainActivity.INTENT_POSITION, -1);
                toCrop(path.get(0), position);
            }
        } else if (requestCode == REQUEST_TEMP) {
            //从模板编辑器返回
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(MainActivity.INTENT_POSITION, -1);
                boolean flag = data.getBooleanExtra(MainActivity.INTENT_PATH, false);
                ImageBean bean = (ImageBean) data.getSerializableExtra(MainActivity.INTENT_BEAN);
                if (bean == null) {
                    return;
                }
                if (flag) {
                    for (ImageBean b : list) {
                        b.setTempPath(bean.getTempPath());
                        b.setTempId(bean.getTempId());
                    }
                    list.get(position).setImagePath(bean.getImagePath());
                } else {
                    list.get(position).setImagePath(bean.getImagePath());
                    list.get(position).setTempPath(bean.getTempPath());
                    list.get(position).setTempId(bean.getTempId());
                }
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == Crop.REQUEST_CROP) {
//            从图片裁剪器返回
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(MainActivity.INTENT_POSITION, -1);
                resultCrop(Crop.getOutput(data), position);
            } else if (resultCode == Crop.RESULT_ERROR) {

            }
        }
    }

    /**
     * 修改背景
     *
     * @param bitmap
     */
    private void setBackground(final Bitmap bitmap) {
        // TODO Auto-generated method stub
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                bitmapBg.add(BlurUtils.Blur(MainActivity.this, bitmap, 25f));
                if (bitmap != null)
                    bitmap.recycle();
                if (bitmapBg.size() == 1) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            setBackground(0);
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            setBackground(1);
                        }
                    });
                }
            }
        }).start();
    }

    // 修改背景
    private void setBackground(final int flag) {
        Animation animation = AnimationUtils.loadAnimation(this,
                R.anim.imageview_alpha);
        final Animation animation2 = AnimationUtils.loadAnimation(this,
                R.anim.imageview_alpha2);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                imageBg.setAnimation(animation2);
                animation2.start();
            }
        });
        animation2.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                imageBg.setImageBitmap(bitmapBg.get(flag));
                if (bitmapBg.size() != 1) {
                    bitmapBg.get(0).recycle();
                    bitmapBg.remove(0);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub

            }
        });
        if (flag != 0) {
            imageBg.setAnimation(animation);
            animation.start();
        } else {
            imageBg.setAnimation(animation2);
            animation2.start();
        }
    }

    private class OnFrameClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.main_btn_frame:
                    linearFrame.setVisibility(View.VISIBLE);
                    new AnimHelper(MainActivity.this, R.anim.bottom_layout_in).into(linearFrame).start();
                    break;
                case R.id.main_frame_btn_clear:
                    imageView.setVisibility(View.INVISIBLE);
                    imageView.setImageResource(0);
                    for (ImageBean b : list) {
                        b.setFrameId(0);
                    }
                    adapter.notifyDataSetChanged();
                case R.id.main_frame_btn_ok:
                    linearFrame.setVisibility(View.GONE);
                    new AnimHelper(MainActivity.this, R.anim.bottom_layout_out).into(linearFrame).start();
                    break;
            }
        }
    }
}
