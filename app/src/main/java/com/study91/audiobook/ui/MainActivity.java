package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.media.MediaClient;
import com.study91.audiobook.media.MediaService;

/**
 * 主窗口
 */
public class MainActivity extends Activity {
    private Field m = new Field(); //私有变量
    private UI ui = new UI(); //私有界面

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置为竖屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //不关屏
        setContentView(R.layout.activity_main);

        startService(getMediaServiceIntent()); //启动媒体服务
        getMediaClient().register(); //注册媒体客户端

        ui.rjYuWen1aButton = (Button) findViewById(R.id.rjYuWen1aButton);
        ui.rjYuWen1aButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBook(1);
            }
        });

        ui.rjYuWen1bButton = (Button) findViewById(R.id.rjYuWen1bButton);
        ui.rjYuWen1bButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playBook(2);
            }
        });
    }

    @Override
    protected void onDestroy() {
        stopService(m.mediaServiceIntent); //停止媒体服务
        getMediaClient().unregister(); //注销媒体客户端
        super.onDestroy();
    }

    /**
     * 获取媒体服务Intent
     * @return 媒体服务Intent
     */
    private Intent getMediaServiceIntent() {
        if (m.mediaServiceIntent == null) {
            m.mediaServiceIntent = new Intent(this, MediaService.class);
        }

        return m.mediaServiceIntent;
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    MediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(this);
        }

        return m.mediaClient;
    }

    /**
     * 播放有声书
     * @param bookID 有声书ID
     */
    private void playBook(int bookID) {
        BookManager.setBook(bookID);
        IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer();
        IBookCatalog catalog = BookManager.getBook().getCurrentAudio();

        mediaPlayer.setAudioFile(
                catalog.getAudioFilename(),
                catalog.getTitle(),
                catalog.getIconFilename());
        mediaPlayer.play();
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 媒体服务Intent
         */
        Intent mediaServiceIntent;

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
         * 人教一年级语文上册按钮
         */
        Button rjYuWen1aButton;

        /**
         * 人教一年级语文下册按钮
         */
        Button rjYuWen1bButton;
    }
}
