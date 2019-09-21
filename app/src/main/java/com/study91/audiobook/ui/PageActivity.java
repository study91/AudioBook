package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookPage;
import com.study91.audiobook.media.MediaClient;
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

        setCurrentPageNumber(getBook().getCurrentPage().getPageNumber()); //缓存当前页码
        getMediaClient().register(); //注册媒体客户端
        getMediaClient().setOnReceiver(new OnMediaClientBroadcastReceiver()); //设置广播接收器

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
        ui.bookImageViewPager = new BookImageViewPager(this); //实例化书图片视图页
        ui.bookImageViewPager.setOnSingleTapListener(new OnBookImageSingleTapListener()); //设置单击事件监听器
        ui.bookImageViewPager.addOnPageChangeListener(new OnBookImagePageChangeListener()); //设置页改变事件监听器
        ui.fullLayout.addView(ui.bookImageViewPager); //添加书图片视图页到全屏视图
    }

    @Override
    protected void onDestroy() {
        getMediaClient().unregister(); //注销媒体客户端
        super.onDestroy();
    }

    /**
     * 获取有声书
     * @return 有声书
     */
    private IBook getBook() {
        return BookManager.getBook();
    }

    /**
     * 设置当前页码
     * @param pageNumber 页码
     */
    private void setCurrentPageNumber(int pageNumber) {
        m.currentPageNumber = pageNumber;
    }

    /**
     * 获取当前页码
     * @return 当前页码
     */
    private int getCurrentPageNumber() {
        return m.currentPageNumber;
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
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private MediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(this);
        }

        return m.mediaClient;
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
     * 有声书图片页改变事件监听器
     */
    private class OnBookImagePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            IBookPage page = getBook().getPages().get(position); //获取当前显示页

            if (page.hasAudio()) {
                ui.playButton.setVisibility(View.VISIBLE); //当前显示页有语音时显示播放按钮
            } else {
                ui.playButton.setVisibility(View.GONE); //当前显示页没有语音时不显示播放按钮
            }

            //如果当前显示页和缓存的当前页码不相同时，重置有声书的当前页
            if (page.getPageNumber() != getCurrentPageNumber()) {
                getBook().setCurrentPage(page); //重置有声书的当前页
                setCurrentPageNumber(page.getPageNumber()); //缓存当前页码
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

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
     * TODO 媒体客户端广播接收器
     */
    private class OnMediaClientBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
//            IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer(); //媒体播放器
            IBookPage currentPage = getBook().getCurrentPage(); //当前页

//            IBookPage currentAudioPage = book.getCurrentAudioPage(mediaPlayer.getPosition()); //当前语音页
//            boolean isPlaying = mediaPlayer.isPlaying(); //是否正在播放
//
//            //设置播放图标
//            if (isPlaying && currentPage.getPageNumber() == currentAudioPage.getPageNumber()) {
//                ui.playButton.setBackgroundResource(R.drawable.button_pause); //设置为暂停图标
//            } else {
//                ui.playButton.setBackgroundResource(R.drawable.button_play); //设置播放图标
//            }
//

            //如果页码有变化，重置当前显示页
            if (currentPage.getPageNumber() != getCurrentPageNumber()) {
                setCurrentPageNumber(currentPage.getPageNumber()); //缓存当前页码

                //重置有声书图片视图页
                ui.bookImageViewPager.setCurrentItem(getBook().getCurrentPage().getPosition());
            }
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 当前页码
         */
        int currentPageNumber;

        /**
         * 是否有工具条
         */
        boolean hasToolbar = true;

        /**
         * 媒体客户端
         */
        MediaClient mediaClient;
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

        /**
         * 有声书图片视图页
         */
        BookImageViewPager bookImageViewPager;
    }
}
