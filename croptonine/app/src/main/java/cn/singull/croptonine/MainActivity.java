package cn.singull.croptonine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SlidingDrawer;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.singull.adapter.GridViewAdapter;
import cn.singull.bean.ImageBean;
import cn.singull.helper.ToNineHelper;
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
    private GridView gridView;
    //    返回选择图片
    private ArrayList<String> resultList = new ArrayList<>();
    //    以上
//    图片数据
    private ArrayList<ImageBean> list;
    private GridViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initGallery();
        initView();
        initData();
        initViewOpea();
        initViewClick();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
        slidingDrawer = (SlidingDrawer) findViewById(R.id.main_slidingdrawer);
        gridView = (GridView) findViewById(R.id.main_gridview);
    }

    private void initData() {
        list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            list.add(new ImageBean());
        }
        adapter = new GridViewAdapter(this, list);
        gridView.setAdapter(adapter);
    }

    private void initViewOpea() {

    }

    private void initViewClick() {
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (null == list.get(position).getImagePath() || "".equals(list.get(position).getImagePath())) {
//                    图片选择器
                    toMultiImageSelector(position);
                } else {
//                    模板编辑器
                    toTemplate(list.get(position),position);
                }
            }
        });
    }

    /**
     * 去模板编辑页面
     */
    private void toTemplate(ImageBean bean,int position){
        Intent intent = new Intent(this, ImageEditActivity.class);
        intent.putExtra(MainActivity.INTENT_BEAN, bean);
        // 将position传至图片选择器，便于回传
        intent.putExtra(MainActivity.INTENT_POSITION, position);
        startActivityForResult(intent, REQUEST_TEMP);
    }

    /**
     * 去图片选择器页面
     * @param position
     */
    private void toMultiImageSelector(int position){
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
     * @param path
     * @param position
     */
    private void toCrop(String path,int position){
        Intent intent = new Intent(this,ImageCropActivity.class);
        intent.putExtra(MainActivity.INTENT_POSITION,position);
        intent.putExtra(MainActivity.INTENT_PATH,path);
        startActivityForResult(intent, REQUEST_CROP);
    }

    /**
     * 将图片裁剪为九张
     * @param path
     * @return 返回裁剪完成的文件路径
     */
    private List<String> toCropNine(String path){
       return ToNineHelper.toNine(BitmapFactory.decodeFile(path));
    }
    @Override
    public void onSingleImageSelected(String path) {
// 当选择模式设定为 单选/MODE_SINGLE, 这个方法就会接受到Fragment返回的数据
        toCrop(path,-1);
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
                int position = data.getIntExtra(MainActivity.INTENT_POSITION,-1);
                toCrop(path.get(0),position);
            }
        } else if (resultCode == REQUEST_CROP) {
            //从图片裁剪器返回
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(MainActivity.INTENT_POSITION,-1);
                Bitmap bitmap = data.getParcelableExtra(MainActivity.INTENT_BITMAP);
                if(bitmap == null){
                    return;
                }
                if(position<0){
                    List<String> paths = ToNineHelper.toNine(bitmap);
                }else{
                    String path = ToNineHelper.saveImage(bitmap, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()));
                    list.get(position).setImagePath(path);
                }
            }
        } else if (resultCode == REQUEST_TEMP) {
            //从模板编辑器返回
            if (resultCode == RESULT_OK) {
                int position = data.getIntExtra(MainActivity.INTENT_POSITION, -1);
                ImageBean bean = (ImageBean) data.getSerializableExtra(MainActivity.INTENT_BEAN);
                if(bean ==null){
                    return;
                }
                if(position<0){
                    for(ImageBean b :list){
                        b.setTempPath(bean.getTempPath());
                    }
                }else{
                    list.get(position).setImagePath(bean.getImagePath());
                    list.get(position).setTempPath(bean.getTempPath());
                }
            }
        }
    }
}
