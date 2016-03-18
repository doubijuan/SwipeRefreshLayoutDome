# 探索SwipeRefreshLayout配合自定义ListView完成下拉刷新、滑到底部自动加载更多
在Android开发过程中经常需要实现上下拉刷新功能，Google推出的下拉刷新控件SwipeRefreshLayout（彩虹条），由于官方版本只有下拉刷新而没有上拉加载更多的功能，很多人也尝试在这个基础上进行改写。今天尝试一下使用SwipeRefreshLayout配合自定义ListView实现下拉刷新、滑到底部自动加载更多的功能。

效果图如下所示，在进入页面的时候加载自动刷新，滑到底部自动加载更多，当数据已经加载完成则显示已经加载完成，否则上拉任可继续加载
![](http://img.blog.csdn.net/20151213002712767?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

先贴一下项目结构图吧，这样可能对于整个项目的了解会比较清晰一些
![](http://img.blog.csdn.net/20151213001300820?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center)

#1.在效果图中我们可以看到在头部刷新时候的进度条ProgressBar的颜色本身是可以改变的，所以在color.xml中定义头部刷新时候的四种颜色，用于通过此颜色资源文件设置进度条动画的颜色

```Java
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <!-- 灰色 -->
    <color name="grey">#FF999999</color>
    <!--头部刷新时候的四种颜色-->
    <color name="refresh_color_1">#ff00ddff</color>
    <color name="refresh_color_2">#ff99cc00</color>
    <color name="refresh_color_3">#ffffbb33</color>
    <color name="refresh_color_4">#ffff4444</color>
</resources>
```

#2.重写SwipeRefreshLayout下拉刷新控件
```Java
package com.xiaolijuan.swiperefreshlayoutdome.widget;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.xiaolijuan.swiperefreshlayoutdome.R;

/**
 * 项目名称：SwipeRefreshLayoutDome
 * 类描述：配合LoadMoreListView 完成下拉刷新、滑到底部自动加载更多
 * 创建人：xiaolijuan
 * 创建时间：2015/12/12 9:00
 */
public class RefreshAndLoadMoreView extends SwipeRefreshLayout {
    private LoadMoreListView mLoadMoreListView;

    /**
     * 构造方法，用于在布局文件中用到这个自定义SwipeRefreshLayout控件
     * @param context
     * @param attrs
     */
    public RefreshAndLoadMoreView(Context context, AttributeSet attrs) {
        super(context, attrs);
        Resources res = getResources();
        //通过颜色资源文件设置进度动画的颜色资源
        setColorSchemeColors(res.getColor(R.color.refresh_color_1),
                res.getColor(R.color.refresh_color_2),
                res.getColor(R.color.refresh_color_3),
                res.getColor(R.color.refresh_color_4));
    }
    public void setLoadMoreListView(LoadMoreListView mLoadMoreListView) {
        this.mLoadMoreListView = mLoadMoreListView;
    }

    /**
     * 触屏事件,如果ListView不为空且数据还在加载中，则继续加载直至完成加载才触摸此事件
     * @param ev
     * @return
     */
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (mLoadMoreListView != null && mLoadMoreListView.isLoading()) {
            return false;
        }
        return super.onTouchEvent(ev);
    }
}
```

#3.在update_loading_progressbar.xml定义进度条旋转的动画效果，用于设置绘制不显示进度的进度条的Drawable对象
```Java
<?xml version="1.0" encoding="utf-8"?>
<!--画面转移旋转动画效果：以组件的中点为中心顺时针从0度旋转到720度-->
<rotate xmlns:android="http://schemas.android.com/apk/res/android"
    android:drawable="@mipmap/default_ptr_rotate_gray"
    android:duration="700"
    android:fromDegrees="0"
    android:pivotX="50%"
    android:pivotY="50%"
    android:toDegrees="720" />
```

#4.pull_to_load_footer.xml这是在ListView中载入的头部布局
```Java
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_gravity="center"
    android:orientation="vertical">

    <View style="@style/horizontalDivider" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="48dp"
        android:gravity="center">

        <ProgressBar
            android:id="@+id/footer_progressbar"
            android:layout_width="20dp"
            android:layout_height="20dp"
            android:layout_marginRight="8dp"
            android:gravity="center"
            android:indeterminateDrawable="@anim/update_loading_progressbar"
            android:visibility="visible" />

        <TextView
            android:id="@+id/footer_hint_textview"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="正在加载中"
            android:textColor="#999999"
            android:textSize="14dp" />
    </LinearLayout>
</LinearLayout>

```

#5.重写ListView，用于配合RefreshAndLoadMoreView 完成下拉刷新、滑到底部自动加载更多
```Java
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

```

#6.MyAdapter
```Java
package com.xiaolijuan.swiperefreshlayoutdome.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xiaolijuan.swiperefreshlayoutdome.R;

import java.util.List;

/**
 * 项目名称：SwipeRefreshLayoutDome
 * 类描述：适配器
 * 创建人：xiaolijuan
 * 创建时间：2015/12/12 22:09
 */
public class MyAdapter extends BaseAdapter {
    private Context context;
    private List<String> mTitleArray;// 标题数组
    private int layoutId;

    /**
     * 构造方法
     * @param context 上下文对象
     * @param mTitleArray 标题数组
     * @param layoutId 布局Id
     */
    public MyAdapter(Context context, List<String> mTitleArray, int layoutId) {
        this.context = context;
        this.mTitleArray = mTitleArray;
        this.layoutId = layoutId;
    }

    /**
     * 获取Item总数
     * @return
     */
    @Override
    public int getCount() {
        return mTitleArray.size();
    }

    /**
     * 获取一个Item对象
     * @param position
     * @return
     */
    @Override
    public Object getItem(int position) {
        return mTitleArray.get(position);
    }

    /**
     * 获取指定item的Id
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 绘制的内容均在此实现
     * @param position position就是位置从0开始
     * @param convertView convertView是Spinner中每一项要显示的view
     * @param parent parent就是父窗体了，也就是ListView
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View item = convertView != null ? convertView : View.inflate(context, layoutId, null);
        TextView txt_name = (TextView) item.findViewById(R.id.txt_title);
        txt_name.setText(mTitleArray.get(position));
        return item;
    }
}
```

#7.具体代码，代码写的很详细
```Java
package com.xiaolijuan.swiperefreshlayoutdome.activits;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.xiaolijuan.swiperefreshlayoutdome.R;
import com.xiaolijuan.swiperefreshlayoutdome.adapter.MyAdapter;
import com.xiaolijuan.swiperefreshlayoutdome.widget.LoadMoreListView;
import com.xiaolijuan.swiperefreshlayoutdome.widget.RefreshAndLoadMoreView;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：SwipeRefreshLayoutDome
 * 类描述：主界面
 * 创建人：xiaolijuan
 * 创建时间：2015/12/12 20:00
 */
public class MainActivity extends Activity {
    private Context mContext;
    private int pageIndex = 0;
    private MyAdapter adapter;
    private LoadMoreListView mLoadMoreListView;
    private RefreshAndLoadMoreView mRefreshAndLoadMoreView;
    private List<String> datas = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mLoadMoreListView = (LoadMoreListView) findViewById(R.id.load_more_list);
        mRefreshAndLoadMoreView = (RefreshAndLoadMoreView) findViewById(R.id.refresh_and_load_more);
        adapter = new MyAdapter(mContext, datas, R.layout.item_layout);
        mLoadMoreListView.setAdapter(adapter);
        initData();
    }

    private void initData() {
        //程序开始就加载第一页数据
        loadData(1);
        mRefreshAndLoadMoreView.setLoadMoreListView(mLoadMoreListView);
        mLoadMoreListView.setRefreshAndLoadMoreView(mRefreshAndLoadMoreView);
        //设置下拉刷新监听
        mRefreshAndLoadMoreView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(1);
            }
        });
        //设置加载监听
        mLoadMoreListView.setOnLoadMoreListener(new LoadMoreListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadData(pageIndex + 1);
            }
        });
        mLoadMoreListView.setOnItemClickListener(new ItemClickListener());
    }


    /**
     * 加载数据
     */
    private void loadData(final int tempPageIndex) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (tempPageIndex == 1) {
                    datas.clear();
                }
                getDatas(tempPageIndex);
                //在这里我设置当加载到第三页时设置已经加载完成
                if (tempPageIndex == 3) {
                    mLoadMoreListView.setHaveMoreData(false);
                } else {
                    mLoadMoreListView.setHaveMoreData(true);
                }
                pageIndex = tempPageIndex;
                adapter.notifyDataSetChanged();
                //当加载完成之后设置此时不在刷新状态
                mRefreshAndLoadMoreView.setRefreshing(false);
                mLoadMoreListView.onLoadComplete();
            }
        }, 1000);
    }

    /**
     * 模拟一些数据源
     *
     * @return
     */
    private List<String> getDatas(final int tempPageIndex) {
        switch (tempPageIndex) {
            case 1:
                for (int i = 0; i < 10; i++) {
                    datas.add("这是第" + (i + 1) + "个Item");
                }
                break;
            case 2:
                for (int i = 0; i < 10; i++) {
                    datas.add("这是第" + (i + 11) + "个Item");
                }
                break;
            case 3:
                for (int i = 0; i < 10; i++) {
                    datas.add("这是第" + (i + 21) + "个Item");
                }
                break;
            default:
                break;
        }

        return datas;
    }

    /**
     * 为ListView每个Item添加点击事件
     */
    public class ItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Toast.makeText(getApplicationContext(), datas.get(position), Toast.LENGTH_LONG).show();
        }
    }
}

```

#8.结束啦，由于代码里边都有注释哒，我就不做解释咯，有不足的还望指导








