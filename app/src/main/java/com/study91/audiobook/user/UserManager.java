package com.study91.audiobook.user;

import com.study91.audiobook.system.SystemManager;

/**
 * 用户管理器
 */
public class UserManager {
    private static Field M = new Field(); //静态私有字段

    /**
     * 设置用户
     * @param userID 用户ID
     */
    public static void setUser(int userID) {
        if (M.userID != userID) {
            M.userID = userID;
            M.user = new User(getUserID());
        }
    }

    /**
     * @return 用户
     */
    public static IUser getUser(){
        if (M.user == null) {
            M.user = new User(getUserID());
        }

        return M.user;
    }

    /**
     * 获取用户数据
     * @return 用户数据
     */
    public static IUserData getUserData() {
        if (M.userData == null) {
            M.userData = new UserData(SystemManager.getContext());
        }

        return M.userData;
    }

    /**
     * 获取用户ID
     * @return 用户ID
     */
    private static int getUserID() {
        return M.userID;
    }

    /**
     * 静态私有字段类
     */
    private static class Field {
        /**
         * 用户ID
         */
        int userID;

        /**
         * 用户
         */
        IUser user;

        /**
         * 用户数据源
         */
        IUserData userData;
    }
}
