package com.study91.audiobook.data;

/**
 * 数据源管理器
 */
public class DataSourceManager {
    private static Field m = new Field(); //静态私有字段

    /**
     * 获取有声书数据源
     * @return 有声书数据源
     */
    public static IDataSource getBookDataSource() {
        if (m.bookDataSource == null) {
            m.bookDataSource = new BookDataSource();
        }

        return m.bookDataSource;
    }

    /**
     * 获取用户数据源
     * @return 用户数据源
     */
    public static IDataSource getUserDataSource() {
        if (m.userDataSource == null) {
            m.userDataSource = new UserDataSource();
        }

        return m.userDataSource;
    }

    /**
     * 静态私有字段类
     */
    private static class Field {
        /**
         * 有声书数据源
         */
        static IDataSource bookDataSource;

        /**
         * 用户数据源
         */
        static IDataSource userDataSource;
    }
}
