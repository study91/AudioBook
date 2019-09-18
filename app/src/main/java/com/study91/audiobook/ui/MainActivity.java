package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.study91.audiobook.R;
import com.study91.audiobook.media.MediaService;

/**
 * 主窗口
 */
public class MainActivity extends Activity {
    private Field m = new Field(); //私有变量

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置为竖屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //不关屏
        setContentView(R.layout.activity_main);

        startService(getMediaServiceIntent()); //启动媒体服务
    }

    @Override
    protected void onDestroy() {
        stopService(m.mediaServiceIntent); //停止媒体服务
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
     * 私有字段类
     */
    private class Field {
        /**
         * 媒体服务Intent
         */
        Intent mediaServiceIntent;
    }
}
