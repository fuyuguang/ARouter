package com.alibaba.android.arouter.register.utils

import com.alibaba.android.arouter.register.core.RegisterTransform
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes

import java.util.jar.JarEntry
import java.util.jar.JarFile

/**
 *
 * 扫描包中的所有类：com/alibaba/android/arouter/
    找出所有路由器、拦截器和提供者

 Scan all class in the package: com/alibaba/android/arouter/
 * find out all routers,interceptors and providers
 * @author billy.qi email: qiyilike@163.com
 * @since 17/3/20 11:48
 */
class ScanUtil {

    /**
     * scan jar file
     * @param jarFile All jar files that are compiled into apk
     * @param destFile dest file after this transform
     *
     * 扫描jar文件
        @param jarFile 编译成apk的所有jar文件
        @param destFile 转换后的目标文件
     */
    static void scanJar(File jarFile, File destFile) {
        if (jarFile) {
            //包装成jar 文件类
            def file = new JarFile(jarFile)
            //得到实体类
            Enumeration enumeration = file.entries()
            //循环 取出实体类
            while (enumeration.hasMoreElements()) {
                //取出实体类
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()

                /**
                 //判断 是否在   com/alibaba/android/arouter/routes/   这个目录包下
                 这个目录中的内容要 经过 asm 动态生成代码

                 'com/alibaba/android/arouter/routes/'
                 com.alibaba.android.arouter.routes

                 {@link  com.alibaba.android.arouter.routes.ARouter$$Interceptors$$modulejava}
                 {@link com.alibaba.android.arouter.routes.ARouter$$Root$$modulejava}

                 */
                if (entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)) {
                    InputStream inputStream = file.getInputStream(jarEntry)
                    scanClass(inputStream)
                    inputStream.close()
                } else if (ScanSetting.GENERATE_TO_CLASS_FILE_NAME == entryName) {
                    /**
                     com/alibaba/android/arouter/core/LogisticsCenter.class
                     如果是 LogisticsCenter.class   */
                    // mark this jar file contains LogisticsCenter.class
                    // After the scan is complete, we will generate register code into this file
//                    标记此 jar 文件包含 LogisticsCenter.class
//                    扫描完成后，我们会生成注册码到这个文件中
                    RegisterTransform.fileContainsInitClass = destFile



                    Logger.i('fygg: getAbsolutePath : ' + RegisterTransform.fileContainsInitClass.getAbsolutePath())
//                    logger.info("The user has configuration the module name, it was [" + moduleName + "]");

                    println("fyg:: ** RegisterTransform.fileContainsInitClass.getAbsolutePath() : "+ RegisterTransform.fileContainsInitClass.getAbsolutePath())

                }
            }
            file.close()
        }
    }

    static boolean shouldProcessPreDexJar(String path) {
        /** 不包含 support 和 m2repository 包  */
        return !path.contains("com.android.support") && !path.contains("/android/m2repository")
    }

    static boolean shouldProcessClass(String entryName) {
        return entryName != null && entryName.startsWith(ScanSetting.ROUTER_CLASS_PACKAGE_NAME)
    }

    /**
     * scan class file
     * @param class file
     */
    static void scanClass(File file) {
        scanClass(new FileInputStream(file))
    }

    static void scanClass(InputStream inputStream) {
        ClassReader cr = new ClassReader(inputStream)
        ClassWriter cw = new ClassWriter(cr, 0)
        ScanClassVisitor cv = new ScanClassVisitor(Opcodes.ASM5, cw)
        cr.accept(cv, ClassReader.EXPAND_FRAMES)
        inputStream.close()
    }

    static class ScanClassVisitor extends ClassVisitor {

        ScanClassVisitor(int api, ClassVisitor cv) {
            super(api, cv)
        }

        /** 
            访问类  */
        void visit(int version, int access, String name, String signature,
                   String superName, String[] interfaces) {
            super.visit(version, access, name, signature, superName, interfaces)
            /**
             RegisterTransform.registerList:
             list.add(new ScanSetting('IRouteRoot'))
             list.add(new ScanSetting('IInterceptorGroup'))
             list.add(new ScanSetting('IProviderGroup'))
                */
            RegisterTransform.registerList.each { ext ->
                // IRouteRoot != null
                if (ext.interfaceName && interfaces != null) {
                    interfaces.each { itName ->
                        /** 遍历该类文件实现的接口 和  registerList中的接口匹配  */
                        if (itName == ext.interfaceName) {
                            //fix repeated inject init code when Multi-channel packaging
                            //修复多通道打包时重复注入初始化代码
                            if (!ext.classList.contains(name)) {
                                /** 如果命中，加入到集合中  */
                                ext.classList.add(name)
                            }
                        }
                    }
                }
            }
        }
    }

}