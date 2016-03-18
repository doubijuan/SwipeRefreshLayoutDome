package com.xiaolijuan.swiperefreshlayoutdome.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xiaolijuan.swiperefreshlayoutdome.R;

/**
 * 项目名称：SwipeRefreshLayoutDome
 * 类描述：配合RefreshAndLoadMoreView 完成下拉刷新、滑到底部自动加载更多
 * 创建人：xiaolijuan
 * 创建时间：2015/12/12 9:02
 */
public class LoadMoreListView extends ListView implements AbsListView.OnScrollListener {
    private View rooterView;
    private boolean isHaveMoreData = true;// 是否有更多数据(默认为有)
    private ProgressBar progressBar;
    private TextView tipContext;

    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private boolean isLoading = false;// 是否正在加载

    private OnLoadMoreListener mOnLoadMoreListener;

    public LoadMoreListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        //动态载入底部布局
        rooterView = LayoutInflater.from(context).inflate(
                R.layout.pull_to_load_footer, null);
        progressBar = (ProgressBar) rooterView.findViewById(R.id.footer_progressbar);
        tipContext = (TextView) rooterView.findViewById(R.id.footer_hint_textview);
        //向listView的底部添加布局(此时当给listView设置Item点击事件的时候，默认不触发这个添加的布局的点击事件)
        addFooterView(rooterView, null, false);
        setOnScrollListener(this);
    }

    public void setRefreshAndLoadMoreView(RefreshAndLoadMoreView mRefreshAndLoadMoreView) {
        this.mRefreshAndLoadMoreView = mRefreshAndLoadMoreView;
    }

    /**
     * 设置是否还有更多数据
     *
     * @param isHaveMoreData
     */
    public void setHaveMoreData(boolean isHaveMoreData) {
        this.isHaveMoreData = isHaveMoreData;
        if (!isHaveMoreData) {
            tipContext.setText("只有这么多啦");
            progressBar.setVisibility(View.GONE);
        } else {
            tipContext.setText("正在加载");
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 加载完成
     */
    public void onLoadComplete() {
        isLoading = false;
    }

    public boolean isLoading() {
        return isLoading;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
            if (view.getLastVisiblePosition() == view.getCount() - 1 && (mRefreshAndLoadMoreView != null &&
                    !mRefreshAndLoadMoreView.isRefreshing()) && !isLoading && mOnLoadMoreListener != null && isHaveMoreData) {
                isLoading = true;
                mOnLoadMoreListener.onLoadMore();
            }
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    /**
     * 加载更多的监听
     */
    public static interface OnLoadMoreListener {
        public void onLoadMore();
    }

    /**
     * 设置加载监听
     *
     * @param mOnLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }
}
