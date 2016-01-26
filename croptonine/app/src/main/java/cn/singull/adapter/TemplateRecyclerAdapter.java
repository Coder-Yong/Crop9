package cn.singull.adapter;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.soundcloud.android.crop.util.Log;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.File;

import cn.singull.application.MyApplication;
import cn.singull.bean.ImageBean;
import cn.singull.croptonine.ImageEditActivity;
import cn.singull.croptonine.R;
import cn.singull.utils.UIUtils;

/**
 * Created by xinou03 on 2016/1/13 0013.
 */
public class TemplateRecyclerAdapter extends RecyclerView.Adapter<TemplateRecyclerAdapter.ViewHolder> {
    private int[] templates;
    private int itemLayout;
    private ImageEditActivity act;

    public TemplateRecyclerAdapter(int[] templates, int itemLayout, ImageEditActivity act) {
        this.templates = templates;
        this.itemLayout = itemLayout;
        this.act = act;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.image = (ImageView) v.findViewById(R.id.edit_recycler_item_image);
        viewHolder.text = (TextView) v.findViewById(R.id.edit_recycler_item_text);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int id = templates[position];
        if (MyApplication.appli.getTemplatePaths() != null) {
            File f = MyApplication.appli.getTemplatePaths().get(position);
            Picasso.with(act).load(f).resize(0, 300).into(holder.image);
        } else {
            Picasso.with(act).load(id).resize(0, 300).into(holder.image);
        }
//        Glide.with(act).load(id).into(holder.image);
        if (position == 0) {
            holder.text.setVisibility(View.VISIBLE);
        } else {
            holder.text.setVisibility(View.INVISIBLE);
        }
        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    act.toMultiImageSelector(ImageEditActivity.MUTI_TYPE_TEMP);
                } else {
                    if (MyApplication.appli.getTemplatePaths() != null) {
                        act.setImageTemplate(MyApplication.appli.getTemplatePaths().get(position).getAbsolutePath());
                    }else{
                        act.setImageTemplate(templates[position]);
                    }
                }

            }
        });
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return MyApplication.appli.getTemplatePaths() != null ? MyApplication.appli.getTemplatePaths().size() : templates.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
