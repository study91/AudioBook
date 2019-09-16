package com.study91.audiobook.user;

import android.database.Cursor;

import com.study91.audiobook.data.DataManager;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.dict.LoopMode;

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

    }

    /**
     * 载入
     * @param userID 用户ID
     */
    private void load(int userID) {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            IUserData userData = UserManager.getUserData(); //获取用户数据
            data = DataManager.createData(userData.getDataSource()); //创建数据对象
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
