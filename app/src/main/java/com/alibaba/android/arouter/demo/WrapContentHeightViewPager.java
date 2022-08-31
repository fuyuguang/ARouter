package com.alibaba.android.arouter.demo;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
/**
 * Created by fuyuguang on 2022/8/31 11:57 上午.
 * E-Mail ：2355245065@qq.com
 * Wechat :fyg13522647431
 * Tel : 13522647431
 * 修改时间：
 * 类描述：
 * 备注：
    []()
    [Android ViewPage高度wrap_content不起作用的问题](https://blog.csdn.net/zjuter/article/details/89406091?spm=1001.2014.3001.5502)

 这儿再重点说一下MeasureSpec.UNSPECIFIED

 有一些父容器是那种不限制子view高度的控件。比如ScrollView, ViewPage等。这类控件的内容高度是无限的。
 所以高度用wrap_content是没有效果的。要使wrap_content有效果，必须重写onMeasure(),  然后在测量子View的时候，
 把MeasureSpec.UNSPECIFIED传给子View,  告诉子View你们的内容高度是多少就回报给我多少。然后父控件就拿到子View们的高度后，
 就可以设置父控件的高度了。 这样一来wrap_content就有用了。

 */
public class WrapContentHeightViewPager extends ViewPager {
 
    public WrapContentHeightViewPager(Context context) {
        super(context);
    }
    
    public WrapContentHeightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
 
       //以子page高度的最高值为ViewPage的高度
        int height = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight() + this.getPaddingTop() + this.getPaddingBottom();
            if (h > height) height = h;
        }
 
        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}