package com.study91.audiobook.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.study91.audiobook.R;
import com.study91.audiobook.book.BookManager;
import com.study91.audiobook.book.IBook;
import com.study91.audiobook.book.IBookCatalog;
import com.study91.audiobook.media.IBookMediaPlayer;
import com.study91.audiobook.media.MediaClient;
import com.study91.audiobook.ui.PageActivity;

import java.util.List;

/**
 * 有声书目录视图
 */
public class BookCatalogView extends RelativeLayout {
    private Field m = new Field(); //私有字段
    private UI ui = new UI(); //私有界面

    /**
     * 构造器
     * @param context 应用程序上下文
     * @param attrs 属性集合
     */
    public BookCatalogView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //从布局文件中获取Layout
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.view_catalog, this);

        getMediaClient().register(); //注册媒体客户端
        getMediaClient().setOnReceiver(new MediaClientBroadcastReceiver()); //设置广播接收器

        ui.listView = (ExpandableListView) findViewById(R.id.catalogListView); //获取列表视图
        ui.listView.setGroupIndicator(null); //去掉默认的下拉箭头图标
        ui.listView.setAdapter(getAdapter()); //设置有声书目录视图适配器
        ui.listView.setSelection(getBook().getCurrentAudio().getPosition()); //设置列表选择项
        ui.listView.setOnGroupExpandListener(new OnCatalogGroupExpandListener()); //列表项展开事件
    }

    @Override
    protected void onDetachedFromWindow() {
        getMediaClient().unregister(); //注销媒体客户端
        super.onDetachedFromWindow();
    }

    /**
     * 获取有声书
     * @return 有声书
     */
    private IBook getBook() {
        return BookManager.getBook();
    }

    /**
     * 获取有声书目录列表
     * @return 声书目录列表
     */
    private List<IBookCatalog> getCatalogs() {
        return getBook().getCatalogs();
    }

    /**
     * 获取有声书目录视图适配器
     * @return 有声书目录视图适配器
     */
    private CatalogViewAdapter getAdapter() {
        if (m.adapter == null) {
            m.adapter = new CatalogViewAdapter();
        }

        return m.adapter;
    }

    /**
     * 设置当前目录ID
     */
    private void setCurrentAudioID(int catalogID) {
        if (catalogID != getCurrentAudioID()) {
            m.currentAudioID = catalogID;
        }
    }

    /**
     * 获取当前目录ID
     */
    private int getCurrentAudioID() {
        return m.currentAudioID;
    }

    /**
     * 设置是否正在播放
     * @param isPlaying true=正在播放，false=没有播放
     */
    private void setIsPlaying(boolean isPlaying) {
        m.isPlaying = isPlaying;
    }

    /**
     * 是否正在播放
     * @return true=正在播放，false=没有播放
     */
    private boolean isPlaying() {
        return m.isPlaying;
    }

    /**
     * 获取媒体客户端
     * @return 媒体客户端
     */
    private MediaClient getMediaClient() {
        if (m.mediaClient == null) {
            m.mediaClient = new MediaClient(getContext());
        }

        return m.mediaClient;
    }

    /**
     * 媒体客户端广播接收器
     */
    private class MediaClientBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean isRefresh = false; //是否刷新变量

            //有声书索引和原索引不相同时，需要刷新目录
            if (getCurrentAudioID() != getBook().getCurrentAudio().getCatalogID()) {
                setCurrentAudioID(getBook().getCurrentAudio().getCatalogID());
                isRefresh = true;
            }

            //播放状态发生变化时，需要刷新目录
            IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer();
            if (mediaPlayer != null && isPlaying() != mediaPlayer.isPlaying()) {
                setIsPlaying(mediaPlayer.isPlaying());
                isRefresh = true;
            }

            //刷新目录
            if (isRefresh) {
                getAdapter().notifyDataSetChanged();
            }
        }
    }

    /**
     * 列表项展开事件监听器
     */
    private class OnCatalogGroupExpandListener implements ExpandableListView.OnGroupExpandListener {
        @Override
        public void onGroupExpand(int groupPosition) {
            //遍历实现只展开当前目录项，非当前目录项全部收缩
            List<IBookCatalog> catalogs = getCatalogs();

            for (int i = 0; i < catalogs.size(); i++) {
                if (groupPosition != i && ui.listView.isGroupExpanded(groupPosition)) {
                    ui.listView.collapseGroup(i);
                }
            }
        }
    }

    /**
     * 有声书目录视图适配器
     */
    private class CatalogViewAdapter extends BaseExpandableListAdapter {
        @Override
        public int getGroupCount() {
            return getCatalogs().size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return getCatalogs().get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return getCatalogs().get(groupPosition).getCatalogID();
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            //载入列表组布局
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_catalog_group, parent, false);

            //加载控件
            ui.group.iconImageView = (ImageView) view.findViewById(R.id.iconImageView); //图标
            ui.group.pageTextView = (TextView) view.findViewById(R.id.pageTextView); //页码
            ui.group.titleTextView = (TextView) view.findViewById(R.id.titleTextView); //标题
            ui.group.playButton = (Button) view.findViewById(R.id.playButton); //播放按钮
            ui.group.loopImageView = (ImageView) view.findViewById(R.id.loopImageView); //循环图标

            IBookCatalog catalog = getCatalogs().get(groupPosition); //获取目录
            ui.group.iconImageView.setImageDrawable(catalog.getIconDrawable()); //设置图标

            //播放按钮（注：如果语音开关值为false时，不显示播放按钮）
            ui.group.playButton.setFocusable(false);
            if (!catalog.hasAudio()) {
                ui.group.playButton.setVisibility(View.INVISIBLE);
            }

            //设置当前项背景色
            if (catalog.getCatalogID() == getBook().getCurrentAudio().getCatalogID()) {
                view.setBackgroundResource(R.color.catalog_group_current); //设置背景色
                ui.group.titleTextView.setEllipsize(TextUtils.TruncateAt.MARQUEE); //当前项标题设置为超长滚动

                IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer();
                if (mediaPlayer != null && mediaPlayer.isPlaying()) { //播放状态
                    ui.group.playButton.setBackgroundResource(R.drawable.catalog_group_pause); //暂停图标
                } else { //暂停状态
                    ui.group.playButton.setBackgroundResource(R.drawable.catalog_group_play); //播放图标
                }
            }

            //设置循环播放图标
            if (catalog.hasAudio() && catalog.allowPlayAudio()) {
                if (catalog.getCatalogID() == getBook().getFirstAudio().getCatalogID()) {
                    ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_first); //复读起点语音图标
                } else if (catalog.getCatalogID() == getBook().getLastAudio().getCatalogID()) {
                    ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_last); //复读终点语音图标
                } else {
                    ui.group.loopImageView.setBackgroundResource(R.mipmap.catalog_group_loop_middle); //复读中间语音图标
                }

                //复读起点和复读终点相同时，不显示循环图标
                if (getBook().getFirstAudio().getCatalogID() == getBook().getLastAudio().getCatalogID()) {
                    ui.group.loopImageView.setVisibility(View.INVISIBLE); //不显示循环图标
                }
            }

            ui.group.titleTextView.setText(catalog.getTitle()); //目录标题

            //设置事件监听器
            ui.group.playButton.setOnClickListener(new OnPlayButtonClickListener(catalog)); //播放按钮单击
            ui.group.iconImageView.setOnClickListener(new OnDisplayPageClickListener(catalog)); //图标单击

            return view;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            //载入列表子视图布局
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_catalog_child, parent, false);

            //载入控件
            ui.child.firstButton = (Button) view.findViewById(R.id.firstButton); //复读起点按钮
            ui.child.lastButton = (Button) view.findViewById(R.id.lastButton); //复读终点按钮
            ui.child.displayButton = (Button) view.findViewById(R.id.displayButton); //显示按钮
            ui.child.explainButton = (Button) view.findViewById(R.id.explainButton); //详解按钮
            ui.child.playEnableButton = (Button) view.findViewById(R.id.playEnableButton); //播放开关按钮

            IBookCatalog catalog = getCatalogs().get(groupPosition); //目录

            //没有解释的目录，关闭详解按钮
            if (!catalog.hasExplain()) {
                ui.child.explainButton.setVisibility(View.GONE);
            }

            if (catalog.hasAudio()) {
                if (catalog.getIndex() < getBook().getCurrentAudio().getIndex()) { //页号小于当前目录页号
                    ui.child.lastButton.setVisibility(View.GONE); //复读终点按钮禁用
                    ui.child.firstButton.setVisibility(View.VISIBLE); //复读起点按钮可用
                } else if (catalog.getIndex() > getBook().getCurrentAudio().getIndex()) { //页号大于当前目录页号
                    ui.child.lastButton.setVisibility(View.VISIBLE); //复读终点按钮可用
                    ui.child.firstButton.setVisibility(View.GONE); //复读起点按钮禁用
                } else { //页号等于当前目录页号
                    ui.child.playEnableButton.setEnabled(false); //播放开关按钮禁用
                    ui.child.playEnableButton.setVisibility(View.GONE);
                }

                if (catalog.getIndex() == getBook().getFirstAudio().getIndex()) {
                    ui.child.firstButton.setVisibility(View.GONE); //复读起点按钮禁用
                }

                if (catalog.getIndex() == getBook().getLastAudio().getIndex()) {
                    ui.child.lastButton.setVisibility(View.GONE); //复读终点按钮禁用
                }

                if(catalog.allowPlayAudio()) { //播放开关打开
                    ui.child.playEnableButton.setBackgroundResource(R.drawable.catalog_child_cancel);
                } else { //播放开关关闭
                    ui.child.playEnableButton.setBackgroundResource(R.drawable.catalog_child_add);
                }
            } else {
                //没有语音的目录，关闭复读起点、复读终点、播放开关按钮
                ui.child.firstButton.setVisibility(View.GONE); //禁用复读起点按钮
                ui.child.lastButton.setVisibility(View.GONE); //禁用复读终点按钮
                ui.child.playEnableButton.setVisibility(View.GONE); //禁用播放开关按钮
            }

            //设置控件事件
            ui.child.displayButton.setOnClickListener(new OnDisplayPageClickListener(catalog)); //显示按钮单击事件
            ui.child.firstButton.setOnClickListener(new OnFirstButtonClickListener(catalog)); //复读起点按钮单击事件
            ui.child.lastButton.setOnClickListener(new OnLastButtonClickListener(catalog)); //复读终点按钮单击事件
            ui.child.playEnableButton.setOnClickListener(new OnPlayEnableButtonClickListener(catalog)); //播放开关按钮单击事件

            return view;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }
    }

    /**
     * 播放按钮单击事件监听器
     */
    private class OnPlayButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        OnPlayButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog;
        }

        @Override
        public void onClick(View v) {
           IBookMediaPlayer mediaPlayer = getMediaClient().getMediaPlayer(); //获取媒体播放器
            if (getBook().getCurrentAudio().getCatalogID() == getCatalog().getCatalogID()) {
                //点击的是当前目录的播放按钮
                if (isPlaying()) {
                    mediaPlayer.pause(); //如果正在播放，就暂停播放
                } else {
                    mediaPlayer.play(); //如果暂停播放，就开始播放
                }
            } else {
                getBook().setCurrentAudio(getCatalog()); //重置当前语音目录

                //重置媒体播放器语音文件并播放语音
                mediaPlayer.setAudioFile(
                        getCatalog().getAudioFilename(),
                        getCatalog().getTitle(),
                        getCatalog().getIconFilename());
                mediaPlayer.play(); //播放当前语音
                getAdapter().notifyDataSetChanged();
            }
        }

        /**
         * 获取目录
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 书目录
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 显示页单击事件监听器
     */
    private class OnDisplayPageClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnDisplayPageClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //目录
        }

        @Override
        public void onClick(View v) {
            getBook().setCurrentPage(getCatalog().getPage()); //重置当前页
            Intent intent = new Intent(getContext(), PageActivity.class);
            getContext().startActivity(intent);
        }

        /**
         * 获取目录
         *
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 有声书内容
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 复读起点按钮单击事件监听器
     */
    private class OnFirstButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnFirstButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //有声书内容
        }

        @Override
        public void onClick(View v) {
            getBook().setFirstAudio(getCatalog()); //重置复读起点
            getAdapter().notifyDataSetChanged();
        }


        /**
         * 获取目录
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 书目录
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 复读终点按钮单击事件监听器
     */
    private class OnLastButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnLastButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //有声书内容
        }

        @Override
        public void onClick(View v) {
            getBook().setLastAudio(getCatalog()); //重置复读终点
            getAdapter().notifyDataSetChanged();
        }

        /**
         * 获取目录
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 书目录
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 播放开关按钮单击事件监听器
     */
    private class OnPlayEnableButtonClickListener implements View.OnClickListener {
        private Field m = new Field(); //私有字段

        /**
         * 构造器
         * @param catalog 目录
         */
        public OnPlayEnableButtonClickListener(IBookCatalog catalog) {
            m.catalog = catalog; //目录
        }

        @Override
        public void onClick(View v) {
            getBook().setAudioPlayEnable(getCatalog()); //重置语音播放开关
            getAdapter().notifyDataSetChanged(); //刷新
        }

        /**
         * 获取目录
         *
         * @return 目录
         */
        private IBookCatalog getCatalog() {
            return m.catalog;
        }

        /**
         * 私有字段类
         */
        private class Field {
            /**
             * 有声书内容
             */
            IBookCatalog catalog;
        }
    }

    /**
     * 私有字段类
     */
    private class Field {
        /**
         * 有声书目录视图适配器
         */
        CatalogViewAdapter adapter;

        /**
         * 媒体客户端
         */
        MediaClient mediaClient;

        /**
         * 是否正在播放
         */
        boolean isPlaying;

        /**
         * 当前语音目录ID
         */
        private int currentAudioID;
    }

    /**
     * 私有界面类
     */
    private class UI {
        /**
         * 列表视图
         */
        ExpandableListView listView;

        /**
         * 目录组项
         */
        CatalogGroup group = new CatalogGroup();

        /**
         * 目录子项
         */
        CatalogChild child = new CatalogChild();

        /**
         * 目录组项类
         */
        private class CatalogGroup {
            /**
             * 图标
             */
            ImageView iconImageView;

            /**
             * 索引
             */
            TextView pageTextView;

            /**
             * 标题
             */
            TextView titleTextView;

            /**
             * 循环图标
             */
            ImageView loopImageView;

            /**
             * 播放按钮
             */
            Button playButton;
        }

        /**
         * 目录子项类
         */
        private class CatalogChild {
            /**
             * 复读起点按钮
             */
            Button firstButton;

            /**
             * 复读终点按钮
             */
            Button lastButton;

            /**
             * 显示按钮
             */
            Button displayButton;

            /**
             * 解释按钮
             */
            Button explainButton;

            /**
             * 播放开关按钮
             */
            Button playEnableButton;
        }
    }
}
