package cn.singull.helper;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

/**
 * Created by xinou03 on 2016/1/12 0012.
 */
public class AnimHelper {
    private Animation animation;

    public AnimHelper(Context context,int id) {
        this.animation = AnimationUtils.loadAnimation(context,id);
    }
    public AnimHelper into(View view) {
        view.setAnimation(animation);
        return this;
    }
    public void start(){
        animation.start();
    }
    public AnimHelper setListener(Animation.AnimationListener listener){
        this.animation.setAnimationListener(listener);
        return this;
    }
}
