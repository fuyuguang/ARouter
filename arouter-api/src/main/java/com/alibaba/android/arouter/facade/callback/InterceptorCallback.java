package com.alibaba.android.arouter.facade.callback;

import com.alibaba.android.arouter.facade.Postcard;

/**
 * The callback of interceptor.
 * 拦截器的回调。
 *
 * @author Alex <a href="mailto:zhilong.liu@aliyun.com">Contact me.</a>
 * @version 1.0
 * @since 16/8/4 17:36
 */
public interface InterceptorCallback {

    /**
     * Continue process
     * 继续进程
     *
     * @param postcard route meta
     */
    void onContinue(Postcard postcard);

    /**
     * Interrupt process, pipeline will be destroy when this method called.
     * 中断进程，调用此方法时管道将被销毁。
     *
     * @param exception Reson of interrupt.
     */
    void onInterrupt(Throwable exception);
}
