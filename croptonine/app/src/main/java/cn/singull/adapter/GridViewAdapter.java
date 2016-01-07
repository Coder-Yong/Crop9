package cn.singull.adapter;

import android.app.Activity;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;
import java.util.List;

import cn.singull.bean.ImageBean;
import cn.singull.croptonine.R;

/**
 * Created by xinou03 on 2016/1/7 0007.
 */
public class GridViewAdapter extends BaseAdapter {
    private AppCompatActivity act;
    private List<ImageBean> list;

    public GridViewAdapter(AppCompatActivity act, List<ImageBean> list) {
        this.act = act;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = act.getLayoutInflater().inflate(R.layout.main_gridview_item,null);
            holder.frameImage = (ImageView) convertView.findViewById(R.id.main_gridview_item_frame);
            holder.imageView = (ImageView) convertView.findViewById(R.id.main_gridview_item_image);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }
        if(list.get(position).getFrameId()!=0){
            holder.frameImage.setImageResource(list.get(position).getFrameId());
        }
        if(null != list.get(position).getImagePath()){
            holder.imageView.setImageURI(Uri.fromFile(new File(list.get(position).getImagePath())));
        }
        return convertView;
    }
    class ViewHolder{
        ImageView frameImage;
        ImageView imageView;
    }
}
