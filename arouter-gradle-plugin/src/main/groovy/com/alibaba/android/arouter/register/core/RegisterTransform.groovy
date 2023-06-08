package com.alibaba.android.arouter.register.core

import com.alibaba.android.arouter.register.utils.Logger
import com.alibaba.android.arouter.register.utils.ScanSetting
import com.alibaba.android.arouter.register.utils.ScanUtil
import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.util.function.Consumer

/**
 * transform api
 * <p>
 *     1. Scan all classes to find which classes implement the specified interface
 *     2. Generate register code into class file: {@link ScanSetting#GENERATE_TO_CLASS_FILE_NAME}
 *

 转换接口
 1.扫描所有类，查找哪些类实现了指定接口
 2.生成注册码到class文件中：
  {@link ScanSetting#GENERATE_TO_CLASS_FILE_NAME}
  {@link scansetting#generate_to_class_file_name}

 * @author billy.qi email: qiyilike@163.com
 * @since 17/3/21 11:48
 */
class RegisterTransform extends Transform {

    Project project
    static ArrayList<ScanSetting> registerList
    static File fileContainsInitClass;

    RegisterTransform(Project project) {
        this.project = project
    }

    /**
     * name of this transform
     * 此转换的名称
     * @return
     */
    @Override
    String getName() {
        return ScanSetting.PLUGIN_NAME
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * The plugin will scan all classes in the project
     * 该插件将扫描项目中的所有类
     * @return
     */
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    @Override
    boolean isIncremental() {
        return false
    }


    @Override
    void transform(Context context, Collection<TransformInput> inputs
                   , Collection<TransformInput> referencedInputs
                   , TransformOutputProvider outputProvider
                   , boolean isIncremental) throws IOException, TransformException, InterruptedException {

        Logger.i('Start scan register info in jar file.')
        println 'fyg:: *****************-------- transform  --------*********************'

        long startTime = System.currentTimeMillis()
        /** 左斜杠  */
        boolean leftSlash = File.separator == '/'

        if (!isIncremental){
            outputProvider.deleteAll()
        }

        inputs.each { TransformInput input ->

            // scan all jars
            input.jarInputs.each { JarInput jarInput ->
                String destName = jarInput.name

                println("fyg:: input.jarInputs  \n jarInput.name : "+ jarInput.name)
                // rename jar files
                def hexName = DigestUtils.md5Hex(jarInput.file.absolutePath)
                if (destName.endsWith(".jar")) {
                    /** 获取不包含文件类型后缀的 文件名  fyg.jar  -> fyg  */
                    destName = destName.substring(0, destName.length() - 4)
                }
                // input file   返回内容的位置。@return 内容位置。
                File src = jarInput.file
                // output file                              fyg_aaafb123134    fyg_md5Hex
                File dest = outputProvider.getContentLocation(destName + "_" + hexName, jarInput.contentTypes, jarInput.scopes, Format.JAR)

                /** scan jar file to find classes  扫描 jar 文件以查找类  */
                if (ScanUtil.shouldProcessPreDexJar(src.absolutePath)) {
                    ScanUtil.scanJar(src, dest)
                }

                println("fyg:: ** src : "+ src.getAbsolutePath()+"   dest : "+dest.getAbsolutePath()+"  destName : "+destName)
                FileUtils.copyFile(src, dest)

            }



            // scan class files
            input.directoryInputs.each { DirectoryInput directoryInput ->
                File dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                String root = directoryInput.file.absolutePath
                /** 目录名后添加 /  */
                if (!root.endsWith(File.separator))
                    root += File.separator

                println("fyg:: outputProvider.getContentLocation  \n directoryInput.name : "+ directoryInput.name+"\n       directoryInput.contentTypes : "+directoryInput.contentTypes + "\n    directoryInput.scopes : "+directoryInput.scopes + "\n Format.DIRECTORY :"+Format.DIRECTORY)
                directoryInput.file.eachFileRecurse { File file ->

                    def path = file.absolutePath.replace(root, '')

                    println("fyg:: ** directoryInput.file.eachFileRecurse :\n  path:"+ path+"\n       root : "+root + "\n    file : "+file.getAbsolutePath()+"\n  dest : "+dest.getAbsolutePath())

                    if (!leftSlash) {
                        //转换 window 的换行符
                        path = path.replaceAll("\\\\", "/")
                    }
                    /**
                      文件在这个包下  ：
                     com/alibaba/android/arouter/routes/  */
                    if(file.isFile() && ScanUtil.shouldProcessClass(path)){
                        println("fyg:: ScanUtil.shouldProcessClass(path)"+ path+"       root : "+root + "    file : "+file.getAbsolutePath())
                        ScanUtil.scanClass(file)
                    }
                }

                // copy to dest
                FileUtils.copyDirectory(directoryInput.file, dest)
            }
        }

        RegisterTransform.registerList.forEach(new Consumer<ScanSetting>(){
            void accept(ScanSetting item){
                println("")
                println("fyg::  RegisterTransform.registerList : "+item.interfaceName+"\n  "+item.classList)
                println("")
            }
        })

        Logger.i('Scan finish, current cost time ' + (System.currentTimeMillis() - startTime) + "ms")

        /** 找到插入代码的入口类  */
        if (fileContainsInitClass) {
            /** 遍历扫描到的接口类  */
            registerList.each { ext ->
                Logger.i('Insert register code to file ' + fileContainsInitClass.absolutePath)

                if (ext.classList.isEmpty()) {
                    Logger.e("No class implements found for interface:" + ext.interfaceName)
                } else {
                    ext.classList.each {
                        Logger.i(it)
                    }
                    RegisterCodeGenerator.insertInitCodeTo(ext)
                }
            }
        }

        Logger.i("Generate code finish, current cost time: " + (System.currentTimeMillis() - startTime) + "ms")
    }
}
