package cn.singull.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.singull.bean.ShareBean;
import cn.singull.croptonine.ImageEditActivity;
import cn.singull.croptonine.R;

/**
 * Created by xinou03 on 2016/1/13 0013.
 */
public class SharedRecyclerAdapter extends RecyclerView.Adapter<SharedRecyclerAdapter.ViewHolder> {
    private List<ShareBean> sharedList;
    private Activity act;

    public SharedRecyclerAdapter(List<ShareBean> list, ImageEditActivity act) {
        this.act = act;
        this.sharedList = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.shared_recycler_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        viewHolder.image = (ImageView) v.findViewById(R.id.shared_recycler_item_image);
        viewHolder.text = (TextView) v.findViewById(R.id.shared_recycler_item_text);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Picasso.with(act).load(sharedList.get(position).getSharedImageId()).fit().skipMemoryCache().into(holder.image);
        holder.text.setText(act.getResources().getString(sharedList.get(position).getSharedNameId()));
    }

    @Override
    public int getItemCount() {
        return sharedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView image;
        public TextView text;

        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
