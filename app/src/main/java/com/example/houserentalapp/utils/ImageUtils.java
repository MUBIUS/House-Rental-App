package com.example.houserentalapp.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.houserentalapp.R;

public class ImageUtils {

    private static boolean isValidContext(Context ctx) {
        if (ctx == null) return false;
        if (ctx instanceof Activity) {
            Activity act = (Activity) ctx;
            return !act.isFinishing() && !act.isDestroyed();
        }
        return true;
    }

    /** Load a property image with center-crop and shimmer placeholder */
    public static void loadPropertyImage(Context ctx, String url, ImageView target) {
        if (!isValidContext(ctx) || target == null) return;
        Glide.with(ctx)
                .load(url)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_property)
                        .error(R.drawable.placeholder_property)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(target);
    }

    /** Load a circular profile avatar */
    public static void loadAvatar(Context ctx, String url, ImageView target) {
        if (!isValidContext(ctx) || target == null) return;
        Glide.with(ctx)
                .load(url)
                .apply(new RequestOptions()
                        .circleCrop()
                        .placeholder(R.drawable.ic_account)
                        .error(R.drawable.ic_account)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(target);
    }

    /** Load a thumbnail for cards */
    public static void loadThumbnail(Context ctx, String url, ImageView target) {
        if (!isValidContext(ctx) || target == null) return;
        Glide.with(ctx)
                .load(url)
                .thumbnail(0.25f)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(R.drawable.placeholder_property)
                        .error(R.drawable.placeholder_property)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(target);
    }

    /** Load with a specific placeholder drawable resource */
    public static void loadWithPlaceholder(Context ctx, String url, ImageView target,
                                            @DrawableRes int placeholder) {
        if (!isValidContext(ctx) || target == null) return;
        Glide.with(ctx)
                .load(url)
                .apply(new RequestOptions()
                        .centerCrop()
                        .placeholder(placeholder)
                        .error(placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(target);
    }
}
