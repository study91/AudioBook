package com.study91.audiobook.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookPage;

/**
 * 页图片视图页
 */
public class BookImageViewPager extends ViewPager {
    private Field m = new Field(); //私有字段

    /**
     * 构造器
     * @param context 应用程序上下文
     */
    public BookImageViewPager(@NonNull Context context) {
        super(context);

        setAdapter(new BookImageViewPagerAdapter()); //设置适配器
        setCurrentItem(getBook().getCurrentPage().getPosition()); //设置当前页
    }

    /**
     * 设置单击事件监听器
     * @param listener 单击事件监听器
     */
    public void setOnSingleTapListener(OnSingleTapListener listener) {
        m.onSingleTapListener = listener;
    }

    /**
     * 获取单击事件监听器
     * @return 单击事件监听器
     */
    private OnSingleTapListener getOnSingleTapListener() {
        return m.onSingleTapListener;
    }

    /**
     * 获取有声书
     * @return 有声书
     */
    private IBook getBook() {
        return BookManager.getBook();
    }

    /**
     * 页图片视图页适配器
     */
    private class BookImageViewPagerAdapter extends PagerAdapter {
        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            IBookPage page = getBook().getPages().get(position); //获取当前显示页

            ZoomImageView zoomImageView = new ZoomImageView(getContext()); //实例化图片缩放控件
            zoomImageView.setImageDrawable(page.getImageDrawable()); //设置图片Drawable

            //设置单击事件监听器
            zoomImageView.setOnSingleTapListener(new OnSingleTapListener() {
                @Override
                public void onSingleTap() {
                    if (getOnSingleTapListener() != null) {
                        getOnSingleTapListener().onSingleTap();
                    }
                }
            });

            container.addView(zoomImageView);
            return zoomImageView;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View)object);
        }

        @Override
        public int getCount() {
            return getBook().getPages().size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return view == object;
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 单击事件监听器
         */
        OnSingleTapListener onSingleTapListener;
    }
}
