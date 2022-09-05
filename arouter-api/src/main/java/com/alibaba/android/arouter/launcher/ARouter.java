package com.alibaba.android.arouter.launcher;

import android.app.Application;
import android.content.Context;
import android.net.Uri;

import com.alibaba.android.arouter.exception.InitException;
import com.alibaba.android.arouter.facade.Postcard;
import com.alibaba.android.arouter.facade.callback.NavigationCallback;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.android.arouter.facade.template.ILogger;
import com.alibaba.android.arouter.facade.template.IProvider;
import com.alibaba.android.arouter.facade.template.IRouteGroup;
import com.alibaba.android.arouter.utils.Consts;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * ARouter facade
 *
 * @author Alex <a href="mailto:zhilong.liu@aliyun.com">Contact me.</a>
 * @version 1.0
 * @since 16/8/16 14:36
 */
public final class ARouter {
    // Key of raw uri
    public static final String RAW_URI = "NTeRQWvye18AkPd6G";
    public static final String AUTO_INJECT = "wmHzgD4lOj5o4241";

    private volatile static ARouter instance = null;
    private volatile static boolean hasInit = false;
    public static ILogger logger;

    private ARouter() {
    }

    /**
     * Init, it must be call before used router.
     */
    public static void init(Application application) {
        if (!hasInit) {
            logger = _ARouter.logger;
            _ARouter.logger.info(Consts.TAG, "ARouter init start.");
            hasInit = _ARouter.init(application);

            if (hasInit) {
                /** 初始化拦截器  */
                _ARouter.afterInit();
            }

            _ARouter.logger.info(Consts.TAG, "ARouter init over.");
        }
    }

    /**
     * Get instance of router. A
     * All feature U use, will be starts here.
     */
    public static ARouter getInstance() {
        if (!hasInit) {
            throw new InitException("ARouter::Init::Invoke init(context) first!");
        } else {
            if (instance == null) {
                synchronized (ARouter.class) {
                    if (instance == null) {
                        instance = new ARouter();
                    }
                }
            }
            return instance;
        }
    }

    public static synchronized void openDebug() {
        _ARouter.openDebug();
    }

    public static boolean debuggable() {
        return _ARouter.debuggable();
    }

    public static synchronized void openLog() {
        _ARouter.openLog();
    }

    public static synchronized void printStackTrace() {
        _ARouter.printStackTrace();
    }

    public static synchronized void setExecutor(ThreadPoolExecutor tpe) {
        _ARouter.setExecutor(tpe);
    }

    public synchronized void destroy() {
        _ARouter.destroy();
        hasInit = false;
    }

    /**
     * The interface is not stable enough, use 'ARouter.inject();';
     */
    @Deprecated
    public static synchronized void enableAutoInject() {
        _ARouter.enableAutoInject();
    }

    @Deprecated
    public static boolean canAutoInject() {
        return _ARouter.canAutoInject();
    }

    /**
     * The interface is not stable enough, use 'ARouter.inject();';
     */
    @Deprecated
    public static void attachBaseContext() {
        _ARouter.attachBaseContext();
    }

    public static synchronized void monitorMode() {
        _ARouter.monitorMode();
    }

    public static boolean isMonitorMode() {
        return _ARouter.isMonitorMode();
    }

    public static void setLogger(ILogger userLogger) {
        _ARouter.setLogger(userLogger);
    }

    /**
     * Inject params and services.
     */
    public void inject(Object thiz) {
        _ARouter.inject(thiz);
    }

    /**
     * Build the roadmap, draw a postcard.
     *
     * @param path Where you go.
     */
    public Postcard build(String path) {
        return _ARouter.getInstance().build(path);
    }

    /**
     * Build the roadmap, draw a postcard.
     *
     * @param path  Where you go.
     * @param group The group of path.
     */
    @Deprecated
    public Postcard build(String path, String group) {
        return _ARouter.getInstance().build(path, group, false);
    }

    /**
     * Build the roadmap, draw a postcard.
     *
     * @param url the path
     */
    public Postcard build(Uri url) {
        return _ARouter.getInstance().build(url);
    }

    /**
     * Launch the navigation by type
     *  按类型启动导航，
     *  从 Warehouse.providersIndex 中查找 provider， 初始化时加载所有的provider
     *  该方式只能查找 provider ，不能查找 Activity
     * @param service interface of service
     * @param <T>     return type
     * @return instance of service
     */
    public <T> T navigation(Class<? extends T> service) {
        return _ARouter.getInstance().navigation(service);
    }


    /**
     * 该方法是自己添加的方法，不是 aroute框架中的方法，该方法是一个 泛型方法，指定T的类型为 IProvider的子类，
     * 此处限定 T 的类型 只能是 IProvider的子类
     * @param service
     * @param <T>
     * @return
     */
    public <T extends IProvider> T navigationCustomService(Class<T> service) {
        return _ARouter.getInstance().navigationCustomService(service);
    }

    /**
     * Launch the navigation.
     *
     * @param mContext    .
     * @param postcard    .
     * @param requestCode Set for startActivityForResult
     * @param callback    cb
     */
    public Object navigation(Context mContext, Postcard postcard, int requestCode, NavigationCallback callback) {
        return _ARouter.getInstance().navigation(mContext, postcard, requestCode, callback);
    }

    /**
     * Add route group dynamic.
     * @param group route group.
     * @return add result.
     */
    public boolean addRouteGroup(IRouteGroup group) {
        return _ARouter.getInstance().addRouteGroup(group);
    }
}
