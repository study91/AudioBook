package com.study91.audiobook.book;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.List;

/**
 * 有声书类
 */
class Book implements IBook {
    private Field m = new Field(); //私有字段

    Book(Context context, int bookID) {
        m.context = context; //应用程序上下文
    }

    @Override
    public int getBookID() {
        return 0;
    }

    @Override
    public String getBookName() {
        return null;
    }

    @Override
    public String getBookDepict() {
        return null;
    }

    @Override
    public String getPublish() {
        return null;
    }

    @Override
    public String getGrade() {
        return null;
    }

    @Override
    public String getSubject() {
        return null;
    }

    @Override
    public String getTerm() {
        return null;
    }

    @Override
    public boolean allowSync() {
        return false;
    }

    @Override
    public boolean syncEnable() {
        return false;
    }

    @Override
    public void moveToNextAudio() {

    }

    @Override
    public String getCoverFilename() {
        return null;
    }

    @Override
    public Drawable getCoverDrawable() {
        return null;
    }

    @Override
    public String getIconFilename() {
        return null;
    }

    @Override
    public Drawable getIconDrawable() {
        return null;
    }

    @Override
    public boolean isFavorite() {
        return false;
    }

    @Override
    public String getPackageName() {
        return null;
    }

    @Override
    public String getAudioPath() {
        return null;
    }

    @Override
    public String getImagePath() {
        return null;
    }

    @Override
    public String getIconPath() {
        return null;
    }

    @Override
    public List<IBookCatalog> getCatalogs() {
        return null;
    }

    @Override
    public List<IBookPage> getPages() {
        return null;
    }

    /**
     * 载入
     * @param bookID 书ID
     */
    private void load(int bookID) {
//        IData data = null; //数据对象
//        Cursor cursor = null; //数据指针
//
//        try {
//            IConfig config = SystemManager.getConfig(getContext()); //获取全局配置
//            data = DataManager.createData(config.getBookDataSource()); //创建数据对象
//
//            //查询字符串
//            String sql = "SELECT * FROM [Book] WHERE [BookID] = " + bookID;
//            cursor = data.query(sql);
//
//            if (cursor.getCount() == 1) {
//                cursor.moveToFirst();
//                m.bookID = bookID; //书ID
//                m.bookName = cursor.getString(cursor.getColumnIndex("BookName")); //书名称
//                m.bookDepict = cursor.getString(cursor.getColumnIndex("BookDepict")); //书描述
//                m.allowSync = cursor.getInt(cursor.getColumnIndex("AllowSync")) != 0; //是否充许同步
//                m.syncEnable = cursor.getInt(cursor.getColumnIndex("SyncEnable")) != 0; //同步开关
//                m.isFavorite = cursor.getInt(cursor.getColumnIndex("IsFavorite")) != 0; //是否收藏
//                m.coverFilename = cursor.getString(cursor.getColumnIndex("CoverFilename")); //封面图片文件名
//                m.packageName = cursor.getString(cursor.getColumnIndex("PackageName")); //包名
//
//                m.publishID = cursor.getInt(cursor.getColumnIndex("Publish")); //出版社
//                m.gradeID = cursor.getInt(cursor.getColumnIndex("Grade")); //年级
//                m.subjectID = cursor.getInt(cursor.getColumnIndex("Subject")); //科目
//                m.termID = cursor.getInt(cursor.getColumnIndex("Term")); //学期
//
//                int contentType = cursor.getInt(cursor.getColumnIndex("ContentType")); //内容类型
//                int soundType = cursor.getInt(cursor.getColumnIndex("SoundType")); //声音类型
//                int fullMode = cursor.getInt(cursor.getColumnIndex("FullMode")); //全屏模式
//                int titleLinkMode = cursor.getInt(cursor.getColumnIndex("TitleLinkMode")); //标题链接模式
//                int iconLinkMode = cursor.getInt(cursor.getColumnIndex("IconLinkMode")); //图标链接模式
//                m.contentType = ContentType.values()[contentType]; //内容类型
//                m.soundType = SoundType.values()[soundType]; //声音类型
//                m.fullMode = FullMode.values()[fullMode]; //全屏模式
//                m.titleLinkMode = LinkMode.values()[titleLinkMode]; //标题链接模式
//                m.iconLinkMode = LinkMode.values()[iconLinkMode]; //图标链接模式
//
//                m.currentAudioIndex = cursor.getInt(cursor.getColumnIndex("CurrentAudio")); //当前语音目录索引
//                m.currentPageNumber = cursor.getInt(cursor.getColumnIndex("CurrentPage")); //当前显示页码
//            }
//        } finally {
//            if(cursor != null) cursor.close(); //关闭数据指针
//            if(data != null) data.close(); //关闭数据对象
//        }
    }

    /**
     * 获取应用程序上下文
     *
     * @return 应用程序上下文
     */
    private Context getContext() {
        return m.context;
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 应用程序上下文
         */
        Context context;

        /**
         * 书ID
         */
        int bookID;

        /**
         * 书名称
         */
        String bookName;

        /**
         * 书描述
         */
        String bookDepict;

        /**
         * 出版社ID
         */
        int publishID;

        /**
         * 出版社
         */
        String publish;

        /**
         * 年级ID
         */
        int gradeID;

        /**
         * 年级
         */
        String grade;

        /**
         * 科目ID
         */
        int subjectID;

        /**
         * 科目
         */
        String subject;

        /**
         * 科目ID
         */
        int termID;

        /**
         * 学期
         */
        String term;

        /**
         * 内容类型
         */
//        ContentType contentType;

        /**
         * 声音类型
         */
//        SoundType soundType;

        /**
         * 全屏模式
         */
//        FullMode fullMode;

        /**
         * 标题链接模式
         */
//        LinkMode titleLinkMode;

        /**
         * 图标链接模式
         */
//        LinkMode iconLinkMode;

        /**
         * 是否充许同步
         */
        boolean allowSync;

        /**
         * 同步开关
         */
        boolean syncEnable;

        /**
         * 当前语音目录索引
         */
        int currentAudioIndex;

        /**
         * 第一个语音目录
         */
        IBookCatalog firstAudio;

        /**
         * 当前语音目录
         */
        IBookCatalog currentAudio;

        /**
         * 最后一个语音目录
         */
        IBookCatalog lastAudio;

        /**
         * 当前显示页码
         */
        int currentPageNumber;

        /**
         * 当前显示页
         */
        IBookPage currentPage;

        /**
         * 封面图片文件名
         */
        String coverFilename;

        /**
         * 封面Drawable
         */
        Drawable coverDrawable;

        /**
         * 图标Drawable
         */
        Drawable iconDrawable;

        /**
         * 是否收藏
         */
        boolean isFavorite;

        /**
         * 包名
         */
        String packageName;

        /**
         * 目录集合
         */
        List<IBookCatalog> catalogs;

        /**
         * 页集合
         */
        List<IBookPage> pages;
    }
}
