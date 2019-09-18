package com.study91.audiobook.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.book.IBookPage;
import com.study91.audiobook.data.DataSourceManager;
import com.study91.audiobook.data.IDataSource;
import com.study91.audiobook.user.IUser;
import com.study91.audiobook.user.UserManager;

import java.util.List;

/**
 * 主活动
 */
public class MainActivity extends AppCompatActivity {
    private final String TAG = "AudioBookTest";
    private UI ui = new UI();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置为竖屏显示
        setContentView(R.layout.activity_main);

        BookManager.setBook(1);

        ui.showPageButton = (Button) findViewById(R.id.showPageButton);
        ui.showPageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PageActivity.class);
                startActivity(intent);
            }
        });


//        testBook();
//        testUser();
//        testDataSource();
    }

    /**
     * 测试数据源
     */
    private void testDataSource() {
        Log.d(TAG, "****** 测试数据源 ******");

        IDataSource dataSource = DataSourceManager.getBookDataSource();
        Log.d(TAG, "有声书数据源=" + dataSource.getDataSource());

        dataSource = DataSourceManager.getUserDataSource();
        Log.d(TAG, "用户数据源=" + dataSource.getDataSource());
    }

    /**
     * 测试有声书
     */
    private void testBook() {
        Log.d(TAG, "****** 测试有声书 ******");
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

        testBookCatalog(book.getCatalogs());
        testBookPage(book.getPages());
    }

    /**
     * 测试有声书目录
     * @param catalogs 目录集合
     */
    private void testBookCatalog(List<IBookCatalog> catalogs) {
        Log.d(TAG, "****** 测试有声书目录 ******");
        for (IBookCatalog catalog : catalogs) {
            Log.d(TAG, catalog.getPosition() + "." + catalog.getTitle());
        }
    }

    /**
     * 测试有声书页
     * @param pages 页集合
     */
    private void testBookPage(List<IBookPage> pages) {
        Log.d(TAG, "****** 测试有声书页 ******");
        for (IBookPage page : pages) {
            Log.d(TAG, page.getPosition() + "." + page.getImageFilename());
        }
    }

    /**
     * 测试用户
     */
    private void testUser() {
        Log.d(TAG, "****** 测试用户 ******");
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
     * 私有界面类
     */
    private class UI {
        Button showPageButton;
    }
}
