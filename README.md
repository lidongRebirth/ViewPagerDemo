[博客地址](https://juejin.im/post/5d08b8a251882559ed71d789)
> 给自己的忠告：虽然轮子很好用，但是使用轮子的前提是：如果不去封装一些复杂的功能，自己会用最基本的方法写一个，不然再好的轮子那也是别人的，当自己项目遇到和轮子不一样的地方，那就只能束手无策或者改人家的源码，当然能看懂轮子的封装思想自己学以致用并且能够很轻松的更改源码那是最好不过的了。

# 1. 实现思路

**两种方案：**

（1）采用Adapter内的getCount()方法返回Integer.MAX_VALUE。

（2）在列表的最前面插入最后一条数据，在列表末尾插入第一个数据，造成循环的假象

# 2. 具体实现

## 2.1 方案一：getCount()返回Integer.MAX_VALUE

### 2.1.1 ViewPager无限循环

在ViewPager的Adapter内的getCount方法中，返回一个很大的数Integer.MAX_VALUE，理论上可以无限滑动。当显示完一个真实列表的周期后，又从真实列表的0位置显示数据，造成无限循环轮播的假象。因为ViewPager第一页不能向左滑动循环，所以我们要通过mViewPager.setCurrentItem(Integer.MAX_VALUE/2)设置选中的位置，这样最开始就可以向左滑动，但是因为要显示第一页所以该值%数据个数==0。因为设置为Integer.MAX_VALUE后会在setCurrentItem（）的时候发生ANR,所以这里使用一个自定义的较大的数比较好，这里我是用500

```java
//当前选中页
private int currentPosition;
//数据项个数
private List<Integer> itemList;

public static final int mLooperCount = 500;


//设置当前选中的item
currentPosition = getStartItem();
viewPager1.setCurrentItem(currentPosition1);

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

//获取数据项个数
private int getRealCount() {
    return itemList == null ? 0 : itemList.size();
}
```

Adapter只需将getCount()返回Integer.MAX_VALUE即可(这里我们改为具体的数值)，其他的操作是正常的操作。

```java
@Override
public int getCount() {
    return getRealCount() * mLooperCount;
}
```

### 2.1.2 加入轮播功能

**采用Handler的postDelayed方法**

```java
private Handler mHandler = new Handler();

@Override
protected void onResume() {
    super.onResume();
    //开始轮播
    mHandler.postDelayed(mLoopRunnable, mDelayedTime);
}

@Override
protected void onPause() {
    super.onPause();
    //停止轮播
    mHandler.removeCallbacks(mLoopRunnable);    
}
```

```java
private final Runnable mLoopRunnable = new Runnable() {
    @Override
    public void run() {
        if (mIsAutoPlay) {

            //方案一
            currentPosition1 = viewPager1.getCurrentItem();
            currentPosition1++;
            if (currentPosition1 == bannerAdapter.getCount() - 1) {		//滑到最后一个时
                currentPosition1 = 0;								  //切换到第0个
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
```

### 2.1.3 使用Integer.MAX_VALUE的争议

有人会觉得会影响内存，大家可以参考这篇文章[Android ViewPager 无限轮播Integer.MAX_VALUE 争议(看源码)](https://www.jianshu.com/p/c572eea78790)就能够解决疑惑。
### 2.1.4 注意
使用Integer.MAX_VALUE会在setCurrentItem（）的时候发生ANR,所以还是设置为一个比较大的数比较好。在代码中我已经更改为返回getRealCount()*500这一数值，如果文章中有返回Integer.MAX_VALUE的，那就是我还没更正，大家请自行更改。


## 2.2 方案二：数据项首尾添加两条数据

### 2.2.1 ViewPager无限循环

假设有三条数据，分别编号1、2、3，我们再创建一个新的列表，长度为真实列表的长度+2，在最前面插入最后一条数据3，在最后面插入第一条数据1，新列表就变为3、1、2、3、1，当viewpager滑动到位置0时就通过`setCurrentItem(int item,boolean smoothScroll)`方法将页面切换到位置3，同理当滑动到位置4时，通过该方法将页面切换到位置1，这样给我们的感觉就是无限循环。


![](https://user-gold-cdn.xitu.io/2019/6/18/16b6a1761fe3f79a?w=661&h=430&f=png&s=6729)

```java
private int currentPosition2;
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
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                if (currentPosition2 == 0) {
                    viewPager2.setCurrentItem(itemList2.size() - 2, false);//切换，不要动画效果
                } else if (currentPosition2 == itemList2.size() - 1) {
                    viewPager2.setCurrentItem(1, false);//切换，不要动画效果
                }
            }
        }
    });
}
```

### 2.2.2 加入轮播功能

```java
private Handler mHandler2 = new Handler();
@Override
protected void onResume() {
    super.onResume();
    //开始轮播
    mHandler2.postDelayed(mLoopRunnable2, mDelayedTime);
}

@Override
protected void onPause() {
    super.onPause();
    //停止轮播
    mHandler2.removeCallbacks(mLoopRunnable2);
}
```

```java
private final Runnable mLoopRunnable2 = new Runnable() {
    @Override
    public void run() {
        if (mIsAutoPlay) {
                //方案二:多添两条数据
                currentPosition2 = viewPager2.getCurrentItem();
                currentPosition2++;
                //不需要为了循环轮播来判断是否到达最后一页，在监听器中已经为我们做了此操作
                viewPager2.setCurrentItem(currentPosition2);
                mHandler2.postDelayed(this, mDelayedTime);

            } else {
                mHandler2.postDelayed(this, mDelayedTime);
            }
        }
};
```

与方案一不同的地方就是当滑动到最后一个时，切换到下标为1的页面，当滑动下标为0的页面时，切换到最后一个

## 2.3 比较

[依然范特稀西](https://www.jianshu.com/u/35167a70aa39) 在文章中这样说到：第二种方案在切换动画的时候，因为当滑到位置4时，我们通过setCurrentItem(int item,boolean smoothScroll)方法，来将其切换到位置1才有了无限循环的效果，但为了不被发现，第二个参数smoothScroll设置为false,这样就没有了切换动画，导致生硬，所以不用这个。

本来没想实现方案二(想着会一种方法就行)，但好奇心使我想看下到底有多生硬，但没有发现生硬的效果。因为我们在onPageScrollStateChanged()方法里监听了动画结束的状态，所以当滑动到第四张，再次开启一个周期的时候，我们其实是滑动到了第五张，就是我们往尾部添加的那张图片，此时是有动画的，并不是itemlist下标为0的位置，而且在此监听器中，当判断其实最后一张的时候，我们已经通过setCurrentItem（）不带动画效果的方式偷偷的把它切换到下标为1的位置了，所以在handler通过currentItem++方式再次滑动时，它滑动到的是下标为2的图片，也是带效果的，所以不存在什么生硬的效果。
以下我一共放了四张图，大家可以仔细看下效果：


![](https://user-gold-cdn.xitu.io/2019/6/19/16b6eada1f1466bf?w=600&h=1067&f=gif&s=4278882)



# 3. 总结

以上就是最基本的方法来实现ViewPager的无限轮播的全部内容。具体代码见[Github](https://github.com/myfittinglife/ViewPagerDemo)。

其实我们常见的Banner图还有Indicator指示器(就是底部的小点)，这个我用的其实还是[依然范特稀西](https://www.jianshu.com/p/7833d8450405)自定义的Indicator，因为确实很好用，而且封装的话也很简单，虽然一样的，但是我还是想下一篇再记录一下封装的过程，让自己加深下印象，下篇文章见。



# 4. 参考文章

[Android ViewPager 无限轮播Integer.MAX_VALUE 争议（看源码）](https://www.jianshu.com/p/c572eea78790)

[ViewPager系列之 仿魅族应用的广告BannerView](https://www.jianshu.com/p/653680cfe877)

[ Android 使用ViewPager实现无限轮播出现空白bug原因及解决方案（Integer.MAX_VALUE实现方式） ](https://blog.csdn.net/qq_27009579/article/details/80266875)





