package com.alibaba.android.arouter.facade.template;

import java.util.Map;

/**
 * Root element.
 *
 * @author Alex <a href="mailto:zhilong.liu@aliyun.com">Contact me.</a>
 * @version 1.0
 * @since 16/8/23 16:36
 * 每个 模块 都 有一个  IRouteRoot 接口的实现类
 * 路由根 接口 的作用：
 *  按组存放，该组下所有的路由表
 */
public interface IRouteRoot {

    /**
     * Load routes to input
     * @param routes input
     *               key : 组名 根据path或group来划分
     *               value:
     *               IRouteGroup 接口实现类的clazz
     *               该组下所有的路由信息
     *               loadInto(Map<String(path = "/module/2"), RouteMeta> atlas);
     *  例；
     *  routes.put("m2", ARouter$$Group$$m2.class);
     *  routes.put("module", ARouter$$Group$$module.class);
     *  routes.put("test", ARouter$$Group$$test.class);
     *  routes.put("yourservicegroupname", ARouter$$Group$$yourservicegroupname.class);
     */
    void loadInto(Map<String, Class<? extends IRouteGroup>> routes);
}
