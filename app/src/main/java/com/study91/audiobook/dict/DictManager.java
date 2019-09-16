package com.study91.audiobook.dict;

/**
 * 字典管理器
 */
public class DictManager {
    /**
     * 创建字典
     * @return 字典
     */
    public static IDict createDict() {
        return new Dict();
    }
}
