package com.alibaba.android.arouter.core;

import com.alibaba.android.arouter.base.UniqueKeyTreeMap;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.android.arouter.facade.template.IInterceptor;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.alibaba.android.arouter.facade.template.IRouteGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Storage of route meta and other data.
 *
 * @author zhilong <a href="mailto:zhilong.lzl@alibaba-inc.com">Contact me.</a>
 * @version 1.0
 * @since 2017/2/23 下午1:39
 */
class Warehouse {
    // Cache route and metas
    /**
     * 每个分组所对应的，该分组下的 所有 具体 path 映射表，这些映射表 之后会 存到 {@link Warehouse#routes} 中
     * key=组名,value = IRouteGroup接口的实现类clazz
     * */
    static Map<String, Class<? extends IRouteGroup>> groupsIndex = new HashMap<>();
    /**
     采用懒加载的方式
     key = path,value = 路由元
     所有的path在该集合中都能找到对应的路由元
     **/
    static Map<String, RouteMeta> routes = new HashMap<>();

    // Cache provider
    /** key = route标注的类所对应的class , IProvider 接口的实现类 */
    static Map<Class, IProvider> providers = new HashMap<>();

    /**
     *
     *key :
     *实现IProvider 接口的 子类全类名
     *如果用@Route 标注的element的父类是IProvider接口，那个key为 element全限定名  例：SingleService
     *如果用@Route 标注的element的父类是IProvider接口的子类，那个key为 element 父类的 全限定名  HelloServiceImpl
     *
     *RouteMeta 对象
     */
    static Map<String, RouteMeta> providersIndex = new HashMap<>();

    // Cache interceptor
    /**
     * key=优先级
     * values= 标注了IIntercepter的 拦截器的class
     */
    static Map<Integer, Class<? extends IInterceptor>> interceptorsIndex = new UniqueKeyTreeMap<>("More than one interceptors use same priority [%s]");
    static List<IInterceptor> interceptors = new ArrayList<>();



    public static String println(Map<?,?> map) {
        StringBuffer sb = new StringBuffer();
        for (Object key : map.keySet()) {
//            System.out.println("key= "+ key + " and value= " + map.get(key));
            sb.append(""+ key + "                   " + map.get(key)+"\n");
        }
        return sb.append("\n").toString();

    }
    static void clear() {
        routes.clear();
        groupsIndex.clear();
        providers.clear();
        providersIndex.clear();
        interceptors.clear();
        interceptorsIndex.clear();
    }
}
