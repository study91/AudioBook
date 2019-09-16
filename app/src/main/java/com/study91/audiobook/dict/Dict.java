package com.study91.audiobook.dict;

import android.database.Cursor;

import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBookData;
import com.study91.audiobook.data.DataManager;
import com.study91.audiobook.data.IData;

/**
 * 字典
 */
class Dict implements IDict {
    /**
     * 构造器
     */
    Dict() {
    }

    @Override
    public String getValue(String type, int id) {
        String result = "";

        IData data = null; //数据对象
        Cursor cursor = null; //数据指针

        try {
            IBookData bookData = BookManager.getBookData(); //获取有声书数据
            data = DataManager.createData(bookData.getDataSource()); //数据对象

            //查询字符串
            String sql = "SELECT * FROM [Dict] " +
                    "WHERE [DictType] = '" + type + "' AND [DictID] = " + id;

            cursor = data.query(sql);

            if (cursor.getCount() == 1) {
                cursor.moveToFirst();
                result = cursor.getString(cursor.getColumnIndex("DictValue")); //字典值
            }
        } finally {
            if(cursor != null) cursor.close(); //关闭数据指针
            if(data != null) data.close(); //关闭数据对象
        }

        return result;
    }
}
