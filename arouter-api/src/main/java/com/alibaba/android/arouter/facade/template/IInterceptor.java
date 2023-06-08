package com.alibaba.android.arouter.facade.template;

import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.InterceptorCallback;

/**
 * Used for inject custom logic when navigation.
 * 用于在导航时注入自定义逻辑。
 *
 * @author Alex <a href="mailto:zhilong.liu@aliyun.com">Contact me.</a>
 * @version 1.0
 * @since 16/8/23 13:56
 */
public interface IInterceptor extends IProvider {

    /**
     * The operation of this interceptor.
     * 这个拦截器的操作。
     *
     * @param postcard meta
     * @param callback cb
     */
    void process(Postcard postcard, InterceptorCallback callback);
}
