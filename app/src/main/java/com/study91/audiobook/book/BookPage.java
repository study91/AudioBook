package com.study91.audiobook.book;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;

import com.study91.audiobook.system.SystemManager;
import com.study91.audiobook.tools.ImageTools;
import com.study91.audiobook.tools.MediaTools;

import java.util.List;

/**
 * 有声书页
 */
class BookPage implements IBookPage {
    private Field m = new Field(); //私有变量

    /**
     * 构造器
     * @param cursor 数据指针
     */
    BookPage(Cursor cursor) {
        load(cursor); //载入数据
    }

    @Override
    public int getPageID() {
        return m.pageID;
    }

    @Override
    public int getBookID() {
        return m.bookID;
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
    public IBookCatalog getCatalog() {
        IBookCatalog bookCatalog = null;

        List<IBookCatalog> catalogs = getBook().getCatalogs();

        //遍历查找当前页所属的目录
        for (IBookCatalog catalog : catalogs) {
            if (catalog.getPageNumber() <= getPageNumber()) {
                bookCatalog = catalog;
            } else {
                break;
            }
        }

        return bookCatalog;
    }

    @Override
    public String getImageFilename() {
        String imageFilename = null;

        if (m.imageFilename != null) {
            imageFilename = getBook().getImagePath() + m.imageFilename + ".jpg";
        }
        return imageFilename;
    }

    @Override
    public Drawable getImageDrawable() {
        return ImageTools.getDrawable(getContext(), getImageFilename());
    }

    @Override
    public String getIconFilename() {
        String iconFilename = null;

        if (m.imageFilename != null) {
            iconFilename = getBook().getIconPath() + m.imageFilename + ".png";
        }

        return iconFilename;
    }

    @Override
    public Drawable getIconDrawable() {
        return ImageTools.getDrawable(getContext(), getIconFilename());
    }

    @Override
    public boolean hasAudio() {
        return m.hasAudio;
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
        long audioStartTime = 0;

        if (m.audioStartTime != null) {
            audioStartTime = MediaTools.parseTime(m.audioStartTime);
        }

        return audioStartTime;
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
     */
    private void load(Cursor cursor) {
        if (cursor != null) {
            m.position = cursor.getPosition(); //页位置
            m.pageID = cursor.getInt(cursor.getColumnIndex("PageID")); //页ID
            m.bookID = cursor.getInt(cursor.getColumnIndex("BookID")); //有声书ID
            m.pageNumber = cursor.getInt(cursor.getColumnIndex("PageNumber")); //页码
            m.imageFilename = cursor.getString(cursor.getColumnIndex("ImageFilename")); //图片文件名

            m.hasAudio = cursor.getInt(cursor.getColumnIndex("HasAudio")) != 0; //是否有语音
            if (m.hasAudio) {
                m.audioFilename = cursor.getString(cursor.getColumnIndex("AudioFilename")); //语音文件名
                m.audioStartTime = cursor.getString(cursor.getColumnIndex("AudioStartTime")); //语音开始时间
            }
        }
    }

    /**
     * 私有字段类
     */
    private class Field{
        /**
         * 页ID
         */
        int pageID;

        /**
         * 书ID
         */
        int bookID;

        /**
         * 页码
         */
        int pageNumber;

        /**
         * 页位置
         */
        int position;

        /**
         * 图片文件名
         */
        String imageFilename;

        /**
         * 是否有语音
         */
        boolean hasAudio;

        /**
         * 语音文件名
         */
        String audioFilename;

        /**
         * 语音开始时间
         */
        String audioStartTime;
    }
}
