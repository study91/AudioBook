package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.viewpager.widget.ViewPager;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.book.IBookPage;
import com.study91.audiobook.media.IBookMediaPlayer;
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
        ui.syncButton = (Button) findViewById(R.id.syncButton); //同步按钮
        ui.catalogButton = (Button) findViewById(R.id.catalogButton); //目录按钮
        ui.mediaPlayerView = (MediaPlayerView) findViewById(R.id.mediaPlayerView); //媒体播放视图

        //设置控件事件监听器
        ui.backButton.setOnClickListener(new OnBackButtonClickListener()); //返回按钮单击事件
        ui.playButton.setOnClickListener(new OnPlayButtonClickListener()); //播放按钮单击事件
        ui.syncButton.setOnClickListener(new OnSyncButtonClickListener()); //同步按钮单击事件
        ui.catalogButton.setOnClickListener(new OnCatalogButtonClickListener()); //目录按钮单击事件

        //全屏视图
        ui.fullLayout.removeAllViews(); //移除所有全屏视图中的视图
        ui.bookImageViewPager = new BookImageViewPager(this); //实例化书图片视图页
        ui.bookImageViewPager.setOnSingleTapListener(new OnBookImageSingleTapListener()); //设置单击事件监听器
        ui.bookImageViewPager.addOnPageChangeListener(new OnBookImagePageChangeListener()); //设置页改变事件监听器
        ui.fullLayout.addView(ui.bookImageViewPager); //添加书图片视图页到全屏视图

        setPlayButton(); //设置播放按钮
        setSyncButton(); //设置同步按钮
    }

    @Override
    protected void onStart() {
        super.onStart();

        IBookPage currentPage = getBook().getCurrentPage();
        if (currentPage.getPageNumber() != getCurrentPageNumber()) {
            setCurrentPageNumber(currentPage.getPageNumber()); //缓存当前页码

            //重置有声书图片视图页
            ui.bookImageViewPager.setCurrentItem(getBook().getCurrentPage().getPosition());
        }
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
     * 设置播放按钮
     */
    private void setPlayButton() {
        int position = ui.bookImageViewPager.getCurrentItem();
        IBookPage page = getBook().getPages().get(position); //获取当前显示页

        if (page.hasAudio()) {
            ui.playButton.setVisibility(View.VISIBLE); //当前显示页有语音时显示播放按钮
        } else {
            ui.playButton.setVisibility(View.GONE); //当前显示页没有语音时不显示播放按钮
        }
    }

    /**
     * 设置同步按钮
     */
    private void setSyncButton() {
        if (getBook().syncEnable()) {
            ui.syncButton.setBackgroundResource(R.drawable.button_sync_enable);
        } else {
            ui.syncButton.setBackgroundResource(R.drawable.button_sync_disable);
        }
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
            setPlayButton(); //设置播放按钮

            IBookPage page = getBook().getPages().get(position); //获取当前显示页

            //如果当前显示页和缓存的当前页码不相同时，重置有声书的当前页
            if (page.getPageNumber() != getCurrentPageNumber()) {
                getBook().setCurrentPage(page); //重置有声书的当前页
                setCurrentPageNumber(page.getPageNumber()); //缓存当前页码
            }

            ///如果同步开关已打开，关闭同步开关
            int mediaPosition = getMediaClient().getMediaPlayer().getPosition(); //媒体播放位置
            IBookPage currentAudioPage = getBook().getCurrentAudioPage(mediaPosition); //当前语音页

            //只有当前页和当前语音页不相同时，才关闭同步开关
            if (page.getPageNumber() != currentAudioPage.getPageNumber()) {
                getBook().setSyncEnable(false); //关闭同步开关
                setSyncButton(); //设置同步按钮
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
     * 播放按钮单击事件监听器
     */
    private class OnPlayButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer(); //媒体播放器
            IBookPage currentAudioPage = getBook().getCurrentAudioPage(mediaPlayer.getPosition()); //当前语音页
            IBookPage currentPage = getBook().getCurrentPage(); //获取当前页

            if (currentPage.getPageID() == currentAudioPage.getPageID()) {
                //当前页是当前语音页
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause(); //暂停播放
                } else {
                    mediaPlayer.play(); //播放
                }
            } else {
                //当前页不是当前语音页
                if (!currentPage.getAudioFilename().equals(currentAudioPage.getAudioFilename())) {
                    IBookCatalog currentAudio = currentPage.getCatalog(); //当前页所属目录
                    getBook().setCurrentAudio(currentAudio); //重置当前语音目录
                    setCurrentPageNumber(currentPage.getPageNumber()); //缓存当前页码

                    //重置播放文件
                    mediaPlayer.setAudioFile(
                            currentAudio.getAudioFilename(),
                            currentAudio.getTitle(),
                            currentAudio.getIconFilename());
                }

                mediaPlayer.seekTo((int)currentPage.getAudioStartTime()); //定位播放位置
                mediaPlayer.play(); //播放
            }
        }
    }

    /**
     * 同步按钮单击事件监听器
     */
    private class OnSyncButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            getBook().setSyncEnable(!getBook().syncEnable()); //设置同步开关
            setSyncButton(); //设置同步按钮
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
     * 媒体客户端广播接收器
     */
    private class OnMediaClientBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ///设置播放按钮
            IBookPage currentPage = getBook().getCurrentPage(); //当前页
            IBookCatalog currentAudio = getBook().getCurrentAudio(); //当前语音
            IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer(); //媒体播放器

            //只有当前语音目录和当前页的语音文件相同时才设置图标
            if (currentAudio.getAudioFilename().equals(currentPage.getAudioFilename())) {
                //当前语音目录和当前页的语音文件相同
                IBookPage currentAudioPage = getBook().getCurrentAudioPage(mediaPlayer.getPosition()); //当前语音页
                if (currentPage.getPageID() == currentAudioPage.getPageID()) {
                    //当前页与当前语音页相同
                    if (mediaPlayer.isPlaying()) {
                        ui.playButton.setBackgroundResource(R.drawable.button_pause); //设置为暂停图标
                    } else {
                        ui.playButton.setBackgroundResource(R.drawable.button_play); //设置播放图标
                    }
                } else {
                    //当前页与当前语音页不相同
                    ui.playButton.setBackgroundResource(R.drawable.button_play); //设置播放图标
                }
            } else {
                //当前语音目录和当前页的语音文件不相同
                ui.playButton.setBackgroundResource(R.drawable.button_play); //设置播放图标
            }

            ///只有打开同步时，进行同步设置
            if (getBook().syncEnable()) {
                //获取当前语音页位置
                int audioPagePosition =
                        getBook().getCurrentAudioPage(mediaPlayer.getPosition()).getPosition();

                ui.bookImageViewPager.setCurrentItem(audioPagePosition); //显示正在播放的语音页
            }

            setSyncButton(); //设置同步按钮
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
         * 当前同步开关
         */
        boolean currentSyncEnable;

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
         * 同步按钮
         */
        Button syncButton;

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
