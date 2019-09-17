package com.study91.audiobook.book;

import android.database.Cursor;

/**
 * 有声书管理器
 */
public class BookManager {
    private static Field m = new Field(); //静态私有字段

    /**
     * 设置有声书
     * @param bookID 有声书ID
     */
    public static void setBook(int bookID) {
        if (m.bookID != bookID) {
            m.bookID = bookID;
            m.book = createBook(getBookID());
        }
    }

    /**
     * 获取有声书
     * @return 有声书
     */
    public static IBook getBook() {
        if (m.book == null) {
            m.book = createBook(getBookID());
        }

        return m.book;
    }

    /**
     * 获取有声书ID
     * @return 有声书ID
     */
    private static int getBookID() {
        return m.bookID;
    }

    /**
     * 创建有声书
     * @param bookID 有声书ID
     * @return 有声书
     */
    static IBook createBook(int bookID) {
        return new Book(bookID);
    }

    /**
     * 创建有声书目录
     * @param cursor 数据指针
     * @return 有声书目录
     */
    static IBookCatalog createCatalog(Cursor cursor) {
        return new BookCatalog(cursor);
    }

    /**
     * 创建有声书页
     * @param cursor 数据指针
     * @return 有声书页
     */
    static IBookPage createPage(Cursor cursor) {
        return new BookPage(cursor);
    }

    /**
     * 静态私有字段类
     */
    private static class Field {
        /**
         * 有声书ID
         */
        static int bookID;

        /**
         * 有声书
         */
        static IBook book;
    }
}
