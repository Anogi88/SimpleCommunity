package com.example.myapplication;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.youth.banner.loader.ImageLoader;

/**
 * glide加载轮播图的
 */
public class GlideImage extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        Glide.with(context)                             //配置上下文
                .load(path)
                .into(imageView);
    }

}
