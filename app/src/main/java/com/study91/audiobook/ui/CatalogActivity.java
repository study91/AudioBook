package com.study91.audiobook.ui;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.study91.audiobook.R;

/**
 * 有声书目录窗口
 */
public class CatalogActivity extends Activity {
    private UI ui = new UI(); //私有界面

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //设置为竖屏显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); //不关屏
        setContentView(R.layout.activity_catalog);

        //载入控件
        ui.backButton = (Button) findViewById(R.id.backButton); //返回按钮
        ui.smallFontButton = (Button) findViewById(R.id.smallFontButton); //缩小字体按钮
        ui.largeFontButton = (Button) findViewById(R.id.largeFontButton); //放大字体按钮

        //设置事件监听器
        ui.backButton.setOnClickListener(new OnBackButtonClickListener()); //返回按钮单击事件
    }

    /**
     * 返回按钮单击事件监听器
     */
    private class OnBackButtonClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            finish();
        }
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 返回按钮
         */
        Button backButton;

        /**
         * 缩小字体按钮
         */
        Button smallFontButton;

        /**
         * 放大字体按钮
         */
        Button largeFontButton;
    }
}
