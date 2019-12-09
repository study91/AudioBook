package com.study91.audiobook.download;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;

import java.io.File;

public class DownloadService extends Service {
    private Field m = new Field(); //私有字段

    @Override
    public IBinder onBind(Intent intent) {
        return m.downloadBinder;
    }

    /**
     * 私有字段
     */
    private class Field {
        /**
         * 下载Binder
         */
        DownloadBinder downloadBinder = new DownloadBinder();
    }
}
