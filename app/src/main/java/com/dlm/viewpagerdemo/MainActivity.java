package com.dlm.viewpagerdemo;

import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BannerAdapter bannerAdapter;         //getCount()返回Integer.MAX_VALUE方案
    private BannerAdapter2 bannerAdapter2;      //首尾填充数据方案
    private List<Integer> itemList1, itemList2;
    private ViewPager viewPager1, viewPager2;
    private int currentPosition1, currentPosition2;

    private boolean mIsAutoPlay = true; //是否自动播放
    private long mDelayedTime = 3000;
    private Handler mHandler = new Handler();
    private Handler mHandler2 = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager1 = findViewById(R.id.viewpager_banner1);
        viewPager2 = findViewById(R.id.viewpager_banner2);

        //方案一
        initData1();
        //方案二
        initData2();

    }

    //*------------------------------------------方案一----------------------------------------------
    private void initData1() {
        itemList1 = new ArrayList<>();
        itemList1.add(R.drawable.ic_pic1);
        itemList1.add(R.drawable.ic_pic2);
        itemList1.add(R.drawable.ic_pic3);
        itemList1.add(R.drawable.ic_pic4);

        bannerAdapter = new BannerAdapter(itemList1);
        viewPager1.setAdapter(bannerAdapter);
//        设置当前选中的item
        currentPosition1 = getStartItem();
        viewPager1.setCurrentItem(currentPosition1);
    }

    private int getStartItem() {

        if(getRealCount() == 0){
            return 0;
        }
        // 我们设置当前选中的位置为Integer.MAX_VALUE / 2,这样开始就能往左滑动
        // 但是要保证这个值与getRealPosition 的 余数为0，因为要从第一页开始显示
        int currentItem = getRealCount() * BannerAdapter.mLooperCount / 2;
        if(currentItem % getRealCount()  ==0 ){
            return currentItem;
        }
        // 直到找到从0开始的位置
        while (currentItem % getRealCount() != 0){
            currentItem++;
        }
        return currentItem;


    }

    /**
     * 获取真实的Count
     *
     * @return
     */
    private int getRealCount() {
        return itemList1 == null ? 0 : itemList1.size();
    }

    //*------------------------------------------方案二----------------------------------------------
    private void initData2() {

        itemList2 = new ArrayList<>();
        itemList2.add(R.drawable.ic_pic4);
        itemList2.add(R.drawable.ic_pic1);
        itemList2.add(R.drawable.ic_pic2);
        itemList2.add(R.drawable.ic_pic3);
        itemList2.add(R.drawable.ic_pic4);
        itemList2.add(R.drawable.ic_pic1);

        bannerAdapter2 = new BannerAdapter2(itemList2);
        viewPager2.setAdapter(bannerAdapter2);
        currentPosition2 = 1;

        viewPager2.setCurrentItem(currentPosition2);

        viewPager2.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                currentPosition2 = i;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                //验证当前的滑动是否结束
                if (state == ViewPager.SCROLL_STATE_IDLE) { //滑动动画做完的时候
                    Log.i("ceshi", "onPageScrollStateChanged: 滑动状态结束");
                    if (currentPosition2 == 0) {
                        viewPager2.setCurrentItem(itemList2.size() - 2, false);//切换，不要动画效果

                    } else if (currentPosition2 == itemList2.size() - 1) {
                        viewPager2.setCurrentItem(1, false);//切换，不要动画效果
                    }
                }
            }
        });
    }

    //*--------------------------------------轮播---------------------------------------------------
    private final Runnable mLoopRunnable = new Runnable() {
        @Override
        public void run() {
            if (mIsAutoPlay) {

                //方案一,
                currentPosition1 = viewPager1.getCurrentItem();
                currentPosition1++;
                if (currentPosition1 == bannerAdapter.getCount() - 1) {
                    currentPosition1 = 0;   //为了循环所以变为0
                    viewPager1.setCurrentItem(currentPosition1, false);
                    mHandler.postDelayed(this, mDelayedTime);
                } else {
                    viewPager1.setCurrentItem(currentPosition1);
                    mHandler.postDelayed(this, mDelayedTime);
                }
            } else {
                mHandler.postDelayed(this, mDelayedTime);
            }
        }
    };

    private final Runnable mLoopRunnable2 = new Runnable() {
        @Override
        public void run() {
            if (mIsAutoPlay) {
                //方案二:多添两条数据
                currentPosition2 = viewPager2.getCurrentItem();
                currentPosition2++;
                //不需要为了循环轮播来判断是否到达最后一页然后
                viewPager2.setCurrentItem(currentPosition2);
                mHandler2.postDelayed(this, mDelayedTime);

            } else {
                mHandler2.postDelayed(this, mDelayedTime);
            }
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        //开始轮播
        mHandler.postDelayed(mLoopRunnable, mDelayedTime);
        mHandler2.postDelayed(mLoopRunnable2, mDelayedTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //停止轮播
        mHandler.removeCallbacks(mLoopRunnable);
        mHandler2.removeCallbacks(mLoopRunnable2);

    }
}
