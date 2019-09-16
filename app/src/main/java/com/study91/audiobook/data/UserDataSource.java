package com.study91.audiobook.data;

import android.content.Context;

import com.study91.audiobook.R;
import com.study91.audiobook.system.SystemManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 用户数据源
 */
class UserDataSource implements IDataSource {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     */
    UserDataSource() {
        checkDatabase(); //检查数据库
    }

    @Override
    public String getDataSource() {
        if (m.dataSource == null) {
            String filename = getFilename(getAssertDataSource());
            m.dataSource = getContext().getDatabasePath(filename).getAbsolutePath();
        }

        return m.dataSource;
    }

    /**
     * 获取Assert数据源
     * @return Assert数据源
     */
    public String getAssertDataSource() {
        if (m.assertDataSource == null) {
            m.assertDataSource = getContext().getResources().getString(R.string.user_data_source);
        }

        return m.assertDataSource;
    }


    /**
     * 检查数据库
     */
    private void checkDatabase() {
        File file = new File(getDataSource()); //实例化文件对象

        //如果目标文件不存在，复制Asserts资源文件到数据库目录中
        if (!file.exists()) {
            String path = file.getParent(); //获取路径字符串
            file = new File(path);

            //如果目录不存在，先创建目录
            if (!file.exists()) {
                file.mkdirs(); //创建目录
            }

            //复制数据库
            try {
                InputStream inputStream = getContext().getAssets().open(getAssertDataSource()); //输入流
                OutputStream outputStream = new FileOutputStream(getDataSource()); //输出流

                byte[] buffer = new byte[8192];//定义缓冲区，缓冲区大小设置为8M
                int count;

                while((count = inputStream.read(buffer)) > 0){
                    outputStream.write(buffer, 0, count);
                }

                inputStream.close(); //关闭输入流
                outputStream.close(); //关闭输出流
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * 获取文件名
     * @param fullFilename 带路径的完整文件名
     * @return 文件名
     */
    private String getFilename(String fullFilename) {
        String filename = null;

        int start=fullFilename.lastIndexOf("/"); //获取最后一个"/"的位置
        int end=fullFilename.length(); //获取最后一个字符的位置
        if(start!=-1 && end!=-1){
            filename = fullFilename.substring(start+1,end); //提取文件名
        }

        return filename;
    }

    /**
     * 获取应用程序上下文
     * @return 应用程序上下文
     */
    private Context getContext() {
        return SystemManager.getContext();
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 数据源
         */
        String dataSource;

        /**
         * Assert数据源
         */
        String assertDataSource;
    }
}
