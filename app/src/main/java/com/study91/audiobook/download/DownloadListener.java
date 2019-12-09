package com.study91.audiobook.download;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.study91.audiobook.system.SystemManager;

/**
 * 下载事件监听器抽象类
 */
public abstract class DownloadListener implements IDownloadListener {
    @Override
    public void onProgress(int progress) {
        getNotificationManager().notify(1, getNotification("正在下载...", progress));
    }

    @Override
    public void onSuccess() {

    }

    @Override
    public void onFailed() {

    }

    @Override
    public void onPaused() {

    }

    @Override
    public void onCanceled() {

    }

    /**
     * 获取通知管理器
     * @return 通知管理器
     */
    private NotificationManager getNotificationManager() {
        Context context = SystemManager.getContext();
        return (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
    }

    /**
     * 获取通知
     * @param title 标题
     * @param progress 进度
     * @return 通知
     */
    private Notification getNotification(String title, int progress) {
        return null;
    }
}
