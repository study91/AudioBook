package com.study91.audiobook.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.view.BookImageViewPager;

/**
 * 有声书页活动
 */
public class PageActivity extends AppCompatActivity {
    private UI ui = new UI(); //私有界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置为竖屏显示

        //载入控件
        ui.fullLayout = (RelativeLayout) findViewById(R.id.fullLayout); //全屏布局

        //设置控件
        BookImageViewPager bookImageViewPager = new BookImageViewPager(this);
        ui.fullLayout.removeAllViews();
        ui.fullLayout.addView(bookImageViewPager);
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 全屏布局
         */
        RelativeLayout fullLayout;
    }
}
