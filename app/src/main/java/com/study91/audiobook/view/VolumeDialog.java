package com.study91.audiobook.view;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.dict.SoundType;
import com.study91.audiobook.media.MediaClient;
import com.study91.audiobook.user.IUser;
import com.study91.audiobook.user.UserManager;

/**
 * 音量对话框类
 */
class VolumeDialog extends Dialog {
    private Field m = new Field(); //私有变量
    private UI ui = new UI(); //界面

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    VolumeDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_volume); //加载对话框布局
        getMediaClient().register(); //注册媒体客户端
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        WindowManager.LayoutParams layoutParams = getWindow().getAttributes(); //获取窗口属性

        DisplayMetrics displayMetrics = new DisplayMetrics(); //获取显示
        WindowManager windowManager = getWindow().getWindowManager(); //获取窗口管理器
        windowManager.getDefaultDisplay().getMetrics(displayMetrics);

        int rotation = windowManager.getDefaultDisplay().getRotation(); //获取屏幕方向
        switch(rotation) {
            case Surface.ROTATION_0: //竖屏
                layoutParams.width = (int)(displayMetrics.widthPixels * 0.8);
                break;
            case Surface.ROTATION_90: //横屏
                layoutParams.width = (int)(displayMetrics.heightPixels * 0.8);
                break;
            case Surface.ROTATION_180: //竖屏
                layoutParams.width = (int)(displayMetrics.widthPixels * 0.8);
                break;
            case Surface.ROTATION_270: //横屏
                layoutParams.width = (int)(displayMetrics.heightPixels * 0.8);
                break;
        }

        getWindow().setAttributes(layoutParams);

        //语音音量拖动条
        ui.audioVolumeTextView = (TextView) findViewById(R.id.audioVolumeTextView);
        ui.audioVolumeSeekBar = (SeekBar) findViewById(R.id.audioVolumeSeekBar);
        ui.audioVolumeSeekBar.setOnSeekBarChangeListener(new OnAudioVolumeSeekBarChangeListener());

        int audioVolume = (int)(getUser().getAudioVolume() * 100); //获取语音音量
        ui.audioVolumeSeekBar.setProgress(audioVolume); //设置语音进度条
        ui.audioVolumeTextView.setText(audioVolume + ""); //设置语音进度文本


        //背景音乐音量拖动条
        ui.musicVolumeTextView = (TextView) findViewById(R.id.musicVolumeTextView);
        ui.musicVolumeSeekBar = (SeekBar) findViewById(R.id.musicVolumeSeekBar);
        ui.musicVolumeSeekBar.setOnSeekBarChangeListener(new OnMusicVolumeSeekBarChangeListener());

        int musicVolume = (int)(getUser().getMusicVolume() * 100); //获取背景音乐音量
        ui.musicVolumeSeekBar.setProgress(musicVolume); //设置背景音乐进度条
        ui.musicVolumeTextView.setText(musicVolume + ""); //设置背景音乐进度文本

        ui.musicLayout = (RelativeLayout) findViewById(R.id.musicLayout); //背景音乐布局

        //只有语音时，不显示背景音乐音量拖动条
        if (getMediaClient().getMediaPlayer().getSoundType() == SoundType.ONLY_AUDIO) {
            ui.musicLayout.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onStop() {
        if(hasChanged()) getUser().update(); //更新配置
        getMediaClient().unregister(); //注销媒体客户端
        super.onStop();
    }

    /**
     * 获取全局用户
     * @return 全局用户
     */
    private IUser getUser() {
        return UserManager.getUser();
    }

    /**
     * 是否有改变
     * @return true=有改变，false=没有改变
     */
    private boolean hasChanged() {
        return m.changed;
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private MediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(getContext());
        }

        return m.mediaClient;
    }

    /**
     * 语音音量拖动条改变事件监听器
     */
    private class OnAudioVolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float volume = ((float)ui.audioVolumeSeekBar.getProgress()) / 100; //获取进度条语音音量值
            getMediaClient().getMediaPlayer().setAudioVolume(volume); //设置媒体播放器语音音量
            getUser().setAudioVolume(volume); //设置全局配置语音音量

            //刷新语音音量值
            int audioVolume = (int)(volume * 100);
            ui.audioVolumeTextView.setText(audioVolume + "");

            m.changed = true; //设置为有变化
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    /**
     * 音乐音量拖动条改变事件监听器
     */
    private class OnMusicVolumeSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            float volume = ((float)ui.musicVolumeSeekBar.getProgress()) / 100; //获取进度条背景音乐音量值
            getMediaClient().getMediaPlayer().setMusicVolume(volume); //设置媒体播放器背景音乐音量
            getUser().setMusicVolume(volume); //设置全局配置背景音乐音量

            //刷新背景音乐音量值
            int musicVolume = (int)(volume * 100);
            ui.musicVolumeTextView.setText(musicVolume + "");

            m.changed = true; //设置为有变化
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {}

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {}
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 是否有改变
         */
        boolean changed = false;

        /**
         * 媒体客户端
         */
        MediaClient mediaClient;
    }

    /**
     * 界面类
     */
    private class UI {
        /**
         * 语音音量进度条
         */
        SeekBar audioVolumeSeekBar;

        /**
         * 音乐音量进度条
         */
        SeekBar musicVolumeSeekBar;

        /**
         * 语音音量文本框
         */
        TextView audioVolumeTextView;

        /**
         * 背景音乐音量文本框
         */
        TextView musicVolumeTextView;

        /**
         * 背景音乐布局
         */
        RelativeLayout musicLayout;
    }
}
