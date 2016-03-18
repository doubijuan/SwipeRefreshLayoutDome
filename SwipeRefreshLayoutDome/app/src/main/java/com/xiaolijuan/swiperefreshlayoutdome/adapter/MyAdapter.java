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