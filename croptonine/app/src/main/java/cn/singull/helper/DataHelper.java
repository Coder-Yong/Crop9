package cn.singull.helper;

import android.content.Context;

import java.io.File;

import cn.singull.croptonine.R;

/**
 * Created by xinou03 on 2016/1/14 0014.
 */
public class DataHelper {

    public static int[][] framesId = {
            {R.mipmap.frame1, R.mipmap.frame1_01, R.mipmap.frame1_02, R.mipmap.frame1_03, R.mipmap.frame1_04, R.mipmap.frame1_05, R.mipmap.frame1_06, R.mipmap.frame1_07, R.mipmap.frame1_08, R.mipmap.frame1_09},
            {R.mipmap.frame2, R.mipmap.frame2_01, R.mipmap.frame2_02, R.mipmap.frame2_03, R.mipmap.frame2_04, R.mipmap.frame2_05, R.mipmap.frame2_06, R.mipmap.frame2_07, R.mipmap.frame2_08, R.mipmap.frame2_09},
            {R.mipmap.frame3, R.mipmap.frame3_01, R.mipmap.frame3_02, R.mipmap.frame3_03, R.mipmap.frame3_04, R.mipmap.frame3_05, R.mipmap.frame3_06, R.mipmap.frame3_07, R.mipmap.frame3_08, R.mipmap.frame3_09},
            {R.mipmap.frame_cartoon_1, R.mipmap.frame_cartoon_1_01, R.mipmap.frame_cartoon_1_02, R.mipmap.frame_cartoon_1_03, R.mipmap.frame_cartoon_1_04, R.mipmap.frame_cartoon_1_05, R.mipmap.frame_cartoon_1_06, R.mipmap.frame_cartoon_1_07, R.mipmap.frame_cartoon_1_08, R.mipmap.frame_cartoon_1_09},
            {R.mipmap.frame_cartoon_2, R.mipmap.frame_cartoon_2_01, R.mipmap.frame_cartoon_2_02, R.mipmap.frame_cartoon_2_03, R.mipmap.frame_cartoon_2_04, R.mipmap.frame_cartoon_2_05, R.mipmap.frame_cartoon_2_06, R.mipmap.frame_cartoon_2_07, R.mipmap.frame_cartoon_2_08, R.mipmap.frame_cartoon_2_09},
            {R.mipmap.frame_movie_1, R.mipmap.frame_movie_1_01, R.mipmap.frame_movie_1_02, R.mipmap.frame_movie_1_03, R.mipmap.frame_movie_1_04, R.mipmap.frame_movie_1_05, R.mipmap.frame_movie_1_06, R.mipmap.frame_movie_1_07, R.mipmap.frame_movie_1_08, R.mipmap.frame_movie_1_09},
            {R.mipmap.frame_movie_2, R.mipmap.frame_movie_2_01, R.mipmap.frame_movie_2_02, R.mipmap.frame_movie_2_03, R.mipmap.frame_movie_2_04, R.mipmap.frame_movie_2_05, R.mipmap.frame_movie_2_06, R.mipmap.frame_movie_2_07, R.mipmap.frame_movie_2_08, R.mipmap.frame_movie_2_09},
            {R.mipmap.frame_movie_3, R.mipmap.frame_movie_3_01, R.mipmap.frame_movie_3_02, R.mipmap.frame_movie_3_03, R.mipmap.frame_movie_3_04, R.mipmap.frame_movie_3_05, R.mipmap.frame_movie_3_06, R.mipmap.frame_movie_3_07, R.mipmap.frame_movie_3_08, R.mipmap.frame_movie_3_09},
            {R.mipmap.frame_movie_4, R.mipmap.frame_movie_4_01, R.mipmap.frame_movie_4_02, R.mipmap.frame_movie_4_03, R.mipmap.frame_movie_4_04, R.mipmap.frame_movie_4_05, R.mipmap.frame_movie_4_06, R.mipmap.frame_movie_4_07, R.mipmap.frame_movie_4_08, R.mipmap.frame_movie_4_09}
    };
    public static int[] templatesId = {
            R.mipmap.template0,
            R.mipmap.template1,
            R.mipmap.template3,
            R.mipmap.template4,
            R.mipmap.template5,
            R.mipmap.template6,
            R.mipmap.template7,
            R.mipmap.template8,
            R.mipmap.template9,
    };

    public static String getFramePath(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator + "frame" + File.separator;
    }
    public static String getTemplatePath(Context context) {
        return context.getFilesDir().getAbsolutePath() + File.separator + "template" + File.separator;
    }
}
