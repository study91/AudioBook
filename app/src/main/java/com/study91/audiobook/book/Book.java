package com.study91.audiobook.book;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.widget.Toast;

import com.study91.audiobook.R;
import com.study91.audiobook.data.DataManager;
import com.study91.audiobook.data.DataSourceManager;
import com.study91.audiobook.data.IData;
import com.study91.audiobook.data.IDataSource;
import com.study91.audiobook.dict.ContentType;
import com.study91.audiobook.dict.DictManager;
import com.study91.audiobook.dict.FullMode;
import com.study91.audiobook.dict.IDict;
import com.study91.audiobook.dict.LinkMode;
import com.study91.audiobook.dict.SoundType;
import com.study91.audiobook.system.SystemManager;
import com.study91.audiobook.tools.ImageTools;

import java.util.ArrayList;
import java.util.List;

/**
 * 有声书类
 */
class Book implements IBook {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param bookID 有声书ID
     */
    Book(int bookID) {
        load(bookID); //载入
        checkCurrentAudio(); //检查当前语音
        checkCurrentPage(); //检查当前页
    }

    @Override
    public int getBookID() {
        return m.bookID;
    }

    @Override
    public String getBookName() {
        return m.bookName;
    }

    @Override
    public String getBookDepict() {
        return m.bookDepict;
    }

    @Override
    public String getPublish() {
        if (m.publish == null) {
            IDict dict = DictManager.createDict();
            m.publish = dict.getValue("Publish", m.publishID);
        }

        return m.publish;
    }

    @Override
    public String getGrade() {
        if (m.grade == null) {
            IDict dict = DictManager.createDict();
            m.grade = dict.getValue("Grade", m.gradeID);
        }

        return m.grade;
    }

    @Override
    public String getSubject() {
        if (m.subject == null) {
            IDict dict = DictManager.createDict();
            m.subject = dict.getValue("Subject", m.subjectID);
        }

        return m.subject;
    }

    @Override
    public String getTerm() {
        if (m.term == null) {
            IDict dict = DictManager.createDict();
            m.term = dict.getValue("Term", m.termID);
        }

        return m.term;
    }

    @Override
    public ContentType getContentType() {
        return m.contentType;
    }

    @Override
    public SoundType getSoundType() {
        return m.soundType;
    }

    @Override
    public FullMode getFullMode() {
        return m.fullMode;
    }

    @Override
    public LinkMode getTitleLinkMode() {
        return m.titleLinkMode;
    }

    @Override
    public LinkMode getIconLinkMode() {
        return m.iconLinkMode;
    }

    @Override
    public boolean allowSync() {
        return m.allowSync;
    }

    @Override
    public boolean syncEnable() {
        return m.syncEnable;
    }

    @Override
    public void setSyncEnable(boolean value) {
        m.syncEnable = value; //重置同步开关
        updateSync(value); //更新同步值

        //显示提示信息
        String msg = getContext().getResources().getString(R.string.msg_sync_disable);
        if (syncEnable()) msg = getContext().getResources().getString(R.string.msg_sync_enable);
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show(); //显示信息
    }

    @Override
    public void moveToNextAudio() {
        if (getCurrentAudio().getCatalogID() == getLastAudio().getCatalogID()) {
            //当前语音目录是复读终点时，将复读起点设置为当前语音目录
            setCurrentAudio(getFirstAudio());
        } else if (getFirstAudio().getCatalogID() != getLastAudio().getCatalogID()) {
            //复读起点和复读终点不相同时，遍历查找下一个语音目录
            List<IBookCatalog> catalogs = getCatalogs();
            for (IBookCatalog catalog : catalogs) {
                if (catalog.getIndex() > getCurrentAudio().getIndex() &&
                        catalog.hasAudio() &&
                        catalog.allowPlayAudio()) {
                    setCurrentAudio(catalog); //设置为当前语音目录
                    break;
                }
            }
        }
    }

    @Override
    public void setFirstAudio(IBookCatalog catalog) {
        IBookCatalog oldFirstAudio = getFirstAudio();

        //目录索引不等于原始复读起点索引时执行
        if (catalog.getIndex() != oldFirstAudio.getIndex()) {
            updateFirstAudio(catalog); //先更新数据库中的复读起点

            //遍历重置目录中的语音播放开关
            List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表
            for (IBookCatalog bookCatalog : catalogs) {
                //目录有语音时执行
                if (bookCatalog.hasAudio()) {
                    if (bookCatalog.getIndex() >= catalog.getIndex() &&
                            bookCatalog.getIndex() < oldFirstAudio.getIndex()) {
                        //大于等于复读起点索引且小于原始复读起点索引的目录设置为充许播放
                        bookCatalog.setAudioPlayEnable(true);
                    } else if (bookCatalog.allowPlayAudio() &&
                            bookCatalog.getIndex() < catalog.getIndex()) {
                        //小于复读起点索引且充许播放的目录设置为禁止播放
                        bookCatalog.setAudioPlayEnable(false);
                    }
                }
            }

            catalog.setAudioPlayEnable(true);
            m.firstAudio = catalog; //重置复读起点语音
        }
    }

    @Override
    public IBookCatalog getFirstAudio() {
        //如果第一个语音目录为空，遍历查找第一个语音目录
        if (m.firstAudio == null) {
            List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表

            for (IBookCatalog catalog : catalogs) {
                //查找到第一个语音时退出遍历
                if (catalog.hasAudio() && catalog.allowPlayAudio()) {
                    m.firstAudio = catalog;
                    break;
                }
            }
        }

        return m.firstAudio;
    }

    @Override
    public void setCurrentAudio(IBookCatalog catalog) {
        //只有目录索引和当前语音目录索引不相同时，才重新设置当前语音目录
        if (catalog.getCatalogID() != getCurrentAudioID()) {
            m.currentAudioID = catalog.getCatalogID(); //重置当前语音目录ID
            m.currentAudio = catalog; //重置当前语音目录
            updateCurrentAudio(); //更新当前语音

            //目录不充许播放时，将此目录以外的其他目录设置为禁止播放，并将复读起点和复读终点都设置为此目录
            if (!catalog.allowPlayAudio()) {
                catalog.setAudioPlayEnable(true); //设置目录为充许播放
                updateAudioPlayEnable(catalog); //更新语音目录播放开关
                setOtherAudioNotPlay(catalog); //设置其他语音目录禁止播放
                updateOtherAudioNotPlay(catalog); //更新其它语音目录禁止播放
                m.firstAudio = catalog; //重置复读起点
                m.lastAudio = catalog; //重置复读终点
            }
        }
    }

    @Override
    public IBookCatalog getCurrentAudio() {
        return m.currentAudio;
    }

    @Override
    public void setCurrentAudioPosition(int position) {
        m.currentAudioPosition = position;
        updateCurrentAudioPosition(position); //更新当前语音位置
    }

    @Override
    public int getCurrentAudioPosition() {
        return m.currentAudioPosition;
    }

    @Override
    public IBookPage getCurrentAudioPage(long position) {
        IBookPage audioPage = null;

        List<IBookPage> pages = getPages(); //获取页集合
        String audioFilename = getCurrentAudio().getAudioFilename(); //当前语音文件名

        //遍历查找当前语音页
        for (IBookPage page : pages) {
            //只对有语音且语音文件与页语音文件相同的页进行判断
            if (page.hasAudio() && page.getAudioFilename().equals(audioFilename)) {
                if (page.getAudioStartTime() <= position) {
                    //找到最后一个小于时间参数的内容
                    audioPage = page;
                } else {
                    //如果开始时间大于时间参数，退出循环
                    break;
                }
            }
        }

        return audioPage;
    }

    @Override
    public void setLastAudio(IBookCatalog catalog) {
        IBookCatalog oldLastAudio = getLastAudio();

        //目录索引不等于原始复读终点索引时执行
        if (catalog.getIndex() != oldLastAudio.getIndex()) {
            updateLastAudio(catalog); //先更新数据库中的复读终点

            //遍历重置目录中的语音播放开关
            List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表
            for (IBookCatalog bookCatalog : catalogs) {
                //目录有语音时执行
                if (bookCatalog.hasAudio()) {
                    if (bookCatalog.getIndex() <= catalog.getIndex() &&
                            bookCatalog.getIndex() > oldLastAudio.getIndex()) {
                        //小于等于复读终点索引且大于原始复读终点索引的目录设置为充许播放
                        bookCatalog.setAudioPlayEnable(true);
                    } else if (bookCatalog.allowPlayAudio() &&
                            bookCatalog.getIndex() > catalog.getIndex()) {
                        //大于复读终点索引且充许播放的目录设置为禁止播放
                        bookCatalog.setAudioPlayEnable(false);
                    }
                }
            }

            catalog.setAudioPlayEnable(true);
            m.lastAudio = catalog; //重置复读终点语音
        }
    }

    @Override
    public IBookCatalog getLastAudio() {
        //如果最后一个语音目录为空，遍历查找最后一个语音目录
        if (m.lastAudio == null) {
            List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表

            for (int i = catalogs.size() - 1; i > 0; i--) {
                IBookCatalog catalog = catalogs.get(i); //目录

                //查找到最后一个语音时退出遍历
                if (catalog.hasAudio() && catalog.allowPlayAudio()) {
                    m.lastAudio = catalog;
                    break;
                }
            }
        }

        return m.lastAudio;
    }

    @Override
    public void setAudioPlayEnable(IBookCatalog catalog) {
        if (catalog.allowPlayAudio()) {
            //充许播放时执行不充许播放操作
            if (catalog.getIndex() == getFirstAudio().getIndex()) {
                setFirstAudio(getNextAudio(catalog));
            } else if (catalog.getIndex() == getLastAudio().getIndex()) {
                setLastAudio(getPreviousAudio(catalog));
            } else if(catalog.getIndex() > getFirstAudio().getIndex() &&
                    catalog.getIndex() < getLastAudio().getIndex()){
                catalog.setAudioPlayEnable(false);
                updateAudioPlayEnable(catalog);
            }
        } else {
            catalog.setAudioPlayEnable(true);
            updateAudioPlayEnable(catalog);

            //不充许播放时执行充许播放操作
            if (catalog.getIndex() < getFirstAudio().getIndex()) {
                m.firstAudio = catalog;
            } else if (catalog.getIndex() > getLastAudio().getIndex()) {
                m.lastAudio = catalog;
            }
        }
    }

    @Override
    public void setCurrentPage(IBookPage page) {
        //只有页参数的页码和当前页码不相同时，才重新设置当前页
        if (page.getPageNumber() != getCurrentPageNumber()) {
            m.currentPageNumber = page.getPageNumber(); //重置当前页码
            m.currentPage = page; //重置当前页
            updateCurrentPage(); //更新当前页
        }
    }

    @Override
    public IBookPage getCurrentPage() {
        return m.currentPage;
    }

    @Override
    public String getCoverFilename() {
        String coverFilename = null;

        if (m.coverFilename != null) {
            coverFilename = getImagePath() + m.coverFilename + ".jpg";
        }

        return coverFilename;
    }

    @Override
    public Drawable getCoverDrawable() {
        return ImageTools.getDrawable(getContext(), getCoverFilename());
    }

    @Override
    public String getIconFilename() {
        String iconFilename = null;

        if (m.coverFilename != null) {
            iconFilename = getIconPath() + m.coverFilename + ".png";
        }

        return iconFilename;
    }

    @Override
    public Drawable getIconDrawable() {
        return ImageTools.getDrawable(getContext(), getCoverFilename());
    }

    @Override
    public boolean isFavorite() {
        return m.isFavorite;
    }

    @Override
    public String getPackageName() {
        return m.packageName;
    }

    @Override
    public String getAudioPath() {
        String audioPath = getContext().getString(R.string.audio_path);
        audioPath = audioPath.replace("[PACKAGE]", getPackageName());
        return audioPath;
    }

    @Override
    public String getImagePath() {
        String imagePath = getContext().getString(R.string.image_path);
        imagePath = imagePath.replace("[PACKAGE]", getPackageName());
        return imagePath;
    }

    @Override
    public String getIconPath() {
        String iconPath = getContext().getString(R.string.icon_path);
        iconPath = iconPath.replace("[PACKAGE]", getPackageName());
        return iconPath;
    }

    @Override
    public List<IBookCatalog> getCatalogs() {
        if (m.catalogs == null) {
            IData data = null;
            Cursor cursor = null;

            try {
                IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取数据源
                data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

                //查询字符串
                String sql = "SELECT * FROM [BookCatalog] " +
                        "WHERE [BookID] = " + getBookID() + " " +
                        "ORDER BY [Index]";

                cursor = data.query(sql); //查询数据

                if (cursor.getCount() > 0) {
                    m.catalogs = new ArrayList<>(); //实例化目录列表

                    //遍历目录并添加到目录列表
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        IBookCatalog catalog = BookManager.createCatalog(cursor);
                        m.catalogs.add(catalog); //添加到集合
                    }
                }
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }

        return m.catalogs;
    }

    @Override
    public List<IBookPage> getPages() {
        if (m.pages == null) {
            IData data = null;
            Cursor cursor = null;

            try {
                IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取数据源
                data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

                //查询字符串
                String sql = "SELECT * FROM [BookPage] " +
                        "WHERE [BookID] = " + getBookID() + " " +
                        "ORDER BY [PageNumber]";

                cursor = data.query(sql); //查询数据

                if (cursor.getCount() > 0) {
                    m.pages = new ArrayList<>(); //实例化页列表

                    //遍历页并添加到页列表
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        IBookPage page = BookManager.createPage(cursor); //创建页
                        m.pages.add(page); //添加到集合
                    }
                }
            } finally {
                if(cursor != null) cursor.close(); //关闭数据指针
                if(data != null) data.close(); //关闭数据对象
            }
        }

        return m.pages;
    }

    /**
     * 载入
     * @param bookID 书ID
     */
    private void load(int bookID) {
        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取有声书数据源
            data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

            //查询字符串
            String sql = "SELECT * FROM [Book] WHERE [BookID] = " + bookID;
            cursor = data.query(sql);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                m.bookID = bookID; //书ID
                m.bookName = cursor.getString(cursor.getColumnIndex("BookName")); //书名称
                m.bookDepict = cursor.getString(cursor.getColumnIndex("BookDepict")); //书描述
                m.allowSync = cursor.getInt(cursor.getColumnIndex("AllowSync")) != 0; //是否充许同步
                m.syncEnable = cursor.getInt(cursor.getColumnIndex("SyncEnable")) != 0; //同步开关
                m.isFavorite = cursor.getInt(cursor.getColumnIndex("IsFavorite")) != 0; //是否收藏
                m.coverFilename = cursor.getString(cursor.getColumnIndex("CoverFilename")); //封面图片文件名
                m.packageName = cursor.getString(cursor.getColumnIndex("PackageName")); //包名

                m.publishID = cursor.getInt(cursor.getColumnIndex("Publish")); //出版社
                m.gradeID = cursor.getInt(cursor.getColumnIndex("Grade")); //年级
                m.subjectID = cursor.getInt(cursor.getColumnIndex("Subject")); //科目
                m.termID = cursor.getInt(cursor.getColumnIndex("Term")); //学期

                int contentType = cursor.getInt(cursor.getColumnIndex("ContentType")); //内容类型
                int soundType = cursor.getInt(cursor.getColumnIndex("SoundType")); //声音类型
                int fullMode = cursor.getInt(cursor.getColumnIndex("FullMode")); //全屏模式
//                int titleLinkMode = cursor.getInt(cursor.getColumnIndex("TitleLinkMode")); //标题链接模式
//                int iconLinkMode = cursor.getInt(cursor.getColumnIndex("IconLinkMode")); //图标链接模式
                m.contentType = ContentType.values()[contentType]; //内容类型
                m.soundType = SoundType.values()[soundType]; //声音类型
                m.fullMode = FullMode.values()[fullMode]; //全屏模式
//                m.titleLinkMode = LinkMode.values()[titleLinkMode]; //标题链接模式
//                m.iconLinkMode = LinkMode.values()[iconLinkMode]; //图标链接模式

                m.currentAudioID = cursor.getInt(cursor.getColumnIndex("CurrentAudio")); //当前语音目录ID
                m.currentAudioPosition = cursor.getInt(cursor.getColumnIndex("CurrentPosition")); //当前语音位置
                m.currentPageNumber = cursor.getInt(cursor.getColumnIndex("CurrentPage")); //当前显示页码
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return SystemManager.getContext();
    }

    /**
     * 获取当前语音目录ID
     * @return 当前语音目录ID
     */
    private int getCurrentAudioID() {
        return m.currentAudioID;
    }

    /**
     * 获取当前页码
     * @return 当前页码
     */
    private int getCurrentPageNumber() {
        return m.currentPageNumber;
    }

    /**
     * 获取传入目录参数的上一个语音目录
     * @param catalog 目录
     * @return 上一个语音目录
     */
    private IBookCatalog getPreviousAudio(IBookCatalog catalog) {
        IBookCatalog previousAudio = null;

        IBookCatalog firstAudio = getFirstAudio(); //第一个语音目录

        if (catalog.getIndex() == firstAudio.getIndex()) {
            //如果当前语音是复读起点时，将复读终点赋给上一个语音目录
            previousAudio = getLastAudio();
        } else {
            //遍历查询上一个语音目录
            List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表
            for (int i = catalogs.size() - 1; i >= 0; i--) {
                IBookCatalog bookCatalog = catalogs.get(i);
                //目录有语音且索引小于当前目录索引时，找到上一个语音目录
                if (bookCatalog.hasAudio() && bookCatalog.allowPlayAudio() &&
                        bookCatalog.getIndex() < catalog.getIndex()) {
                    previousAudio = bookCatalog;
                    break;
                }
            }
        }

        return previousAudio;
    }

    /**
     * 获取传入目录参数的下一个语音目录
     * @param catalog 目录
     * @return 下一个语音目录
     */
    private IBookCatalog getNextAudio(IBookCatalog catalog) {
        IBookCatalog nextAudio = null;

        IBookCatalog lastAudio = getLastAudio(); //最后一个语音目录

        if (catalog.getIndex() == lastAudio.getIndex()) {
            //如果当前语音是复读终点时，将复读起点赋给下一个语音目录
            nextAudio = getFirstAudio();
        } else {
            //遍历查询下一个语音目录
            List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表
            for (IBookCatalog bookCatalog : catalogs) {
                //目录有语音且索引大于当前目录索引时，找到下一个语音目录
                if (bookCatalog.hasAudio() && bookCatalog.allowPlayAudio() &&
                        bookCatalog.getIndex() > catalog.getIndex()) {
                    nextAudio = bookCatalog;
                    break;
                }
            }
        }

        return nextAudio;
    }


    /**
     * 设置其他语音目录禁止播放
     * @param catalog 目录
     */
    private void setOtherAudioNotPlay(IBookCatalog catalog) {
        //更新目录列表
        List<IBookCatalog> catalogs = getCatalogs();
        for (IBookCatalog bookCatalog : catalogs) {
            if (bookCatalog.hasAudio()) {
                if (bookCatalog.getCatalogID() != catalog.getCatalogID()) {
                    bookCatalog.setAudioPlayEnable(false);
                }
            }
        }
    }

    /**
     * 检查当前语音目录
     */
    private void checkCurrentAudio() {
        //遍历查找当前语音目录
        List<IBookCatalog> catalogs = getCatalogs(); //获取目录列表
        for (IBookCatalog catalog : catalogs) {
            if (catalog.hasAudio() && catalog.allowPlayAudio()) {
                //如果找到当前语音目录，退出遍历
                if (catalog.getCatalogID() == getCurrentAudioID()) {
                    m.currentAudio = catalog;
                    break;
                }
            }
        }

        //如果遍历结束仍未找到当前语音目录时将复读起点设置为当前语音目录
        if (m.currentAudio == null) {
            setCurrentAudio(getFirstAudio()); //设置复读起点为当前语音目录
            updateCurrentAudio(); //更新当前语音
        }
    }

    /**
     * 检查当前页
     */
    private void checkCurrentPage() {
        //遍历查找当前页
        List<IBookPage> pages = getPages(); //获取页列表
        for (IBookPage page : pages) {
            //如果找到当前页，退出遍历
            if (page.getPageNumber() == getCurrentPageNumber()) {
                m.currentPage = page;
                break;
            }
        }

        //如果遍历结束仍未找到当前页时将第一页设置为当前页
        if (m.currentPage == null) {
            setCurrentPage(pages.get(0)); //设置第一页为当前页
            updateCurrentPage(); //更新当前页
        }
    }

    /**
     * 更新当前语音
     */
    private void updateCurrentAudio() {
        //创建线程更新当前语音
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IData data = null; //数据对象

                try {
                    IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取数据源
                    data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

                    //更新数据库
                    String sql = "UPDATE [Book] " +
                            "SET [CurrentAudio] = " + getCurrentAudio().getCatalogID() + " " +
                            "WHERE [BookID] = " + getBookID();
                    data.execute(sql); //执行更新
                } finally {
                    if(data != null) data.close(); //关闭数据对象
                }
            }
        });

        thread.start();
    }

    /**
     * 更新当前页
     */
    private void updateCurrentPage() {
        //创建线程更新当前语音
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IData data = null; //数据对象

                try {
                    IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取数据源
                    data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

                    //更新数据库
                    String sql = "UPDATE [Book] " +
                            "SET [CurrentPage] = " + getCurrentPage().getPageNumber() + " " +
                            "WHERE [BookID] = " + getBookID();
                    data.execute(sql); //执行更新
                } finally {
                    if(data != null) data.close(); //关闭数据对象
                }
            }
        });

        thread.start();
    }

    /**
     * 更新复读起点
     */
    private void updateFirstAudio(final IBookCatalog catalog) {
        //创建线程更新当前语音
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IData data = null; //数据对象

                try {
                    IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取数据源
                    data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

                    //更新字符串
                    String sql = "UPDATE [BookCatalog] " +
                            "SET [AllowPlayAudio] = 0 " +
                            "WHERE " +
                            "[BookID] = " + getBookID() + " AND " +
                            "[HasAudio] = 1 AND " +
                            "[Index] < " + catalog.getIndex();

                    data.execute(sql); //执行更新

                    //如果新复读起点小于原复读起点的页号，打开新复读起点和原复读起点之间的播放开关
                    IBookCatalog oldFirstAudio = getFirstAudio();
                    if (catalog.getIndex() < oldFirstAudio.getIndex()) {
                        sql = "UPDATE [BookCatalog] " +
                                "SET [AllowPlayAudio] = 1 " +
                                "WHERE " +
                                "[BookID] = " + getBookID() + " AND " +
                                "[HasAudio] = 1 AND " +
                                "[Index] >= " + catalog.getIndex() + " AND " +
                                "[Index] < " + oldFirstAudio.getIndex();

                        data.execute(sql); //执行更新
                    }
                } finally {
                    if(data != null) data.close(); //关闭数据对象
                }
            }
        });

        thread.start();
    }

    /**
     * 更新复读终点
     * @param catalog 目录
     */
    private void updateLastAudio(final IBookCatalog catalog) {
        //创建线程更新当前语音
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IData data = null;

                try {
                    IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取数据源
                    data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

                    //更新字符串
                    String sql = "UPDATE [BookCatalog] " +
                            "SET [AllowPlayAudio] = 0 " +
                            "WHERE " +
                            "[BookID] = " + getBookID() + " AND " +
                            "[HasAudio] = 1 AND " +
                            "[Index] > " + catalog.getIndex();

                    data.execute(sql); //执行更新

                    //如果新复读起点小于原复读起点的页号，打开新复读起点和原复读起点之间的播放开关
                    IBookCatalog oldLastAudio = getLastAudio();
                    if (catalog.getIndex() < oldLastAudio.getIndex()) {
                        sql = "UPDATE [BookCatalog] " +
                                "SET [AllowPlayAudio] = 1 " +
                                "WHERE " +
                                "[BookID] = " + getBookID() + " AND " +
                                "[HasAudio] = 1 AND " +
                                "[Index] <= " + catalog.getIndex() + " AND " +
                                "[Index] > " + oldLastAudio.getIndex();

                        data.execute(sql); //执行更新
                    }
                } finally {
                    if (data != null) data.close(); //关闭数据对象
                }
            }
        });

        thread.start();
    }

    /**
     * 更新语音播放开关
     * @param catalog 目录
     */
    private void updateAudioPlayEnable(final IBookCatalog catalog) {
        //创建线程更新当前语音
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IData data = null;

                try {
                    IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取数据源
                    data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

                    //将播放开关值转换为数据库中的实际存储值
                    int allowPlayAudio = 0;
                    if (catalog.allowPlayAudio()) allowPlayAudio = 1;

                    //更新字符串
                    String sql = "UPDATE [BookCatalog] " +
                            "SET [AllowPlayAudio] = " + allowPlayAudio + " " +
                            "WHERE " +
                            "[BookID] = " + getBookID() + " AND " +
                            "[Index] = " + catalog.getIndex();

                    data.execute(sql); //执行更新
                }  finally {
                    if(data != null) data.close(); //关闭数据对象
                }
            }
        });

        thread.start();
    }

    /**
     * 更新其它语音目录禁止播放
     * @param catalog 目录
     */
    private void updateOtherAudioNotPlay(final IBookCatalog catalog) {
        //创建线程更新当前语音
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IData data = null;

                try {
                    IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取数据源
                    data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

                    //更新数据库
                    String sql = "UPDATE [BookCatalog] " +
                            "SET [AllowPlayAudio] = 0 " +
                            "WHERE " +
                            "[BookID] = " + getBookID() + " AND " +
                            "[HasAudio] = 1 AND " +
                            "[Index] <> " + catalog.getIndex();
                    data.execute(sql); //执行更新
                } finally {
                    if(data != null) data.close(); //关闭数据对象
                }
            }
        });

        thread.start();
    }

    /**
     * 更新同步
     * @param value 同步值
     */
    private void updateSync(final boolean value) {
        //创建线程更新当前语音
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IData data = null;

                try {
                    IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取数据源
                    data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

                    //将同步复读开关参数值转换为数据库中实际存储的整型值
                    int syncEnable = 0;
                    if(value) syncEnable = 1;

                    //更新数据库
                    String sql = "UPDATE [Book] " +
                            "SET [SyncEnable] = " + syncEnable + " " +
                            "WHERE " +
                            "[BookID] = " + getBookID();
                    data.execute(sql); //执行更新
                } finally {
                    if(data != null) data.close(); //关闭数据对象
                }
            }
        });

        thread.start();
    }

    /**
     * 更新当前语音位置
     * @param position 语音位置
     */
    private void updateCurrentAudioPosition(final int position) {
        //创建线程更新当前语音
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                IData data = null;

                try {
                    IDataSource dataSource = DataSourceManager.getBookDataSource(); //获取数据源
                    data = DataManager.createData(dataSource.getDataSource()); //创建数据对象

                    //更新数据库
                    String sql = "UPDATE [Book] " +
                            "SET [CurrentPosition] = " + position + " " +
                            "WHERE " +
                            "[BookID] = " + getBookID();
                    data.execute(sql); //执行更新
                } finally {
                    if(data != null) data.close(); //关闭数据对象
                }
            }
        });

        thread.start();
    }

    /**
     * 私有字段类
     */
    private class Field {
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
        ContentType contentType;

        /**
         * 声音类型
         */
        SoundType soundType;

        /**
         * 全屏模式
         */
        FullMode fullMode;

        /**
         * 标题链接模式
         */
        LinkMode titleLinkMode;

        /**
         * 图标链接模式
         */
        LinkMode iconLinkMode;

        /**
         * 是否充许同步
         */
        boolean allowSync;

        /**
         * 同步开关
         */
        boolean syncEnable;

        /**
         * 当前语音目录ID
         */
        int currentAudioID;

        /**
         * 第一个语音目录
         */
        IBookCatalog firstAudio;

        /**
         * 当前语音目录
         */
        IBookCatalog currentAudio;

        /**
         * 当前语音位置
         */
        int currentAudioPosition;

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
