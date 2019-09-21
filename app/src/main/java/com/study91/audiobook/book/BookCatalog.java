package com.study91.audiobook.book;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.study91.audiobook.dict.DisplayMode;
import com.study91.audiobook.dict.FamiliarLevel;
import com.study91.audiobook.system.SystemManager;
import com.study91.audiobook.tools.ImageTools;

import java.util.List;

/**
 * 有声书目录
 */
class BookCatalog implements IBookCatalog {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param cursor 数据指针
     */
    BookCatalog(Cursor cursor) {
        load(cursor); //载入数据
    }

    @Override
    public int getCatalogID() {
        return m.catalogID;
    }

    @Override
    public int getBookID() {
        return m.bookID;
    }

    @Override
    public int getIndex() {
        return m.index;
    }

    @Override
    public int getPageNumber() {
        return m.pageNumber;
    }

    @Override
    public int getPosition() {
        return m.position;
    }

    @Override
    public IBookPage getPage() {
        IBookPage page = null;

        //遍历查找目录页
        List<IBookPage> pages = getBook().getPages();
        for (IBookPage bookPage : pages) {
            if (bookPage.getPageNumber() == getPageNumber()) {
                page = bookPage;
                break;
            }
        }

        return page;
    }

    @Override
    public String getTitle() {
        return m.title;
    }

    @Override
    public boolean hasExplain() {
        return m.hasExplain;
    }

    @Override
    public boolean hasAudio() {
        return m.hasAudio;
    }

    @Override
    public void setAudioPlayEnable(boolean value) {
        //TODO 设置语音播放开关
    }

    @Override
    public boolean allowPlayAudio() {
        return m.allowPlayAudio;
    }

    @Override
    public String getAudioFilename() {
        String audioFilename = null;

        if (m.audioFilename != null) {
            audioFilename = getBook().getAudioPath() + m.audioFilename + ".mp3";
        }

        return audioFilename;
    }

    @Override
    public long getAudioStartTime() {
        return 0;
    }

    @Override
    public long getAudioEndTime() {
        return 0;
    }

    @Override
    public DisplayMode getIconDisplayMode() {
        return m.iconDisplayMode;
    }

    @Override
    public String getIconFilename() {
        String iconFilename = null;

        if (m.iconFilename != null) {
            iconFilename = getBook().getIconPath() + m.iconFilename + ".png";
        }

        return iconFilename;
    }

    @Override
    public Drawable getIconDrawable() {
        return ImageTools.getDrawable(getContext(), getIconFilename());
    }

    @Override
    public FamiliarLevel getFamiliarLevel() {
        return m.familiarLevel;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return SystemManager.getContext();
    }

    /**
     * 获取有声书
     * @return 有声书
     */
    private IBook getBook() {
        return BookManager.getBook();
    }

    /**
     * 载入数据
     * @param cursor 数据指针
     */
    private void load(Cursor cursor) {
        if (cursor != null) {
            m.position = cursor.getPosition(); //目录位置
            m.catalogID = cursor.getInt(cursor.getColumnIndex("CatalogID")); //目录ID
            m.bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //有声书ID
            m.index = cursor.getInt(cursor.getColumnIndex("Index")); //目录索引
            m.pageNumber = cursor.getInt(cursor.getColumnIndex("PageNumber")); //页码
            m.title = cursor.getString(cursor.getColumnIndex("Title")); //目录标题
            m.hasExplain = cursor.getInt(cursor.getColumnIndex("HasExplain")) != 0; //是否有解释

            m.hasAudio = cursor.getInt(cursor.getColumnIndex("HasAudio")) != 0; //是否有语音
            if (m.hasAudio) {
                m.allowPlayAudio = cursor.getInt(cursor.getColumnIndex("AllowPlayAudio")) != 0; //是否充许播放语音
                m.audioFilename = cursor.getString(cursor.getColumnIndex("AudioFilename")); //语音文件名
                m.audioStartTime = cursor.getString(cursor.getColumnIndex("AudioStartTime")); //语音开始时间
                m.audioEndTime = cursor.getString(cursor.getColumnIndex("AudioEndTime")); //语音结束时间
            }

            int iconDisplayMode = cursor.getInt(cursor.getColumnIndex("IconDisplayMode")); //图标显示模式
            int familiarLevel = cursor.getInt(cursor.getColumnIndex("FamiliarLevel")); //熟悉级别

            m.iconDisplayMode = DisplayMode.values()[iconDisplayMode]; //图标显示模式
            m.familiarLevel = FamiliarLevel.values()[familiarLevel]; //熟悉级别
            m.iconFilename = cursor.getString(cursor.getColumnIndex("IconFilename")); //图标文件名

        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 目录ID
         */
        int catalogID;

        /**
         * 书ID
         */
        int bookID;

        /**
         * 目录索引
         */
        int index;

        /**
         * 目录位置
         */
        int position;

        /**
         * 目录页码
         */
        int pageNumber;

        /**
         * 目录标题
         */
        String title;

        /**
         * 是否有解释
         */
        boolean hasExplain;

        /**
         * 是否有语音
         */
        boolean hasAudio;

        /**
         * 是否充许播放语音
         */
        boolean allowPlayAudio;

        /**
         * 语音文件名
         */
        String audioFilename;

        /**
         * 语音开始时间
         */
        String audioStartTime;

        /**
         * 语音结束时间
         */
        String audioEndTime;

        /**
         * 图标显示模式
         */
        DisplayMode iconDisplayMode;

        /**
         * 图标文件名
         */
        String iconFilename;

        /**
         * 熟悉级别
         */
        FamiliarLevel familiarLevel;
    }
}
