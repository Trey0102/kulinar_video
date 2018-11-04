package com.aristocrat.cooking.ImageLoader;

import android.content.Context;

import com.aristocrat.cooking.ImageLoader.core.DisplayImageOptions;
import com.aristocrat.cooking.ImageLoader.core.ImageLoader;
import com.aristocrat.cooking.ImageLoader.core.ImageLoaderConfiguration;
import com.aristocrat.cooking.ImageLoader.core.assist.QueueProcessingType;
import com.aristocrat.cooking.ImageLoader.core.display.FadeInBitmapDisplayer;


/**
 * Created by Nursultan on 29.12.2014.
 */
public class ImageLoaderHelper {
    public static void init(Context context){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .delayBeforeLoading(50)
                .cacheInMemory(true) // default
                .cacheOnDisk(true) // default
                .displayer(new FadeInBitmapDisplayer(100, true, true, true)) // default
                .build();
        // Create global configuration and initialize ImageLoader with this config
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
                .defaultDisplayImageOptions(options)
                .tasksProcessingOrder(QueueProcessingType.FIFO)
                .threadPoolSize(4)
                .build();
        ImageLoader.getInstance().init(config);
    }
}
