package com.study91.audiobook.book;

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

    }

    /**
     * 获取有声书
     * @return 有声书
     */
    public static IBook getBook() {
        return M.book;
    }

    /**
     * 静态私有字段类
     */
    private static class Field {
        /**
         * 有声书
         */
        IBook book;
    }

}
