package com.study91.audiobook.book;

import com.study91.audiobook.system.SystemManager;

/**
 * 有声书管理器
 */
public class BookManager {
    private static Field M = new Field(); //静态私有字段

    /**
     * 设置有声书
     * @param bookID 有声书ID
     */
    public static void setBook(int bookID) {
        if (M.bookID != bookID) {
            M.bookID = bookID;
        }
    }

    /**
     * 获取有声书
     * @return 有声书
     */
    public static IBook getBook() {
        if (M.book == null) {

        }

        return M.book;
    }

    private static IBookData getBookData() {
        if (M.bookData == null) {
            M.bookData = new BookData(SystemManager.getContext());
        }

        return M.bookData;
    }



    /**
     * 静态私有字段类
     */
    private static class Field {
        /**
         * 有声书ID
         */
        int bookID;

        /**
         * 有声书
         */
        IBook book;

        /**
         * 有声书数据
         */
        IBookData bookData;
    }

}
