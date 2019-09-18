package com.study91.audiobook.user;

import android.database.Cursor;

import com.study91.audiobook.data.DataManager;
import com.study91.audiobook.data.DataSourceManager;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.data.IDataSource;
import com.study91.audiobook.dict.LoopMode;
import com.study91.audiobook.system.SystemManager;

/**
 * 用户类
 */
class User implements IUser {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param userID 用户ID
     */
    User(int userID) {
        load(userID); //载入
        checkBookID(); //检查有声书ID
    }

    @Override
    public int getUserID() {
        return m.userID;
    }

    @Override
    public String getUserName() {
        return m.userName;
    }

    @Override
    public boolean isTest() {
        return m.isTest;
    }

    @Override
    public void setBookID(int bookID) {
        m.bookID = bookID;
    }

    @Override
    public int getBookID() {
        return m.bookID;
    }

    @Override
    public void setAudioVolume(float volume) {
        m.audioVolume = volume;
    }

    @Override
    public float getAudioVolume() {
        return m.audioVolume;
    }

    @Override
    public void setMusicVolume(float volume) {
        m.musicVolume = volume;
    }

    @Override
    public float getMusicVolume() {
        return m.musicVolume;
    }

    @Override
    public LoopMode getAudioLoopMode() {
        return m.audioLoopMode;
    }

    @Override
    public LoopMode getMusicLoopMode() {
        return m.musicLoopMode;
    }

    @Override
    public void update() {
        IData data = null;

        try {
            IDataSource dataSource  = DataSourceManager.getUserDataSource(); //获取用户数据源
            data = DataManager.createData(dataSource.getDataSource()); //获取数据对象

            //更新字符串
            String sql = "UPDATE [User] " +
                    "SET " +
                    "[BookID] = " + getBookID() + "," +
                    "[AudioVolume] = " + getAudioVolume() + "," +
                    "[MusicVolume] = " + getMusicVolume() + " " +
                    "WHERE " +
                    "[UserID] = " + getUserID();

            data.execute(sql); //执行更新
        } finally {
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 载入
     * @param userID 用户ID
     */
    private void load(int userID) {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            IDataSource dataSource = DataSourceManager.getUserDataSource(); //获取用户数据源
            data = DataManager.createData(dataSource.getDataSource()); //创建数据对象
            String sql = "SELECT * FROM [User] WHERE [UserID] = " + userID; //查询字符串
            cursor = data.query(sql);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                m.userID = userID;
                m.userName = cursor.getString(cursor.getColumnIndex("UserName")); //用户名
                m.isTest = cursor.getInt(cursor.getColumnIndex("IsTest")) != 0; //是否测试用户
                m.bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //有声书ID
                m.audioVolume = cursor.getFloat(cursor.getColumnIndex("AudioVolume")); //语音音量
                m.musicVolume = cursor.getFloat(cursor.getColumnIndex("MusicVolume")); //背景音乐音量

                //语音循环模式
                int audioLoopMode = cursor.getInt(cursor.getColumnIndex("AudioLoopMode"));
                m.audioLoopMode = LoopMode.values()[audioLoopMode];

                //音乐循环模式
                int musicLoopMode = cursor.getInt(cursor.getColumnIndex("MusicLoopMode"));
                m.musicLoopMode = LoopMode.values()[musicLoopMode];
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 检查有声书ID
     */
    private void checkBookID() {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取有声书数据源
            data = DataManager.createData(dataSource.getDataSource()); //创建数据对象
            String sql = "SELECT [BookID] FROM [Book] WHERE [BookID] = " + getBookID(); //查询字符串
            cursor = data.query(sql); //执行查询

            if (cursor.getCount() == 0) {
                //如果没有查询到有声书ID，重新查询有声书的第一条记录，并将第一条记录的BookID作为有声书ID
                sql = "SELECT [BookID] FROM [Book] WHERE [BookID] LIMIT 0,1"; //查询第一条记录的字符串
                cursor = data.query(sql); //执行查询

                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    m.bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //有声书ID
                    update(); //更新
                }
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 用户ID
         */
        int userID;

        /**
         * 用户名
         */
        String userName;

        /**
         * 是否测试用户
         */
        boolean isTest;

        /**
         * 有声书ID
         */
        int bookID;

        /**
         * 语音音量
         */
        float audioVolume;

        /**
         * 音乐音量
         */
        float musicVolume;

        /**
         * 语音循环模式
         */
        LoopMode audioLoopMode;

        /**
         * 音乐循环模式
         */
        LoopMode musicLoopMode;
    }
}
