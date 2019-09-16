package com.study91.audiobook.dict;

import android.content.Context;

import com.study91.audiobook.system.SystemManager;

/**
 * 显示模式
 */
public enum DisplayMode {
    /**
     * 不显示
     */
    NONE,

    /**
     * 显示页码
     */
    DISPLAY_PAGE,

    /**
     * 显示图标
     */
    DISPLAY_ICON;

    /**
     * 获取显示模式字符串
     * @return 显示模式字符串
     */
    public String getString() {
        IDict dict = DictManager.createDict();
        return dict.getValue(this.getClass().getSimpleName(), this.ordinal());
    }
}
