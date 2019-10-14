package com.study91.audiobook.user;

import android.content.Context;

import com.study91.audiobook.R;
import com.study91.audiobook.system.SystemManager;

/**
 * 用户管理器
 */
public class UserManager {
    private static Field m = new Field(); //静态私有字段

    /**
     * @return 用户
     */
    public static IUser getUser(){
        if (m.user == null) {
            m.user = new User(getUserID());
        }

        return m.user;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private static Context getContext() {
        return SystemManager.getContext();
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    private static int getUserID() {
        return getContext().getResources().getInteger(R.integer.user_id);
    }

    /**
     * 静态私有字段类
     */
    private static class Field {
        /**
         * 用户ID
         */
        static int userID;

        /**
         * 用户
         */
        static IUser user;
    }
}
