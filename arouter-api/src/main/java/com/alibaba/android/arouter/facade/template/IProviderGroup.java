package com.alibaba.android.arouter.facade.template;

import com.alibaba.android.arouter.facade.model.RouteMeta;

import java.util.Map;

/**
 * Template of provider group.
 *
 * @author Alex <a href="mailto:zhilong.liu@aliyun.com">Contact me.</a>
 * @version 1.0
 * @since 16/08/30 12:42
 */
public interface IProviderGroup {
    /**
     * Load providers map to input
     *
     * @param providers input
     *                  key :
     *                   实现IProvider 接口的 子类全类名
     *                   如果用@Route 标注的element的父类是IProvider接口，那个key为 element全限定名  例：SingleService
     *                   如果用@Route 标注的element的父类是IProvider接口的子类，那个key为 element 父类的 全限定名  HelloServiceImpl
     *
     *                  RouteMeta 对象
     */
    void loadInto(Map<String, RouteMeta> providers);
}