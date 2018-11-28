package com.jay.rabbit.ui.animation;

import android.content.Context;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jay.rabbit.R;

public class Animation {

    private static Drawable accentRectangleBackground;
    private static Drawable normalRectangleBackground;
    private static Drawable accentCircleBackground;
    private static Drawable normalCircleBackground;


    public static void initDrawableRes(Context c){
        accentRectangleBackground = c.getResources().getDrawable(R.drawable.shape_rounded_rectangle_accent);
        normalRectangleBackground = c.getResources().getDrawable(R.drawable.shape_rounded_rectangle_white);
        accentCircleBackground = c.getResources().getDrawable(R.drawable.shape_circle_accent);
        normalCircleBackground = c.getResources().getDrawable(R.drawable.shape_circle_white);
    }

    public static void onButtonPressed(View view){

        view.setBackground(accentRectangleBackground);
        new Handler().postDelayed(() -> view.setBackground(normalRectangleBackground), 300);
    }


    public static void onImagePressed(View view){

        view.setBackground(accentCircleBackground);
        new Handler().postDelayed(() -> view.setBackground(normalCircleBackground), 300);
    }

    public static void startProgressAnimation(ImageView view) {

        view.setImageResource(R.drawable.logo_animated_progress);
        Drawable drawable = view.getDrawable();
        view.post(((Animatable) drawable)::start);

    }


    public static void stopProgressAnimation(ImageView view) {

        view.setImageResource(R.drawable.logo_rabbit);
    }
}
