package com.study91.audiobook.download;

/**
 * 下载事件监听器接口
 */
public interface IDownloadListener {
    /**
     * 通知当前下载进度事件
     * @param progress 进度值
     */
    void onProgress(int progress);

    /**
     * 下载成功事件
     */
    void onSuccess();

    /**
     * 下载失败事件
     */
    void onFailed();

    /**
     * 暂停下载事件
     */
    void onPaused();

    /**
     * 取消下载事件
     */
    void onCanceled();
}
