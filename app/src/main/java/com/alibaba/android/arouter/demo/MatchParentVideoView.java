package com.alibaba.android.arouter.demo;

import android.content.Context;
import android.graphics.Rect;
import android.media.MediaMetadataRetriever;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.VideoView;

/**
 * Created by fuyuguang on 2022/8/31 12:19 下午.
 * E-Mail ：2355245065@qq.com
 * Wechat :fyg13522647431
 * Tel : 13522647431
 * 修改时间：
 * 类描述：
 * 备注：
    []()
    [Android VideoView的布局适配](https://blog.csdn.net/zjuter/article/details/119141772?spm=1001.2014.3001.5502)
    
 */
public class MatchParentVideoView extends VideoView {
    private int videoWidth = 0;
    private int videoHeight = 0;
    private int videoRotation = 0;
    private int contentWidth = 0;
    private int contentHeight = 0;
 
    public int getVideoWidth() {
        return videoWidth;
    }
 
    public int getVideoHeight() {
        return videoHeight;
    }
 
 
    public MatchParentVideoView(Context context) {
        this(context, null);
    }
 
    public MatchParentVideoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }
 
    public MatchParentVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        int width = getDefaultSize(getWidth(), widthMeasureSpec);
//        int height = getDefaultSize(getHeight(), heightMeasureSpec);
//        setMeasuredDimension(width, height);
//    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(getWidth(), widthMeasureSpec);
        int height = getDefaultSize(getHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);
    }
 
    @Override
    public void setVideoPath(String path) {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(path);
            String widthString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
            String heightString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
            String rotationString = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION);
 
            videoWidth = Integer.parseInt(widthString);
            videoHeight = Integer.parseInt(heightString);
            int tempRotation = Integer.parseInt(rotationString);
            videoRotation = (tempRotation % 360 + 360) % 360;
            if (videoRotation == 90 || videoRotation == 270) {
                int temp = videoWidth;
                videoWidth = videoHeight;
                videoHeight = temp;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.setVideoPath(path);
    }
 
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (contentHeight > 0 && contentWidth > 0) { // always call onSizeChanged, make sure only do measure rect once.
            return;
        } else {
            contentHeight = h;
            contentWidth = w;
 
            int width = getVideoWidth();
            int height = getVideoHeight();
            if (width > 0 && height > 0) { //make sure validate video
                float measuredWidth = 0;
                float measuredHeight = 0;
 
                Rect measureRect = getMeasuredRect(width, height, contentWidth, contentHeight);
                measuredWidth = measureRect.width();
                measuredHeight = measureRect.height();
 
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) getLayoutParams();
                layoutParams.height = (int) measuredHeight;
                layoutParams.width = (int) measuredWidth;
                layoutParams.leftMargin = Math.max(0, (contentWidth - (int) measuredWidth) / 2);
                layoutParams.topMargin = Math.max(0, (contentHeight - (int) measuredHeight) / 2);
                setLayoutParams(layoutParams);
                requestLayout();
            }
        }
    }
 
    private Rect getMeasuredRect(int videoWidth, int videoHeight, int contentWidth, int contentHeight) {
        float measuredWidth = 0;
        float measuredHeight = 0;

        /**
         根据视频宽高比，裁剪VideoView的宽高。有四种情况需要处理。

         视频宽高都比显示区域小。直接取视频宽高。
         视频只有宽都比显示区域宽大。视频宽取显示区域的最大宽， 并以此为基准，算出视频高。
         视频只有高都比显示区域高大。视频高取显示区域的最大高， 并以此为基准，算出视频宽。
         视频宽高都比显示区域大。递归处理，以上两种情况。
            []()
            */
        if (videoWidth <= contentWidth && videoHeight <= contentHeight) {
            measuredWidth = videoWidth;
            measuredHeight = videoHeight;
        }
 
        if (videoWidth > contentWidth && videoHeight <= contentHeight) {
            measuredWidth = contentWidth;
            measuredHeight = measuredWidth * ((float) videoHeight / (float) videoWidth);
        }
 
        if (videoHeight > contentHeight && videoWidth <= contentWidth) {
            measuredHeight = contentHeight;
            measuredWidth = measuredHeight * ((float) videoWidth / (float) videoHeight);
        }
 
        if (videoHeight > contentHeight && videoWidth > contentWidth) {
            measuredWidth = contentWidth;
            measuredHeight = (float) contentWidth * (float) videoHeight / (float) videoWidth;
            return getMeasuredRect((int) measuredWidth, (int) measuredHeight, contentWidth, contentHeight);
        }
 
        Rect rect = new Rect();
        rect.right = (int) measuredWidth;
        rect.bottom = (int) measuredHeight;
 
        return rect;
    }
}