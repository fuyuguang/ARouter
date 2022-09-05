package com.alibaba.android.arouter.compiler.processor;

import com.alibaba.android.arouter.compiler.utils.Logger;
import com.alibaba.android.arouter.compiler.utils.TypeUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.alibaba.android.arouter.compiler.utils.Consts.*;
import static com.alibaba.android.arouter.compiler.utils.Consts.NO_MODULE_NAME_TIPS;

/**
 * Base Processor
 *
 * @author zhilong [Contact me.](mailto:zhilong.lzl@alibaba-inc.com)
 * @version 1.0
 * @since 2019-03-01 12:31
 * [接口 Elements](https://download.oracle.com/technetwork/java/javase/6/docs/zh/api/javax/lang/model/util/Elements.html)
 */
public abstract class BaseProcessor extends AbstractProcessor {
    Filer mFiler;
    Logger logger;
    Types types;
    /** 用来对程序元素进行操作的实用工具方法。  */
    Elements elementUtils;
    TypeUtils typeUtils;
    // Module name, maybe its 'app' or others
    String moduleName = null;
    // If need generate router doc
    boolean generateDoc;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        mFiler = processingEnv.getFiler();
        types = processingEnv.getTypeUtils();
        elementUtils = processingEnv.getElementUtils();
        typeUtils = new TypeUtils(types, elementUtils);
        logger = new Logger(processingEnv.getMessager());

        // Attempt to get user configuration [moduleName]
        Map<String, String> options = processingEnv.getOptions();
        if (MapUtils.isNotEmpty(options)) {
            moduleName = options.get(KEY_MODULE_NAME);
            generateDoc = VALUE_ENABLE.equals(options.get(KEY_GENERATE_DOC_NAME));
        }

        logger.info("The user has configuration the module name, it was [" + moduleName + "]  beform");

        if (StringUtils.isNotEmpty(moduleName)) {
            /**
             [module-java] 变
             [modulejava]
             */
            moduleName = moduleName.replaceAll("[^0-9a-zA-Z_]+", "");

            logger.info("The user has configuration the module name, it was [" + moduleName + "]");
        } else {
            logger.error(NO_MODULE_NAME_TIPS);
            throw new RuntimeException("ARouter::Compiler >>> No module name, for more information, look at gradle log.");
        }
    }

    /** 配置注解处理器支持的Java版本  */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return new HashSet<String>() {{
            this.add(KEY_MODULE_NAME);
            this.add(KEY_GENERATE_DOC_NAME);
        }};
    }


    public static String println(Map<?,?> map) {
        StringBuffer sb = new StringBuffer("\n");
        for (Object key : map.keySet()) {
            sb.append(""+ key + "                   " + map.get(key)+"\n");
        }
        return sb.append("\n").toString();
    }

    public static String println(Set<?> set) {
        StringBuffer sb = new StringBuffer("\n");
        for (Object obj : set) {
            sb.append(obj+"\n");
        }
        return sb.append("\n").toString();
    }

    public static String println(List<?> set) {
        StringBuffer sb = new StringBuffer("\n");
        for (Object obj : set) {
            sb.append(obj+"\n");
        }
        return sb.append("\n").toString();
    }

}
