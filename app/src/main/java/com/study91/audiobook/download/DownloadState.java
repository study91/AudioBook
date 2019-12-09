package com.study91.audiobook.download;

/**
 * 下载状态
 */
public enum DownloadState {
    /**
     * 下载成功
     */
    SUCCESS,

    /**
     * 下载失败
     */
    FAILED,

    /**
     * 暂停下载
     */
    PAUSED,

    /**
     * 取消下载
     */
    CANCELED
}
