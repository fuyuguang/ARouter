package com.alibaba.android.arouter.facade.template;

import com.alibaba.android.arouter.facade.model.RouteMeta;

import java.util.Map;

/**
 * Group element.
 *
 * @author Alex <a href="mailto:zhilong.liu@aliyun.com">Contact me.</a>
 * @version 1.0
 * @since 16/8/23 16:37
 */
public interface IRouteGroup {
    /**
     * Fill the atlas with routes in group.
     * 根据组中路由填充地图集
     * @param atlas input
     *             key :
     *              @Route组解的path
     *
     *              例如：
     *              @Route(path = "/kotlin/java")
     *              public class TestNormalActivity
     *              key = "/kotlin/java"
     *
     *             value :RouteMeta 对象
     */
    void loadInto(Map<String, RouteMeta> atlas);
}
