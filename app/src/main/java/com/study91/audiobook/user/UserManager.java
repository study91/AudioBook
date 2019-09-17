package com.study91.audiobook.user;

/**
 * 用户管理器
 */
public class UserManager {
    private static Field m = new Field(); //静态私有字段

    /**
     * 设置用户
     * @param userID 用户ID
     */
    public static void setUser(int userID) {
        if (m.userID != userID) {
            m.userID = userID;
            m.user = new User(getUserID());
        }
    }

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
     * 获取用户ID
     * @return 用户ID
     */
    private static int getUserID() {
        return m.userID;
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
