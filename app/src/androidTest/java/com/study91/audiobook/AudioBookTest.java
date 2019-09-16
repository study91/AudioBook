package com.study91.audiobook;

import android.util.Log;

import androidx.test.runner.AndroidJUnit4;

import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.data.DataSourceManager;
import com.study91.audiobook.data.IDataSource;
import com.study91.audiobook.user.IUser;
import com.study91.audiobook.user.UserManager;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class AudioBookTest {
    private final String TAG = "AudioBookTest";

    @Test
    public void test() {
        testBook();
        testUser();
    }

    /**
     * 测试有声书
     */
    @Test
    private void testBook() {
        BookManager.setBook(1);
        IBook book = BookManager.getBook();
        Log.d(TAG, "书ID=" + book.getBookID());
        Log.d(TAG, "书名称=" + book.getBookName());
        Log.d(TAG, "书描述=" + book.getBookDepict());
        Log.d(TAG, "出版社=" + book.getPublish());
        Log.d(TAG, "年级=" + book.getGrade());
        Log.d(TAG, "学科=" + book.getSubject());
        Log.d(TAG, "学期=" + book.getTerm());
        Log.d(TAG, "内容类型=" + book.getContentType().getString());
        Log.d(TAG, "声音类型=" + book.getSoundType().getString());
        Log.d(TAG, "全屏模式=" + book.getFullMode().getString());
        Log.d(TAG, "标题链接模式=" + book.getTitleLinkMode().getString());
        Log.d(TAG, "图标链接模式=" + book.getIconLinkMode().getString());
        Log.d(TAG, "是否充许同步=" + book.allowSync());
        Log.d(TAG, "同步开关=" + book.syncEnable());
        Log.d(TAG, "封面图片文件名=" + book.getCoverFilename());
        Log.d(TAG, "是否收藏=" + book.isFavorite());
        Log.d(TAG, "包名=" + book.getPackageName());
        Log.d(TAG, "语音文件路径=" + book.getAudioPath());
        Log.d(TAG, "图片文件路径=" + book.getImagePath());
        Log.d(TAG, "图标文件路径=" + book.getIconPath());
    }

    /**
     * 测试用户
     */
    @Test
    private void testUser() {
        UserManager.setUser(1);
        IUser user = UserManager.getUser();
        Log.d(TAG, "用户ID=" + user.getUserID());
        Log.d(TAG, "用户名=" + user.getUserName());
        Log.d(TAG, "是否测试用户=" + user.isTest());
        Log.d(TAG, "有声书ID=" + user.getBookID());
        Log.d(TAG, "语音音量=" + (int)(user.getAudioVolume() * 100) + "%");
        Log.d(TAG, "音乐音量=" + (int)(user.getMusicVolume() * 100) + "%");
        Log.d(TAG, "语音循环模式=" + user.getAudioLoopMode().getString());
        Log.d(TAG, "音乐循环模式=" + user.getMusicLoopMode().getString());
    }

    /**
     * 测试数据源
     */
    @Test
    private void testDataSource() {
        IDataSource dataSource = DataSourceManager.getBookDataSource();
        Log.d(TAG, "有声书数据源=" + dataSource.getDataSource());

        dataSource = DataSourceManager.getUserDataSource();
        Log.d(TAG, "用户数据源=" + dataSource.getDataSource());
    }
}
