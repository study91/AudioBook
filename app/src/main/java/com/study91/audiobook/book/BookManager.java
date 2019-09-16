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
        if (M.bookID != bookID) {
            M.bookID = bookID;
            M.book = new Book(getBookID());
        }
    }

    /**
     * 获取有声书
     * @return 有声书
     */
    public static IBook getBook() {
        if (M.book == null) {
            M.book = new Book(getBookID());
        }

        return M.book;
    }

    /**
     * 获取有声书数据
     * @return 有声书数据
     */
    public static IBookData getBookData() {
        if (M.bookData == null) {
            M.bookData = new BookData();
        }

        return M.bookData;
    }

    /**
     * 获取有声书ID
     * @return 有声书ID
     */
    private static int getBookID() {
        return M.bookID;
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
