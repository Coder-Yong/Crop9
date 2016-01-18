package cn.singull.croptonine;

import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PagerActivity extends AppCompatActivity {
    private ViewPager viewPager;
    private Button btnBack;
    private ArrayList<Uri> list;
    private List<ImageView> mViews;
    int count;
    private ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pager);
        initView();
        initData();
        initViewClick();
    }

    private void initView() {
        viewPager = (ViewPager) findViewById(R.id.pager_viewpager);
        btnBack = (Button) findViewById(R.id.pager_btn_back);
    }

    private void initData() {
        list = getIntent().getParcelableArrayListExtra("nineUris");
        mViews = new ArrayList<>();
        for(Uri uri:list){
            ImageView image = new ImageView(PagerActivity.this);
            image.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            image.setScaleType(ImageView.ScaleType.FIT_CENTER);
//            image.setImageURI(uri);
            mViews.add(image);
        }
        count = getIntent().getIntExtra("count", 0);
        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(count,false);
    }

    private void initViewClick() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PagerActivity.super.onBackPressed();
            }
        });
    }
    class ViewPagerAdapter extends PagerAdapter{

        @Override
        public int getCount() {
           return mViews.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {

            Picasso.with(PagerActivity.this).load(list.get(position)).fit().centerInside().skipMemoryCache().into(mViews.get(position));
            container.addView(mViews.get(position));
            return mViews.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(mViews.get(position));
        }
    }
}
