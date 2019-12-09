package com.study91.audiobook.download;

import android.os.AsyncTask;

/**
 * 下载类
 */
public class DownloadTask extends AsyncTask<String, Integer, Integer> {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param downloadListener 下载事件监听器
     */
    public DownloadTask(IDownloadListener downloadListener) {
        m.downloadListener = downloadListener;
    }

    @Override
    protected Integer doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
    }

    /**
     * 暂停下载
     */
    public void pauseDownload() {
        m.isPaused = true;
    }

    /**
     * 取消下载
     */
    public void cancelDownload() {
        m.isCanceled = true;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 下载事件监听器
         */
        IDownloadListener downloadListener;

        /**
         * 是否取消
         */
        boolean isCanceled = false;

        /**
         * 是否暂停
         */
        boolean isPaused = false;

        /**
         * 最后下载进度
         */
        int lastProgress;
    }
}
