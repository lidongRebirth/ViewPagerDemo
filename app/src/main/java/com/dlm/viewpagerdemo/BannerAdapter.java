package com.dlm.viewpagerdemo;

import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author LD
 * @Time 2019/6/18 10:38
 * @Describe 方案一：getCount()方法返回Integer.MAX_VALUE
 * @Modify
 */
public class BannerAdapter extends PagerAdapter {


    private List<Integer> myItemList;
    public static final int mLooperCount = 500;


    public BannerAdapter(List<Integer> itemList) {

        myItemList = new ArrayList<>();
        myItemList.addAll(itemList);
    }

    //*2019.6.20
    // 解决：如果getCount 的返回值为Integer.MAX_VALUE 的话，那么在setCurrentItem的时候会ANR(除了在onCreate 调用之外)
    @Override
    public int getCount() {
        return getRealCount() * mLooperCount;
    }

    //View 是否和 Object有关联关系
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    //销毁一个item数据的时候会回调
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    //初始化一个item数据的时候的回调
    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        //在Adapter instantiateItem(ViewGroup container, final int position) 中，现在的这个position是一个很大的数字，我们需要将它转换成一个真实的position，否则会越界报错。

        int realPosition = position%getRealCount();
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.itemview, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        imageView.setImageResource(myItemList.get(realPosition));
//        imageView.setImageResource(myItemList.get(position));

        container.addView(view);
        return view;
    }

    /**
     * 获取真实的Count
     *
     * @return
     */
    private int getRealCount() {
        return myItemList == null ? 0 : myItemList.size();
    }
}
