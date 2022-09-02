package com.alibaba.android.arouter.compiler.processor;

import com.alibaba.android.arouter.compiler.entity.RouteDoc;
import com.alibaba.android.arouter.compiler.utils.Consts;
import com.alibaba.android.arouter.facade.annotation.Autowired;
import com.alibaba.android.arouter.facade.annotation.Route;
import com.alibaba.android.arouter.facade.enums.RouteType;
import com.alibaba.android.arouter.facade.enums.TypeKind;
import com.alibaba.android.arouter.facade.model.RouteMeta;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import com.squareup.javapoet.WildcardTypeName;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;
import javax.tools.StandardLocation;

import static com.alibaba.android.arouter.compiler.utils.Consts.ACTIVITY;
import static com.alibaba.android.arouter.compiler.utils.Consts.ANNOTATION_TYPE_AUTOWIRED;
import static com.alibaba.android.arouter.compiler.utils.Consts.ANNOTATION_TYPE_ROUTE;
import static com.alibaba.android.arouter.compiler.utils.Consts.FRAGMENT;
import static com.alibaba.android.arouter.compiler.utils.Consts.IPROVIDER_GROUP;
import static com.alibaba.android.arouter.compiler.utils.Consts.IROUTE_GROUP;
import static com.alibaba.android.arouter.compiler.utils.Consts.ITROUTE_ROOT;
import static com.alibaba.android.arouter.compiler.utils.Consts.METHOD_LOAD_INTO;
import static com.alibaba.android.arouter.compiler.utils.Consts.NAME_OF_GROUP;
import static com.alibaba.android.arouter.compiler.utils.Consts.NAME_OF_PROVIDER;
import static com.alibaba.android.arouter.compiler.utils.Consts.NAME_OF_ROOT;
import static com.alibaba.android.arouter.compiler.utils.Consts.PACKAGE_OF_GENERATE_DOCS;
import static com.alibaba.android.arouter.compiler.utils.Consts.PACKAGE_OF_GENERATE_FILE;
import static com.alibaba.android.arouter.compiler.utils.Consts.SEPARATOR;
import static com.alibaba.android.arouter.compiler.utils.Consts.SERVICE;
import static com.alibaba.android.arouter.compiler.utils.Consts.WARNING_TIPS;
import static javax.lang.model.element.Modifier.PUBLIC;

/**
 * A processor used for find route.
 *
 * @author Alex <a href="mailto:zhilong.liu@aliyun.com">Contact me.</a>
 * @version 1.0
 * @since 16/8/15 下午10:08
 * []()
 * []()
 * []()
 * []()
 * []()
 */
@AutoService(Processor.class)
/** 配置注解处理器支持处理的注解类型为 Route,  Autowired*/
@SupportedAnnotationTypes({ANNOTATION_TYPE_ROUTE, ANNOTATION_TYPE_AUTOWIRED})
/** 定义注解处理器继承自AbstractProcessor  */
public class RouteProcessor extends BaseProcessor {
    private Map<String, Set<RouteMeta>> groupMap = new HashMap<>(); // ModuleName and routeMeta.
    private Map<String, String> rootMap = new TreeMap<>();  // Map of root metas, used for generate class file in order.

    private TypeMirror iProvider = null;
    private Writer docWriter;       // Writer used for write doc

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        if (generateDoc) {
            try {
                docWriter = mFiler.createResource(
                        StandardLocation.SOURCE_OUTPUT,
                        PACKAGE_OF_GENERATE_DOCS,
                        "arouter-map-of-" + moduleName + ".json"
                ).openWriter();
            } catch (IOException e) {
                logger.error("Create doc writer failed, because " + e.getMessage());
            }
        }

        /** {@link com.alibaba.android.arouter.facade.template.IProvider}  */
        /**
         Element在javax.lang.model.element包下，一个Element表示一个程序元素，比如包、类、方法，都是一个元素，Element子类常用的有:
         ExecutableElement、 方法
         PackageElement、 包
         TypeElement 类
         VariableElement 变量
            []()
            */
        iProvider = elementUtils.getTypeElement(Consts.IPROVIDER).asType();

        logger.info(">>> RouteProcessor init. <<<");
    }

    /**
     * 实现process方法，完成注解的解析和处理，通常生成文件或者校验处理
     * @param annotations 定义的注解类型的集合[Aroute,Autowired,等]
     * @param roundEnv 回合环境，注解的处理可能要经过几个回合的处理，每个回合处理一批注解
     * @return 返回true表示注解被当前注解处理器处理，就不会再交给其他注解处理器
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        //[BUTTERKNIFE源码探究（附实现自定义注解处理器）](https://www.freesion.com/article/2866847718/)
        //遍历所有的TypeElement的，一个注解类型对应一个TypeElement
        for (TypeElement typeElement : annotations) {
            //遍历在代码中使用typeElement对应注解类型来注解的元素
            //例如：如果typeElement对应的是InjectString注解类型，那么Element对应为使用@InjectString注解的成员变量
            for (Element element : roundEnv.getElementsAnnotatedWith(typeElement)) {
                //添加注解元素到将要生成的java文件对应的GenerateJavaFile的对象中
                //addElementToGenerateJavaFile(element);
            }
        }



        if (CollectionUtils.isNotEmpty(annotations)) {
            Set<? extends Element> routeElements = roundEnv.getElementsAnnotatedWith(Route.class);
            try {
                logger.info(">>> Found routes, start... <<<");
                this.parseRoutes(routeElements);

            } catch (Exception e) {
                logger.error(e);
            }
            return true;
        }

        return false;
    }


    /** 开始处理 所有的 Route 注解  */
    private void parseRoutes(Set<? extends Element> routeElements) throws IOException {
        if (CollectionUtils.isNotEmpty(routeElements)) {
            // prepare the type an so on.
            
            logger.info(">>> Found routes, size is " + routeElements.size() + " <<<");
            
            rootMap.clear();
            
            TypeMirror type_Activity = elementUtils.getTypeElement(ACTIVITY).asType();
            TypeMirror type_Service = elementUtils.getTypeElement(SERVICE).asType();
            TypeMirror fragmentTm = elementUtils.getTypeElement(FRAGMENT).asType();
            TypeMirror fragmentTmV4 = elementUtils.getTypeElement(Consts.FRAGMENT_V4).asType();
            
            /**
             * {@link com.alibaba.android.arouter.facade.template.IRouteGroup}
             *  void loadInto(Map<String, RouteMeta> atlas);
             * */
            // Interface of ARouter
            TypeElement type_IRouteGroup = elementUtils.getTypeElement(IROUTE_GROUP);

            /** 
             * {@link com.alibaba.android.arouter.facade.template.IProviderGroup}
             * void loadInto(Map<String, RouteMeta> providers);
             * */
            TypeElement type_IProviderGroup = elementUtils.getTypeElement(IPROVIDER_GROUP);
            ClassName routeMetaCn = ClassName.get(RouteMeta.class);
            ClassName routeTypeCn = ClassName.get(RouteType.class);

            /*
               Build input type, format as :

               ```Map<String, Class<? extends IRouteGroup>>```
             */
            /**
             {@link com.alibaba.android.arouter.facade.template.IRouteRoot}
             void loadInto(Map<String, Class<? extends IRouteGroup>> routes);
             */

            ParameterizedTypeName inputMapTypeOfRoot = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ParameterizedTypeName.get(
                            ClassName.get(Class.class),
                            WildcardTypeName.subtypeOf(ClassName.get(type_IRouteGroup))
                    )
            );

            /*

              ```Map<String, RouteMeta>```
             */
            /**
             {@link com.alibaba.android.arouter.facade.template.IProviderGroup}
             void loadInto(Map<String, RouteMeta> providers);

             {@link com.alibaba.android.arouter.facade.template.IRouteGroup}
             void loadInto(Map<String, RouteMeta> atlas);

             */
            ParameterizedTypeName inputMapTypeOfGroup = ParameterizedTypeName.get(
                    ClassName.get(Map.class),
                    ClassName.get(String.class),
                    ClassName.get(RouteMeta.class)
            );

            /*
              Build input param name.
             */
            ParameterSpec rootParamSpec = ParameterSpec.builder(inputMapTypeOfRoot, "routes").build();  //Map<String, Class<? extends IRouteGroup>> routes
            ParameterSpec groupParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "atlas").build(); //Map<String, RouteMeta> atlas
            ParameterSpec providerParamSpec = ParameterSpec.builder(inputMapTypeOfGroup, "providers").build();// Map<String, RouteMeta> providers   Ps. its param type same as groupParamSpec!

            /*
              Build method : 'loadInto'
              public void loadInto()
             */
            MethodSpec.Builder loadIntoMethodOfRootBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(rootParamSpec);

            //  Follow a sequence, find out metas of group first, generate java file, then statistics them as root.
            logger.info(">>> routeElements  is : " + println(routeElements) + " <<<");
            for (Element element : routeElements) {

                TypeMirror tm = element.asType();
                Route route = element.getAnnotation(Route.class);
                RouteMeta routeMeta;

                // Activity or Fragment
                /** 测试是否第一个TypeMirror是第二个TypeMirror的子类型.任何类型都被认为是它自身的子类型
                 属于 activity 或 fragment  的类型才 遍历*/
                if (types.isSubtype(tm, type_Activity) || types.isSubtype(tm, fragmentTm) || types.isSubtype(tm, fragmentTmV4)) {
                    // Get all fields annotation by @Autowired
                    /** 注解名 ： 真实的类型  */
                    Map<String, Integer> paramsType = new HashMap<>();
                    /** 注解名 ： 真实的注解类型  */
                    Map<String, Autowired> injectConfig = new HashMap<>();
                    injectParamCollector(element, paramsType, injectConfig);

                    if (types.isSubtype(tm, type_Activity)) {
                        // Activity
                        logger.info(">>> Found activity route: " + tm.toString() + " <<<");
                        routeMeta = new RouteMeta(route, element, RouteType.ACTIVITY, paramsType);
                    } else {
                        // Fragment
                        logger.info(">>> Found fragment route: " + tm.toString() + " <<<");
                        routeMeta = new RouteMeta(route, element, RouteType.parse(FRAGMENT), paramsType);
                    }
                    /** 该元素上所有的注解，存到routeMeta 中   */
                    routeMeta.setInjectConfig(injectConfig);
                } else if (types.isSubtype(tm, iProvider)) {         // IProvider
                    logger.info(">>> Found provider route: " + tm.toString() + " <<<");
                    routeMeta = new RouteMeta(route, element, RouteType.PROVIDER, null);
                } else if (types.isSubtype(tm, type_Service)) {           // Service
                    logger.info(">>> Found service route: " + tm.toString() + " <<<");
                    routeMeta = new RouteMeta(route, element, RouteType.parse(SERVICE), null);
                } else {
                    //@Route 标记在不受支持的类上，请查看
                    throw new RuntimeException("The @Route is marked on unsupported class, look at [" + tm.toString() + "].");
                }

                /** 根据注解 收集所有的 routeMete，存到集合中  */
                categories(routeMeta);
            }

            /**
             @Override
             public void loadInto(Map<String, RouteMeta> providers);
             */
            MethodSpec.Builder loadIntoMethodOfProviderBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                    .addAnnotation(Override.class)
                    .addModifiers(PUBLIC)
                    .addParameter(providerParamSpec);

            Map<String, List<RouteDoc>> docSource = new HashMap<>();

            //开始生成java源码，结构分为上下两层，用于需求初始化。
            // Start generate java source, structure is divided into upper and lower levels, used for demand initialization.
            for (Map.Entry<String, Set<RouteMeta>> entry : groupMap.entrySet()) {
                String groupName = entry.getKey();

                /**
                 @Override
                 public void loadInto(Map<String, RouteMeta> atlas);

                 */
                MethodSpec.Builder loadIntoMethodOfGroupBuilder = MethodSpec.methodBuilder(METHOD_LOAD_INTO)
                        .addAnnotation(Override.class)
                        .addModifiers(PUBLIC)
                        .addParameter(groupParamSpec);

                List<RouteDoc> routeDocList = new ArrayList<>();

                // Build group method body  构建组方法体
                Set<RouteMeta> groupData = entry.getValue();
                /**
                    遍历 groupData，如何 element的父类是 iProvider 类型，
                  */
                for (RouteMeta routeMeta : groupData) {
                    RouteDoc routeDoc = extractDocInfo(routeMeta);

                    //构建类
                    ClassName className = ClassName.get((TypeElement) routeMeta.getRawType());

                    switch (routeMeta.getType()) {
                        /**
                         遍历 groupData，
                         如果 element的父类是 iProvider 类型，Map 的key  存 routeMeta.getRawType()
                         否则，element的父类是 iProvider 子类型 ，Map 的key  存 routeMeta.getRawType()).getInterfaces()

                         */
                        case PROVIDER:  // Need cache provider's super class    需要缓存提供者的超类
                            /** 获取 父 接口   遍历父接口 */
                            List<? extends TypeMirror> interfaces = ((TypeElement) routeMeta.getRawType()).getInterfaces();

                            logger.info(">>> print  interfaces :    " + println(interfaces));

                            for (TypeMirror tm : interfaces) {
                                routeDoc.addPrototype(tm.toString());

                                 /**
                                 * 测试是否2个对象代表的是同一个类型.
                                 * 警告：如果此方法的任何一个参数表示通配符，则此方法将返回false。因此，通配符不是与自
                                 * 身相同的类型。这可能首先令人惊讶，但一旦你认为这样的例子必须被编译器拒绝，才有意义：
                                 *    List<?> list = new ArrayList<Object>();
                                 *  list.add(list.get(0));
                                 *  由于注释仅是与类型相关联的元数据，所以在计算两个TypeMirror对象是否为相同类型时，
                                 *  不考虑两个参数上的注释集。特别地，两个类型的反射对象可以具有不同的注释，并且仍然被
                                 *  认为是相同的
                                 */
                                // 父接口 是  iProvider 类型
                                if (types.isSameType(tm, iProvider)) {   // Its implements iProvider interface himself. 它自己实现 iProvider 接口。
                                    // This interface extend the IProvider, so it can be used for mark provider  该接口扩展了 IProvider，因此可以用于标记提供者

                                    logger.info(">>> print  first param :    routeMeta.getRawType() :" + routeMeta.getRawType() + " <<<   isSameType   tm "+tm);

                                    loadIntoMethodOfProviderBuilder.addStatement(
                                            /**
                                            providers.put(
                                             "com.alibaba.android.arouter.demo.service.HelloService",
                                             RouteMeta.build(
                                             RouteType.PROVIDER,
                                             HelloServiceImpl.class,
                                             "/yourservicegroupname/hello",
                                             "yourservicegroupname",
                                             null,
                                             -1,
                                             -2147483648
                                             ));
                                              */
                                            "providers.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, null, " + routeMeta.getPriority() + ", " + routeMeta.getExtra() + "))",
                                            (routeMeta.getRawType()).toString(), //父接口 是  iProvider 类型,那么 就存 element 自己，真实的类型   SingleService
                                            routeMetaCn,    // RouteMeta
                                            routeTypeCn,    // RouteType
                                            className,      // HelloServiceImpl.class
                                            routeMeta.getPath(),    // "/yourservicegroupname/hello",
                                            routeMeta.getGroup());  // "yourservicegroupname",

                                } else if (types.isSubtype(tm, iProvider)) {  // 父接口 是  iProvider 的  子类型
                                    /** 测试是否第一个TypeMirror是第二个TypeMirror的子类型.任何类型都被认为是它自身的子类型  */
                                    // This interface extend the IProvider, so it can be used for mark provider
                                    //该接口扩展了 IProvider，因此可以用于标记提供者

                                    logger.info(">>> print  first param   routeMeta.getRawType() : " + routeMeta.getRawType()  + " <<<   isSubtype    tm "+tm);

                                    /** 父接口 不是  iProvider 类型,那么 就存 父类型   HelloService 和  SerializationService  */
                                    loadIntoMethodOfProviderBuilder.addStatement(
                                            "providers.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, null, " + routeMeta.getPriority() + ", " + routeMeta.getExtra() + "))",
                                            tm.toString(),    // So stupid, will duplicate only save class name.     太愚蠢了，只会重复保存类名。 
                                            routeMetaCn,
                                            routeTypeCn,
                                            className,
                                            routeMeta.getPath(),
                                            routeMeta.getGroup());
                                }
                            }
                            break;
                        default:
                            break;
                    }

                    // Make map body for paramsType
                    StringBuilder mapBodyBuilder = new StringBuilder();
                    /** 注解名 ： 真实的类型  */
                    Map<String, Integer> paramsType = routeMeta.getParamsType();
                    /** 注解名 ： 真实的注解类型  */
                    Map<String, Autowired> injectConfigs = routeMeta.getInjectConfig();
                    if (MapUtils.isNotEmpty(paramsType)) {
                        List<RouteDoc.Param> paramList = new ArrayList<>();

                        for (Map.Entry<String, Integer> types : paramsType.entrySet()) {
                            mapBodyBuilder.append("put(\"").append(types.getKey()).append("\", ").append(types.getValue()).append("); ");

                            RouteDoc.Param param = new RouteDoc.Param();
                            Autowired injectConfig = injectConfigs.get(types.getKey());
                            param.setKey(types.getKey()); //参数名称
                            param.setType(TypeKind.values()[types.getValue()].name().toLowerCase()); //参数类型
                            param.setDescription(injectConfig.desc());
                            param.setRequired(injectConfig.required());

                            paramList.add(param);
                        }

                        routeDoc.setParams(paramList);
                    }
                    String mapBody = mapBodyBuilder.toString();

                    loadIntoMethodOfGroupBuilder.addStatement(
                            "atlas.put($S, $T.build($T." + routeMeta.getType() + ", $T.class, $S, $S, " + (StringUtils.isEmpty(mapBody) ? null : ("new java.util.HashMap<String, Integer>(){{" + mapBodyBuilder.toString() + "}}")) + ", " + routeMeta.getPriority() + ", " + routeMeta.getExtra() + "))",
                            routeMeta.getPath(),
                            routeMetaCn,
                            routeTypeCn,
                            className,
                            routeMeta.getPath().toLowerCase(),
                            routeMeta.getGroup().toLowerCase());

                    routeDoc.setClassName(className.toString());
                    routeDocList.add(routeDoc);
                }

                // Generate groups
                //ARouter + $$ + "Group" + $$ + groupName;
                //ARouter$$Group$$groupName;
                String groupFileName = NAME_OF_GROUP + groupName;
                JavaFile.builder(PACKAGE_OF_GENERATE_FILE, //com.alibaba.android.arouter.routes
                        TypeSpec.classBuilder(groupFileName) //构建类文件
                                .addJavadoc(WARNING_TIPS)
                                .addSuperinterface(ClassName.get(type_IRouteGroup)) //IRouteGroup
                                .addModifiers(PUBLIC)
                                .addMethod(loadIntoMethodOfGroupBuilder.build()) //添加方法
                                .build()
                ).build().writeTo(mFiler);

                logger.info(">>> Generated group: " + groupName + "<<<");
                /** 组名，文件名  */
                rootMap.put(groupName, groupFileName);
                docSource.put(groupName, routeDocList);
            }

            if (MapUtils.isNotEmpty(rootMap)) {
                // Generate root meta by group name, it must be generated before root, then I can find out the class of group.
                //通过组名生成root meta，必须在root之前生成，然后我可以找出组的类。
                for (Map.Entry<String, String> entry : rootMap.entrySet()) {
                    //void loadInto(Map<String, Class<? extends IRouteGroup>> routes);
                    loadIntoMethodOfRootBuilder.addStatement("routes.put($S, $T.class)", entry.getKey(), ClassName.get(PACKAGE_OF_GENERATE_FILE, entry.getValue()));
                }
            }

            // Output route doc
            if (generateDoc) {
                docWriter.append(JSON.toJSONString(docSource, SerializerFeature.PrettyFormat));
                docWriter.flush();
                docWriter.close();
            }

            // Write provider into disk
            //ARouter$$Providers$$moduleName
            String providerMapFileName = NAME_OF_PROVIDER + SEPARATOR + moduleName;
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(providerMapFileName)
                            .addJavadoc(WARNING_TIPS)
                            .addSuperinterface(ClassName.get(type_IProviderGroup))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfProviderBuilder.build())
                            .build()
            ).build().writeTo(mFiler);

            logger.info(">>> Generated provider map, name is " + providerMapFileName + " <<<");

            // Write root meta into disk.
            //ARouter$$Root$$moduleName
            String rootFileName = NAME_OF_ROOT + SEPARATOR + moduleName;
            JavaFile.builder(PACKAGE_OF_GENERATE_FILE,
                    TypeSpec.classBuilder(rootFileName)
                            .addJavadoc(WARNING_TIPS)
                            /**  {@link com.alibaba.android.arouter.facade.template.IRouteRoot}  */
                            .addSuperinterface(ClassName.get(elementUtils.getTypeElement(ITROUTE_ROOT)))
                            .addModifiers(PUBLIC)
                            .addMethod(loadIntoMethodOfRootBuilder.build())
                            .build()
            ).build().writeTo(mFiler);

            logger.info(">>> Generated root, name is " + rootFileName + " <<<");
        }
    }

    /**
     * Recursive inject config collector.
     *  递归注入配置收集器。
     * @param element current element.
     */
    private void injectParamCollector(Element element, Map<String, Integer> paramsType, Map<String, Autowired> injectConfig) {
        /** 获取一个元素的内部包含的元素集合  得到VariableElement和ExecutableElement构成的Element集合。
         getEnclosingElement 获取一个元素的外部元素，比如上述例子中的value和add方法分别对应VariableElement和ExecutableElement，如果调用他们的getEnclosingElement方法，得到的是Test的元素
          */
        for (Element field : element.getEnclosedElements()) {
            /** 是 this == FIELD || this == ENUM_CONSTANT  && 有 Autowired 注解   &&  不是  iProvider 的子类
                它必须是字段，然后它有注释，但它不是提供者。
             */
            if (field.getKind().isField() && field.getAnnotation(Autowired.class) != null && !types.isSubtype(field.asType(), iProvider)) {
                // It must be field, then it has annotation, but it not be provider.
                Autowired paramConfig = field.getAnnotation(Autowired.class);
                /** 注解name的值为空 取  类字段的getSimpleName   */
                String injectName = StringUtils.isEmpty(paramConfig.name()) ? field.getSimpleName().toString() : paramConfig.name();
                /** Autowired 标注的注解名或字段名  ： 真实的类型  */
                paramsType.put(injectName, typeUtils.typeExchange(field));
                /** Autowired 标注的注解名或字段名 ： 真实的注解类型  */
                injectConfig.put(injectName, paramConfig);
            }
        }

        // if has parent?
        /** 获取 element 的父类 ，比如 BaseActivity 或 BaseFragment  */
        TypeMirror parent = ((TypeElement) element).getSuperclass();
        /**
         DeclaredType表示声明的类型， class类型或interface类型。 这包括参数化类型，例如java.util.Set<String>以及原始类型。
         虽然TypeElement表示class或interface元素，但是DeclaredType表示class或interface类型，后者是前者的使用（或调用）。
         */
        if (parent instanceof DeclaredType) {
            Element parentElement = ((DeclaredType) parent).asElement();
            /** 全类名 不是 以 android 开头，  */
            if (parentElement instanceof TypeElement && !((TypeElement) parentElement).getQualifiedName().toString().startsWith("android")) {
                /** 遍历 自定义的 BaseActivity 或 BaseFragment 父类型的字段
                     android.app.Activity
                     android.app.Fragment
                     android.app.Service */
                injectParamCollector(parentElement, paramsType, injectConfig);
            }
        }
    }

    /**
     * Extra doc info from route meta
     *  来自路由元的额外文档信息
     * @param routeMeta meta
     * @return doc
     */
    private RouteDoc extractDocInfo(RouteMeta routeMeta) {
        RouteDoc routeDoc = new RouteDoc();
        routeDoc.setGroup(routeMeta.getGroup());
        routeDoc.setPath(routeMeta.getPath());
        routeDoc.setDescription(routeMeta.getName());
        routeDoc.setType(routeMeta.getType().name().toLowerCase());
        routeDoc.setMark(routeMeta.getExtra());

        return routeDoc;
    }

    /**
     * Sort metas in group.
     * 对组中的元进行排序。
     * @param routeMete metas.
     */
    private void categories(RouteMeta routeMete) {
        if (routeVerify(routeMete)) {
            logger.info(">>> Start categories, group = " + routeMete.getGroup() + ", path = " + routeMete.getPath() + " <<<");
            Set<RouteMeta> routeMetas = groupMap.get(routeMete.getGroup());
            if (CollectionUtils.isEmpty(routeMetas)) {
                /** 它不允许出现重复元素。TreeSet是用compareTo()来判断重复元素的,而非 equals(),  */
                Set<RouteMeta> routeMetaSet = new TreeSet<>(new Comparator<RouteMeta>() {
                    @Override
                    public int compare(RouteMeta r1, RouteMeta r2) {
                        try {
                            /** 根据path 排序  */
                            return r1.getPath().compareTo(r2.getPath());
                        } catch (NullPointerException npe) {
                            logger.error(npe.getMessage());
                            return 0;
                        }
                    }
                });
                routeMetaSet.add(routeMete);
                groupMap.put(routeMete.getGroup(), routeMetaSet);
            } else {
                routeMetas.add(routeMete);
            }
        } else {
            //路由元验证错误，
            logger.warning(">>> Route meta verify error, group is " + routeMete.getGroup() + " <<<");
        }
    }

    /**
     * Verify the route meta
     *
     * @param meta raw meta
     */
    private boolean routeVerify(RouteMeta meta) {
        String path = meta.getPath();

        //路径必须以“/”开头，不能为空！
        if (StringUtils.isEmpty(path) || !path.startsWith("/")) {   // The path must be start with '/' and not empty!
            return false;
        }

        if (StringUtils.isEmpty(meta.getGroup())) { // Use default group(the first word in path)
            try {
                //使用默认组（路径中的第一个单词）
                String defaultGroup = path.substring(1, path.indexOf("/", 1));
                if (StringUtils.isEmpty(defaultGroup)) {
                    return false;
                }

                meta.setGroup(defaultGroup);
                return true;
            } catch (Exception e) {
                //提取默认组失败！
                logger.error("Failed to extract default group! " + e.getMessage());
                return false;
            }
        }

        return true;
    }
}
