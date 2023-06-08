package com.alibaba.android.arouter.facade.template;

import android.content.Context;

/**
 * Provider interface, base of other interface.
 *
 * 提供者接口，其他接口的基础。
 *
 * @author Alex <a href="mailto:zhilong.liu@aliyun.com">Contact me.</a>
 * @version 1.0
 * @since 16/8/23 23:08
 */
public interface IProvider {

    /**
     * Do your init work in this method, it well be call when processor has been load.
     *
     * 在这个方法中做你的 init 工作，当处理器加载时它会被调用。
     *
     * @param context ctx
     */
    void init(Context context);
}
