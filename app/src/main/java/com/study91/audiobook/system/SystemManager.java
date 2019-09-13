package com.study91.audiobook.system;

import android.app.Application;
import android.content.Context;

/**
 * 系统管理类
 * 注：需要在AndroidManifest.xml文件中加入android:name=".system.SystemManager"，示例如下：
 *     <application
 *         android:name=".system.SystemManager"
 *         ...
 *     </application>
 */
public class SystemManager extends Application {
    private static Context context; //应用程序上下文

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    /**
     * 获取全局应用程序上下文
     * @return 应用程序上下文
     */
    public static Context getContext() {
        return context;
    }
}
