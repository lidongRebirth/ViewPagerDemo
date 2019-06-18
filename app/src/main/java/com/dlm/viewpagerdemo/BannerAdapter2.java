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
 * @Time 2019/6/18 15:01
 * @Describe 方案二：数据首尾各再填充一条数据
 * @Modify
 */
public class BannerAdapter2 extends PagerAdapter {

    private List<Integer> myItemList;


    public BannerAdapter2(List<Integer> itemList) {

        myItemList = new ArrayList<>();
        myItemList.addAll(itemList);
    }

    @Override
    public int getCount() {
        return myItemList == null ? 0 : myItemList.size();
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

        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.itemview, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.item_image);
        imageView.setImageResource(myItemList.get(position));
        container.addView(view);
        return view;
    }


}
