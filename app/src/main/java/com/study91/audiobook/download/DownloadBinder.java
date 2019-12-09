package com.study91.audiobook.download;

import android.os.Binder;
import android.os.Environment;

import java.io.File;

/**
 * 下载Binder
 */
public class DownloadBinder extends Binder {
    private Field m = new Field(); //私有字段

    /**
     * 设置下载事件监听器
     * @param listener 下载事件监听器
     */
    public void setOnDownloadListener(DownloadListener listener) {
        m.downloadListener = listener;
    }

    /**
     * 开始下载
     * @param url 下载地址
     */
    public void startDownload(String url) {
        if (m.downloadTask == null) {
            m.downloadUrl = url;
            m.downloadTask = new DownloadTask(m.downloadListener);
            m.downloadTask.execute(m.downloadUrl);
        }
    }

    /**
     * 暂停下载
     */
    public void pauseDownload() {
        if (m.downloadTask != null) {
            m.downloadTask.pauseDownload();
        }
    }

    /**
     * 取消下载
     */
    public void cancelDownload() {
        if (m.downloadTask != null) {
            m.downloadTask.cancelDownload();
        } else {
            if (m.downloadUrl != null) {
                // 取消下载时需将文件删除，并将通知关闭
                String fileName = m.downloadUrl.substring(m.downloadUrl.lastIndexOf("/"));
                String directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                File file = new File(directory + fileName);
                if (file.exists()) {
                    file.delete();
                }
            }
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 下载任务
         */
        DownloadTask downloadTask;

        /**
         * 下载事件监听器
         */
        DownloadListener downloadListener;

        /**
         * 下载地址
         */
        String downloadUrl;
    }
}
