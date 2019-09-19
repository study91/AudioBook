package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.study91.audiobook.R;
import com.study91.audiobook.view.BookImageViewPager;
import com.study91.audiobook.view.MediaPlayerView;
import com.study91.audiobook.view.OnSingleTapListener;

/**
 * 有声书页窗口
 */
public class PageActivity extends Activity {
    private Field m = new Field(); //私有字段
    private UI ui = new UI(); //私有界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置为竖屏显示
        setContentView(R.layout.activity_page);

        //载入控件
        ui.fullLayout = (RelativeLayout) findViewById(R.id.fullLayout); //全屏布局
        ui.topLayout = (RelativeLayout) findViewById(R.id.topLayout); //顶部布局
        ui.backButton = (Button) findViewById(R.id.backButton); //返回按钮
        ui.playButton = (Button) findViewById(R.id.playButton); //播放按钮
        ui.catalogButton = (Button) findViewById(R.id.catalogButton); //目录按钮
        ui.mediaPlayerView = (MediaPlayerView) findViewById(R.id.mediaPlayerView); //媒体播放视图

        //设置控件事件监听器
        ui.backButton.setOnClickListener(new OnBackButtonClickListener()); //返回按钮单击事件
        ui.catalogButton.setOnClickListener(new OnCatalogButtonClickListener()); //目录按钮单击事件

        //全屏视图
        ui.fullLayout.removeAllViews(); //移除所有全屏视图中的视图
        BookImageViewPager bookImageViewPager = new BookImageViewPager(this); //实例化书图片视图页
        bookImageViewPager.setOnSingleTapListener(new OnBookImageSingleTapListener()); //设置单击事件监听器
        ui.fullLayout.addView(bookImageViewPager); //添加书图片视图页到全屏视图
    }

    /**
     * 是否有工具条
     * @return true=有工具条，false=没有工具条
     */
    private boolean hasToolbar() {
        return m.hasToolbar;
    }

    /**
     * 设置工具条
     * @param hasToolbar ture=显示工具条，false=隐藏工具条
     */
    private void setToolbar(boolean hasToolbar) {
        if (hasToolbar) {
            //显示工具条
            ui.mediaPlayerView.setVisibility(View.VISIBLE); //显示媒体播放工具条
            ui.topLayout.setVisibility(View.VISIBLE); //显示顶部工具条
        } else {
            //隐藏工具条
            ui.mediaPlayerView.setVisibility(View.GONE); //隐藏媒体播放工具条
            ui.topLayout.setVisibility(View.GONE); //隐藏顶部工具条
        }

        m.hasToolbar = hasToolbar; //缓存工具条状态
    }

    /**
     * 有声书图片单击事件监听器
     */
    private class OnBookImageSingleTapListener implements OnSingleTapListener {
        @Override
        public void onSingleTap() {
            setToolbar(!hasToolbar()); //设置工具条
        }
    }

    /**
     * 返回按钮单击事件监听器
     */
    private class OnBackButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    /**
     * 目录按钮单击事件监听器
     */
    private class OnCatalogButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(PageActivity.this, CatalogActivity.class);
            startActivity(intent);
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 是否有工具条
         */
        boolean hasToolbar = true;
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 全屏布局
         */
        RelativeLayout fullLayout;

        /**
         * 顶部布局
         */
        RelativeLayout topLayout;

        /**
         * 返回按钮
         */
        Button backButton;

        /**
         * 播放按钮
         */
        Button playButton;

        /**
         * 目录按钮
         */
        Button catalogButton;

        /**
         * 媒体播放器视图
         */
        MediaPlayerView mediaPlayerView;
    }
}
