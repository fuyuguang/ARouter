package com.alibaba.android.arouter.register.utils

import org.gradle.api.Project
import org.slf4j.impl.StaticLoggerBinder


/**
 * Format log
 *
 * @author zhilong <a href="mailto:zhilong.lzl@alibaba-inc.com">Contact me.</a>
 * @version 1.0
 * @since 2017/12/18 下午2:43
 */
class Logger {
    static org.gradle.api.logging.Logger logger

    static void make(Project project) {
//        logger = project.getLogger()
//        logger = new SubstituteLogger()
        logger = StaticLoggerBinder.getSingleton().getLoggerFactory().getLogger(Project.class.getName())
        println("fyg:: logger >>> "+logger.getName()+"")
    }

    static void i(String info) {
        if (null != info && null != logger) {
            logger.info("ARouter::Register >>> " + info)
            println("fyg::info ARouter::Register >>> "+info)
        }
    }

    static void e(String error) {
        if (null != error && null != logger) {
            logger.error("ARouter::Register >>> " + error)
            println("fyg::error  ARouter::Register >>> "+error)
        }
    }

    static void w(String warning) {
        if (null != warning && null != logger) {
            logger.warn("ARouter::Register >>> " + warning)
            println("fyg::warning  ARouter::Register >>> "+warning)
        }
    }
}
