package cn.singull.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import cn.singull.bean.ImageBean;
import cn.singull.croptonine.MainActivity;
import cn.singull.croptonine.R;
import cn.singull.view.SquareImageView;

/**
 * Created by xinou03 on 2016/1/11 0011.
 */
public class FrameRecyclerAdapter extends RecyclerView.Adapter<FrameRecyclerAdapter.ViewHolder> {

    private int[][] frames;
    private int itemLayout;
    private MainActivity act;

    public FrameRecyclerAdapter(int[][] frames, int itemLayout, MainActivity act) {
        this.frames = frames;
        this.itemLayout = itemLayout;
        this.act = act;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.image = (ImageView) v.findViewById(R.id.main_recycler_item);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int id = frames[position][0];
        Picasso.with(act).load(id).fit().into(holder.image);
//        Glide.with(act).load(id).into(holder.image);
//        holder.image.setImageResource(id);
                holder.image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (int i=0;i<9;i++) {
                            act.list.get(i).setFrameId(frames[position][i+1]);
                        }
                        act.adapter.notifyDataSetChanged();
//                act.imageView.setVisibility(View.VISIBLE);
//                Picasso.with(act).load(frames[position]).fit().into(act.imageView);
//                Glide.with(act).load(frames[position]).into(act.imageView);
                    }
                });
        holder.image.setBackgroundResource(R.mipmap.frame_background);
        holder.itemView.setTag(id);
    }

    @Override
    public int getItemCount() {
        return frames.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
